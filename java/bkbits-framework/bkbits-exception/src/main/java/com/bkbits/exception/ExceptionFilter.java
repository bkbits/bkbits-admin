package com.bkbits.exception;

import com.bkbits.core.Result;
import com.bkbits.exception.annotations.ExceptionHandler;
import com.bkbits.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.exception.StatusException;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Filter;
import org.noear.solon.core.handle.FilterChain;

import java.lang.reflect.Method;
import java.util.*;

@Slf4j
@Component(index = Integer.MIN_VALUE)
public class ExceptionFilter implements Filter {
    // 最外层异常深度为 0，最多向下检查 10 层 cause。
    private static final int MAX_CAUSE_DEPTH = 10;
    private static final String INTERNAL_ERROR_JSON = "{\"ok\":false,\"code\":500,\"message\":\"内部错误\"}";

    private final Map<Class<?>, ExceptionAdviceHandler> handlers;

    public ExceptionFilter(@Inject List<ExceptionAdvice> advices) {
        Map<Class<?>, ExceptionAdviceHandler> registry = new HashMap<>();
        for (ExceptionAdvice advice : advices) {
            int before = registry.size();
            for (Method method : advice.getClass().getMethods()) {
                ExceptionHandler annotation = method.getAnnotation(ExceptionHandler.class);
                if (annotation != null && !method.isBridge() && !method.isSynthetic()) {
                    register(registry, advice, method, annotation.value());
                }
            }
            if (registry.size() > before) {
                log.info("ExceptionAdvice: {} 注册了 {} 个异常映射",
                        advice.getClass().getName(),
                        registry.size() - before);
            }
        }
        handlers = Collections.unmodifiableMap(registry);
    }

    private static void register(Map<Class<?>, ExceptionAdviceHandler> registry, ExceptionAdvice advice,
                                 Method method, Class<? extends Throwable>[] types) {
        ExceptionAdviceHandler handler = new ExceptionAdviceHandler(advice, method);
        if (types.length == 0) {
            log.error("ExceptionHandler {} 未指定异常类型，已跳过", handler);
            return;
        }
        if (!Result.class.isAssignableFrom(method.getReturnType())) {
            log.error("ExceptionHandler {} 必须返回 Result，实际返回 {}，已跳过",
                    handler,
                    method.getReturnType().getName());
            return;
        }
        Class<?>[] parameters = method.getParameterTypes();
        if (parameters.length > 1) {
            log.error("ExceptionHandler {} 最多接受 1 个异常参数，实际有 {} 个，已跳过", handler, parameters.length);
            return;
        }
        for (Class<? extends Throwable> type : types) {
            if (parameters.length == 1 && !parameters[0].isAssignableFrom(type)) {
                log.error("ExceptionHandler {} 的参数 {} 无法接收 {}，已跳过该映射",
                        handler,
                        parameters[0].getName(),
                        type.getName());
                continue;
            }
            ExceptionAdviceHandler existing = registry.putIfAbsent(type, handler);
            if (existing == null) {
                log.debug("ExceptionHandler {} 已注册异常类型 {}", handler, type.getName());
            } else {
                log.warn("异常类型 {} 重复注册，保留 {}，跳过 {}", type.getName(), existing, handler);
            }
        }
    }

    @Override
    public void doFilter(Context ctx, FilterChain chain) throws Throwable {
        try {
            chain.doFilter(ctx);
        } catch (Throwable failure) {
            try {
                List<Throwable> causes = inspectCauses(failure);
                if (responseStarted(ctx)) {
                    log.warn("响应已开始，无法返回异常结果", failure);
                    return;
                }
                render(ctx, resolveResult(causes));
            } catch (InterruptedException cancelled) {
                Thread.currentThread().interrupt();
                writeStatus(ctx, 499, "请求被取消");
            }
            return;
        }
        if (!ctx.getHandled()) {
            writeStatus(ctx, 404, "资源不存在");
        }
    }

    // 匹配前先扫描控制异常，避免外层业务 handler 截获内层 Error 或中断。
    private static List<Throwable> inspectCauses(Throwable failure) throws InterruptedException {
        CycleDetector<Throwable> detector = new CycleDetector<>();
        List<Throwable> causes = new ArrayList<>();
        InterruptedException cancellation = null;
        for (Throwable cause = failure; cause != null && !detector.visitBegin(cause); cause = cause.getCause()) {
            if (cause instanceof Error fatal) {
                throw fatal;
            }
            if (cause instanceof InterruptedException interrupted) {
                Thread.currentThread().interrupt();
                cancellation = interrupted;
            }
            causes.add(cause);
            if (causes.size() == MAX_CAUSE_DEPTH + 1) {
                break;
            }
        }
        if (cancellation != null) {
            throw cancellation;
        }
        return causes;
    }

    private Result<?> resolveResult(List<Throwable> causes) throws InterruptedException {
        StatusException status = null;
        for (Throwable cause : causes) {
            if (cause instanceof StatusException found) {
                status = found;
                break;
            }
        }
        for (Throwable cause : causes) {
            ExceptionAdviceHandler handler = findHandler(cause);
            if (handler == null) {
                continue;
            }
            try {
                Result<?> result = handler.handle(cause);
                if (result != null) {
                    log.debug("ExceptionHandler {} 已处理 {}，结果码 {}",
                            handler,
                            cause.getClass().getName(),
                            result.getCode());
                    return result;
                }
            } catch (Throwable adviceFailure) {
                inspectCauses(adviceFailure);
                log.error("ExceptionHandler {} 处理 {} 时失败", handler, cause.getClass().getName(), adviceFailure);
                return defaultResult(status);
            }
            // 返回 null 表示交给默认处理，不调用第二个 advice。
            break;
        }
        if (status != null && status.getCode() >= 400 && status.getCode() < 500) {
            log.warn("请求未完成，HTTP 状态码 {}：{}", status.getCode(), status.getMessage());
        } else {
            log.error("请求处理发生异常", causes.getFirst());
        }
        return defaultResult(status);
    }

    private ExceptionAdviceHandler findHandler(Throwable cause) {
        for (Class<?> type = cause.getClass(); type != Object.class; type = type.getSuperclass()) {
            ExceptionAdviceHandler handler = handlers.get(type);
            if (handler != null) {
                return handler;
            }
        }
        return null;
    }

    private static Result<?> defaultResult(StatusException status) {
        return status == null ? Result.fail(Result.INTERNAL_ERROR, "内部错误")
                : Result.fail(status.getCode(), status.getMessage());
    }

    private static boolean responseStarted(Context ctx) {
        return ctx.getRendered() || ctx.isHeadersSent();
    }

    private static void writeStatus(Context ctx, int code, String message) {
        if (!responseStarted(ctx)) {
            ctx.status(code, message);
            ctx.setHandled(true);
        }
    }

    private static void render(Context ctx, Result<?> result) throws InterruptedException {
        if (responseStarted(ctx)) {
            return;
        }
        switch (result.getCode()) {
            case 400, 404, 405 -> writeStatus(ctx, result.getCode(), result.getMessage());
            default -> {
                String json;
                try {
                    json = JsonUtil.stringify(result);
                } catch (Exception serializationFailure) {
                    inspectCauses(serializationFailure);
                    log.error("异常结果序列化失败，结果码 {}", result.getCode(), serializationFailure);
                    json = INTERNAL_ERROR_JSON;
                }
                // 输出失败可能已经写入部分正文，不在此处重试。
                if (!responseStarted(ctx)) {
                    ctx.outputAsJson(json);
                    ctx.setHandled(true);
                }
            }
        }
    }
}

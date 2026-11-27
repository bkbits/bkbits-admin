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

/**
 * 全局异常过滤器。
 *
 * <p>位于过滤链最外层，负责三件事：</p>
 * <ul>
 *     <li>将未处理的请求映射为 404；</li>
 *     <li>沿异常 cause 链匹配 {@link ExceptionAdvice} 处理器并渲染结果；</li>
 *     <li>兜底处理 {@link StatusException} 与未知异常。</li>
 * </ul>
 *
 * <p>400、404、405 使用纯文本正文，其余状态码输出 {@link Result} JSON。
 * 响应一旦开始写出，不再覆盖原有内容。</p>
 *
 * @see ExceptionAdvice
 * @see ExceptionAdviceHandler
 */
@Slf4j
@Component(index = Integer.MIN_VALUE)
public class ExceptionFilter implements Filter {
    // 最外层异常深度为 0，最多向下检查 10 层 cause。
    private static final int MAX_CAUSE_DEPTH = 10;

    // 序列化本身失败时的固定兜底正文，避免再次进入序列化流程。
    private static final String INTERNAL_ERROR_JSON = "{\"ok\":false,\"code\":500,\"message\":\"内部错误\"}";

    private final Map<Class<?>, ExceptionAdviceHandler> handlers;

    /**
     * 扫描容器中全部 {@link ExceptionAdvice}，注册它们的异常处理方法。
     *
     * @param advices 容器中全部异常处理扩展
     */
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
        // 对外暴露只读视图，防止运行期修改注册表。
        handlers = Collections.unmodifiableMap(registry);
    }

    /**
     * 校验单个处理方法的签名，并把注解声明的异常类型写入注册表。
     *
     * <p>签名不合法时只记录日志并跳过，不影响应用启动。</p>
     *
     * @param registry 异常类型到处理器的注册表
     * @param advice   处理器所在的扩展实例
     * @param method   标注了 {@link ExceptionHandler} 的方法
     * @param types    注解声明的异常类型
     */
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

    /**
     * 执行过滤链，并在异常或未处理请求时写出统一响应。
     *
     * @param ctx   当前请求上下文
     * @param chain 后续过滤链
     * @throws Throwable 无法处理的 {@link Error}，继续向上传播
     */
    @Override
    public void doFilter(Context ctx, FilterChain chain) throws Throwable {
        try {
            chain.doFilter(ctx);
        } catch (Throwable failure) {
            try {
                // 先扫描整条 cause 链，确保内层 Error 或中断不会被外层 handler 截获。
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
        // 请求正常返回但没有任何处理器接管，按资源不存在处理。
        if (!ctx.getHandled()) {
            writeStatus(ctx, 404, "资源不存在");
        }
    }

    /**
     * 收集由外到内的异常链，遇到环或超过深度上限时停止。
     *
     * <p>在匹配 advice 前先整链扫描，确保内层 Error 或中断不会被外层业务处理器截获。</p>
     *
     * @param failure 过滤链抛出的异常
     * @return 异常链，索引越小越靠外层
     * @throws InterruptedException 链中存在中断异常，调用方需恢复中断标志
     * @throws Error                链中存在不可恢复的错误
     */
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

    /**
     * 按由外到内的顺序匹配 advice，未命中时回退到默认结果。
     *
     * @param causes 由外到内的异常链
     * @return 最终响应结果
     * @throws InterruptedException advice 处理过程中传播的中断
     */
    private Result<?> resolveResult(List<Throwable> causes) throws InterruptedException {
        // 提前记录最外层 StatusException，供未匹配 advice 时回退状态码。
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
                // advice 内部的 Error 或中断同样需要传播，其余失败降级为默认结果。
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

    /**
     * 从异常类开始向上查找最近的已注册处理器。
     *
     * @param cause 待匹配的异常
     * @return 匹配的处理器，未注册时返回 {@code null}
     */
    private ExceptionAdviceHandler findHandler(Throwable cause) {
        for (Class<?> type = cause.getClass(); type != Object.class; type = type.getSuperclass()) {
            ExceptionAdviceHandler handler = handlers.get(type);
            if (handler != null) {
                return handler;
            }
        }
        return null;
    }

    /**
     * 生成默认响应结果：优先沿用 {@link StatusException} 的状态码，否则返回内部错误。
     *
     * @param status 异常链中的状态异常，可为 {@code null}
     * @return 默认响应结果
     */
    private static Result<?> defaultResult(StatusException status) {
        return status == null ? Result.fail(Result.INTERNAL_ERROR, "内部错误")
                : Result.fail(status.getCode(), status.getMessage());
    }

    /**
     * 判断响应是否已经开始写出，此时不能再修改状态码或正文。
     *
     * @param ctx 当前请求上下文
     * @return 渲染链已执行或响应头已发送时返回 {@code true}
     */
    private static boolean responseStarted(Context ctx) {
        return ctx.getRendered() || ctx.isHeadersSent();
    }

    /**
     * 以纯文本正文写出状态码，响应已开始时跳过。
     *
     * @param ctx     当前请求上下文
     * @param code    HTTP 状态码
     * @param message 响应正文
     */
    private static void writeStatus(Context ctx, int code, String message) {
        if (!responseStarted(ctx)) {
            ctx.status(code, message);
            ctx.setHandled(true);
        }
    }

    /**
     * 按结果码渲染响应：400、404、405 输出纯文本，其余输出 JSON。
     *
     * @param ctx    当前请求上下文
     * @param result 待输出的结果
     * @throws InterruptedException 序列化过程中出现中断
     */
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

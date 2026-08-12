package com.bkbits.logging.mvc;

import com.bkbits.auth.LoginUtil;
import com.bkbits.logging.annotations.Log;
import com.bkbits.logging.dbo.LogOperation;
import com.bkbits.util.AsyncUtil;
import com.bkbits.util.StringUtil;
import com.easy.query.api.proxy.client.EasyEntityQuery;
import lombok.extern.slf4j.Slf4j;
import org.noear.solon.Solon;
import org.noear.solon.core.aspect.Invocation;
import org.noear.solon.core.aspect.MethodInterceptor;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.serialize.Serializer;
import org.noear.solon.serialization.SerializerNames;

import java.io.IOException;

@Slf4j
public class LogInterceptor implements MethodInterceptor {
    private final Serializer<String> serializer = Solon.app().serializers().get(SerializerNames.AT_JSON);
    private final EasyEntityQuery easyEntityQuery;

    public LogInterceptor(EasyEntityQuery easyEntityQuery) {
        this.easyEntityQuery = easyEntityQuery;
    }

    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        Log logAnno = inv.getMethodAnnotation(Log.class);
        if (logAnno == null) {
            logAnno = inv.getTargetAnnotation(Log.class);
        }
        final LogOperation logOperation = new LogOperation();

        try {
            Context context = Context.current();
            logOperation.setIp(context.realIp());
            logOperation.setUserAgent(context.userAgent());
            logOperation.setUrl(context.url());
        } catch (Throwable ignored) {
        }

        logOperation.setName(logAnno.value());
        logOperation.setType(StringUtil.trimToEmpty(logAnno.type()));
        logOperation.setModule(StringUtil.isBlank(logAnno.module()) ?
                "默认模块" : StringUtil.trimToEmpty(logAnno.module()));
        logOperation.setMethod(inv.getTargetClz().getName() + "." + inv.method().getMethod().getName());
        try {
            if (logAnno.args() && inv.args().length > 0) {
                logOperation.setArgs(serializer.serialize(inv.args()));
            }
        } catch (IOException e) {
            log.error("Log参数序列化失败", e);
        }

        try {
            logOperation.setCreateBy(LoginUtil.getLoginUserName());
        } catch (Throwable e) {
            log.error("获取createBy失败", e);
        }

        try {
            String remark = StringUtil.trim(logAnno.remark());
            if (StringUtil.isNotEmpty(remark)) {
                logOperation.setRemark(remark);
            }
        } catch (Throwable e) {
            log.error("获取remark失败", e);
        }

        Object result = null;
        long recordTime = System.currentTimeMillis();
        try {
            result = inv.invoke();
            logOperation.setCostTime(Math.toIntExact(System.currentTimeMillis() - recordTime));
        } catch (Throwable throwable) {
            logOperation.setCostTime(Math.toIntExact(System.currentTimeMillis() - recordTime));
            logOperation.setResult(throwable.getMessage());

            AsyncUtil.submit(() -> {
                easyEntityQuery.insertable(logOperation).executeRows();
            });

            throw throwable;
        }

        try {
            if (logAnno.result() && result != null) {
                logOperation.setResult(serializer.serialize(result));
            }
        } catch (IOException e) {
            log.error("Log返回值序列化失败", e);
        }

        AsyncUtil.submit(() -> {
            easyEntityQuery.insertable(logOperation).executeRows();
        });
        return result;
    }
}

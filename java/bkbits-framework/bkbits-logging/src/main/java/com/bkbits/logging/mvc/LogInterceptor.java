package com.bkbits.logging.mvc;

import com.bkbits.logging.ILogProvider;
import com.bkbits.logging.annotations.Log;
import com.bkbits.logging.dbo.LogRecord;
import com.bkbits.utils.AsyncUtil;
import com.bkbits.utils.StringUtil;
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
    private final ILogProvider logProvider;
    private final EasyEntityQuery easyEntityQuery;

    public LogInterceptor(ILogProvider logProvider, EasyEntityQuery easyEntityQuery) {
        this.logProvider = logProvider;
        this.easyEntityQuery = easyEntityQuery;
    }

    @Override
    public Object doIntercept(Invocation inv) throws Throwable {
        Log logAnno = inv.getMethodAnnotation(Log.class);
        if (logAnno == null) {
            logAnno = inv.getTargetAnnotation(Log.class);
        }
        final LogRecord logRecord = new LogRecord();

        try {
            Context context = Context.current();
            logRecord.setIp(context.realIp());
            logRecord.setUserAgent(context.userAgent());
            logRecord.setUrl(context.url());
        } catch (Throwable ignored) {
        }

        logRecord.setName(logAnno.value());
        logRecord.setType(StringUtil.trimToEmpty(logAnno.type()));
        logRecord.setModule(StringUtil.isBlank(logAnno.module()) ?
                "默认模块" : StringUtil.trimToEmpty(logAnno.module()));
        logRecord.setMethod(inv.getTargetClz().getName() + "." + inv.method().getMethod().getName());
        try {
            if (logAnno.args() && inv.args().length > 0) {
                logRecord.setArgs(serializer.serialize(inv.args()));
            }
        } catch (IOException e) {
            log.error("Log参数序列化失败", e);
        }

        try {
            logRecord.setCreateBy(logProvider.getCreateBy());
        } catch (Throwable e) {
            log.error("获取createBy失败", e);
        }

        try {
            logRecord.setRemark(logProvider.getRemark(logAnno));
        } catch (Throwable e) {
            log.error("获取remark失败", e);
        }

        Object result = null;
        long recordTime = System.currentTimeMillis();
        try {
            result = inv.invoke();
            logRecord.setCostTime(Math.toIntExact(System.currentTimeMillis() - recordTime));
        } catch (Throwable throwable) {
            logRecord.setCostTime(Math.toIntExact(System.currentTimeMillis() - recordTime));
            logRecord.setResult(throwable.getMessage());

            AsyncUtil.submit(() -> {
                easyEntityQuery.insertable(logRecord).executeRows();
            });

            throw throwable;
        }

        try {
            if (logAnno.result() && result != null) {
                logRecord.setResult(serializer.serialize(result));
            }
        } catch (IOException e) {
            log.error("Log返回值序列化失败", e);
        }

        AsyncUtil.submit(() -> {
            easyEntityQuery.insertable(logRecord).executeRows();
        });
        return result;
    }
}

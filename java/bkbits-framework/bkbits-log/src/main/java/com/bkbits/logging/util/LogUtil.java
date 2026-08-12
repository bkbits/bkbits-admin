package com.bkbits.logging.util;

import com.bkbits.logging.ILogProvider;
import com.bkbits.logging.dbo.LogLogin;
import com.bkbits.logging.dbo.LogOperation;
import com.bkbits.util.AsyncUtil;
import com.easy.query.api.proxy.client.EasyEntityQuery;
import lombok.experimental.UtilityClass;
import org.noear.solon.Solon;

import java.time.LocalDateTime;

@UtilityClass
public class LogUtil {
    private static final EasyEntityQuery easyEntityQuery = Solon.context().getBean(EasyEntityQuery.class);
    private static final ILogProvider logProvider = Solon.context().getBean(ILogProvider.class);

    /**
     * 添加操作日志
     *
     * @param logOperation 操作日志信息
     */
    public void log(LogOperation logOperation) {
        if (logOperation.getCreateBy() == null) {
            logOperation.setCreateBy(logProvider.getCreateBy());
        }
        if (logOperation.getCreateTime() == null) {
            logOperation.setCreateTime(LocalDateTime.now());
        }
        AsyncUtil.submit(() -> easyEntityQuery.insertable(logOperation).executeRows());
    }

    /**
     * 添加登录日志
     *
     * @param logLogin 登录信息
     */
    public void addLoginLog(LogLogin logLogin) {
        if (logLogin.getCreateBy() == null) {
            logLogin.setCreateBy(logProvider.getCreateBy());
        }
        if (logLogin.getCreateTime() == null) {
            logLogin.setCreateTime(LocalDateTime.now());
        }
        AsyncUtil.submit(() -> easyEntityQuery.insertable(logLogin).executeRows());
    }
}

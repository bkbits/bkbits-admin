package com.bkbits.logging.config;

import com.bkbits.logging.annotations.Log;
import com.bkbits.logging.dbo.LogLogin;
import com.bkbits.logging.dbo.LogOperation;
import com.bkbits.logging.mvc.LogInterceptor;
import com.easy.query.api.proxy.client.EasyEntityQuery;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import java.util.Arrays;

/**
 * 日志配置。
 */
@Configuration
public class LogConfig {

    @Bean
    public void init(
            @Inject EasyEntityQuery easyEntityQuery
    ) {
        Solon.context()
                .beanInterceptorAdd(
                        Log.class,
                        new LogInterceptor()
                );

        easyEntityQuery.getDatabaseCodeFirst().syncTableCommand(Arrays.asList(
                LogLogin.class,
                LogOperation.class
        ));
    }
}

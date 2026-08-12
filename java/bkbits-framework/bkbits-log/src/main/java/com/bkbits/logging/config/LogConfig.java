package com.bkbits.logging.config;

import com.bkbits.logging.annotations.Log;
import com.bkbits.logging.mvc.LogInterceptor;
import com.easy.query.api.proxy.client.EasyEntityQuery;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

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
                        new LogInterceptor(easyEntityQuery)
                );
    }
}

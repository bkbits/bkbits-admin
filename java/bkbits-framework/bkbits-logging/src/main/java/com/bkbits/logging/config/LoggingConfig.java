package com.bkbits.logging.config;

import com.bkbits.logging.ILogProvider;
import com.bkbits.logging.annotations.Log;
import com.bkbits.logging.mvc.LogInterceptor;
import com.easy.query.api.proxy.client.EasyEntityQuery;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Inject;
import org.noear.solon.logging.LogOptions;
import org.noear.solon.logging.event.Level;

/**
 * 日志配置。
 */
@Configuration
public class LoggingConfig {

    @Bean
    public void init(@Inject ILogProvider logProvider, @Inject EasyEntityQuery easyEntityQuery) {
        Solon.context()
                .beanInterceptorAdd(
                        Log.class,
                        new LogInterceptor(logProvider, easyEntityQuery)
                );
    }
}

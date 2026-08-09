package com.bkbits.scheduling.config;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 定时任务配置。
 */
@Configuration
public class SchedulingConfig {

    private static final Logger log = LoggerFactory.getLogger(SchedulingConfig.class);

    /**
     * 示例任务：每 5 分钟执行一次。
     */
    @Bean
    @Scheduled(cron = "0 0/5 * * * ?")
    public void demoTask() {
        log.info("demo quartz task executed");
    }
}

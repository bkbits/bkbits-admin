package com.bkbits.scheduling.config;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.scheduling.annotation.Scheduled;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * 定时任务配置。
 */
@Configuration
public class SchedulingConfig {

    private static final Logger log = LoggerFactory.getLogger(SchedulingConfig.class);

    @Bean
    public Scheduler scheduled(@Inject("${solon.dataSources.db1}") Properties properties) throws SchedulerException {
        Properties prop = new Properties();

        // jobStore：JDBC 持久化
        prop.setProperty("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
        prop.setProperty("org.quartz.jobStore.acquireTriggersWithinLock", "true");
        prop.setProperty("org.quartz.jobStore.misfireThreshold", "5000");
        prop.setProperty("org.quartz.jobStore.tablePrefix", "QRTZ_");
        prop.setProperty("org.quartz.jobStore.dataSource", "qzDS");

        // 数据源（取自 solon.dataSources.db1 配置）。
        // 连接池用 hikaricp：Quartz 2.3.2 对显式 connectionProvider.class 走无参构造，
        // 而 HikariCpPoolingConnectionProvider 只有 Properties 构造器，须用 provider=hikaricp 触发
        prop.setProperty("org.quartz.dataSource.qzDS.provider", "hikaricp");
        prop.setProperty("org.quartz.dataSource.qzDS.driver",
                properties.getProperty("driverClassName", "com.mysql.cj.jdbc.Driver"));
        prop.setProperty("org.quartz.dataSource.qzDS.URL",
                properties.getProperty("jdbcUrl", "jdbc:mysql://localhost:3306/bkbits?useUnicode=true&characterEncoding=utf8"));
        prop.setProperty("org.quartz.dataSource.qzDS.user",
                properties.getProperty("username", "root"));
        prop.setProperty("org.quartz.dataSource.qzDS.password",
                properties.getProperty("password", ""));
        prop.setProperty("org.quartz.dataSource.qzDS.maxConnections", "10");
        prop.setProperty("org.quartz.dataSource.qzDS.validationQuery", "select 1");

        // 线程池
        prop.setProperty("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        prop.setProperty("org.quartz.threadPool.threadCount", "5");
        prop.setProperty("org.quartz.threadPool.threadPriority", "5");
        prop.setProperty("org.quartz.threadPool.threadsInheritContextClassLoaderOfInitializingThread", "true");

        StdSchedulerFactory factory = new StdSchedulerFactory(prop);

        return factory.getScheduler();
    }

    /**
     * 示例任务：每 5 分钟执行一次。
     */
    @Bean
    @Scheduled(cron = "0 0/5 * * * ?")
    public void demoTask() {
        log.info("demo quartz task executed");
    }
}

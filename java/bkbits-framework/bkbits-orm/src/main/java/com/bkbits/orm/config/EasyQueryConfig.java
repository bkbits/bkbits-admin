package com.bkbits.orm.config;

import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.easy.query.core.api.client.EasyQueryClient;
import com.easy.query.core.basic.extension.conversion.ValueConverter;
import com.easy.query.core.basic.extension.encryption.EncryptionStrategy;
import com.easy.query.core.basic.extension.interceptor.Interceptor;
import com.easy.query.core.basic.extension.logicdel.LogicDeleteStrategy;
import com.easy.query.core.configuration.QueryConfiguration;
import com.easy.query.core.sharding.initializer.ShardingInitializer;
import com.easy.query.solon.annotation.Db;
import com.easy.query.solon.integration.QueryTrackInterceptor;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

import java.util.List;

/**
 * easy-query orm 配置。
 *
 * <p>easy-query 主体装配由 sql-solon-plugin 自动完成，配置项前缀为 {@code easy-query}，按数据源名分组，如：</p>
 * <pre>
 * easy-query:
 *   db:
 *     database: mysql
 *     name-conversion: underlined
 *     delete-throw: true
 * </pre>
 */
@Configuration
public class EasyQueryConfig {

    /**
     * EasyQueryClient（基础查询客户端）注册为 bean，可通过 {@code @Inject} 注入。
     * 未配置数据源 db 时不注册。
     */
    @Bean
    public EasyQueryClient easyQueryClient(@Db("main") EasyQueryClient easyQueryClient) {
        return easyQueryClient;
    }

    /**
     * EasyEntityQuery（实体代理查询客户端）注册为 bean，可通过 {@code @Inject} 注入。
     * 未配置数据源 db 时不注册。
     */
    @Bean
    public EasyEntityQuery easyEntityQuery(@Db("main") EasyEntityQuery easyEntityQuery) {
        return easyEntityQuery;
    }

    /**
     * 数据源 db 的查询配置个性化入口（逻辑删除、加密策略、拦截器、分片初始化器、值转换等）。
     */
    @Bean
    public void dbQueryConfiguration(
            @Db("main") QueryConfiguration configuration,
            @Inject List<LogicDeleteStrategy> logicDeleteStrategies,
            @Inject List<EncryptionStrategy> encryptionStrategies,
            @Inject List<Interceptor> interceptors,
            @Inject ShardingInitializer shardingInitializer,
            @Inject List<ValueConverter<?, ?>> converters
    ) {
        logicDeleteStrategies.forEach(configuration::applyLogicDeleteStrategy);
        encryptionStrategies.forEach(configuration::applyEncryptionStrategy);
        interceptors.forEach(configuration::applyInterceptor);
        converters.forEach(configuration::applyValueConverter);
        configuration.applyShardingInitializer(shardingInitializer);
    }
}

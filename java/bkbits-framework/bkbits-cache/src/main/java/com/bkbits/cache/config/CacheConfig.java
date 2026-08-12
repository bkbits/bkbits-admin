package com.bkbits.cache.config;

import org.noear.redisx.RedisClient;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.cache.jedis.RedisCacheService;
import org.noear.solon.data.cache.CacheService;

/**
 * cache 配置。
 *
 * <p>缓存服务由 {@code cache.redis} 配置块自动构建，示例：</p>
 * <pre>
 * cache.redis:
 *   driverType: "redis"
 *   server: "localhost:6379"
 *   db: 0
 *   password: ""
 *   maxTotal: 200
 * </pre>
 */
@Configuration
public class CacheConfig {

    /**
     * 注册默认缓存服务；未配置 {@code cache.redis} 时不注册。
     */
    @Bean
    public CacheService cacheService(
            @Inject("${solon.redis}") RedisCacheService cacheService
    ) {
        return cacheService;
    }

    @Bean
    public RedisClient redisClient(
            @Inject("${solon.redis}") RedisCacheService cacheService
    ) {
        return cacheService.client();
    }
}

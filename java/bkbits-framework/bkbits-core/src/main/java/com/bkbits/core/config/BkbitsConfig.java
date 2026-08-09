package com.bkbits.core.config;

import com.bkbits.core.BkbitsProperties;
import com.bkbits.core.generator.IdGenerator;
import com.bkbits.core.generator.IdGeneratorProperties;
import com.bkbits.core.generator.IdGeneratorType;
import com.bkbits.core.generator.impl.PearIdGenerator;
import com.bkbits.core.generator.impl.SnowflakeIdWorker;
import com.bkbits.core.generator.impl.UUIDGenerator;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

/**
 * 核心基础库配置。
 */
@Configuration
public class BkbitsConfig {

    @Inject
    private BkbitsProperties bkbitsProperties;

    @Bean
    public IdGenerator idGenerator() {
        IdGeneratorProperties generatorProperties = bkbitsProperties.getIdGenerator();
        IdGeneratorType type = generatorProperties.getType() == null
                ? IdGeneratorType.Pear
                : generatorProperties.getType();

        switch (type) {
            case Snowflake:
                return new SnowflakeIdWorker(
                        generatorProperties.getWorkerId(),
                        generatorProperties.getDatacenterId());
            case Pear:
                return new PearIdGenerator(
                        generatorProperties.getWorkerId(),
                        generatorProperties.getDatacenterId());
            case UUID:
                return new UUIDGenerator();
            default:
                throw new IllegalStateException("不支持的 ID 生成器类型: " + type);
        }
    }
}

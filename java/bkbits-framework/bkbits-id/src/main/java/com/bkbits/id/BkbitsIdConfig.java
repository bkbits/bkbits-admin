package com.bkbits.id;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

@Configuration
public class BkbitsIdConfig {
    @Bean
    @Condition(onMissingBean = BkbitsIdProperties.class)
    public BkbitsIdProperties bkbitsIdProperties() {
        return new BkbitsIdProperties();
    }

    /**
     * 依据配置初始化 {@link IdUtil} 的默认 ID 生成器。
     *
     * <p>按 {@code bkbits.id.type} 选择的算法创建生成器，并使用
     * {@code workerId}、{@code datacenterId}、{@code segmentId} 作为构造参数；
     * 类型未配置时按 {@link IdGeneratorType#Pear} 处理。</p>
     *
     * @param idProperties ID 生成器配置属性
     */
    @Bean
    public void configIdUtils(@Inject BkbitsIdProperties idProperties) {
        IdGeneratorType type = idProperties.getType() == null ? IdGeneratorType.Pear : idProperties.getType();
        IdGenerator generator;
        switch (type) {
            case Snowflake:
                generator = IdGenerator.snowflake(idProperties.getWorkerId(), idProperties.getDatacenterId());
                break;
            case Pear:
                generator = IdGenerator.pearId(idProperties.getWorkerId(), idProperties.getSegmentId());
                break;
            default:
                generator = IdGenerator.uuid();
                break;
        }
        IdUtil.init(generator);
    }
}

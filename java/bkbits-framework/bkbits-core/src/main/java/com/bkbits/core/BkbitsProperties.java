package com.bkbits.core;

import com.bkbits.generator.IdGeneratorProperties;
import lombok.Data;
import org.noear.solon.annotation.BindProps;
import org.noear.solon.annotation.Configuration;

/**
 * bkbits 应用配置属性。
 *
 * <p>对应 app.yml 中 {@code bkbits.*} 前缀的配置，如：</p>
 * <pre>
 * bkbits:
 *   id-generator:
 *     type: Snowflake
 *     worker-id: 1
 *     datacenter-id: 1
 * </pre>
 */
@BindProps(prefix = "bkbits")
@Configuration
@Data
public class BkbitsProperties {

    /** ID 生成器配置 */
    private IdGeneratorProperties idGenerator = new IdGeneratorProperties();
}

package com.bkbits.id;

import lombok.Data;
import org.noear.solon.annotation.BindProps;
import org.noear.solon.annotation.Configuration;

/**
 * ID 生成器配置属性。
 *
 * <p>对应 {@code bkbits.id-generator.*} 前缀的配置。</p>
 */
@Data
@Configuration
@BindProps(prefix = "bkbits.id")
public class BkbitsIdProperties {

    /**
     * 生成器类型（雪花 / Pear）
     */
    private IdGeneratorType type = IdGeneratorType.Pear;

    /**
     * 工作节点 ID（雪花算法）
     */
    private long workerId = 0L;

    /**
     * 数据中心 ID（雪花算法）
     */
    private long datacenterId = 0L;

    /**
     * 数据中心 ID（雪花算法）
     */
    private long segmentId = 0L;
}

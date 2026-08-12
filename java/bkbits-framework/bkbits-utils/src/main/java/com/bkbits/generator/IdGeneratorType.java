package com.bkbits.generator;

/**
 * ID 生成器类型。
 */
public enum IdGeneratorType {

    /** 雪花算法 */
    Snowflake,

    /** 梨花（Pear）算法 */
    Pear,

    /** UUID 算法 */
    UUID,
}

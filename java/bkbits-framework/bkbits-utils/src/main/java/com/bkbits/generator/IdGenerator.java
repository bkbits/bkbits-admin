package com.bkbits.generator;

/**
 * ID 生成器接口。
 */
@FunctionalInterface
public interface IdGenerator {
    /**
     * 生成下一个 ID。
     */
    String nextId();
}

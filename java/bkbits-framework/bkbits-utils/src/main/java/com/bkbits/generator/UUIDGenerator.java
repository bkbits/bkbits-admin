package com.bkbits.generator;

import java.util.UUID;

/**
 * UUID 算法
 *
 * @author lkq
 * @version 2023-10-19
 */
public class UUIDGenerator implements IdGenerator {

    /**
     * 生成下一个 ID（UUID 去除横线）。
     *
     * @return 32 位 UUID 字符串
     */
    @Override
    public String nextId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}

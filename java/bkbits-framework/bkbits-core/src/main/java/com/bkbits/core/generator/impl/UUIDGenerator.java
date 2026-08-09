package com.bkbits.core.generator.impl;

import com.bkbits.core.generator.IdGenerator;

import java.util.UUID;

/**
 * UUID 算法
 *
 * @author lkq
 * @version 2023-10-19
 */
public class UUIDGenerator implements IdGenerator {
    @Override
    public String nextId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}

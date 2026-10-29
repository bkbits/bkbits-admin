package com.bkbits.id;

/**
 * ID 生成器接口。
 */
@FunctionalInterface
public interface IdGenerator {
    static IdGenerator uuid() {
        return new UUIDGenerator();
    }

    static IdGenerator snowflake(long workerId, long datacenterId) {
        return new SnowflakeIdGenerator(workerId, datacenterId);
    }

    static IdGenerator pearId(long workerId, long segmentId) {
        return new PearIdGenerator(workerId, segmentId);
    }

    /**
     * 生成下一个 ID。
     */
    String nextId();
}

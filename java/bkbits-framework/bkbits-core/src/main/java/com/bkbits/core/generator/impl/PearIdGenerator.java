package com.bkbits.core.generator.impl;

import com.bkbits.core.generator.IdGenerator;

/**
 * 梨花算法
 *
 * @author lkq
 * @version 2023-10-19
 */
public class PearIdGenerator
        implements IdGenerator {
    private static final long MAX_SEQUENCE = 0x7FFFF;
    private static final long SEQUENCE_MASK = MAX_SEQUENCE;

    private static final long WORKER_ID_MASK = 0x3FFF;
    private static final long WORKER_ID_SHIFT = 19;

    private static final long SEGMENT_ID_MASK = 0x7;
    private static final long SEGMENT_ID_SHIFT = WORKER_ID_SHIFT + 10;
    private static final long TIMESTAMP_SHIFT = SEGMENT_ID_SHIFT + 3;
    private static final long TIMESTAMP_MASK = 0x7FFFFFFF;
    private final long workerId;
    private final long segmentId;

    private long timestamp = System.currentTimeMillis() / 1000;
    private long sequence = 0;

    public PearIdGenerator() {
        this.workerId = 0;
        this.segmentId = 0;
    }

    public PearIdGenerator(long workerId, long segmentId) {
        this.segmentId = segmentId & 0x7;
        this.workerId = workerId & 0x3FF;
    }

    @Override
    public String nextId() {
        long curTimestamp = System.currentTimeMillis() / 1000;
        long curSequence;
        synchronized (this) {
            if (curTimestamp <= timestamp) {
                if ((curSequence = ++sequence) > MAX_SEQUENCE) {
                    curSequence = sequence = 1;
                    curTimestamp = ++timestamp;
                }
            } else {
                timestamp = curTimestamp;
                curSequence = sequence = 1;
            }
        }

        return String.valueOf(
                maskShift(curTimestamp, TIMESTAMP_MASK, TIMESTAMP_SHIFT) |
                        maskShift(segmentId, SEGMENT_ID_MASK, SEGMENT_ID_SHIFT) |
                        maskShift(workerId, WORKER_ID_MASK, WORKER_ID_SHIFT) |
                        maskShift(curSequence, SEQUENCE_MASK, 0)
        );
    }

    private long maskShift(final long val, final long mask, final long shift) {
        return (val & mask) << shift;
    }
}

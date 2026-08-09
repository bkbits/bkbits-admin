package com.bkbits.tests;

import com.bkbits.core.Result;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Result 测试。
 */
public class ResultTest {

    @Test
    public void testOk() {
        Result<Void> ok = Result.ok();
        assertTrue(ok.isOk());
        assertEquals(200, ok.getCode());
        assertEquals("OK", ok.getMessage());
        assertNull(ok.getData());

        Result<String> withData = Result.ok("data");
        assertTrue(withData.isOk());
        assertEquals("data", withData.getData());

        Result<Integer> custom = Result.ok(1, "自定义", 201);
        assertTrue(custom.isOk());
        assertEquals(201, custom.getCode());
        assertEquals("自定义", custom.getMessage());
        assertEquals(1, custom.getData());
    }

    @Test
    public void testFail() {
        Result<?> fail = Result.fail();
        assertFalse(fail.isOk());
        assertEquals(500, fail.getCode());
        assertEquals("操作失败", fail.getMessage());

        Result<?> withMsg = Result.fail("参数错误");
        assertEquals(500, withMsg.getCode());
        assertEquals("参数错误", withMsg.getMessage());

        Result<?> custom = Result.fail(400, "非法请求");
        assertFalse(custom.isOk());
        assertEquals(400, custom.getCode());
        assertEquals("非法请求", custom.getMessage());
    }
}

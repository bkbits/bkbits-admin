package com.bkbits.tests;

import com.bkbits.core.PageResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * PageResult 测试。
 */
public class PageResultTest {

    @Test
    public void testPage() {
        PageResult<String> result = PageResult.page(100, List.of("a", "b"));
        assertTrue(result.isOk());
        assertEquals(200, result.getCode());
        assertEquals(100, result.getTotal());
        assertEquals(List.of("a", "b"), result.getData());
    }

    @Test
    public void testFail() {
        PageResult<Void> fail = PageResult.pageFail();
        assertFalse(fail.isOk());
        assertEquals(500, fail.getCode());
        assertEquals("操作失败", fail.getMessage());

        PageResult<Void> withMsg = PageResult.pageFail("参数错误");
        assertEquals(500, withMsg.getCode());
        assertEquals("参数错误", withMsg.getMessage());

        PageResult<Void> custom = PageResult.pageFail(400, "非法请求");
        assertFalse(custom.isOk());
        assertEquals(400, custom.getCode());
        assertEquals("非法请求", custom.getMessage());
    }
}

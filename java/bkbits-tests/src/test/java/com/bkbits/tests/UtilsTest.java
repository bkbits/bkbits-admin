package com.bkbits.tests;

import com.bkbits.utils.AsyncUtil;
import com.bkbits.utils.CollectionUtil;
import com.bkbits.utils.DateUtil;
import com.bkbits.utils.StringUtil;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 工具类测试。
 */
public class UtilsTest {

    @Test
    public void testStringUtil() {
        assertTrue(StringUtil.isBlank("  "));
        assertFalse(StringUtil.isBlank("x"));
        assertNull(StringUtil.trimToNull("  "));
        assertEquals("user_name", StringUtil.camelToSnake("userName"));
        assertEquals("userName", StringUtil.snakeToCamel("user_name"));
        assertEquals("138****5678", StringUtil.mask("13812345678", 3, 7, '*'));
        assertEquals("Hello", StringUtil.capitalize("hello"));
        assertEquals("a,b,c", StringUtil.join(List.of("a", "b", "c"), ","));
    }

    @Test
    public void testCollectionUtil() {
        List<Integer> list = List.of(1, 2, 3, 4, 5);
        assertTrue(CollectionUtil.isNotEmpty(list));
        assertEquals(5, CollectionUtil.size(list));
        assertEquals(1, CollectionUtil.first(list));
        assertEquals(5, CollectionUtil.last(list));
        assertEquals(List.of(List.of(1, 2), List.of(3, 4), List.of(5)), CollectionUtil.partition(list, 2));
        assertEquals(List.of(1, 2, 3), CollectionUtil.distinct(List.of(1, 2, 2, 3, 1)));
        assertEquals("1,2,3", CollectionUtil.join(List.of(1, 2, 3), ","));
    }

    @Test
    public void testDateUtil() {
        String str = DateUtil.now(DateUtil.PATTERN_DATETIME);
        LocalDateTime dateTime = DateUtil.parseLocalDateTime(str, DateUtil.PATTERN_DATETIME);
        assertEquals(str, DateUtil.format(dateTime, DateUtil.PATTERN_DATETIME));
        assertEquals("2024-01-02", DateUtil.format(DateUtil.parseLocalDate("2024-01-02", DateUtil.PATTERN_DATE), DateUtil.PATTERN_DATE));
        assertTrue(DateUtil.betweenDays(
                DateUtil.parseLocalDateTime("2024-01-01 00:00:00", DateUtil.PATTERN_DATETIME),
                DateUtil.parseLocalDateTime("2024-01-10 00:00:00", DateUtil.PATTERN_DATETIME)) == 9);
    }

    @Test
    public void testAsyncUtil() throws Exception {
        AsyncUtil.execute(() -> {
            // 异步任务无返回值
        });
        Future<String> future = AsyncUtil.submit(() -> "async-result");
        assertEquals("async-result", future.get(3, TimeUnit.SECONDS));
        assertTrue(AsyncUtil.schedule(() -> {
        }, 100, TimeUnit.MILLISECONDS) != null);
    }
}

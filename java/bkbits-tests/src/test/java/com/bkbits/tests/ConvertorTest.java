package com.bkbits.tests;

import com.bkbits.orm.convertor.DoubleListConvertor;
import com.bkbits.orm.convertor.IntListConvertor;
import com.bkbits.orm.convertor.LongListConvertor;
import com.bkbits.orm.convertor.StringListConvertor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * easy-query 转换器测试。
 */
public class ConvertorTest {

    @Test
    public void testStringList() {
        StringListConvertor convertor = new StringListConvertor();
        assertEquals("a,b", convertor.serialize(List.of("a", "b"), null));
        assertEquals(List.of("a", "b"), convertor.deserialize("a,b", null));
        assertEquals(List.of("a", ""), convertor.deserialize("a,", null));
        assertEquals(new ArrayList<>(), convertor.deserialize("", null));
        assertNull(convertor.serialize(null, null));
        assertNull(convertor.deserialize(null, null));
    }

    @Test
    public void testLongList() {
        LongListConvertor convertor = new LongListConvertor();
        assertEquals("1,2", convertor.serialize(List.of(1L, 2L), null));
        assertEquals(List.of(1L, 2L), convertor.deserialize("1, 2", null));
        assertEquals(new ArrayList<>(), convertor.deserialize("  ", null));
        assertNull(convertor.deserialize(null, null));
    }

    @Test
    public void testIntList() {
        IntListConvertor convertor = new IntListConvertor();
        assertEquals("1,2", convertor.serialize(List.of(1, 2), null));
        assertEquals(List.of(1, 2), convertor.deserialize("1,2", null));
        assertNull(convertor.deserialize(null, null));
    }

    @Test
    public void testDoubleList() {
        DoubleListConvertor convertor = new DoubleListConvertor();
        assertEquals("1.5,2.5", convertor.serialize(List.of(1.5, 2.5), null));
        assertEquals(List.of(1.5, 2.5), convertor.deserialize("1.5,2.5", null));
        assertNull(convertor.deserialize(null, null));
    }
}

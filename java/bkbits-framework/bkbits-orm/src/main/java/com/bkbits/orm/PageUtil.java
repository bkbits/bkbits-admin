package com.bkbits.orm;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PageUtil {
    public static <T> EasyPager<T> current() {
        return EasyPager.current();
    }

    public static <T> EasyPager<T> of(long page, long pageSize) {
        return EasyPager.of(page, pageSize);
    }
}

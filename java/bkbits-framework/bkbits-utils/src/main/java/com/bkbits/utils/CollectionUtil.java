package com.bkbits.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 集合工具类。
 */
public final class CollectionUtil {

    private CollectionUtil() {
    }

    /** 集合是否为空（null 或空集合） */
    public static boolean isEmpty(Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }

    /** 集合是否非空 */
    public static boolean isNotEmpty(Collection<?> coll) {
        return !isEmpty(coll);
    }

    /** Map 是否为空 */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /** Map 是否非空 */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    /** 集合大小（null 返回 0） */
    public static int size(Collection<?> coll) {
        return coll == null ? 0 : coll.size();
    }

    /** Map 大小（null 返回 0） */
    public static int size(Map<?, ?> map) {
        return map == null ? 0 : map.size();
    }

    /** 数组转 List */
    @SafeVarargs
    public static <T> List<T> toList(T... values) {
        List<T> list = new ArrayList<>();
        if (values != null) {
            java.util.Collections.addAll(list, values);
        }
        return list;
    }

    /** 取第一个元素（空集合返回 null） */
    public static <T> T first(List<T> list) {
        return isEmpty(list) ? null : list.get(0);
    }

    /** 取最后一个元素（空集合返回 null） */
    public static <T> T last(List<T> list) {
        return isEmpty(list) ? null : list.get(list.size() - 1);
    }

    /** 去重（保持原顺序） */
    public static <T> List<T> distinct(List<T> list) {
        if (isEmpty(list)) {
            return list;
        }
        return new ArrayList<>(new java.util.LinkedHashSet<>(list));
    }

    /**
     * 分批：将列表按指定大小切分为多个子列表。
     *
     * <pre>
     * partition([1,2,3,4,5], 2) -> [[1,2],[3,4],[5]]
     * </pre>
     */
    public static <T> List<List<T>> partition(List<T> list, int size) {
        if (isEmpty(list)) {
            return new ArrayList<>();
        }
        if (size <= 0) {
            throw new IllegalArgumentException("size 必须大于 0");
        }
        List<List<T>> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            result.add(new ArrayList<>(list.subList(i, Math.min(i + size, list.size()))));
        }
        return result;
    }

    /** 拼接集合元素 */
    public static String join(Collection<?> coll, CharSequence delimiter) {
        if (coll == null || coll.isEmpty()) {
            return "";
        }
        return StringUtil.join(coll, delimiter);
    }
}

package com.bkbits.util;

import lombok.experimental.UtilityClass;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * 集合工具类。
 */
@UtilityClass
public class CollectionUtil {

    public interface ITree<T extends ITree<T, ID>, ID> {
        ID getId();

        ID getParentId();

        void setChildren(List<T> children);

        List<T> getChildren();
    }

    /**
     * 集合是否为空（null 或空集合）。
     *
     * @param coll 待判断集合
     * @return 为空返回 {@code true}
     */
    public static boolean isEmpty(Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }

    /**
     * 集合是否非空。
     *
     * @param coll 待判断集合
     * @return 非空返回 {@code true}
     */
    public static boolean isNotEmpty(Collection<?> coll) {
        return !isEmpty(coll);
    }

    /**
     * Map 是否为空（null 或空 Map）。
     *
     * @param map 待判断 Map
     * @return 为空返回 {@code true}
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * Map 是否非空。
     *
     * @param map 待判断 Map
     * @return 非空返回 {@code true}
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    /**
     * 集合大小（null 返回 0）。
     *
     * @param coll 待统计集合
     * @return 元素个数
     */
    public static int size(Collection<?> coll) {
        return coll == null ? 0 : coll.size();
    }

    /**
     * Map 大小（null 返回 0）。
     *
     * @param map 待统计 Map
     * @return 键值对个数
     */
    public static int size(Map<?, ?> map) {
        return map == null ? 0 : map.size();
    }

    /**
     * 数组转 List。
     *
     * @param values 可变参数数组，可为空
     * @param <T>    元素类型
     * @return 包含全部元素的 List；数组为空时返回空 List
     */
    @SafeVarargs
    public static <T> List<T> toList(T... values) {
        List<T> list = new ArrayList<>();
        if (values != null) {
            java.util.Collections.addAll(list, values);
        }
        return list;
    }

    /**
     * 取第一个元素（空集合返回 null）。
     *
     * @param list 待取值列表
     * @param <T>  元素类型
     * @return 第一个元素；空列表返回 null
     */
    public static <T> T first(List<T> list) {
        return isEmpty(list) ? null : list.get(0);
    }

    /**
     * 取最后一个元素（空集合返回 null）。
     *
     * @param list 待取值列表
     * @param <T>  元素类型
     * @return 最后一个元素；空列表返回 null
     */
    public static <T> T last(List<T> list) {
        return isEmpty(list) ? null : list.get(list.size() - 1);
    }

    /**
     * 去重（保持原顺序）。
     *
     * @param list 待去重列表
     * @param <T>  元素类型
     * @return 去重后的新列表；空列表返回原列表
     */
    public static <T> List<T> distinct(Collection<T> list) {
        if (isEmpty(list)) {
            return new ArrayList<>(list);
        }
        return new ArrayList<>(new java.util.LinkedHashSet<>(list));
    }

    /**
     * 分批：将列表按指定大小切分为多个子列表。
     *
     * <pre>
     * partition([1,2,3,4,5], 2) -> [[1,2],[3,4],[5]]
     * </pre>
     *
     * @param list 待分批列表
     * @param size 每批大小，必须大于 0
     * @param <T>  元素类型
     * @return 子列表集合；空列表返回空集合
     * @throws IllegalArgumentException size 小于等于 0 时抛出
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

    /**
     * 拼接集合元素。
     *
     * @param coll      待拼接集合
     * @param delimiter 分隔符
     * @return 拼接后的字符串；空集合返回空串
     */
    public static String join(Collection<?> coll, CharSequence delimiter) {
        if (coll == null || coll.isEmpty()) {
            return "";
        }
        return StringUtil.join(coll, delimiter);
    }

    /**
     * 构建树
     *
     * @param collection     元素列表
     * @param idGetter       id获取器
     * @param parentIdGetter parentId获取器
     * @param addChild       追加child
     * @param <T>            元素类型
     * @param <ID>           id类型
     * @return 根节点集合
     */
    public <T, ID> List<T> toTree(
            Collection<T> collection,
            Function<T, ID> idGetter,
            Function<T, ID> parentIdGetter,
            BiConsumer<T, T> addChild
    ) {
        Map<ID, T> map = new HashMap<>();
        for (T t : collection) {
            map.put(idGetter.apply(t), t);
        }

        List<T> roots = new ArrayList<>();
        for (T t : collection) {
            ID parentId = parentIdGetter.apply(t);
            if (parentId == null) {
                roots.add(t);
            } else {
                T parent = map.get(parentId);
                if (parent != null) {
                    addChild.accept(parent, t);
                }
            }
        }

        return roots;
    }

    /**
     * 构建树
     *
     * @param collection 元素列表
     * @param <T>        元素类型
     * @param <ID>       id类型
     * @return 根节点集合
     */
    public <T extends ITree<T, ID>, ID> List<T> toTree(
            Collection<T> collection
    ) {
        return toTree(
                collection,
                ITree::getId,
                ITree::getParentId,
                (parent, child) -> {
                    List<T> children = parent.getChildren();
                    if (children == null) {
                        children = new ArrayList<>();
                        parent.setChildren(children);
                    }

                    children.add(child);
                }
        );
    }
}

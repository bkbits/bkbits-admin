package com.bkbits.utils;

import java.util.Objects;

/**
 * 字符串工具类。
 */
public final class StringUtil {

    private StringUtil() {
    }

    /** 是否为空（null 或长度 0） */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /** 是否非空 */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /** 是否为空白（null、空串或全空白字符） */
    public static boolean isBlank(String str) {
        return str == null || str.isBlank();
    }

    /** 是否非空白 */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /** 去空白，null 返回空串 */
    public static String trimToEmpty(String str) {
        return str == null ? "" : str.trim();
    }

    /** 去空白，空白串返回 null */
    public static String trimToNull(String str) {
        if (str == null || str.isBlank()) {
            return null;
        }
        return str.trim();
    }

    /** 相等比较（null 安全） */
    public static boolean equals(String a, String b) {
        return Objects.equals(a, b);
    }

    /** 忽略大小写相等比较（null 安全） */
    public static boolean equalsIgnoreCase(String a, String b) {
        return a == null ? b == null : a.equalsIgnoreCase(b);
    }

    /** 是否包含子串 */
    public static boolean contains(String str, CharSequence seq) {
        return str != null && seq != null && str.contains(seq);
    }

    /** 是否以指定前缀开头 */
    public static boolean startsWith(String str, String prefix) {
        return str != null && prefix != null && str.startsWith(prefix);
    }

    /** 是否以指定后缀结尾 */
    public static boolean endsWith(String str, String suffix) {
        return str != null && suffix != null && str.endsWith(suffix);
    }

    /** 空白时返回默认值 */
    public static String defaultIfBlank(String str, String defaultStr) {
        return isBlank(str) ? defaultStr : str;
    }

    /** 拼接集合元素 */
    public static String join(Iterable<?> iterable, CharSequence delimiter) {
        if (iterable == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Object item : iterable) {
            if (!first) {
                sb.append(delimiter);
            }
            sb.append(item);
            first = false;
        }
        return sb.toString();
    }

    /** 首字母大写 */
    public static String capitalize(String str) {
        if (isBlank(str)) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    /** 驼峰转下划线：userName -> user_name */
    public static String camelToSnake(String str) {
        if (isBlank(str)) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                sb.append('_');
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }

    /** 下划线转驼峰：user_name -> userName */
    public static String snakeToCamel(String str) {
        if (isBlank(str)) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        boolean upperNext = false;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c == '_') {
                upperNext = true;
            } else if (upperNext) {
                sb.append(Character.toUpperCase(c));
                upperNext = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 脱敏：将指定区间字符替换为掩码字符。
     *
     * <pre>
     * mask("13812345678", 3, 7, '*') -> "138****5678"
     * </pre>
     */
    public static String mask(String str, int start, int end, char maskChar) {
        if (str == null || start < 0 || end <= start || end > str.length()) {
            return str;
        }
        char[] chars = str.toCharArray();
        for (int i = start; i < end; i++) {
            chars[i] = maskChar;
        }
        return new String(chars);
    }

    /** 安全截取 [start, end)，越界时自动修正 */
    public static String substring(String str, int start, int end) {
        if (str == null) {
            return null;
        }
        int from = Math.max(start, 0);
        int to = Math.min(end, str.length());
        if (from >= to) {
            return "";
        }
        return str.substring(from, to);
    }

    /** 字符串长度（null 返回 0） */
    public static int length(String str) {
        return str == null ? 0 : str.length();
    }
}

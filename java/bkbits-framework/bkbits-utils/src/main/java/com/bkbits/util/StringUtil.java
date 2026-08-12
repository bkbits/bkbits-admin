package com.bkbits.util;

import lombok.experimental.UtilityClass;

import java.util.Base64;
import java.util.Objects;

/**
 * 字符串工具类。
 */
@UtilityClass
public class StringUtil {

    /**
     * 是否为空（null 或长度 0）。
     *
     * @param str 待判断字符串
     * @return 为空返回 {@code true}
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * 是否非空（非 null 且长度大于 0）。
     *
     * @param str 待判断字符串
     * @return 非空返回 {@code true}
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 是否为空白（null、空串或全空白字符）。
     *
     * @param str 待判断字符串
     * @return 为空白返回 {@code true}
     */
    public static boolean isBlank(String str) {
        return str == null || str.isBlank();
    }

    /**
     * 是否非空白。
     *
     * @param str 待判断字符串
     * @return 非空白返回 {@code true}
     */
    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 去空白（null 返回 null）。
     *
     * @param str 待处理字符串
     * @return 去首尾空白后的字符串；入参为 null 时返回 null
     */
    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

    /**
     * 去空白（null 返回空串）。
     *
     * @param str 待处理字符串
     * @return 去首尾空白后的字符串；入参为 null 时返回空串
     */
    public static String trimToEmpty(String str) {
        return str == null ? "" : str.trim();
    }

    /**
     * 去空白（空白串返回 null）。
     *
     * @param str 待处理字符串
     * @return 去首尾空白后的字符串；null 或空白串返回 null
     */
    public static String trimToNull(String str) {
        if (str == null || str.isBlank()) {
            return null;
        }
        return str.trim();
    }

    /**
     * 相等比较（null 安全）。
     *
     * @param a 字符串 a
     * @param b 字符串 b
     * @return 相等返回 {@code true}；两者均为 null 时也返回 {@code true}
     */
    public static boolean equals(String a, String b) {
        return Objects.equals(a, b);
    }

    /**
     * 忽略大小写相等比较（null 安全）。
     *
     * @param a 字符串 a
     * @param b 字符串 b
     * @return 忽略大小写相等返回 {@code true}；两者均为 null 时也返回 {@code true}
     */
    public static boolean equalsIgnoreCase(String a, String b) {
        return a == null ? b == null : a.equalsIgnoreCase(b);
    }

    /**
     * 是否包含子串。
     *
     * @param str 原字符串
     * @param seq 子串
     * @return 包含返回 {@code true}；任一参数为 null 时返回 {@code false}
     */
    public static boolean contains(String str, CharSequence seq) {
        return str != null && seq != null && str.contains(seq);
    }

    /**
     * 是否以指定前缀开头。
     *
     * @param str    原字符串
     * @param prefix 前缀
     * @return 以该前缀开头返回 {@code true}；任一参数为 null 时返回 {@code false}
     */
    public static boolean startsWith(String str, String prefix) {
        return str != null && prefix != null && str.startsWith(prefix);
    }

    /**
     * 是否以指定后缀结尾。
     *
     * @param str    原字符串
     * @param suffix 后缀
     * @return 以该后缀结尾返回 {@code true}；任一参数为 null 时返回 {@code false}
     */
    public static boolean endsWith(String str, String suffix) {
        return str != null && suffix != null && str.endsWith(suffix);
    }

    /**
     * 空白时返回默认值。
     *
     * @param str        待判断字符串
     * @param defaultStr 默认值
     * @return 空白时返回 {@code defaultStr}，否则返回原字符串
     */
    public static String defaultIfBlank(String str, String defaultStr) {
        return isBlank(str) ? defaultStr : str;
    }

    /**
     * 拼接集合元素。
     *
     * @param iterable  待拼接集合
     * @param delimiter 分隔符
     * @return 拼接后的字符串；集合为 null 时返回空串
     */
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

    /**
     * 首字母大写。
     *
     * @param str 待处理字符串
     * @return 首字母大写后的字符串；空白字符串原样返回
     */
    public static String capitalize(String str) {
        if (isBlank(str)) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * 驼峰转下划线（userName -&gt; user_name）。
     *
     * @param str 驼峰字符串
     * @return 下划线分隔的小写字符串；空白字符串原样返回
     */
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

    /**
     * 下划线转驼峰（user_name -&gt; userName）。
     *
     * @param str 下划线字符串
     * @return 驼峰字符串；空白字符串原样返回
     */
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
     *
     * @param str      待脱敏字符串
     * @param start    掩码起始下标（含）
     * @param end      掩码结束下标（不含）
     * @param maskChar 掩码字符
     * @return 脱敏后的字符串；参数非法时原样返回
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

    /**
     * 安全截取 [start, end)，越界时自动修正。
     *
     * @param str   原字符串
     * @param start 起始下标（含）
     * @param end   结束下标（不含）
     * @return 截取后的字符串；入参为 null 时返回 null
     */
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

    /**
     * 字符串长度（null 返回 0）。
     *
     * @param str 待统计字符串
     * @return 字符个数；null 返回 0
     */
    public static int length(String str) {
        return str == null ? 0 : str.length();
    }

    /**
     * 字节数组转十六进制字符串。
     *
     * @param bytes 待转换字节数组
     * @return 十六进制字符串；null 返回 null
     */
    public static String toHex(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(Character.forDigit((b >> 4) & 0xF, 16));
            sb.append(Character.forDigit(b & 0xF, 16));
        }
        return sb.toString();
    }

    /**
     * 移除字符串开头的指定前缀（不以该前缀开头时返回原串）。
     *
     * @param str    原字符串
     * @param prefix 待移除前缀
     * @return 移除前缀后的字符串；参数为 null 或前缀不匹配时返回原串
     */
    public static String removeStartsWith(String str, String prefix) {
        if (str == null || prefix == null || !str.startsWith(prefix)) {
            return str;
        }
        return str.substring(prefix.length());
    }

    /**
     * 移除字符串结尾的指定后缀（不以该后缀结尾时返回原串）。
     *
     * @param str    原字符串
     * @param suffix 待移除后缀
     * @return 移除后缀后的字符串；参数为 null 或后缀不匹配时返回原串
     */
    public static String removeEndsWith(String str, String suffix) {
        if (str == null || suffix == null || !str.endsWith(suffix)) {
            return str;
        }
        return str.substring(0, str.length() - suffix.length());
    }

    /**
     * 移除字符串中的换行符（\r 与 \n）。
     *
     * @param str 原字符串
     * @return 移除换行符后的字符串；null 返回 null
     */
    public static String removeLineBreaks(String str) {
        return str == null ? null : str.replaceAll("[\\r\\n]", "");
    }

    /**
     * 字节数组转 Base64 字符串。
     *
     * @param bytes 待编码字节数组
     * @return Base64 字符串；null 返回 null
     */
    public static String base64Encode(byte[] bytes) {
        return bytes == null ? null : Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Base64 字符串转字节数组。
     *
     * @param str Base64 字符串
     * @return 解码后的字节数组；null 返回 null
     */
    public static byte[] base64Decode(String str) {
        return str == null ? null : Base64.getDecoder().decode(str);
    }

    /**
     * 将字节数组转换为带头尾标记的 PEM 块文本（每 64 字符换行）。
     *
     * @param bytes  内容字节
     * @param header 块头标记，如 {@code -----BEGIN PUBLIC KEY-----}
     * @param footer 块尾标记，如 {@code -----END PUBLIC KEY-----}
     * @return PEM 块文本；null 返回 null
     */
    public static String toPEM(byte[] bytes, String header, String footer) {
        if (bytes == null) {
            return null;
        }
        return header + "\n" + Base64.getMimeEncoder(64, "\n".getBytes())
                .encodeToString(bytes) + "\n" + footer;
    }

    /**
     * 解析 PEM 块文本为字节数组（去除头尾标记与换行符后 Base64 解码）。
     *
     * @param pem    PEM 块文本
     * @param header 块头标记
     * @param footer 块尾标记
     * @return 解码后的字节数组；null 返回 null
     */
    public static byte[] parsePEM(String pem, String header, String footer) {
        if (pem == null) {
            return null;
        }
        String body = removeLineBreaks(trim(pem));
        body = removeStartsWith(body, header);
        body = removeEndsWith(body, footer);
        body = removeLineBreaks(body);
        return base64Decode(body);
    }
}

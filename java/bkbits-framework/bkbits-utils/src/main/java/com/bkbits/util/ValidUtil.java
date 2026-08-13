package com.bkbits.util;

import lombok.experimental.UtilityClass;
import org.noear.solon.core.exception.StatusException;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 参数校验工具，校验失败抛出 {@link StatusException}，校验通过返回对应值。
 */
@UtilityClass
public class ValidUtil {
    private static final int STATUS_FAIL = 699;

    private static final Pattern PATTERN_EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PATTERN_PHONE = Pattern.compile("^1[3-9]\\d{9}$");

    private void throwException(String message, int code) {
        throw new StatusException(message, code);
    }

    /**
     * 校验字符串非空。
     *
     * @param str     校验字符串
     * @param message 校验失败时的异常消息
     * @return 原字符串
     */
    public String requireString(String str, String message) {
        return requireString(str, message, STATUS_FAIL);
    }

    /**
     * 校验字符串非空。
     *
     * @param str     校验字符串
     * @param message 校验失败时的异常消息
     * @param code    校验失败时的异常码
     * @return 原字符串
     */
    public String requireString(String str, String message, int code) {
        if (StringUtil.isBlank(str)) {
            throwException(message, code);
        }
        return str;
    }

    /**
     * 校验字符串长度在指定范围内（含边界）。
     *
     * @param str     校验字符串
     * @param min     最小长度
     * @param max     最大长度
     * @param message 校验失败时的异常消息
     * @return 原字符串
     */
    public String requireLength(String str, int min, int max, String message) {
        return requireLength(str, min, max, message, STATUS_FAIL);
    }

    /**
     * 校验字符串长度在指定范围内（含边界）。
     *
     * @param str     校验字符串
     * @param min     最小长度
     * @param max     最大长度
     * @param message 校验失败时的异常消息
     * @param code    校验失败时的异常码
     * @return 原字符串
     */
    public String requireLength(String str, int min, int max, String message, int code) {
        if (str == null || str.length() < min || str.length() > max) {
            throwException(message, code);
        }
        return str;
    }

    /**
     * 校验对象非空。
     *
     * @param obj     校验对象
     * @param message 校验失败时的异常消息
     * @param <T>     对象类型
     * @return 原对象
     */
    public <T> T requireNotNull(T obj, String message) {
        if (obj == null) {
            throwException(message, STATUS_FAIL);
        }
        return obj;
    }

    /**
     * 校验对象非空。
     *
     * @param obj     校验对象
     * @param message 校验失败时的异常消息
     * @param code    校验失败时的异常码
     * @param <T>     对象类型
     * @return 原对象
     */
    public <T> T requireNotNull(T obj, String message, int code) {
        if (obj == null) {
            throwException(message, code);
        }
        return obj;
    }

    /**
     * 校验集合非空。
     *
     * @param coll    校验集合
     * @param message 校验失败时的异常消息
     * @param <T>     集合类型
     * @return 原集合
     */
    public <T extends Collection<?>> T requireNotEmpty(T coll, String message) {
        if (CollectionUtil.isEmpty(coll)) {
            throwException(message, STATUS_FAIL);
        }
        return coll;
    }

    /**
     * 校验集合非空。
     *
     * @param coll    校验集合
     * @param message 校验失败时的异常消息
     * @param code    校验失败时的异常码
     * @param <T>     集合类型
     * @return 原集合
     */
    public <T extends Collection<?>> T requireNotEmpty(T coll, String message, int code) {
        if (CollectionUtil.isEmpty(coll)) {
            throwException(message, code);
        }
        return coll;
    }

    /**
     * 校验集合大小在指定范围内（含边界）。
     *
     * @param coll    校验集合
     * @param min     最小大小
     * @param max     最大大小
     * @param message 校验失败时的异常消息
     * @param <T>     集合类型
     * @return 原集合
     */
    public <T extends Collection<?>> T requireSize(T coll, int min, int max, String message) {
        if (coll == null || coll.size() < min || coll.size() > max) {
            throwException(message, STATUS_FAIL);
        }
        return coll;
    }

    /**
     * 校验集合大小在指定范围内（含边界）。
     *
     * @param coll    校验集合
     * @param min     最小大小
     * @param max     最大大小
     * @param message 校验失败时的异常消息
     * @param code    校验失败时的异常码
     * @param <T>     集合类型
     * @return 原集合
     */
    public <T extends Collection<?>> T requireSize(T coll, int min, int max, String message, int code) {
        if (coll == null || coll.size() < min || coll.size() > max) {
            throwException(message, code);
        }
        return coll;
    }

    /**
     * 校验集合非空。
     *
     * @param map     校验集合
     * @param message 校验失败时的异常消息
     * @param <T>     集合类型
     * @return 原集合
     */
    public <T extends Map<?, ?>> T requireNotEmpty(T map, String message) {
        if (CollectionUtil.isEmpty(map)) {
            throwException(message, STATUS_FAIL);
        }
        return map;
    }

    /**
     * 校验集合非空。
     *
     * @param map     校验集合
     * @param message 校验失败时的异常消息
     * @param code    校验失败时的异常码
     * @param <T>     集合类型
     * @return 原集合
     */
    public <T extends Map<?, ?>> T requireNotEmpty(T map, String message, int code) {
        if (CollectionUtil.isEmpty(map)) {
            throwException(message, code);
        }
        return map;
    }

    /**
     * 校验条件为真。
     *
     * @param condition 校验条件
     * @param message   校验失败时的异常消息
     * @return 原条件
     */
    public boolean requireTrue(boolean condition, String message) {
        return requireTrue(condition, message, STATUS_FAIL);
    }

    /**
     * 校验条件为真。
     *
     * @param condition 校验条件
     * @param message   校验失败时的异常消息
     * @param code      校验失败时的异常码
     * @return 原条件
     */
    public boolean requireTrue(boolean condition, String message, int code) {
        if (!condition) {
            throwException(message, code);
        }
        return condition;
    }

    /**
     * 校验条件为假。
     *
     * @param condition 校验条件
     * @param message   校验失败时的异常消息
     * @return 原条件
     */
    public boolean requireFalse(boolean condition, String message) {
        return requireFalse(condition, message, STATUS_FAIL);
    }

    /**
     * 校验条件为假。
     *
     * @param condition 校验条件
     * @param message   校验失败时的异常消息
     * @param code      校验失败时的异常码
     * @return 原条件
     */
    public boolean requireFalse(boolean condition, String message, int code) {
        if (condition) {
            throwException(message, code);
        }
        return condition;
    }

    /**
     * 校验两个对象相等。
     *
     * @param expected 期望值
     * @param actual   实际值
     * @param message  校验失败时的异常消息
     * @param <T>      实际值类型
     * @return 实际值
     */
    public <T> T requireEquals(Object expected, T actual, String message) {
        if (!Objects.equals(expected, actual)) {
            throwException(message, STATUS_FAIL);
        }
        return actual;
    }

    /**
     * 校验两个对象相等。
     *
     * @param expected 期望值
     * @param actual   实际值
     * @param message  校验失败时的异常消息
     * @param code     校验失败时的异常码
     * @param <T>      实际值类型
     * @return 实际值
     */
    public <T> T requireEquals(Object expected, T actual, String message, int code) {
        if (!Objects.equals(expected, actual)) {
            throwException(message, code);
        }
        return actual;
    }

    /**
     * 校验两个对象不相等。
     *
     * @param expected 期望值
     * @param actual   实际值
     * @param message  校验失败时的异常消息
     * @param <T>      实际值类型
     * @return 实际值
     */
    public <T> T requireNotEquals(Object expected, T actual, String message) {
        if (Objects.equals(expected, actual)) {
            throwException(message, STATUS_FAIL);
        }
        return actual;
    }

    /**
     * 校验两个对象不相等。
     *
     * @param expected 期望值
     * @param actual   实际值
     * @param message  校验失败时的异常消息
     * @param code     校验失败时的异常码
     * @param <T>      实际值类型
     * @return 实际值
     */
    public <T> T requireNotEquals(Object expected, T actual, String message, int code) {
        if (Objects.equals(expected, actual)) {
            throwException(message, code);
        }
        return actual;
    }

    /**
     * 校验长整型数值在指定范围内（含边界）。
     *
     * @param value   校验数值
     * @param min     最小值
     * @param max     最大值
     * @param message 校验失败时的异常消息
     * @return 原数值
     */
    public long requireInRange(long value, long min, long max, String message) {
        return requireInRange(value, min, max, message, STATUS_FAIL);
    }

    /**
     * 校验长整型数值在指定范围内（含边界）。
     *
     * @param value   校验数值
     * @param min     最小值
     * @param max     最大值
     * @param message 校验失败时的异常消息
     * @param code    校验失败时的异常码
     * @return 原数值
     */
    public long requireInRange(long value, long min, long max, String message, int code) {
        if (value < min || value > max) {
            throwException(message, code);
        }
        return value;
    }

    /**
     * 校验字符串匹配指定正则表达式。
     *
     * @param str     校验字符串
     * @param regex   正则表达式
     * @param message 校验失败时的异常消息
     * @return 原字符串
     */
    public String requireMatch(String str, String regex, String message) {
        return requireMatch(str, regex, message, STATUS_FAIL);
    }

    /**
     * 校验字符串匹配指定正则表达式。
     *
     * @param str     校验字符串
     * @param regex   正则表达式
     * @param message 校验失败时的异常消息
     * @param code    校验失败时的异常码
     * @return 原字符串
     */
    public String requireMatch(String str, String regex, String message, int code) {
        if (str == null || !str.matches(regex)) {
            throwException(message, code);
        }
        return str;
    }

    /**
     * 校验邮箱格式。
     *
     * @param email   校验邮箱
     * @param message 校验失败时的异常消息
     * @return 原邮箱
     */
    public String requireEmail(String email, String message) {
        return requireEmail(email, message, STATUS_FAIL);
    }

    /**
     * 校验邮箱格式。
     *
     * @param email   校验邮箱
     * @param message 校验失败时的异常消息
     * @param code    校验失败时的异常码
     * @return 原邮箱
     */
    public String requireEmail(String email, String message, int code) {
        if (email == null || !PATTERN_EMAIL.matcher(email).matches()) {
            throwException(message, code);
        }
        return email;
    }

    /**
     * 校验手机号格式（中国大陆）。
     *
     * @param phone   校验手机号
     * @param message 校验失败时的异常消息
     * @return 原手机号
     */
    public String requirePhone(String phone, String message) {
        return requirePhone(phone, message, STATUS_FAIL);
    }

    /**
     * 校验手机号格式（中国大陆）。
     *
     * @param phone   校验手机号
     * @param message 校验失败时的异常消息
     * @param code    校验失败时的异常码
     * @return 原手机号
     */
    public String requirePhone(String phone, String message, int code) {
        if (phone == null || !PATTERN_PHONE.matcher(phone).matches()) {
            throwException(message, code);
        }
        return phone;
    }
}

package com.bkbits.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 日期时间工具类（基于 java.time，线程安全）。
 */
public final class DateUtil {

    /** yyyy-MM-dd */
    public static final String PATTERN_DATE = "yyyy-MM-dd";
    /** yyyy-MM-dd HH:mm:ss */
    public static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";
    /** yyyy-MM-dd HH:mm:ss.SSS */
    public static final String PATTERN_DATETIME_MS = "yyyy-MM-dd HH:mm:ss.SSS";
    /** yyyyMMdd */
    public static final String PATTERN_COMPACT_DATE = "yyyyMMdd";
    /** yyyyMMddHHmmss */
    public static final String PATTERN_COMPACT_DATETIME = "yyyyMMddHHmmss";

    /** 格式化器缓存（pattern -> formatter） */
    private static final Map<String, DateTimeFormatter> FORMATTER_CACHE = new ConcurrentHashMap<>();

    private DateUtil() {
    }

    private static DateTimeFormatter formatter(String pattern) {
        return FORMATTER_CACHE.computeIfAbsent(pattern, DateTimeFormatter::ofPattern);
    }

    /** 格式化 LocalDateTime */
    public static String format(LocalDateTime dateTime, String pattern) {
        return dateTime == null ? null : dateTime.format(formatter(pattern));
    }

    /** 格式化 LocalDate */
    public static String format(LocalDate date, String pattern) {
        return date == null ? null : date.format(formatter(pattern));
    }

    /** 格式化 Date */
    public static String format(Date date, String pattern) {
        return date == null ? null : format(toLocalDateTime(date), pattern);
    }

    /** 当前时间字符串（yyyy-MM-dd HH:mm:ss） */
    public static String now() {
        return now(PATTERN_DATETIME);
    }

    /** 当前时间字符串（指定格式） */
    public static String now(String pattern) {
        return format(LocalDateTime.now(), pattern);
    }

    /** 解析为 LocalDateTime */
    public static LocalDateTime parseLocalDateTime(String str, String pattern) {
        return str == null ? null : LocalDateTime.parse(str, formatter(pattern));
    }

    /** 解析为 LocalDate */
    public static LocalDate parseLocalDate(String str, String pattern) {
        return str == null ? null : LocalDate.parse(str, formatter(pattern));
    }

    /** 解析为 Date */
    public static Date parse(String str, String pattern) {
        LocalDateTime dateTime = parseLocalDateTime(str, pattern);
        return dateTime == null ? null : toDate(dateTime);
    }

    /** LocalDateTime 转 Date */
    public static Date toDate(LocalDateTime dateTime) {
        return dateTime == null ? null : Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /** Date 转 LocalDateTime */
    public static LocalDateTime toLocalDateTime(Date date) {
        return date == null ? null : LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /** 时间戳（毫秒）转 LocalDateTime */
    public static LocalDateTime toLocalDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }

    /** LocalDateTime 转时间戳（毫秒） */
    public static long toTimestamp(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /** 加天数 */
    public static LocalDateTime plusDays(LocalDateTime dateTime, long days) {
        return dateTime == null ? null : dateTime.plusDays(days);
    }

    /** 减天数 */
    public static LocalDateTime minusDays(LocalDateTime dateTime, long days) {
        return dateTime == null ? null : dateTime.minusDays(days);
    }

    /** 两个时间相差天数（start 在前为正，绝对值） */
    public static long betweenDays(LocalDateTime start, LocalDateTime end) {
        return Math.abs(java.time.Duration.between(start, end).toDays());
    }

    /** 当天开始（00:00:00） */
    public static LocalDateTime startOfDay(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.toLocalDate().atStartOfDay();
    }

    /** 当天结束（23:59:59.999999999） */
    public static LocalDateTime endOfDay(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.toLocalDate().atTime(23, 59, 59, 999_999_999);
    }
}

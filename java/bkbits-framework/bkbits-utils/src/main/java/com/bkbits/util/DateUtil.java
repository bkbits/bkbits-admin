package com.bkbits.util;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 日期时间工具类（基于 java.time，线程安全）。
 */
@UtilityClass
public class DateUtil {

    /** yyyy-MM-dd */
    public static final String PATTERN_DATE = "yyyy-MM-dd";
    /** yyyy-MM-dd HH:mm:ss */
    public static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";
    /** HH:mm:ss */
    public static final String PATTERN_TIME = "HH:mm:ss";

    /** 预编译格式化器：yyyy-MM-dd */
    private static final DateTimeFormatter FORMATTER_DATE = DateTimeFormatter.ofPattern(PATTERN_DATE);
    /** 预编译格式化器：yyyy-MM-dd HH:mm:ss */
    private static final DateTimeFormatter FORMATTER_DATETIME = DateTimeFormatter.ofPattern(PATTERN_DATETIME);
    /** 预编译格式化器：HH:mm:ss */
    private static final DateTimeFormatter FORMATTER_TIME = DateTimeFormatter.ofPattern(PATTERN_TIME);

    /** 格式化器缓存最大容量 */
    private static final int MAX_CACHE_SIZE = 64;

    /** 格式化器缓存（pattern -&gt; formatter，最多 64 条） */
    private static final Map<String, DateTimeFormatter> FORMATTER_CACHE = new ConcurrentHashMap<>();

    /**
     * 获取缓存中的格式化器；缓存已满且为新模式时直接新建（不入缓存）。
     *
     * @param pattern 日期格式模式
     * @return 对应的 {@link DateTimeFormatter}
     */
    private static DateTimeFormatter formatter(String pattern) {
        DateTimeFormatter cached = FORMATTER_CACHE.get(pattern);
        if (cached != null) {
            return cached;
        }
        DateTimeFormatter created = DateTimeFormatter.ofPattern(pattern);
        if (FORMATTER_CACHE.size() < MAX_CACHE_SIZE) {
            DateTimeFormatter existing = FORMATTER_CACHE.putIfAbsent(pattern, created);
            return existing != null ? existing : created;
        }
        return created;
    }

    /**
     * 格式化 LocalDate 为日期字符串（yyyy-MM-dd）。
     *
     * @param date 待格式化日期；null 返回 null
     * @return 日期字符串；入参为 null 时返回 null
     */
    public static String formatDate(LocalDate date) {
        return date == null ? null : date.format(FORMATTER_DATE);
    }

    /**
     * 格式化 LocalDateTime 为日期字符串（yyyy-MM-dd）。
     *
     * @param dateTime 待格式化时间；null 返回 null
     * @return 日期字符串；入参为 null 时返回 null
     */
    public static String formatDate(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.format(FORMATTER_DATE);
    }

    /**
     * 格式化 Date 为日期字符串（yyyy-MM-dd，按系统默认时区转换）。
     *
     * @param date 待格式化日期；null 返回 null
     * @return 日期字符串；入参为 null 时返回 null
     */
    public static String formatDate(Date date) {
        return date == null ? null : formatDate(toLocalDateTime(date));
    }

    /**
     * 解析日期字符串（yyyy-MM-dd）为 LocalDate。
     *
     * @param str 待解析字符串；null 返回 null
     * @return 解析后的 LocalDate；入参为 null 时返回 null
     */
    public static LocalDate parseDate(String str) {
        return str == null ? null : LocalDate.parse(str, FORMATTER_DATE);
    }

    /**
     * 格式化 LocalDateTime 为日期时间字符串（yyyy-MM-dd HH:mm:ss）。
     *
     * @param dateTime 待格式化时间；null 返回 null
     * @return 日期时间字符串；入参为 null 时返回 null
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.format(FORMATTER_DATETIME);
    }

    /**
     * 格式化 Date 为日期时间字符串（yyyy-MM-dd HH:mm:ss，按系统默认时区转换）。
     *
     * @param date 待格式化日期；null 返回 null
     * @return 日期时间字符串；入参为 null 时返回 null
     */
    public static String formatDateTime(Date date) {
        return date == null ? null : formatDateTime(toLocalDateTime(date));
    }

    /**
     * 解析日期时间字符串（yyyy-MM-dd HH:mm:ss）为 LocalDateTime。
     *
     * @param str 待解析字符串；null 返回 null
     * @return 解析后的 LocalDateTime；入参为 null 时返回 null
     */
    public static LocalDateTime parseDateTime(String str) {
        return str == null ? null : LocalDateTime.parse(str, FORMATTER_DATETIME);
    }

    /**
     * 格式化 LocalTime 为时间字符串（HH:mm:ss）。
     *
     * @param time 待格式化时间；null 返回 null
     * @return 时间字符串；入参为 null 时返回 null
     */
    public static String formatTime(LocalTime time) {
        return time == null ? null : time.format(FORMATTER_TIME);
    }

    /**
     * 解析时间字符串（HH:mm:ss）为 LocalTime。
     *
     * @param str 待解析字符串；null 返回 null
     * @return 解析后的 LocalTime；入参为 null 时返回 null
     */
    public static LocalTime parseTime(String str) {
        return str == null ? null : LocalTime.parse(str, FORMATTER_TIME);
    }

    /**
     * 格式化 LocalDateTime 为字符串。
     *
     * @param dateTime 待格式化时间；null 返回 null
     * @param pattern  日期格式模式
     * @return 格式化后的字符串；入参为 null 时返回 null
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        return dateTime == null ? null : dateTime.format(formatter(pattern));
    }

    /**
     * 格式化 LocalDate 为字符串。
     *
     * @param date    待格式化日期；null 返回 null
     * @param pattern 日期格式模式
     * @return 格式化后的字符串；入参为 null 时返回 null
     */
    public static String format(LocalDate date, String pattern) {
        return date == null ? null : date.format(formatter(pattern));
    }

    /**
     * 格式化 Date 为字符串（按系统默认时区转换）。
     *
     * @param date    待格式化日期；null 返回 null
     * @param pattern 日期格式模式
     * @return 格式化后的字符串；入参为 null 时返回 null
     */
    public static String format(Date date, String pattern) {
        return date == null ? null : format(toLocalDateTime(date), pattern);
    }

    /**
     * 当前时间字符串（yyyy-MM-dd HH:mm:ss）。
     *
     * @return 当前时间格式化字符串
     */
    public static String now() {
        return formatDateTime(LocalDateTime.now());
    }

    /**
     * 当前时间字符串（指定格式）。
     *
     * @param pattern 日期格式模式
     * @return 当前时间按指定格式格式化后的字符串
     */
    public static String now(String pattern) {
        return format(LocalDateTime.now(), pattern);
    }

    /**
     * 解析字符串为 LocalDateTime。
     *
     * @param str     待解析字符串；null 返回 null
     * @param pattern 日期格式模式
     * @return 解析后的 LocalDateTime；入参为 null 时返回 null
     */
    public static LocalDateTime parseLocalDateTime(String str, String pattern) {
        return str == null ? null : LocalDateTime.parse(str, formatter(pattern));
    }

    /**
     * 解析字符串为 LocalDate。
     *
     * @param str     待解析字符串；null 返回 null
     * @param pattern 日期格式模式
     * @return 解析后的 LocalDate；入参为 null 时返回 null
     */
    public static LocalDate parseLocalDate(String str, String pattern) {
        return str == null ? null : LocalDate.parse(str, formatter(pattern));
    }

    /**
     * 解析字符串为 Date。
     *
     * @param str     待解析字符串；null 返回 null
     * @param pattern 日期格式模式
     * @return 解析后的 Date；入参为 null 时返回 null
     */
    public static Date parse(String str, String pattern) {
        LocalDateTime dateTime = parseLocalDateTime(str, pattern);
        return dateTime == null ? null : toDate(dateTime);
    }

    /**
     * LocalDateTime 转 Date（按系统默认时区）。
     *
     * @param dateTime 待转换时间；null 返回 null
     * @return 转换后的 Date；入参为 null 时返回 null
     */
    public static Date toDate(LocalDateTime dateTime) {
        return dateTime == null ? null : Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Date 转 LocalDateTime（按系统默认时区）。
     *
     * @param date 待转换日期；null 返回 null
     * @return 转换后的 LocalDateTime；入参为 null 时返回 null
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        return date == null ? null : LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * 时间戳（毫秒）转 LocalDateTime（按系统默认时区）。
     *
     * @param timestamp 毫秒时间戳
     * @return 转换后的 LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }

    /**
     * LocalDateTime 转时间戳（毫秒，按系统默认时区）。
     *
     * @param dateTime 待转换时间
     * @return 毫秒时间戳
     */
    public static long toTimestamp(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 加天数。
     *
     * @param dateTime 基准时间；null 返回 null
     * @param days     增加的天数（可为负）
     * @return 增加后的 LocalDateTime；入参为 null 时返回 null
     */
    public static LocalDateTime plusDays(LocalDateTime dateTime, long days) {
        return dateTime == null ? null : dateTime.plusDays(days);
    }

    /**
     * 减天数。
     *
     * @param dateTime 基准时间；null 返回 null
     * @param days     减少的天数（可为负）
     * @return 减少后的 LocalDateTime；入参为 null 时返回 null
     */
    public static LocalDateTime minusDays(LocalDateTime dateTime, long days) {
        return dateTime == null ? null : dateTime.minusDays(days);
    }

    /**
     * 两个时间相差天数（取绝对值）。
     *
     * @param start 起始时间
     * @param end   结束时间
     * @return 相差天数（非负）
     */
    public static long betweenDays(LocalDateTime start, LocalDateTime end) {
        return Math.abs(java.time.Duration.between(start, end).toDays());
    }

    /**
     * 当天开始（00:00:00）。
     *
     * @param dateTime 待计算时间；null 返回 null
     * @return 当天 00:00:00；入参为 null 时返回 null
     */
    public static LocalDateTime startOfDay(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.toLocalDate().atStartOfDay();
    }

    /**
     * 当天结束（23:59:59.999999999）。
     *
     * @param dateTime 待计算时间；null 返回 null
     * @return 当天 23:59:59.999999999；入参为 null 时返回 null
     */
    public static LocalDateTime endOfDay(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.toLocalDate().atTime(23, 59, 59, 999_999_999);
    }
}

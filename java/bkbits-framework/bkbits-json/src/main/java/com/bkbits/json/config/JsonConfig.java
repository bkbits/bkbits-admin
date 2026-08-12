package com.bkbits.json.config;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.serialization.jackson.JacksonStringSerializer;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.function.Function;

/**
 * JSON 配置。
 */
@Configuration
public class JsonConfig {

    /** 统一日期时间格式 */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** 统一日期格式 */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /** 统一时间格式 */
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    /** JS 安全整数上界（超出则 Long 转字符串输出，避免前端精度丢失） */
    private static final long JS_SAFE_INTEGER_MAX = 9007199254740991L;

    /** JS 安全整数下界 */
    private static final long JS_SAFE_INTEGER_MIN = -9007199254740991L;

    /**
     * jackson 序列化配置：高级格式化定制（基于 {@link JacksonStringSerializer} 接口）。
     */
    @Bean
    public void jacksonConfig(JacksonStringSerializer serializer) {
        //::序列化（渲染输出）
        serializer.addEncoder(Date.class,
                s -> s.toInstant().atZone(ZoneId.systemDefault()).format(DATE_TIME_FORMATTER));
        serializer.addEncoder(LocalDateTime.class, s -> s.format(DATE_TIME_FORMATTER));
        serializer.addEncoder(LocalDate.class, s -> s.format(DATE_FORMATTER));
        serializer.addEncoder(LocalTime.class, s -> s.format(TIME_FORMATTER));

        // 超出 JS 安全整数范围的 Long 转字符串，范围内保持数字类型
        serializer.addEncoder(Long.class, s -> {
            if (s > JS_SAFE_INTEGER_MAX || s < JS_SAFE_INTEGER_MIN) {
                return String.valueOf(s);
            }
            return s;
        });

        // BigDecimal 转字符串，避免科学计数法与精度丢失
        serializer.addEncoder(BigDecimal.class, BigDecimal::toPlainString);

        //::反序列化（接收参数）：与序列化定制对应
        // 时间类型（Date/LocalDate/LocalTime/LocalDateTime）已由框架 JacksonEntityConverter
        // 注册 TimeDeserializer 支持字符串解析，无需重复定制
        // Long：字符串转回 Long（对应超范围 Long 转字符串输出）
        serializer.getDeserializeConfig().getCustomModule().addDeserializer(Long.class,
                stringValueDeserializer(Long::parseLong));
        // BigDecimal：字符串转回 BigDecimal（对应 toPlainString 输出）
        serializer.getDeserializeConfig().getCustomModule().addDeserializer(BigDecimal.class,
                stringValueDeserializer(BigDecimal::new));
    }

    /**
     * 构建字符串值反序列化器：任意节点取值后按 {@code fromString} 解析（兼容字符串与数值节点）。
     */
    private static <T> JsonDeserializer<T> stringValueDeserializer(Function<String, T> fromString) {
        return new JsonDeserializer<T>() {
            @Override
            public T deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                    throws IOException, JacksonException {
                String val = jsonParser.getValueAsString();
                try {
                    return fromString.apply(val);
                } catch (RuntimeException ex) {
                    throw JsonMappingException.wrapWithPath(ex, jsonParser, "parse fail: '" + val + "'");
                }
            }
        };
    }
}

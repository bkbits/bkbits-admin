package com.bkbits.orm.convertor;

import com.easy.query.core.basic.extension.conversion.ValueConverter;
import com.easy.query.core.metadata.ColumnMetadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * {@code List<String>} 与逗号分隔字符串互转。
 *
 * <p>用法：{@code @Column(conversion = StringListConvertor.class)}。</p>
 *
 * <p>shortcut: 元素自身含逗号时无法还原，需要存任意文本时改用 {@link JsonConvertor}。</p>
 */
public class StringListConvertor implements ValueConverter<List<String>, String> {

    @Override
    public String serialize(List<String> value, ColumnMetadata columnMetadata) {
        return value == null ? null : String.join(",", value);
    }

    @Override
    public List<String> deserialize(String value, ColumnMetadata columnMetadata) {
        if (value == null) {
            return null;
        }
        return value.isEmpty()
                ? new ArrayList<>()
                : new ArrayList<>(Arrays.asList(value.split(",", -1)));
    }
}

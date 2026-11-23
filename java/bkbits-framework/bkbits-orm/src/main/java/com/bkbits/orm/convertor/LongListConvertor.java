package com.bkbits.orm.convertor;

import com.easy.query.core.basic.extension.conversion.ValueConverter;
import com.easy.query.core.metadata.ColumnMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code List<Long>} 与逗号分隔字符串互转。
 *
 * <p>用法：{@code @Column(conversion = LongListConvertor.class)}。</p>
 *
 * <p>shortcut: 元素自身含逗号时无法还原，需要存任意文本时改用 {@link JsonConvertor}。</p>
 */
public class LongListConvertor implements ValueConverter<List<Long>, String> {

    @Override
    public String serialize(List<Long> value, ColumnMetadata columnMetadata) {
        return value == null ? null : String.join(",", value.stream().map(String::valueOf).toList());
    }

    @Override
    public List<Long> deserialize(String value, ColumnMetadata columnMetadata) {
        if (value == null) {
            return null;
        }
        List<Long> list = new ArrayList<>();
        if (value.isBlank()) {
            return list;
        }
        for (String item : value.split(",")) {
            list.add(Long.valueOf(item.trim()));
        }
        return list;
    }
}

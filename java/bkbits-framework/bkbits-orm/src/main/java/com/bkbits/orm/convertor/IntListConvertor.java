package com.bkbits.orm.convertor;

import com.easy.query.core.basic.extension.conversion.ValueConverter;
import com.easy.query.core.metadata.ColumnMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code List<Integer>} 与逗号分隔字符串互转。
 *
 * <p>用法：{@code @Column(conversion = IntListConvertor.class)}。</p>
 *
 * <p>shortcut: 元素自身含逗号时无法还原，需要存任意文本时改用 {@link JsonConvertor}。</p>
 */
public class IntListConvertor implements ValueConverter<List<Integer>, String> {

    @Override
    public String serialize(List<Integer> value, ColumnMetadata columnMetadata) {
        return value == null ? null : String.join(",", value.stream().map(String::valueOf).toList());
    }

    @Override
    public List<Integer> deserialize(String value, ColumnMetadata columnMetadata) {
        if (value == null) {
            return null;
        }
        List<Integer> list = new ArrayList<>();
        if (value.isBlank()) {
            return list;
        }
        for (String item : value.split(",")) {
            list.add(Integer.valueOf(item.trim()));
        }
        return list;
    }
}

package com.bkbits.orm.convertor;

import com.easy.query.core.basic.extension.conversion.ValueConverter;
import com.easy.query.core.metadata.ColumnMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code List<Double>} 与逗号分隔字符串互转。
 *
 * <p>用法：{@code @Column(conversion = DoubleListConvertor.class)}。</p>
 *
 * <p>shortcut: 元素自身含逗号时无法还原，需要存任意文本时改用 {@link JsonConvertor}。</p>
 */
public class DoubleListConvertor implements ValueConverter<List<Double>, String> {

    @Override
    public String serialize(List<Double> value, ColumnMetadata columnMetadata) {
        return value == null ? null : String.join(",", value.stream().map(String::valueOf).toList());
    }

    @Override
    public List<Double> deserialize(String value, ColumnMetadata columnMetadata) {
        if (value == null) {
            return null;
        }
        List<Double> list = new ArrayList<>();
        if (value.isBlank()) {
            return list;
        }
        for (String item : value.split(",")) {
            list.add(Double.valueOf(item.trim()));
        }
        return list;
    }
}

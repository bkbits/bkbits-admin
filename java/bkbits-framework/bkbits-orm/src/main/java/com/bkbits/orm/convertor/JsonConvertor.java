package com.bkbits.orm.convertor;

import com.bkbits.util.JsonUtil;
import com.bkbits.util.StringUtil;
import com.easy.query.core.basic.extension.conversion.ValueConverter;
import com.easy.query.core.metadata.ColumnMetadata;

/**
 * 任意对象与 JSON 字符串互转，反序列化按 {@code complexPropType} 还原。
 *
 * <p>
 * 用法：{@code @Column(conversion = JsonConvertor.class, complexPropType = User.class)}。
 * </p>
 *
 * <p>
 * 使用 Solon 容器中的 JSON 序列化器（见 {@link JsonUtil}），与接口输出保持一致；
 * 脱离容器运行（如纯单元测试）时不可用。
 * </p>
 */
public class JsonConvertor implements ValueConverter<Object, String> {

    @Override
    public String serialize(Object value, ColumnMetadata columnMetadata) {
        return value == null ? null : JsonUtil.stringify(value);
    }

    @Override
    public Object deserialize(String value, ColumnMetadata columnMetadata) {
        return StringUtil.isEmpty(value) ? null
                : JsonUtil.parse(value, columnMetadata.getComplexPropType().complexType());
    }
}

package com.bkbits.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.noear.solon.Solon;
import org.noear.solon.core.serialize.Serializer;
import org.noear.solon.core.util.TypeReference;
import org.noear.solon.serialization.SerializerNames;

import java.lang.reflect.Type;

/**
 * JSON 工具类，基于 solon 容器中的 JSON 序列化器（jackson3）实现。
 *
 * <p>序列化器取自 {@link Solon#app()} 中注册的 JSON 序列化器，与框架配置保持一致
 * （日期格式、特性等均继承 {@code bkbits-json} 模块的 {@code JsonConfig} 配置）。</p>
 *
 * <p>提供两套方法：</p>
 * <ul>
 *     <li>{@code parse}/{@code stringify}：普通序列化，反序列化时需显式指定目标类型；</li>
 *     <li>{@code parseTyped}/{@code stringifyTyped}：序列化时附带类信息（{@code @type}），
 *         反序列化时按类信息还原为原始具体类型，适用于跨系统传输、缓存等场景。</li>
 * </ul>
 *
 * <pre>
 * String json = JsonUtil.stringify(user);
 * User user = JsonUtil.parse(json, User.class);
 *
 * String typedJson = JsonUtil.stringifyTyped(user);
 * User copy = JsonUtil.parseTyped(typedJson);
 * </pre>
 */
@SuppressWarnings("unchecked")
@UtilityClass
public class JsonUtil {

    /**
     * 容器中的 JSON 序列化器
     */
    private static final Serializer<String> serializer = Solon.app().serializers().get(SerializerNames.AT_JSON);

    /**
     * 容器中的 JSON typed 序列化器（输出附带 {@code @type} 类信息）
     */
    private static final Serializer<String> serializerTyped = Solon.app().serializers().get(SerializerNames.AT_JSON_TYPED);

    /**
     * 反序列化附带类信息的 JSON 字符串，按 {@code @type} 还原为原始具体类型。
     *
     * @param json JSON 字符串
     * @param <T>  目标类型（由调用方按 {@code @type} 对应类型指定）
     * @return 还原后的对象；若 JSON 中无 {@code @type}，则返回 {@code Map} 等通用结构
     */
    @SneakyThrows
    public static <T> T parseTyped(String json) {
        return (T) serializerTyped.deserialize(json, Object.class);
    }

    /**
     * 序列化对象为 JSON 字符串，并附带类信息（{@code @type} 字段）。
     *
     * @param obj 待序列化对象
     * @return 附带类信息的 JSON 字符串
     */
    @SneakyThrows
    public static String stringifyTyped(Object obj) {
        return serializerTyped.serialize(obj);
    }

    /**
     * 反序列化 JSON 字符串为指定类型对象。
     *
     * @param json  JSON 字符串
     * @param clazz 目标类型
     * @param <T>   目标类型
     * @return 目标类型对象
     */
    @SneakyThrows
    public static <T> T parse(String json, Class<T> clazz) {
        return (T) serializer.deserialize(json, clazz);
    }

    /**
     * 反序列化 JSON 字符串为指定类型对象（支持泛型类型，如 {@code List<User>}）。
     *
     * @param json JSON 字符串
     * @param type 目标类型（如 {@code new TypeToken<List<User>>(){}.getType()}）
     * @param <T>  目标类型
     * @return 目标类型对象
     */
    @SneakyThrows
    public static <T> T parse(String json, Type type) {
        return (T) serializer.deserialize(json, type);
    }

    /**
     * 反序列化 JSON 字符串为指定类型对象（基于 {@link TypeReference}，支持泛型）。
     *
     * @param json    JSON 字符串
     * @param typeRef 类型引用，如 {@code new TypeReference<List<User>>() {}}
     * @param <T>     目标类型
     * @return 目标类型对象
     */
    @SneakyThrows
    public static <T> T parse(String json, TypeReference<T> typeRef) {
        return (T) serializer.deserialize(json, typeRef.getType());
    }

    /**
     * 序列化对象为 JSON 字符串。
     *
     * @param obj 待序列化对象
     * @return JSON 字符串
     */
    @SneakyThrows
    public static String stringify(Object obj) {
        return serializer.serialize(obj);
    }
}

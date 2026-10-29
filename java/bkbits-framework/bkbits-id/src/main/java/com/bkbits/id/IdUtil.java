package com.bkbits.id;

import lombok.experimental.UtilityClass;

/**
 * ID 生成工具类
 *
 * <p>提供统一的主键生成入口。默认生成器取自 Solon 容器（{@link #nextId()}），
 * 同时内置雪花、Pear、UUID 三种生成器的独立快捷方法。</p>
 */
@UtilityClass
public class IdUtil {

    /**
     * 容器装配的默认 ID 生成器
     */
    private static IdGenerator defaultGenerator = IdGenerator.pearId(0, 0);

    /**
     * 初始化默认 ID 生成器。
     *
     * <p>用于替换内置的默认生成器，应在应用启动阶段调用一次；
     * 传入 {@code null} 时忽略本次调用，保留原有生成器。</p>
     *
     * @param generator 新的默认 ID 生成器，可为 {@code null}
     */
    public static void init(IdGenerator generator) {
        if (generator != null) {
            defaultGenerator = generator;
        }
    }


    /**
     * 获取默认 ID
     *
     * @return 默认生成器生成的 ID
     */
    public static String nextId() {
        return defaultGenerator.nextId();
    }
}

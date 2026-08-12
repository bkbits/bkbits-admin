package com.bkbits.util;

import com.bkbits.generator.IdGenerator;
import com.bkbits.generator.PearIdGenerator;
import com.bkbits.generator.SnowflakeIdGenerator;
import com.bkbits.generator.UUIDGenerator;
import lombok.experimental.UtilityClass;
import org.noear.solon.Solon;

/**
 * ID 生成工具类
 *
 * <p>提供统一的主键生成入口。默认生成器取自 Solon 容器（{@link #nextId()}），
 * 同时内置雪花、Pear、UUID 三种生成器的独立快捷方法。</p>
 */
@UtilityClass
public class IdUtil {

    /** 容器装配的默认 ID 生成器 */
    private static final IdGenerator idGenerator = Solon.context().getBean(IdGenerator.class);

    /** 雪花算法生成器 */
    private static final IdGenerator snowflake = new SnowflakeIdGenerator(0, 0);

    /** Pear 算法生成器 */
    private static final IdGenerator pearId = new PearIdGenerator(0, 0);

    /** UUID 生成器 */
    private static final IdGenerator uuid = new UUIDGenerator();

    /**
     * 获取默认 ID
     *
     * @return 默认生成器生成的 ID
     */
    String nextId() {
        return idGenerator.nextId();
    }

    /**
     * 获取雪花算法 ID
     *
     * @return 雪花算法生成的 ID
     */
    String snowflakeId() {
        return snowflake.nextId();
    }

    /**
     * 获取 Pear 算法 ID
     *
     * @return Pear 算法生成的 ID
     */
    String pearId() {
        return pearId.nextId();
    }

    /**
     * 获取 UUID
     *
     * @return UUID 生成的 ID
     */
    String uuid() {
        return uuid.nextId();
    }
}

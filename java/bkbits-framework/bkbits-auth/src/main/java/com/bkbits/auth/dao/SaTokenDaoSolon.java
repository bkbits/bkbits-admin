package com.bkbits.auth.dao;

import cn.dev33.satoken.dao.SaTokenDao;
import cn.dev33.satoken.session.SaSession;
import lombok.SneakyThrows;
import org.noear.redisx.RedisClient;
import org.noear.redisx.RedisSession;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.serialize.Serializer;
import org.noear.solon.serialization.SerializerNames;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 基于 Solon 生态（RedisClient + 序列化器）实现的 Sa-Token 持久层。
 *
 * <p>数据存储于 Redis：字符串与对象均经序列化器转为 JSON 字符串后写入；
 * 对象序列化使用 typed 模式（附带 {@code @type} 类信息），可还原为原始具体类型。</p>
 *
 * <p>当容器中存在 {@link RedisClient} Bean 时本实现自动注册（插件
 * {@code SaBeanInject} 会将 {@link SaTokenDao} Bean 装配到 {@code SaManager}）；
 * 未配置 Redis 时本实现不注册，Sa-Token 回退默认内存实现。</p>
 */
@Component
@Condition(onBean = RedisClient.class)
public class SaTokenDaoSolon implements SaTokenDao {

    /**
     * Redis 客户端
     */
    private final RedisClient redisClient;

    /**
     * typed JSON 序列化器（序列化时附带 @type 类信息）
     */
    private final Serializer<String> serializer;

    public SaTokenDaoSolon(@Inject RedisClient redisClient) {
        this.redisClient = redisClient;
        this.serializer = Solon.app().serializers().get(SerializerNames.AT_JSON_TYPED);
    }

    // --------------------- 字符串读写 ---------------------

    @Override
    public String get(String key) {
        return redisClient.openAndGet(rs -> rs.key(key).get());
    }

    @Override
    public void set(String key, String value, long timeout) {
        if (timeout == 0 || timeout <= -2) {
            // 值等于0或小于等于-2时不存储
            return;
        }
        redisClient.open(rs -> {
            rs.key(key).set(value);
            applyTimeout(rs, key, timeout);
        });
    }

    @Override
    public void update(String key, String value) {
        // 纯 SET 命令不带过期参数，Redis 会保留原 key 的存活时间
        redisClient.open(rs -> rs.key(key).set(value));
    }

    @Override
    public void delete(String key) {
        redisClient.open(rs -> rs.key(key).delete());
    }

    @Override
    public long getTimeout(String key) {
        // redis ttl：-1 永不过期（NEVER_EXPIRE），-2 不存在（NOT_VALUE_EXPIRE）
        return redisClient.openAndGet(rs -> rs.key(key).ttl());
    }

    @Override
    public void updateTimeout(String key, long timeout) {
        redisClient.open(rs -> {
            rs.key(key);
            applyTimeout(rs, key, timeout);
        });
    }

    // --------------------- 对象读写 ---------------------

    @SneakyThrows
    @Override
    public Object getObject(String key) {
        String value = get(key);
        return value == null ? null : serializer.deserialize(value, Object.class);
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows
    @Override
    public <T> T getObject(String key, Class<T> classType) {
        String value = get(key);
        return value == null ? null : (T) serializer.deserialize(value, classType);
    }

    @SneakyThrows
    @Override
    public void setObject(String key, Object object, long timeout) {
        if (object == null) {
            return;
        }
        set(key, serializer.serialize(object), timeout);
    }

    @SneakyThrows
    @Override
    public void updateObject(String key, Object object) {
        if (object == null) {
            return;
        }
        update(key, serializer.serialize(object));
    }

    @Override
    public void deleteObject(String key) {
        delete(key);
    }

    @Override
    public long getObjectTimeout(String key) {
        return getTimeout(key);
    }

    @Override
    public void updateObjectTimeout(String key, long timeout) {
        updateTimeout(key, timeout);
    }

    // --------------------- SaSession 读写 ---------------------

    @Override
    public SaSession getSession(String sessionId) {
        return getObject(sessionId, SaSession.class);
    }

    @Override
    public void setSession(SaSession session, long timeout) {
        setObject(session.getId(), session, timeout);
    }

    @Override
    public void updateSession(SaSession session) {
        updateObject(session.getId(), session);
    }

    @Override
    public void deleteSession(String sessionId) {
        deleteObject(sessionId);
    }

    @Override
    public long getSessionTimeout(String sessionId) {
        return getObjectTimeout(sessionId);
    }

    @Override
    public void updateSessionTimeout(String sessionId, long timeout) {
        updateObjectTimeout(sessionId, timeout);
    }

    // --------------------- 会话管理 ---------------------

    @Override
    public List<String> searchData(String prefix, String keyword, int start, int size, boolean sortType) {
        Set<String> keys = redisClient.openAndGet(rs -> rs.keys(prefix + "*"));
        if (keys == null || keys.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> list = new ArrayList<>();
        for (String key : keys) {
            if (keyword == null || keyword.isEmpty() || key.contains(keyword)) {
                list.add(key);
            }
        }

        if (sortType) {
            Collections.sort(list);
        } else {
            list.sort(Collections.reverseOrder());
        }

        int from = Math.min(start, list.size());
        int to = size == -1 ? list.size() : Math.min(from + size, list.size());
        return new ArrayList<>(list.subList(from, to));
    }

    // --------------------- 内部工具 ---------------------

    /**
     * 为当前已选中的 key 设置存活时间。
     *
     * @param rs      redis 会话
     * @param key     key（占位参数，保持调用语义）
     * @param timeout 存活时间（大于0限时，等于-1永久，其它忽略）
     */
    private void applyTimeout(RedisSession rs, String key, long timeout) {
        if (timeout > 0) {
            rs.expire((int) Math.min(timeout, Integer.MAX_VALUE));
        } else if (timeout == NEVER_EXPIRE) {
            rs.persist();
        }
    }
}

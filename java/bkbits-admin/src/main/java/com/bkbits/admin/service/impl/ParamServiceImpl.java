package com.bkbits.admin.service.impl;

import com.bkbits.admin.service.ParamService;
import com.bkbits.dbo.entity.Param;
import com.bkbits.util.StringUtil;
import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.easy.query.core.enums.SQLExecuteStrategyEnum;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.annotation.Cache;
import org.noear.solon.data.annotation.CacheRemove;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Component
public class ParamServiceImpl implements ParamService {

    private static final String CACHE_TAG = "admin:param";

    @Inject
    EasyEntityQuery easyEntityQuery;

    @Override
    @CacheRemove(tags = CACHE_TAG)
    public Param add(Param param) {
        Objects.requireNonNull(param, "系统参数不能为空");
        if (easyEntityQuery.insertable(param).executeRows() != 1) {
            throw new IllegalStateException("创建系统参数失败");
        }
        return param;
    }

    @Override
    @Cache(key = "admin:param:${key}", tags = CACHE_TAG, seconds = 3600)
    public Param getByKey(String key) {
        return easyEntityQuery.queryable(Param.class)
                .where(o -> o.paramKey().eq(requireText(key, "参数键")))
                .singleOrNull();
    }

    @Cache(key = "admin:param:${key}", tags = CACHE_TAG, seconds = 3600)
    private String getStringOrNull(String key) {
        return easyEntityQuery.queryable(Param.class)
                .where(p -> p.paramKey().eq(key))
                .selectColumn(p -> p.value())
                .firstOrNull();
    }

    private <T> T getAndParse(String key, T defaultValue, Function<String, T> parser) {
        String value = getStringOrNull(key);
        if (StringUtil.isEmpty(value)) {
            return defaultValue;
        }

        try {
            return parser.apply(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @Override
    public String getString(String key, String defaultValue) {
        String value = getStringOrNull(key);
        return value == null ? defaultValue : value;
    }

    @Override
    public int getInt(String key, int defaultValue) {
        return getAndParse(key, defaultValue, Integer::parseInt);
    }

    @Override
    public long getLong(String key, long defaultValue) {
        return getAndParse(key, defaultValue, Long::parseLong);
    }

    @Override
    public double getDouble(String key, double defaultValue) {
        return getAndParse(key, defaultValue, Double::parseDouble);
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return getAndParse(key, defaultValue, Boolean::parseBoolean);
    }

    @Override
    @Cache(key = "admin:param:list", tags = CACHE_TAG, seconds = 3600)
    public List<Param> list() {
        return easyEntityQuery.queryable(Param.class)
                .orderBy(o -> {
                    o.sort().asc();
                    o.createTime().asc();
                })
                .toList();
    }

    @Override
    @CacheRemove(tags = CACHE_TAG)
    public Param update(Param param) {
        Objects.requireNonNull(param, "系统参数不能为空");
        requireText(param.getId(), "参数编号");
        easyEntityQuery.updatable(param)
                .setSQLStrategy(SQLExecuteStrategyEnum.ONLY_NOT_NULL_COLUMNS)
                .executeRows(1, "更新系统参数失败");
        return param;
    }

    @Override
    @CacheRemove(tags = CACHE_TAG)
    public void removeById(String id) {
        easyEntityQuery.deletable(Param.class)
                .whereById(requireText(id, "参数编号"))
                .executeRows(1, "删除系统参数失败");
    }

    private String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + "不能为空");
        }
        return value;
    }
}

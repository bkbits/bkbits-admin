package com.bkbits.admin.service.impl;

import com.bkbits.admin.service.DictService;
import com.bkbits.dbo.entity.Dict;
import com.bkbits.dbo.entity.DictValue;
import com.easy.query.api.proxy.client.EasyEntityQuery;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.annotation.Cache;
import org.noear.solon.data.annotation.CacheRemove;
import org.noear.solon.data.annotation.Transaction;

import java.util.List;
import java.util.Objects;

@Component
public class DictServiceImpl implements DictService {

    private static final String CACHE_TAG = "admin:dict";

    @Inject
    EasyEntityQuery easyEntityQuery;

    @Override
    @CacheRemove(tags = CACHE_TAG)
    public Dict add(Dict dict) {
        Objects.requireNonNull(dict, "系统字典不能为空");
        if (easyEntityQuery.insertable(dict).executeRows() != 1) {
            throw new IllegalStateException("创建系统字典失败");
        }
        return dict;
    }

    @Override
    @Cache(key = "admin:dict:${key}", tags = CACHE_TAG, seconds = 3600)
    public Dict getByKey(String key) {
        return easyEntityQuery.queryable(Dict.class)
                .include2((c, s) -> c.query(s.valueList()).orderBy(x -> {
                    x.sort().asc();
                    x.id().asc();
                }))
                .where(o -> o.dictKey().eq(requireText(key, "字典键")))
                .singleOrNull();
    }

    @Override
    public List<Dict> list() {
        return easyEntityQuery.queryable(Dict.class)
                .orderBy(o -> {
                    o.sort().asc();
                    o.createTime().asc();
                })
                .toList();
    }

    @Override
    @CacheRemove(tags = CACHE_TAG)
    public Dict update(Dict dict) {
        Objects.requireNonNull(dict, "系统字典不能为空");
        requireText(dict.getId(), "字典编号");
        easyEntityQuery.updatable(dict)
                .executeRows(1, "更新系统字典失败");
        return dict;
    }

    @Override
    @Transaction
    @CacheRemove(tags = CACHE_TAG)
    public void removeById(String id) {
        String checkedId = requireText(id, "字典编号");
        easyEntityQuery.deletable(DictValue.class)
                .where(o -> o.dictId().eq(checkedId))
                .executeRows();
        easyEntityQuery.deletable(Dict.class)
                .whereById(checkedId)
                .executeRows(1, "删除系统字典失败");
    }

    @Override
    @CacheRemove(tags = CACHE_TAG)
    public DictValue addValue(DictValue dictValue) {
        Objects.requireNonNull(dictValue, "字典值不能为空");
        requireText(dictValue.getDictId(), "字典编号");
        if (easyEntityQuery.insertable(dictValue).executeRows() != 1) {
            throw new IllegalStateException("创建字典值失败");
        }
        return dictValue;
    }

    @Override
    @Cache(key = "admin:dict:values:${dictKey}", tags = CACHE_TAG, seconds = 3600)
    public List<DictValue> listValues(String dictKey) {
        return easyEntityQuery.queryable(DictValue.class)
                .where(o -> o.valueKey().eq(requireText(dictKey, "字典编号")))
                .orderBy(o -> {
                    o.sort().asc();
                    o.id().asc();
                })
                .toList();
    }

    @Override
    @CacheRemove(tags = CACHE_TAG)
    public DictValue updateValue(DictValue dictValue) {
        Objects.requireNonNull(dictValue, "字典值不能为空");
        requireText(dictValue.getId(), "字典值编号");
        easyEntityQuery.updatable(dictValue)
                .executeRows(1, "更新字典值失败");
        return dictValue;
    }

    @Override
    @CacheRemove(tags = CACHE_TAG)
    public void removeValueById(String id) {
        easyEntityQuery.deletable(DictValue.class)
                .whereById(requireText(id, "字典值编号"))
                .executeRows(1, "删除字典值失败");
    }

    private String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + "不能为空");
        }
        return value;
    }
}

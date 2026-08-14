package com.bkbits.admin.service.impl;

import com.bkbits.admin.service.DictService;
import com.bkbits.dbo.entity.Dict;
import com.bkbits.dbo.entity.DictValue;
import com.bkbits.util.ValidUtil;
import com.easy.query.api.proxy.client.EasyEntityQuery;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.annotation.Cache;
import org.noear.solon.data.annotation.CacheRemove;
import org.noear.solon.data.annotation.Transaction;

import java.util.List;

@Component
public class DictServiceImpl implements DictService {

    private static final String CACHE_TAG = "admin:dict";

    @Inject
    EasyEntityQuery easyEntityQuery;

    @Override
    @CacheRemove(tags = CACHE_TAG)
    public Dict add(Dict dict) {
        ValidUtil.requireNotNull(dict, "系统字典不能为空");
        ValidUtil.requireEquals(
                easyEntityQuery.insertable(dict).executeRows(),
                1,
                "创建系统字典失败");
        return dict;
    }

    @Override
    @Cache(key = "admin:dict:${key}", tags = CACHE_TAG, seconds = 3600)
    public Dict getByKey(String key) {
        return easyEntityQuery.queryable(Dict.class)
                .where(o -> o.dictKey().eq(ValidUtil.requireString(key, "字典键不能为空")))
                .singleOrNull();
    }

    @Override
    @CacheRemove(tags = CACHE_TAG)
    public Dict update(Dict dict) {
        ValidUtil.requireNotNull(dict, "系统字典不能为空");
        easyEntityQuery.updatable(dict)
                .executeRows(1, "更新系统字典失败");
        return dict;
    }

    @Override
    @Transaction
    @CacheRemove(tags = CACHE_TAG)
    public void removeById(String id) {
        String checkedId = ValidUtil.requireString(id, "字典编号不能为空");
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
        ValidUtil.requireNotNull(dictValue, "字典值不能为空");
        easyEntityQuery.insertable(dictValue).executeRows();
        return dictValue;
    }

    @Override
    @Cache(key = "admin:dict:values:${dictKey}", tags = CACHE_TAG, seconds = 3600)
    public List<DictValue> listValues(String dictKey) {
        return easyEntityQuery.queryable(DictValue.class)
                .where(o -> o.valueKey().eq(ValidUtil.requireString(dictKey, "字典编号不能为空")))
                .orderBy(o -> {
                    o.sort().asc();
                    o.id().asc();
                })
                .toList();
    }

    @Override
    @CacheRemove(tags = CACHE_TAG)
    public DictValue updateValue(DictValue dictValue) {
        ValidUtil.requireNotNull(dictValue, "字典值不能为空");
        easyEntityQuery.updatable(dictValue)
                .executeRows(1, "更新字典值失败");
        return dictValue;
    }

    @Override
    @CacheRemove(tags = CACHE_TAG)
    public void removeValueById(String id) {
        easyEntityQuery.deletable(DictValue.class)
                .allowDeleteStatement(true)
                .whereById(ValidUtil.requireString(id, "字典值编号不能为空"))
                .executeRows(1, "删除字典值失败");
    }

}

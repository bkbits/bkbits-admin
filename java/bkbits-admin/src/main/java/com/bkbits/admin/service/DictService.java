package com.bkbits.admin.service;

import com.bkbits.dbo.entity.Dict;
import com.bkbits.dbo.entity.DictValue;

import java.util.List;

/**
 * 系统字典服务。
 */
public interface DictService {

    /**
     * 新增系统字典。
     *
     * @param dict 字典信息
     */
    void add(Dict dict);

    /**
     * 按字典键查询字典及其字典值。
     *
     * @param key 字典键
     * @return 字典；不存在时返回 {@code null}
     */
    Dict getByKey(String key);

    /**
     * 更新系统字典。
     *
     * @param dict 待更新的字典信息
     */
    void update(Dict dict);

    /**
     * 按编号删除字典及其全部字典值。
     *
     * @param id 字典编号
     */
    void removeById(String id);

    /**
     * 新增字典值。
     *
     * @param dictValue 字典值信息
     */
    void addValue(DictValue dictValue);

    /**
     * 查询指定字典下的全部字典值。
     *
     * @param dictKey 字典编号
     * @return 字典值列表
     */
    List<DictValue> listValues(String dictKey);

    /**
     * 更新字典值。
     *
     * @param dictValue 待更新的字典值信息
     */
    void updateValue(DictValue dictValue);

    /**
     * 按编号删除字典值。
     *
     * @param id 字典值编号
     */
    void removeValueById(String id);
}

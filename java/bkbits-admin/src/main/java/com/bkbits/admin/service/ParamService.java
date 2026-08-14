package com.bkbits.admin.service;

import com.bkbits.dbo.entity.Param;

import java.util.List;

/**
 * 系统参数服务。
 */
public interface ParamService {

    /**
     * 新增系统参数。
     *
     * @param param 参数信息
     * @return 新增后的参数
     */
    Param add(Param param);

    /**
     * 按参数键查询系统参数。
     *
     * @param key 参数键
     * @return 系统参数；不存在时返回 {@code null}
     */
    Param getByKey(String key);

    /**
     * 按参数键查询系统参数。
     *
     * @param id 参数id
     * @return 系统参数；不存在时返回 {@code null}
     */
    Param getById(String id);

    /**
     * 按参数键获取字符串值。
     *
     * @param key          参数键
     * @param defaultValue 默认值；参数不存在或值为空时返回
     * @return 参数值字符串；参数不存在或值为空时返回默认值
     */
    String getString(String key, String defaultValue);

    /**
     * 按参数键获取 int 值。
     *
     * @param key          参数键
     * @param defaultValue 默认值；参数不存在或格式非法时返回
     * @return 参数值转换后的 int；参数不存在或格式非法时返回默认值
     */
    int getInt(String key, int defaultValue);

    /**
     * 按参数键获取 long 值。
     *
     * @param key          参数键
     * @param defaultValue 默认值；参数不存在或格式非法时返回
     * @return 参数值转换后的 long；参数不存在或格式非法时返回默认值
     */
    long getLong(String key, long defaultValue);

    /**
     * 按参数键获取 double 值。
     *
     * @param key          参数键
     * @param defaultValue 默认值；参数不存在或格式非法时返回
     * @return 参数值转换后的 double；参数不存在或格式非法时返回默认值
     */
    double getDouble(String key, double defaultValue);

    /**
     * 按参数键获取 boolean 值。
     *
     * @param key          参数键
     * @param defaultValue 默认值；参数不存在或格式非法时返回
     * @return 参数值转换后的 boolean；参数不存在或格式非法时返回默认值
     */
    boolean getBoolean(String key, boolean defaultValue);

    /**
     * 查询全部系统参数。
     *
     * @return 系统参数列表
     */
    List<Param> list();

    /**
     * 更新系统参数。
     *
     * @param param 待更新的参数信息
     * @return 更新后的参数
     */
    Param update(Param param);

    /**
     * 按编号删除系统参数。
     *
     * @param id 参数编号
     */
    void removeById(String id);
}

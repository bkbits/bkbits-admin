package com.bkbits.orm;

/**
 * 主键生成接口
 *
 * <p>实现该接口的实体使用统一的主键生成策略，主键字段由外部生成后通过 {@link #setId(String)} 注入。</p>
 */
public interface IGenId {

    /**
     * 设置主键
     *
     * @param id 主键值
     */
    void setId(String id);
}

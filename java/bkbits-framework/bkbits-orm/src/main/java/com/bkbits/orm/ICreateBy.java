package com.bkbits.orm;

import java.time.LocalDateTime;

/**
 * 创建审计接口
 *
 * <p>实现该接口的实体自动维护创建人、创建时间字段。</p>
 */
public interface ICreateBy {

    /**
     * 获取创建人
     *
     * @return 创建人
     */
    String getCreateBy();

    /**
     * 设置创建人
     *
     * @param createBy 创建人
     */
    void setCreateBy(String createBy);

    /**
     * 获取创建时间
     *
     * @return 创建时间
     */
    LocalDateTime getCreateTime();

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    void setCreateTime(LocalDateTime createTime);
}

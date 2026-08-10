package com.bkbits.orm;

import java.time.LocalDateTime;

/**
 * 更新审计接口
 *
 * <p>实现该接口的实体自动维护更新人、更新时间字段。</p>
 */
public interface IUpdateBy {

    /**
     * 获取更新人
     *
     * @return 更新人
     */
    String getUpdateBy();

    /**
     * 设置更新人
     *
     * @param updateBy 更新人
     */
    void setUpdateBy(String updateBy);

    /**
     * 获取更新时间
     *
     * @return 更新时间
     */
    LocalDateTime getUpdateTime();

    /**
     * 设置更新时间
     *
     * @param updateTime 更新时间
     */
    void setUpdateTime(LocalDateTime updateTime);
}

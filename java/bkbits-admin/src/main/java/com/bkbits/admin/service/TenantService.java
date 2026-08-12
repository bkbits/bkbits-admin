package com.bkbits.admin.service;

import com.bkbits.dbo.entity.Tenant;

import java.util.List;

/**
 * 租户服务。
 */
public interface TenantService {

    /**
     * 新增租户。
     *
     * @param tenant 租户信息
     * @return 新增后的租户
     */
    Tenant add(Tenant tenant);

    /**
     * 按编号查询租户。
     *
     * @param id 租户编号
     * @return 租户；不存在时返回 {@code null}
     */
    Tenant getById(String id);

    /**
     * 查询全部租户。
     *
     * @return 租户列表
     */
    List<Tenant> list();

    /**
     * 更新租户。
     *
     * @param tenant 待更新的租户信息
     * @return 更新后的租户
     */
    Tenant update(Tenant tenant);

    /**
     * 按编号删除租户。
     *
     * @param id 租户编号
     */
    void removeById(String id);
}

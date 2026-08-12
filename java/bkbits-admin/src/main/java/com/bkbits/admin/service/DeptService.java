package com.bkbits.admin.service;

import com.bkbits.dbo.entity.Dept;

import java.util.List;

/**
 * 部门服务。
 */
public interface DeptService {

    /**
     * 新增部门。
     *
     * @param dept 部门信息
     * @return 新增后的部门
     */
    Dept add(Dept dept);

    /**
     * 按编号查询部门。
     *
     * @param deptId 部门编号
     * @return 部门；不存在时返回 {@code null}
     */
    Dept getById(String deptId);

    /**
     * 查询指定租户下的部门。
     *
     * @param tenantId 租户编号
     * @return 部门列表
     */
    List<Dept> listByTenantId(String tenantId);

    /**
     * 查询指定父部门下的直属子部门。
     *
     * @param parentId 父部门编号；为空时查询顶级部门
     * @return 部门列表
     */
    List<Dept> listByParentId(String parentId);

    /**
     * 更新部门。
     *
     * @param dept 待更新的部门信息
     * @return 更新后的部门
     */
    Dept update(Dept dept);

    /**
     * 按编号删除部门。
     *
     * @param deptId 部门编号
     */
    void removeById(String deptId);
}

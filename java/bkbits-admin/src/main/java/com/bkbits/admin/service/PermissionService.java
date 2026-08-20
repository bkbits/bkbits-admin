package com.bkbits.admin.service;

import com.bkbits.dbo.entity.DataPermission;
import com.bkbits.dbo.entity.Permission;

import java.util.Arrays;
import java.util.List;

/**
 * 权限及数据权限服务。
 */
public interface PermissionService {

    /**
     * 按编号查询权限。
     *
     * @param id 权限编号
     * @return 权限；不存在时抛异常
     */
    Permission getById(String id);

    /**
     * 查询权限树。
     *
     * @param search 查询条件；非空时按条件过滤
     * @return 权限树根节点列表
     */
    List<Permission> listTree(Object search);

    /**
     * 查询指定角色关联的权限树。
     *
     * @param roleId 角色编号
     * @return 权限树根节点列表
     */
    List<Permission> listTreeByRoleId(String roleId);

    /**
     * 新增权限。
     *
     * @param permission 权限信息
     */
    void addPermission(Permission permission);

    /**
     * 更新权限。
     *
     * @param permission 待更新的权限信息
     */
    void updatePermission(Permission permission);

    /**
     * 按编号批量删除权限及其关联关系。
     *
     * @param permissionIds 权限编号集合
     */
    void removePermissions(List<String> permissionIds);

    /**
     * 按编号删除权限及其关联关系。
     *
     * @param permissionIds 权限编号（可变参数）
     */
    default void removePermissions(String... permissionIds) {
        removePermissions(Arrays.asList(permissionIds));
    }

    /**
     * 新增数据权限，并校验其所属权限存在。
     *
     * @param dataPermission 数据权限信息
     */
    void addDataPermission(DataPermission dataPermission);

    /**
     * 更新数据权限（不允许变更所属权限）。
     *
     * @param dataPermission 待更新的数据权限信息
     */
    void updateDataPermission(DataPermission dataPermission);

    /**
     * 按编号删除数据权限及其关联关系。
     *
     * @param dataPermissionId 数据权限编号
     */
    void removeDataPermission(String dataPermissionId);
}
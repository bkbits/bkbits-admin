package com.bkbits.admin.service;

import com.bkbits.dbo.entity.Role;

import java.util.Collections;
import java.util.List;

/**
 * 角色服务。
 */
public interface RoleService {

    /**
     * 按用户编号查询角色。
     *
     * @param userId      用户编号
     * @param onlyEnabled 是否仅返回启用状态的角色
     * @return 角色列表
     */
    List<Role> getRolesByUserId(String userId, boolean onlyEnabled);

    /**
     * 按用户名查询角色。
     *
     * @param userName    用户名
     * @param onlyEnabled 是否仅返回启用状态的角色
     * @return 角色列表
     */
    List<Role> getRolesByUserName(String userName, boolean onlyEnabled);

    /**
     * 新增角色。
     *
     * @param role 角色信息
     */
    void addRole(Role role);

    /**
     * 更新角色。
     *
     * @param role 待更新的角色信息
     */
    void updateRole(Role role);

    /**
     * 批量删除角色及其关联关系。
     *
     * @param roleIds 角色编号集合
     */
    void removeRoleByIds(List<String> roleIds);

    /**
     * 删除单个角色及其关联关系。
     *
     * @param roleId 角色编号
     */
    default void removeRoleById(String roleId) {
        removeRoleByIds(Collections.singletonList(roleId));
    }

    /**
     * 为指定用户绑定角色（替换原有绑定）。
     *
     * @param userId  用户编号
     * @param roleIds 角色编号集合
     */
    void bingUserRole(String userId, List<String> roleIds);

    /**
     * 为指定角色绑定权限（替换原有绑定）。
     *
     * @param roleId        角色编号
     * @param permissionIds 权限编号集合
     */
    void bindRolePermissions(String roleId, List<String> permissionIds);

    /**
     * 为指定角色绑定菜单权限的数据域（替换原有绑定）。
     *
     * @param roleId       角色编号
     * @param permissionId 菜单权限编号
     * @param dataScopes   数据域集合
     */
    void bingRoleDataPermission(String roleId, String permissionId, List<String> dataScopes);

    /**
     * 查询指定角色、菜单权限已绑定的数据域。
     *
     * @param roleId       角色编号
     * @param permissionId 菜单权限编号
     * @return 数据域标识集合
     */
    List<String> listRoleDataScopes(String roleId, String permissionId);

    /**
     * 为指定角色绑定数据域（替换原有绑定）。
     *
     * @param roleId        角色编号
     * @param dataScopeList 数据域集合
     */
    void bindRoleDataScopes(String roleId, List<String> dataScopeList);
}
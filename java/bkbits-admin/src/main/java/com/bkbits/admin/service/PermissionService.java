package com.bkbits.admin.service;

import com.bkbits.dbo.entity.DataPermission;
import com.bkbits.dbo.entity.Permission;
import com.bkbits.dbo.entity.Role;
import com.bkbits.dbo.entity.RoleDataPermissionRel;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * 角色、权限及数据权限服务。
 */
public interface PermissionService {

    /**
     * 新增角色。
     *
     * @param role 角色信息
     * @return 新增后的角色
     */
    Role addRole(Role role);

    /**
     * 按编号查询角色及其权限、数据权限关联。
     *
     * @param roleId 角色编号
     * @return 角色；不存在时返回 {@code null}
     */
    Role getRoleById(String roleId);

    /**
     * 查询指定租户下的角色。
     *
     * @param tenantId 租户编号
     * @return 角色列表
     */
    List<Role> listRolesByTenantId(String tenantId);

    /**
     * 更新角色。
     *
     * @param role 待更新的角色信息
     * @return 更新后的角色
     */
    Role updateRole(Role role);

    /**
     * 删除角色及其用户、权限、数据权限关联。
     *
     * @param roleId 角色编号
     */
    void removeRoleById(String roleId);

    /**
     * 新增权限。
     *
     * @param permission 权限信息
     * @return 新增后的权限
     */
    Permission addPermission(Permission permission);

    /**
     * 按编号查询权限及其数据权限。
     *
     * @param permissionId 权限编号
     * @return 权限；不存在时返回 {@code null}
     */
    Permission getPermissionById(String permissionId);

    /**
     * 更新权限。
     *
     * @param permission 待更新的权限信息
     * @return 更新后的权限
     */
    Permission updatePermission(Permission permission);

    /**
     * 删除权限及其角色、数据权限关联。
     *
     * @param permissionId 权限编号
     */
    void removePermissionById(String permissionId);

    /**
     * 为菜单权限添加数据权限。
     *
     * @param dataPermission 数据权限信息
     * @return 新增后的数据权限
     */
    DataPermission addDataPermission(DataPermission dataPermission);

    /**
     * 查询指定菜单权限下的数据权限。
     *
     * @param permissionId 菜单权限编号
     * @return 数据权限列表
     */
    List<DataPermission> listDataPermissions(String permissionId);

    /**
     * 更新数据权限。
     *
     * @param dataPermission 待更新的数据权限信息
     * @return 更新后的数据权限
     */
    DataPermission updateDataPermission(DataPermission dataPermission);

    /**
     * 删除数据权限及其角色关联。
     *
     * @param dataPermissionId 数据权限编号
     */
    void removeDataPermissionById(String dataPermissionId);

    /**
     * 使用给定角色集合替换用户现有的全部角色绑定。
     *
     * @param userId  用户编号
     * @param roleIds 角色编号集合；为空时清空绑定
     */
    void bindRolesToUser(String userId, Collection<String> roleIds);

    /**
     * 查询用户绑定的角色。
     *
     * @param userId 用户编号
     * @return 角色列表
     */
    List<Role> listRolesByUserId(String userId);

    /**
     * 使用给定权限集合替换角色现有的全部权限绑定。
     *
     * @param roleId        角色编号
     * @param permissionIds 权限编号集合；为空时清空绑定
     */
    void bindPermissionsToRole(String roleId, Collection<String> permissionIds);

    /**
     * 查询角色绑定的权限。
     *
     * @param roleId 角色编号
     * @return 权限列表
     */
    List<String> listPermissionsByRoleId(String roleId);

    /**
     * 使用给定数据权限集合替换角色在指定菜单权限下的全部数据权限绑定。
     *
     * @param roleId            角色编号
     * @param dataPermissionIds 数据权限编号集合；为空时清空绑定
     * @param permissionId      权限id，不为空时进行父级权限校验
     */
    void bindDataPermissionsToRole(String roleId,
                                   Collection<String> dataPermissionIds,
                                   @Nullable String permissionId);

    /**
     * 查询角色在指定菜单权限下的数据权限关联。
     *
     * @param roleId       角色编号
     * @param permissionId 菜单权限编号
     * @return 角色数据权限关联列表
     */
    List<String> listRoleDataPermissions(String roleId, String permissionId);
}

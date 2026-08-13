package com.bkbits.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.bkbits.admin.mapper.PermissionMapper;
import com.bkbits.admin.pojo.*;
import com.bkbits.admin.service.PermissionService;
import com.bkbits.core.PageQuery;
import com.bkbits.core.PageResult;
import com.bkbits.core.Result;
import com.bkbits.dbo.entity.DataPermission;
import com.bkbits.dbo.entity.Permission;
import com.bkbits.dbo.entity.Role;
import com.bkbits.util.CollectionUtil;
import com.easy.query.api.proxy.client.EasyEntityQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.noear.solon.annotation.Body;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Param;
import org.noear.solon.annotation.Post;
import org.noear.solon.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色、权限及数据权限控制器。
 */
@Api("角色权限接口")
@Controller
@Mapping("/api")
public class PermissionController {

    @Inject
    private PermissionService permissionService;

    @Inject
    private EasyEntityQuery easyEntityQuery;

    /**
     * 新增角色。
     *
     * @param dto 角色输入参数
     * @return 新增后的角色
     */
    @ApiOperation("新增角色")
    @Post
    @Mapping("/role/add")
    @SaCheckPermission("admin.role.add")
    public Result<Role> addRole(@Validated @Body RoleDTO dto) {
        return Result.ok(permissionService.addRole(PermissionMapper.INSTANCE.toRoleEntity(dto)));
    }

    /**
     * 按编号查询角色及其权限、数据权限关联。
     *
     * @param roleId 角色编号
     * @return 角色；不存在时返回 null
     */
    @ApiOperation("按编号查询角色")
    @Get
    @Mapping("/role/getById")
    @SaCheckPermission("admin.role.query")
    public Result<Role> getRoleById(@ApiParam("角色编号") @Param("roleId") String roleId) {
        return Result.ok(permissionService.getRoleById(roleId));
    }

    /**
     * 更新角色。
     *
     * @param dto 角色输入参数
     * @return 更新后的角色
     */
    @ApiOperation("更新角色")
    @Post
    @Mapping("/role/update")
    @SaCheckPermission("admin.role.update")
    public Result<Role> updateRole(@Validated @Body RoleDTO dto) {
        return Result.ok(permissionService.updateRole(PermissionMapper.INSTANCE.toRoleEntity(dto)));
    }

    /**
     * 删除角色及其用户、权限、数据权限关联。
     *
     * @param dto 编号参数
     * @return 操作结果
     */
    @ApiOperation("删除角色")
    @Post
    @Mapping("/role/remove")
    @SaCheckPermission("admin.role.remove")
    public Result<Void> removeRole(@Validated @Body IdDTO dto) {
        permissionService.removeRoleById(dto.getId());
        return Result.ok();
    }

    /**
     * 新增权限。
     *
     * @param dto 权限输入参数
     * @return 新增后的权限
     */
    @ApiOperation("新增权限")
    @Post
    @Mapping("/permission/add")
    @SaCheckPermission("admin.permission.add")
    public Result<Permission> addPermission(@Validated @Body PermissionDTO dto) {
        return Result.ok(
                permissionService.addPermission(PermissionMapper.INSTANCE.toPermissionEntity(dto)));
    }

    /**
     * 按编号查询权限及其数据权限。
     *
     * @param permissionId 权限编号
     * @return 权限；不存在时返回 null
     */
    @ApiOperation("按编号查询权限")
    @Get
    @Mapping("/permission/getById")
    @SaCheckPermission("admin.permission.query")
    public Result<Permission> getPermissionById(
            @ApiParam("权限编号") @Param("permissionId") String permissionId) {
        return Result.ok(permissionService.getPermissionById(permissionId));
    }

    /**
     * 查询全部权限。
     *
     * @return 权限列表
     */
    @ApiOperation("查询全部权限")
    @Get
    @Mapping("/permission/list")
    @SaCheckPermission("admin.permission.query")
    public Result<List<Permission>> listPermissions(
            PermissionQueryDTO dto
    ) {
        return Result.ok(
                CollectionUtil.toTree(
                        easyEntityQuery.queryable(Permission.class)
                                .whereObject(dto)
                                .orderBy(p -> {
                                    p.parentId().asc();
                                    p.sort().asc();
                                    p.createTime().asc();
                                })
                                .toList()
                )
        );
    }

    /**
     * 更新权限。
     *
     * @param dto 权限输入参数
     * @return 更新后的权限
     */
    @ApiOperation("更新权限")
    @Post
    @Mapping("/permission/update")
    @SaCheckPermission("admin.permission.update")
    public Result<Permission> updatePermission(@Body PermissionDTO dto) {
        return Result.ok(permissionService.updatePermission(PermissionMapper.INSTANCE.toPermissionEntity(dto)));
    }

    /**
     * 删除权限及其角色、数据权限关联。
     *
     * @param dto 编号参数
     * @return 操作结果
     */
    @ApiOperation("删除权限")
    @Post
    @Mapping("/permission/remove")
    @SaCheckPermission("admin.permission.remove")
    public Result<Void> removePermission(@Body IdDTO dto) {
        permissionService.removePermissionById(dto.getId());
        return Result.ok();
    }

    /**
     * 为菜单权限添加数据权限。
     *
     * @param dto 数据权限输入参数
     * @return 新增后的数据权限
     */
    @ApiOperation("为菜单权限添加数据权限")
    @Post
    @Mapping("/dataPermission/add")
    @SaCheckPermission("admin.dataPermission.add")
    public Result<DataPermission> addDataPermission(@Body DataPermissionDTO dto) {
        return Result.ok(permissionService.addDataPermission(
                dto.getMenuPermissionId(),
                PermissionMapper.INSTANCE.toDataPermissionEntity(dto)));
    }

    /**
     * 查询指定菜单权限下的数据权限。
     *
     * @param menuPermissionId 菜单权限编号
     * @return 数据权限列表
     */
    @ApiOperation("查询菜单权限下数据权限")
    @Get
    @Mapping("/dataPermission/list")
    @SaCheckPermission("admin.dataPermission.query")
    public Result<List<DataPermission>> listDataPermissions(
            @ApiParam("菜单权限编号") @Param("menuPermissionId") String menuPermissionId) {
        return Result.ok(permissionService.listDataPermissions(menuPermissionId));
    }

    /**
     * 更新数据权限。
     *
     * @param dto 数据权限输入参数
     * @return 更新后的数据权限
     */
    @ApiOperation("更新数据权限")
    @Post
    @Mapping("/dataPermission/update")
    @SaCheckPermission("admin.dataPermission.update")
    public Result<DataPermission> updateDataPermission(@Body DataPermissionDTO dto) {
        return Result.ok(permissionService.updateDataPermission(PermissionMapper.INSTANCE.toDataPermissionEntity(dto)));
    }

    /**
     * 删除数据权限及其角色关联。
     *
     * @param dto 编号参数
     * @return 操作结果
     */
    @ApiOperation("删除数据权限")
    @Post
    @Mapping("/dataPermission/remove")
    @SaCheckPermission("admin.dataPermission.remove")
    public Result<Void> removeDataPermission(@Body IdDTO dto) {
        permissionService.removeDataPermissionById(dto.getId());
        return Result.ok();
    }

    /**
     * 使用给定权限集合替换角色现有的全部权限绑定。
     *
     * @param dto 角色绑定权限参数
     * @return 操作结果
     */
    @ApiOperation("绑定权限到角色")
    @Post
    @Mapping("/role/bindPermissions")
    @SaCheckPermission("admin.role.bind")
    public Result<Void> bindPermissionsToRole(@Validated @Body BindPermissionsToRoleDTO dto) {
        permissionService.bindPermissionsToRole(dto.getRoleId(), dto.getPermissionIds());
        return Result.ok();
    }

    /**
     * 查询角色绑定的权限。
     *
     * @param roleId 角色编号
     * @return 权限列表
     */
    @ApiOperation("查询角色绑定的权限id")
    @Get
    @Mapping("/role/listPermissionIds")
    @SaCheckPermission("admin.role.query")
    public Result<List<String>> listPermissionIdsByRoleId(@ApiParam("角色编号") @Param("roleId") String roleId) {
        return Result.ok(permissionService.listPermissionsByRoleId(roleId));
    }

    /**
     * 使用给定数据权限集合替换角色在指定菜单权限下的全部数据权限绑定。
     *
     * @param dto 角色绑定数据权限参数
     * @return 操作结果
     */
    @ApiOperation("绑定数据权限到角色")
    @Post
    @Mapping("/role/bindDataPermissions")
    @SaCheckPermission("admin.role.bind")
    public Result<Void> bindDataPermissionsToRole(@Body BindDataPermissionsToRoleDTO dto) {
        permissionService.bindDataPermissionsToRole(
                dto.getRoleId(),
                dto.getMenuPermissionId(),
                dto.getDataPermissionIds()
        );
        return Result.ok();
    }

    /**
     * 查询角色在指定菜单权限下的数据权限关联。
     *
     * @param roleId           角色编号
     * @param menuPermissionId 菜单权限编号
     * @return 角色数据权限关联列表
     */
    @ApiOperation("查询角色菜单数据权限关联")
    @Get
    @Mapping("/role/listDataPermissionIds")
    @SaCheckPermission("admin.role.query")
    public Result<List<String>> listRoleDataPermissions(
            @ApiParam("角色编号") @Param("roleId") String roleId,
            @ApiParam("菜单权限编号") @Param("menuPermissionId") String menuPermissionId) {
        return Result.ok(
                permissionService.listRoleDataPermissions(roleId, menuPermissionId)
        );
    }
}

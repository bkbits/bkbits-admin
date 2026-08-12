package com.bkbits.admin.controller;

import com.bkbits.admin.mapper.PermissionMapper;
import com.bkbits.admin.pojo.BindDataPermissionsToRoleDTO;
import com.bkbits.admin.pojo.BindPermissionsToRoleDTO;
import com.bkbits.admin.pojo.BindRolesToUserDTO;
import com.bkbits.admin.pojo.DataPermissionDTO;
import com.bkbits.admin.pojo.DataPermissionVO;
import com.bkbits.admin.pojo.IdDTO;
import com.bkbits.admin.pojo.PermissionDTO;
import com.bkbits.admin.pojo.PermissionVO;
import com.bkbits.admin.pojo.RoleDTO;
import com.bkbits.admin.pojo.RoleDataPermissionRelVO;
import com.bkbits.admin.pojo.RoleVO;
import com.bkbits.admin.service.PermissionService;
import com.bkbits.core.Result;
import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.Body;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Param;
import org.noear.solon.annotation.Post;

import java.util.List;

/**
 * 角色、权限及数据权限控制器。
 */
@Controller
@Mapping("/api/permission")
public class PermissionController {


    @Inject
    private PermissionService permissionService;

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
    public Result<RoleVO> addRole(@Body RoleDTO dto) {
        return Result.ok(PermissionMapper.INSTANCE.toRoleVO(permissionService.addRole(PermissionMapper.INSTANCE.toRoleEntity(dto))));
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
    public Result<RoleVO> getRoleById(@Param("roleId") String roleId) {
        return Result.ok(PermissionMapper.INSTANCE.toRoleVO(permissionService.getRoleById(roleId)));
    }

    /**
     * 查询指定租户下的角色。
     *
     * @param tenantId 租户编号
     * @return 角色列表
     */
    @ApiOperation("查询租户下角色")
    @Get
    @Mapping("/role/listByTenantId")
    @SaCheckPermission("admin.role.query")
    public Result<List<RoleVO>> listRolesByTenantId(@Param("tenantId") String tenantId) {
        return Result.ok(PermissionMapper.INSTANCE.toRoleVOList(permissionService.listRolesByTenantId(tenantId)));
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
    public Result<RoleVO> updateRole(@Body RoleDTO dto) {
        return Result.ok(PermissionMapper.INSTANCE.toRoleVO(permissionService.updateRole(PermissionMapper.INSTANCE.toRoleEntity(dto))));
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
    public Result<Void> removeRole(@Body IdDTO dto) {
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
    public Result<PermissionVO> addPermission(@Body PermissionDTO dto) {
        return Result.ok(PermissionMapper.INSTANCE.toPermissionVO(permissionService.addPermission(PermissionMapper.INSTANCE.toPermissionEntity(dto))));
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
    public Result<PermissionVO> getPermissionById(@Param("permissionId") String permissionId) {
        return Result.ok(PermissionMapper.INSTANCE.toPermissionVO(permissionService.getPermissionById(permissionId)));
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
    public Result<List<PermissionVO>> listPermissions() {
        return Result.ok(PermissionMapper.INSTANCE.toPermissionVOList(permissionService.listPermissions()));
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
    public Result<PermissionVO> updatePermission(@Body PermissionDTO dto) {
        return Result.ok(PermissionMapper.INSTANCE.toPermissionVO(permissionService.updatePermission(PermissionMapper.INSTANCE.toPermissionEntity(dto))));
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
    public Result<DataPermissionVO> addDataPermission(@Body DataPermissionDTO dto) {
        return Result.ok(PermissionMapper.INSTANCE.toDataPermissionVO(permissionService.addDataPermission(
                dto.getMenuPermissionId(),
                PermissionMapper.INSTANCE.toDataPermissionEntity(dto))));
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
    public Result<List<DataPermissionVO>> listDataPermissions(@Param("menuPermissionId") String menuPermissionId) {
        return Result.ok(PermissionMapper.INSTANCE.toDataPermissionVOList(permissionService.listDataPermissions(menuPermissionId)));
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
    public Result<DataPermissionVO> updateDataPermission(@Body DataPermissionDTO dto) {
        return Result.ok(PermissionMapper.INSTANCE.toDataPermissionVO(permissionService.updateDataPermission(PermissionMapper.INSTANCE.toDataPermissionEntity(dto))));
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
     * 使用给定角色集合替换用户现有的全部角色绑定。
     *
     * @param dto 用户绑定角色参数
     * @return 操作结果
     */
    @ApiOperation("绑定角色到用户")
    @Post
    @Mapping("/bindRolesToUser")
    @SaCheckPermission("admin.user.bind")
    public Result<Void> bindRolesToUser(@Body BindRolesToUserDTO dto) {
        permissionService.bindRolesToUser(dto.getUserId(), dto.getRoleIds());
        return Result.ok();
    }

    /**
     * 查询用户绑定的角色。
     *
     * @param userId 用户编号
     * @return 角色列表
     */
    @ApiOperation("查询用户绑定的角色")
    @Get
    @Mapping("/listRolesByUserId")
    @SaCheckPermission("admin.user.query")
    public Result<List<RoleVO>> listRolesByUserId(@Param("userId") String userId) {
        return Result.ok(PermissionMapper.INSTANCE.toRoleVOList(permissionService.listRolesByUserId(userId)));
    }

    /**
     * 使用给定权限集合替换角色现有的全部权限绑定。
     *
     * @param dto 角色绑定权限参数
     * @return 操作结果
     */
    @ApiOperation("绑定权限到角色")
    @Post
    @Mapping("/bindPermissionsToRole")
    @SaCheckPermission("admin.role.bind")
    public Result<Void> bindPermissionsToRole(@Body BindPermissionsToRoleDTO dto) {
        permissionService.bindPermissionsToRole(dto.getRoleId(), dto.getPermissionIds());
        return Result.ok();
    }

    /**
     * 查询角色绑定的权限。
     *
     * @param roleId 角色编号
     * @return 权限列表
     */
    @ApiOperation("查询角色绑定的权限")
    @Get
    @Mapping("/listPermissionsByRoleId")
    @SaCheckPermission("admin.role.query")
    public Result<List<PermissionVO>> listPermissionsByRoleId(@Param("roleId") String roleId) {
        return Result.ok(PermissionMapper.INSTANCE.toPermissionVOList(permissionService.listPermissionsByRoleId(roleId)));
    }

    /**
     * 使用给定数据权限集合替换角色在指定菜单权限下的全部数据权限绑定。
     *
     * @param dto 角色绑定数据权限参数
     * @return 操作结果
     */
    @ApiOperation("绑定数据权限到角色")
    @Post
    @Mapping("/bindDataPermissionsToRole")
    @SaCheckPermission("admin.role.bind")
    public Result<Void> bindDataPermissionsToRole(@Body BindDataPermissionsToRoleDTO dto) {
        permissionService.bindDataPermissionsToRole(dto.getRoleId(), dto.getMenuPermissionId(), dto.getDataPermissionIds());
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
    @Mapping("/listRoleDataPermissions")
    @SaCheckPermission("admin.role.query")
    public Result<List<RoleDataPermissionRelVO>> listRoleDataPermissions(@Param("roleId") String roleId,
                                                                         @Param("menuPermissionId") String menuPermissionId) {
        return Result.ok(PermissionMapper.INSTANCE.toRoleDataPermissionRelVOList(permissionService.listRoleDataPermissions(roleId, menuPermissionId)));
    }
}

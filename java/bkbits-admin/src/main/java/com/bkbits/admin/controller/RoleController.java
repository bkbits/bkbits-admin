package com.bkbits.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.bkbits.admin.mapper.PermissionMapper;
import com.bkbits.admin.pojo.BindDataPermissionsToRoleDTO;
import com.bkbits.admin.pojo.BindDataScopesToRoleDTO;
import com.bkbits.admin.pojo.BindPermissionsToRoleDTO;
import com.bkbits.admin.pojo.IdDTO;
import com.bkbits.admin.pojo.RoleAddDTO;
import com.bkbits.admin.pojo.RoleQueryDTO;
import com.bkbits.admin.pojo.RoleUpdateDTO;
import com.bkbits.admin.pojo.UserQueryDTO;
import com.bkbits.admin.service.PermissionService;
import com.bkbits.admin.service.RoleService;
import com.bkbits.core.PageQuery;
import com.bkbits.core.PageResult;
import com.bkbits.core.Result;
import com.bkbits.dbo.entity.Permission;
import com.bkbits.dbo.entity.Role;
import com.bkbits.dbo.entity.User;
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

/**
 * 角色控制器。
 */
@Api("角色接口")
@Controller
@Mapping("/api/sys/role")
public class RoleController {

    @Inject
    private RoleService roleService;

    @Inject
    private PermissionService permissionService;

    @Inject
    private EasyEntityQuery easyEntityQuery;

    /**
     * 新增角色。
     *
     * @param dto 角色输入参数
     * @return 操作结果
     */
    @ApiOperation("新增角色")
    @Post
    @Mapping("/add")
    @SaCheckPermission("admin.role.add")
    public Result<Void> add(@Validated @Body RoleAddDTO dto) {
        roleService.addRole(PermissionMapper.INSTANCE.toRoleEntity(dto));
        return Result.ok();
    }

    /**
     * 更新角色。
     *
     * @param dto 角色输入参数
     * @return 操作结果
     */
    @ApiOperation("更新角色")
    @Post
    @Mapping("/update")
    @SaCheckPermission("admin.role.update")
    public Result<Void> update(@Validated @Body RoleUpdateDTO dto) {
        roleService.updateRole(PermissionMapper.INSTANCE.toRoleEntity(dto));
        return Result.ok();
    }

    /**
     * 删除角色及其关联关系。
     *
     * @param dto 编号参数
     * @return 操作结果
     */
    @ApiOperation("删除角色")
    @Post
    @Mapping("/remove")
    @SaCheckPermission("admin.role.remove")
    public Result<Void> remove(@Validated @Body IdDTO dto) {
        roleService.removeRoleById(dto.getId());
        return Result.ok();
    }

    /**
     * 按编号查询角色。
     *
     * @param id 角色编号
     * @return 角色；不存在时抛异常
     */
    @ApiOperation("按编号查询角色")
    @Get
    @Mapping("/getById")
    @SaCheckPermission("admin.role.query")
    public Result<Role> getById(@ApiParam("角色编号") @Param("id") String id) {
        return Result.ok(easyEntityQuery.queryable(Role.class)
                .whereById(id)
                .singleNotNull("角色不存在"));
    }

    /**
     * 分页查询角色。
     *
     * @param dto 查询参数
     * @return 分页结果
     */
    @ApiOperation("分页查询角色")
    @Get
    @Mapping("/query")
    @SaCheckPermission("admin.role.query")
    public PageResult<Role> query(RoleQueryDTO dto) {
        return easyEntityQuery.queryable(Role.class)
                .whereObject(dto)
                .orderBy(r -> r.sort().asc())
                .toPageResult(PageQuery.current().toPager(Role.class));
    }

    /**
     * 分页查询角色绑定的用户。
     *
     * @param roleId 角色编号
     * @param dto    用户查询参数
     * @return 分页结果
     */
    @ApiOperation("分页查询角色绑定的用户")
    @Get
    @Mapping("/users")
    @SaCheckPermission("admin.role.query")
    public PageResult<User> listUsers(
            @ApiParam("角色编号") @Param("roleId") String roleId,
            UserQueryDTO dto) {
        return easyEntityQuery.queryable(User.class)
                .where(u -> u.roleList().any(r -> r.id().eq(roleId)))
                .whereObject(dto)
                .orderBy(u -> u.createTime().desc())
                .toPageResult(PageQuery.current().toPager(User.class));
    }

    /**
     * 查询角色绑定的权限树。
     *
     * @param roleId 角色编号
     * @return 权限树根节点列表
     */
    @ApiOperation("查询角色绑定的权限树")
    @Get
    @Mapping("/permissions")
    @SaCheckPermission("admin.role.query")
    public Result<List<Permission>> listPermissions(@ApiParam("角色编号") @Param("roleId") String roleId) {
        return Result.ok(permissionService.listTreeByRoleId(roleId));
    }

    /**
     * 绑定权限到角色（替换原有绑定）。
     *
     * @param dto 角色绑定权限参数
     * @return 操作结果
     */
    @ApiOperation("绑定权限到角色")
    @Post
    @Mapping("/bindPermissions")
    @SaCheckPermission("admin.role.bind")
    public Result<Void> bindPermissions(@Validated @Body BindPermissionsToRoleDTO dto) {
        roleService.bindRolePermissions(dto.getRoleId(), dto.getPermissionIds());
        return Result.ok();
    }

    /**
     * 绑定数据权限到角色（替换原有绑定）。
     *
     * @param dto 角色绑定数据权限参数
     * @return 操作结果
     */
    @ApiOperation("绑定数据权限到角色")
    @Post
    @Mapping("/bindDataPermissions")
    @SaCheckPermission("admin.role.bind")
    public Result<Void> bindDataPermissions(@Validated @Body BindDataPermissionsToRoleDTO dto) {
        roleService.bingRoleDataPermission(dto.getRoleId(), dto.getDataPermissionIds());
        return Result.ok();
    }

    /**
     * 绑定数据域到角色（替换原有绑定）。
     *
     * @param dto 角色绑定数据域参数
     * @return 操作结果
     */
    @ApiOperation("绑定数据域到角色")
    @Post
    @Mapping("/bindDataScope")
    @SaCheckPermission("admin.role.bind")
    public Result<Void> bindDataScope(@Validated @Body BindDataScopesToRoleDTO dto) {
        roleService.bindRoleDataScopes(dto.getRoleId(), dto.getDataScopes());
        return Result.ok();
    }
}
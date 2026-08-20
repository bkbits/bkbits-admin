package com.bkbits.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.bkbits.admin.mapper.PermissionMapper;
import com.bkbits.admin.pojo.DataPermissionAddDTO;
import com.bkbits.admin.pojo.DataPermissionUpdateDTO;
import com.bkbits.admin.pojo.IdDTO;
import com.bkbits.admin.pojo.PermissionAddDTO;
import com.bkbits.admin.pojo.PermissionQueryDTO;
import com.bkbits.admin.pojo.PermissionUpdateDTO;
import com.bkbits.admin.service.PermissionService;
import com.bkbits.core.Result;
import com.bkbits.dbo.entity.DataPermission;
import com.bkbits.dbo.entity.Permission;
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

/**
 * 权限及数据权限控制器。
 */
@Api("权限接口")
@Controller
@Mapping("/api")
public class PermissionController {

    @Inject
    private PermissionService permissionService;

    @Inject
    private EasyEntityQuery easyEntityQuery;

    /**
     * 新增权限。
     *
     * @param dto 权限输入参数
     * @return 操作结果
     */
    @ApiOperation("新增权限")
    @Post
    @Mapping("/permission/add")
    @SaCheckPermission("admin.permission.add")
    public Result<Void> addPermission(@Validated @Body PermissionAddDTO dto) {
        permissionService.addPermission(PermissionMapper.INSTANCE.toPermissionEntity(dto));
        return Result.ok();
    }

    /**
     * 按编号查询权限及其数据权限。
     *
     * @param id 权限编号
     * @return 权限；不存在时返回 null
     */
    @ApiOperation("按编号查询权限")
    @Get
    @Mapping("/permission/getById")
    @SaCheckPermission("admin.permission.query")
    public Result<Permission> getPermissionById(
            @ApiParam("权限编号") @Param("id") String id) {
        return Result.ok(permissionService.getById(id));
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
    public Result<List<Permission>> listPermissions(PermissionQueryDTO dto) {
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
     * @return 操作结果
     */
    @ApiOperation("更新权限")
    @Post
    @Mapping("/permission/update")
    @SaCheckPermission("admin.permission.update")
    public Result<Void> updatePermission(@Validated @Body PermissionUpdateDTO dto) {
        permissionService.updatePermission(PermissionMapper.INSTANCE.toPermissionEntity(dto));
        return Result.ok();
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
        permissionService.removePermissions(dto.getId());
        return Result.ok();
    }

    /**
     * 为菜单权限添加数据权限。
     *
     * @param dto 数据权限输入参数
     * @return 操作结果
     */
    @ApiOperation("为菜单权限添加数据权限")
    @Post
    @Mapping("/dataPermission/add")
    @SaCheckPermission("admin.permission.add")
    public Result<Void> addDataPermission(@Validated @Body DataPermissionAddDTO dto) {
        permissionService.addDataPermission(PermissionMapper.INSTANCE.toDataPermissionEntity(dto));
        return Result.ok();
    }

    /**
     * 查询指定菜单权限下的数据权限。
     *
     * @param permissionId 菜单权限编号
     * @return 数据权限列表
     */
    @ApiOperation("查询菜单权限下数据权限")
    @Get
    @Mapping("/dataPermission/list")
    @SaCheckPermission("admin.permission.query")
    public Result<List<DataPermission>> listDataPermissions(
            @ApiParam("菜单权限编号") @Param("permissionId") String permissionId) {
        return Result.ok(
                easyEntityQuery.queryable(DataPermission.class)
                        .where(p -> p.permissionId().eq(permissionId))
                        .toList()
        );
    }

    /**
     * 更新数据权限。
     *
     * @param dto 数据权限输入参数
     * @return 操作结果
     */
    @ApiOperation("更新数据权限")
    @Post
    @Mapping("/dataPermission/update")
    @SaCheckPermission("admin.permission.update")
    public Result<Void> updateDataPermission(@Validated @Body DataPermissionUpdateDTO dto) {
        permissionService.updateDataPermission(PermissionMapper.INSTANCE.toDataPermissionEntity(dto));
        return Result.ok();
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
    @SaCheckPermission("admin.permission.remove")
    public Result<Void> removeDataPermission(@Body IdDTO dto) {
        permissionService.removeDataPermission(dto.getId());
        return Result.ok();
    }
}
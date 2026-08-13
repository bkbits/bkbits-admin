package com.bkbits.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.bkbits.admin.mapper.TenantMapper;
import com.bkbits.admin.pojo.IdDTO;
import com.bkbits.admin.pojo.TenantDTO;
import com.bkbits.admin.pojo.TenantVO;
import com.bkbits.admin.service.TenantService;
import com.bkbits.core.PageQuery;
import com.bkbits.core.PageResult;
import com.bkbits.core.Result;
import com.bkbits.dbo.entity.Tenant;
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

import java.util.List;

/**
 * 租户控制器。
 */
@Api("租户接口")
@Controller
@Mapping("/api/tenant")
public class TenantController {

    @Inject
    private TenantService tenantService;

    @Inject
    private EasyEntityQuery easyEntityQuery;

    /**
     * 新增租户。
     *
     * @param dto 租户输入参数
     * @return 新增后的租户
     */
    @ApiOperation("新增租户")
    @Post
    @Mapping("/add")
    @SaCheckPermission("admin.tenant.add")
    public Result<TenantVO> add(@Body TenantDTO dto) {
        return Result.ok(TenantMapper.INSTANCE.toVO(tenantService.add(TenantMapper.INSTANCE.toEntity(dto))));
    }

    /**
     * 按编号查询租户。
     *
     * @param id 租户编号
     * @return 租户；不存在时返回 null
     */
    @ApiOperation("按编号查询租户")
    @Get
    @Mapping("/getById")
    @SaCheckPermission("admin.tenant.query")
    public Result<TenantVO> getById(@ApiParam("租户编号") @Param("id") String id) {
        return Result.ok(TenantMapper.INSTANCE.toVO(tenantService.getById(id)));
    }

    /**
     * 查询全部租户。
     *
     * @return 租户列表
     */
    @ApiOperation("查询全部租户")
    @Get
    @Mapping("/list")
    @SaCheckPermission("admin.tenant.query")
    public Result<List<TenantVO>> list() {
        return Result.ok(TenantMapper.INSTANCE.toVOList(tenantService.list()));
    }

    /**
     * 更新租户。
     *
     * @param dto 租户输入参数
     * @return 更新后的租户
     */
    @ApiOperation("更新租户")
    @Post
    @Mapping("/update")
    @SaCheckPermission("admin.tenant.update")
    public Result<TenantVO> update(@Body TenantDTO dto) {
        return Result.ok(TenantMapper.INSTANCE.toVO(tenantService.update(TenantMapper.INSTANCE.toEntity(dto))));
    }

    /**
     * 按编号删除租户。
     *
     * @param dto 编号参数
     * @return 操作结果
     */
    @ApiOperation("删除租户")
    @Post
    @Mapping("/remove")
    @SaCheckPermission("admin.tenant.remove")
    public Result<Void> remove(@Body IdDTO dto) {
        tenantService.removeById(dto.getId());
        return Result.ok();
    }

    /**
     * 分页查询租户。
     *
     * @param name   租户名称（可选，模糊匹配）
     * @param type   租户类型（可选）
     * @param status 状态（可选）
     * @return 分页结果
     */
    @ApiOperation("分页查询租户")
    @Get
    @Mapping("/query")
    @SaCheckPermission("admin.tenant.query")
    public PageResult<Tenant> query(@ApiParam("租户名称") @Param("name") String name,
                                    @ApiParam("租户类型（S=系统租户,U=用户租户,T=租户模板）") @Param("type") String type,
                                    @ApiParam("状态（E=启用,D=禁用）") @Param("status") String status) {
        return easyEntityQuery.queryable(Tenant.class)
                .where(o -> {
                    if (name != null && !name.isBlank()) {
                        o.name().like(name);
                    }
                    if (type != null && !type.isBlank()) {
                        o.type().eq(type);
                    }
                    if (status != null && !status.isBlank()) {
                        o.status().eq(status);
                    }
                })
                .orderBy(o -> o.id().asc())
                .toPageResult(PageQuery.current().toPager(Tenant.class));
    }
}

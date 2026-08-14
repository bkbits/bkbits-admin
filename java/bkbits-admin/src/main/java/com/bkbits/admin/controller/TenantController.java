package com.bkbits.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.bkbits.admin.mapper.TenantMapper;
import com.bkbits.admin.pojo.IdDTO;
import com.bkbits.admin.pojo.TenantAddDTO;
import com.bkbits.admin.pojo.TenantQueryDTO;
import com.bkbits.admin.pojo.TenantUpdateDTO;
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
import org.noear.solon.validation.annotation.Validated;

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
     * 分页查询租户。
     *
     * @param dto 查询参数
     * @return 分页结果
     */
    @ApiOperation("分页查询租户")
    @Get
    @Mapping("/query")
    @SaCheckPermission("admin.tenant.query")
    public PageResult<Tenant> query(TenantQueryDTO dto) {
        return easyEntityQuery.queryable(Tenant.class)
                .whereObject(dto)
                .orderBy(o -> o.id().asc())
                .toPageResult(PageQuery.current().toPager(Tenant.class));
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
     * 新增租户。
     *
     * @param dto 租户输入参数
     * @return 新增后的租户
     */
    @ApiOperation("新增租户")
    @Post
    @Mapping("/add")
    @SaCheckPermission("admin.tenant.add")
    public Result<Void> add(@Validated @Body TenantAddDTO dto) {
        tenantService.add(TenantMapper.INSTANCE.toEntity(dto));
        return Result.ok();
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
    public Result<Void> update(@Validated @Body TenantUpdateDTO dto) {
        tenantService.update(TenantMapper.INSTANCE.toEntity(dto));
        return Result.ok();
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
}

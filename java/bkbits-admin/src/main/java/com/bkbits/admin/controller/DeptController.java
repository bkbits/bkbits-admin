package com.bkbits.admin.controller;

import com.bkbits.admin.mapper.DeptMapper;
import com.bkbits.admin.pojo.DeptDTO;
import com.bkbits.admin.pojo.DeptVO;
import com.bkbits.admin.pojo.IdDTO;
import com.bkbits.admin.service.DeptService;
import com.bkbits.core.PageQuery;
import com.bkbits.core.PageResult;
import com.bkbits.core.Result;
import com.bkbits.dbo.entity.Dept;
import cn.dev33.satoken.annotation.SaCheckPermission;
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
 * 部门控制器。
 */
@Api("部门管理接口")
@Controller
@Mapping("/api/dept")
public class DeptController {

    @Inject
    private DeptService deptService;

    @Inject
    private EasyEntityQuery easyEntityQuery;

    /**
     * 新增部门。
     *
     * @param dto 部门输入参数
     * @return 新增后的部门
     */
    @ApiOperation("新增部门")
    @Post
    @Mapping("/add")
    @SaCheckPermission("admin.dept.add")
    public Result<DeptVO> add(@Body DeptDTO dto) {
        return Result.ok(DeptMapper.INSTANCE.toVO(deptService.add(DeptMapper.INSTANCE.toEntity(dto))));
    }

    /**
     * 按编号查询部门。
     *
     * @param deptId 部门编号
     * @return 部门；不存在时返回 null
     */
    @ApiOperation("按编号查询部门")
    @Get
    @Mapping("/getById")
    @SaCheckPermission("admin.dept.query")
    public Result<DeptVO> getById(@ApiParam("部门编号") @Param("deptId") String deptId) {
        return Result.ok(DeptMapper.INSTANCE.toVO(deptService.getById(deptId)));
    }

    /**
     * 查询指定租户下的部门。
     *
     * @param tenantId 租户编号
     * @return 部门列表
     */
    @ApiOperation("查询租户下部门")
    @Get
    @Mapping("/listByTenantId")
    @SaCheckPermission("admin.dept.query")
    public Result<List<DeptVO>> listByTenantId(@ApiParam("租户编号") @Param("tenantId") String tenantId) {
        return Result.ok(DeptMapper.INSTANCE.toVOList(deptService.listByTenantId(tenantId)));
    }

    /**
     * 查询指定父部门下的直属子部门。
     *
     * @param parentId 父部门编号；为空时查询顶级部门
     * @return 部门列表
     */
    @ApiOperation("查询子部门")
    @Get
    @Mapping("/listByParentId")
    @SaCheckPermission("admin.dept.query")
    public Result<List<DeptVO>> listByParentId(@ApiParam("父部门编号；为空时查询顶级部门") @Param("parentId") String parentId) {
        return Result.ok(DeptMapper.INSTANCE.toVOList(deptService.listByParentId(parentId)));
    }

    /**
     * 更新部门。
     *
     * @param dto 部门输入参数
     * @return 更新后的部门
     */
    @ApiOperation("更新部门")
    @Post
    @Mapping("/update")
    @SaCheckPermission("admin.dept.update")
    public Result<DeptVO> update(@Body DeptDTO dto) {
        return Result.ok(DeptMapper.INSTANCE.toVO(deptService.update(DeptMapper.INSTANCE.toEntity(dto))));
    }

    /**
     * 按编号删除部门。
     *
     * @param dto 编号参数
     * @return 操作结果
     */
    @ApiOperation("删除部门")
    @Post
    @Mapping("/remove")
    @SaCheckPermission("admin.dept.remove")
    public Result<Void> remove(@Body IdDTO dto) {
        deptService.removeById(dto.getId());
        return Result.ok();
    }

    /**
     * 分页查询部门。
     *
     * @param tenantId 租户编号（可选）
     * @param name     部门名称（可选，模糊匹配）
     * @param status   状态（可选）
     * @return 分页结果
     */
    @ApiOperation("分页查询部门")
    @Get
    @Mapping("/query")
    @SaCheckPermission("admin.dept.query")
    public PageResult<Dept> query(@ApiParam("租户编号") @Param("tenantId") String tenantId,
                                  @ApiParam("部门名称") @Param("name") String name,
                                  @ApiParam("状态（E=启用,D=禁用）") @Param("status") String status) {
        return easyEntityQuery.queryable(Dept.class)
                .where(o -> {
                    if (tenantId != null && !tenantId.isBlank()) {
                        o.tenantId().eq(tenantId);
                    }
                    if (name != null && !name.isBlank()) {
                        o.name().like(name);
                    }
                    if (status != null && !status.isBlank()) {
                        o.status().eq(status);
                    }
                })
                .orderBy(o -> {
                    o.parentId().asc();
                    o.sort().asc();
                    o.deptId().asc();
                })
                .toPageResult(PageQuery.current().toPager(Dept.class));
    }
}

package com.bkbits.admin.controller;

import com.bkbits.admin.mapper.DeptMapper;
import com.bkbits.admin.pojo.*;
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
import org.noear.solon.validation.annotation.Validated;

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
     * 分页查询部门。
     *
     * @param dto 查询参数
     * @return 分页结果
     */
    @ApiOperation("分页查询部门")
    @Get
    @Mapping("/query")
    @SaCheckPermission("admin.dept.query")
    public PageResult<Dept> query(DeptQueryDTO dto) {
        return easyEntityQuery.queryable(Dept.class)
                .whereObject(dto)
                .orderBy(o -> {
                    o.parentId().asc();
                    o.sort().asc();
                    o.deptId().asc();
                })
                .toPageResult(PageQuery.current().toPager(Dept.class));
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
     * 新增部门。
     *
     * @param dto 部门输入参数
     * @return 新增后的部门
     */
    @ApiOperation("新增部门")
    @Post
    @Mapping("/add")
    @SaCheckPermission("admin.dept.add")
    public Result<Void> add(@Validated @Body DeptAddDTO dto) {
        deptService.add(DeptMapper.INSTANCE.toEntity(dto));
        return Result.ok();
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
    public Result<Void> update(@Validated @Body DeptUpdateDTO dto) {
        deptService.update(DeptMapper.INSTANCE.toEntity(dto));
        return Result.ok();
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
    public Result<Void> remove(@Validated @Body IdDTO dto) {
        deptService.removeById(dto.getId());
        return Result.ok();
    }
}

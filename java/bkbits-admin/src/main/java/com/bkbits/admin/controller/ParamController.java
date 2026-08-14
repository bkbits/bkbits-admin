package com.bkbits.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.bkbits.admin.mapper.ParamMapper;
import com.bkbits.admin.pojo.IdDTO;
import com.bkbits.admin.pojo.ParamAddDTO;
import com.bkbits.admin.pojo.ParamQueryDTO;
import com.bkbits.admin.pojo.ParamUpdateDTO;
import com.bkbits.admin.pojo.ParamVO;
import com.bkbits.admin.service.ParamService;
import com.bkbits.core.PageQuery;
import com.bkbits.core.PageResult;
import com.bkbits.core.Result;
import com.easy.query.api.proxy.client.EasyEntityQuery;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.noear.solon.annotation.*;
import org.noear.solon.validation.annotation.Validated;

import java.util.List;

/**
 * 系统参数控制器。
 */
@Api("系统参数接口")
@Controller
@Mapping("/api/param")
public class ParamController {

    @Inject
    private ParamService paramService;

    @Inject
    private EasyEntityQuery easyEntityQuery;

    /**
     * 分页查询系统参数。
     *
     * @param dto 查询参数
     * @return 分页结果
     */
    @ApiOperation("分页查询系统参数")
    @Get
    @Mapping("/query")
    @SaCheckPermission("admin.param.query")
    public PageResult<ParamVO> query(ParamQueryDTO dto) {
        return easyEntityQuery.queryable(com.bkbits.dbo.entity.Param.class)
                .whereObject(dto)
                .orderBy(o -> {
                    o.sort().asc();
                    o.createTime().asc();
                })
                .selectAutoInclude(ParamVO.class)
                .toPageResult(PageQuery.current().toPager(ParamVO.class));
    }

    /**
     * 按参数键查询系统参数。
     *
     * @param paramKey 参数键
     * @return 系统参数；不存在时返回 null
     */
    @ApiOperation("按参数键查询系统参数")
    @Get
    @Mapping("/getByKey")
    @SaCheckPermission("admin.param.query")
    public Result<ParamVO> getByKey(@ApiParam("参数键") @Param("paramKey") String paramKey) {
        return Result.ok(ParamMapper.INSTANCE.toVO(paramService.getByKey(paramKey)));
    }

    /**
     * 按参数键查询系统参数。
     *
     * @param id 参数键
     * @return 系统参数；不存在时返回 null
     */
    @ApiOperation("按参数键查询系统参数")
    @Get
    @Mapping("/getById")
    @SaCheckPermission("admin.param.query")
    public Result<ParamVO> getById(@ApiParam("参数键") @Param("id") String id) {
        return Result.ok(ParamMapper.INSTANCE.toVO(paramService.getById(id)));
    }

    /**
     * 新增系统参数。
     *
     * @param dto 系统参数输入参数
     * @return 新增后的参数
     */
    @ApiOperation("新增系统参数")
    @Post
    @Mapping("/add")
    @SaCheckPermission("admin.param.add")
    public Result<Void> add(@Validated @Body ParamAddDTO dto) {
        paramService.add(ParamMapper.INSTANCE.toEntity(dto));
        return Result.ok();
    }

    /**
     * 更新系统参数。
     *
     * @param dto 系统参数输入参数
     * @return 更新后的参数
     */
    @ApiOperation("更新系统参数")
    @Post
    @Mapping("/update")
    @SaCheckPermission("admin.param.update")
    public Result<Void> update(@Validated @Body ParamUpdateDTO dto) {
        paramService.update(ParamMapper.INSTANCE.toEntity(dto));
        return Result.ok();
    }

    /**
     * 按编号删除系统参数。
     *
     * @param dto 编号参数
     * @return 操作结果
     */
    @ApiOperation("删除系统参数")
    @Post
    @Mapping("/remove")
    @SaCheckPermission("admin.param.remove")
    public Result<Void> remove(@Body IdDTO dto) {
        paramService.removeById(dto.getId());
        return Result.ok();
    }
}

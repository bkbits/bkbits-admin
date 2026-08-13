package com.bkbits.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.bkbits.admin.mapper.ParamMapper;
import com.bkbits.admin.pojo.IdDTO;
import com.bkbits.admin.pojo.ParamDTO;
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
     * 新增系统参数。
     *
     * @param dto 系统参数输入参数
     * @return 新增后的参数
     */
    @ApiOperation("新增系统参数")
    @Post
    @Mapping("/add")
    @SaCheckPermission("admin.param.add")
    public Result<ParamVO> add(@Body ParamDTO dto) {
        return Result.ok(ParamMapper.INSTANCE.toVO(paramService.add(ParamMapper.INSTANCE.toEntity(dto))));
    }

    /**
     * 按参数键查询系统参数。
     *
     * @param key 参数键
     * @return 系统参数；不存在时返回 null
     */
    @ApiOperation("按参数键查询系统参数")
    @Get
    @Mapping("/getByKey")
    @SaCheckPermission("admin.param.query")
    public Result<ParamVO> getByKey(@ApiParam("参数键") @Param("key") String key) {
        return Result.ok(ParamMapper.INSTANCE.toVO(paramService.getByKey(key)));
    }

    /**
     * 按参数键获取字符串值。
     *
     * @param key          参数键
     * @param defaultValue 默认值
     * @return 参数值；不存在或值为空时返回默认值
     */
    @ApiOperation("获取字符串参数值")
    @Get
    @Mapping("/getString")
    @SaCheckPermission("admin.param.query")
    public Result<String> getString(@ApiParam("参数键") @Param("key") String key,
                                    @ApiParam("默认值") @Param("defaultValue") String defaultValue) {
        return Result.ok(paramService.getString(key, defaultValue));
    }

    /**
     * 按参数键获取 int 值。
     *
     * @param key          参数键
     * @param defaultValue 默认值
     * @return 参数值；不存在或格式非法时返回默认值
     */
    @ApiOperation("获取 int 参数值")
    @Get
    @Mapping("/getInt")
    @SaCheckPermission("admin.param.query")
    public Result<Integer> getInt(@ApiParam("参数键") @Param("key") String key,
                                  @ApiParam("默认值") @Param("defaultValue") int defaultValue) {
        return Result.ok(paramService.getInt(key, defaultValue));
    }

    /**
     * 按参数键获取 long 值。
     *
     * @param key          参数键
     * @param defaultValue 默认值
     * @return 参数值；不存在或格式非法时返回默认值
     */
    @ApiOperation("获取 long 参数值")
    @Get
    @Mapping("/getLong")
    @SaCheckPermission("admin.param.query")
    public Result<Long> getLong(@ApiParam("参数键") @Param("key") String key,
                                @ApiParam("默认值") @Param("defaultValue") long defaultValue) {
        return Result.ok(paramService.getLong(key, defaultValue));
    }

    /**
     * 按参数键获取 double 值。
     *
     * @param key          参数键
     * @param defaultValue 默认值
     * @return 参数值；不存在或格式非法时返回默认值
     */
    @ApiOperation("获取 double 参数值")
    @Get
    @Mapping("/getDouble")
    @SaCheckPermission("admin.param.query")
    public Result<Double> getDouble(@ApiParam("参数键") @Param("key") String key,
                                    @ApiParam("默认值") @Param("defaultValue") double defaultValue) {
        return Result.ok(paramService.getDouble(key, defaultValue));
    }

    /**
     * 按参数键获取 boolean 值。
     *
     * @param key          参数键
     * @param defaultValue 默认值
     * @return 参数值；不存在或格式非法时返回默认值
     */
    @ApiOperation("获取 boolean 参数值")
    @Get
    @Mapping("/getBoolean")
    @SaCheckPermission("admin.param.query")
    public Result<Boolean> getBoolean(
            @ApiParam("参数键") @Param("key") String key,
            @ApiParam("默认值") @Param("defaultValue") boolean defaultValue) {
        return Result.ok(paramService.getBoolean(key, defaultValue));
    }

    /**
     * 查询全部系统参数。
     *
     * @return 系统参数列表
     */
    @ApiOperation("查询全部系统参数")
    @Get
    @Mapping("/list")
    @SaCheckPermission("admin.param.query")
    public Result<List<ParamVO>> list() {
        return Result.ok(ParamMapper.INSTANCE.toVOList(paramService.list()));
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
    public Result<ParamVO> update(@Body ParamDTO dto) {
        return Result.ok(ParamMapper.INSTANCE.toVO(paramService.update(ParamMapper.INSTANCE.toEntity(dto))));
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

    /**
     * 分页查询系统参数。
     *
     * @param key  参数键（可选，模糊匹配）
     * @param name 参数名称（可选，模糊匹配）
     * @param type 参数类型（可选）
     * @return 分页结果
     */
    @ApiOperation("分页查询系统参数")
    @Get
    @Mapping("/query")
    @SaCheckPermission("admin.param.query")
    public PageResult<com.bkbits.dbo.entity.Param> query(
            @ApiParam("参数键") @Param(value = "key", required = false) String key,
            @ApiParam("参数名称") @Param(value = "name", required = false) String name,
            @ApiParam("参数类型（S=系统参数,U=用户参数）") @Param(value = "type", required = false) String type) {
        return easyEntityQuery.queryable(com.bkbits.dbo.entity.Param.class)
                .where(o -> {
                    if (key != null && !key.isBlank()) {
                        o.paramKey().like(key);
                    }
                    if (name != null && !name.isBlank()) {
                        o.name().like(name);
                    }
                    if (type != null && !type.isBlank()) {
                        o.type().eq(type);
                    }
                })
                .orderBy(o -> {
                    o.sort().asc();
                    o.createTime().asc();
                })
                .toPageResult(PageQuery.current().toPager(com.bkbits.dbo.entity.Param.class));
    }
}

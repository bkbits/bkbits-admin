package com.bkbits.admin.controller;

import com.bkbits.admin.mapper.ParamMapper;
import com.bkbits.admin.pojo.IdDTO;
import com.bkbits.admin.pojo.ParamDTO;
import com.bkbits.admin.pojo.ParamVO;
import com.bkbits.admin.service.ParamService;
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
 * 系统参数控制器。
 */
@Controller
@Mapping("/api/param")
public class ParamController {


    @Inject
    private ParamService paramService;

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
    public Result<ParamVO> getByKey(@Param("key") String key) {
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
    public Result<String> getString(@Param("key") String key, @Param("defaultValue") String defaultValue) {
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
    public Result<Integer> getInt(@Param("key") String key, @Param("defaultValue") int defaultValue) {
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
    public Result<Long> getLong(@Param("key") String key, @Param("defaultValue") long defaultValue) {
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
    public Result<Double> getDouble(@Param("key") String key, @Param("defaultValue") double defaultValue) {
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
    public Result<Boolean> getBoolean(@Param("key") String key, @Param("defaultValue") boolean defaultValue) {
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
}

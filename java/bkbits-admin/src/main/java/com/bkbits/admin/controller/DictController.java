package com.bkbits.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.bkbits.admin.mapper.DictMapper;
import com.bkbits.admin.pojo.*;
import com.bkbits.admin.service.DictService;
import com.bkbits.core.PageQuery;
import com.bkbits.core.PageResult;
import com.bkbits.core.Result;
import com.bkbits.dbo.entity.Dict;
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
 * 系统字典控制器。
 */
@Api("系统字典接口")
@Controller
@Mapping("/api/dict")
public class DictController {

    @Inject
    private DictService dictService;

    @Inject
    private EasyEntityQuery easyEntityQuery;

    /**
     * 分页查询系统字典。
     *
     * @param dto 查询参数
     * @return 分页结果
     */
    @ApiOperation("分页查询系统字典")
    @Get
    @Mapping("/query")
    @SaCheckPermission("admin.dict.query")
    public PageResult<Dict> query(DictQueryDTO dto) {
        return easyEntityQuery.queryable(Dict.class)
                .whereObject(dto)
                .orderBy(o -> {
                    o.sort().asc();
                    o.createTime().asc();
                })
                .toPageResult(PageQuery.current().toPager(Dict.class));
    }

    /**
     * 按字典键查询字典及其字典值。
     *
     * @param dictKey 字典键
     * @return 字典；不存在时返回 null
     */
    @ApiOperation("按字典键查询字典")
    @Get
    @Mapping("/getByKey")
    @SaCheckPermission("admin.dict.query")
    public Result<DictVO> getByKey(@ApiParam("字典键") @Param("dictKey") String dictKey) {
        return Result.ok(DictMapper.INSTANCE.toDictVO(dictService.getByKey(dictKey)));
    }


    /**
     * 新增系统字典。
     *
     * @param dto 字典输入参数
     * @return 新增后的字典
     */
    @ApiOperation("新增系统字典")
    @Post
    @Mapping("/add")
    @SaCheckPermission("admin.dict.add")
    public Result<Void> add(@Validated @Body DictAddDTO dto) {
        dictService.add(DictMapper.INSTANCE.toDictEntity(dto));
        return Result.ok();
    }

    /**
     * 更新系统字典。
     *
     * @param dto 字典输入参数
     * @return 操作结果
     */
    @ApiOperation("更新系统字典")
    @Post
    @Mapping("/update")
    @SaCheckPermission("admin.dict.update")
    public Result<Void> update(@Validated @Body DictUpdateDTO dto) {
        dictService.update(DictMapper.INSTANCE.toDictEntity(dto));
        return Result.ok();
    }

    /**
     * 按编号删除字典及其全部字典值。
     *
     * @param dto 编号参数
     * @return 操作结果
     */
    @ApiOperation("删除系统字典")
    @Post
    @Mapping("/remove")
    @SaCheckPermission("admin.dict.remove")
    public Result<Void> remove(@Body IdDTO dto) {
        dictService.removeById(dto.getId());
        return Result.ok();
    }

    /**
     * 新增字典值。
     *
     * @param dto 字典值输入参数
     * @return 新增后的字典值
     */
    @ApiOperation("新增字典值")
    @Post
    @Mapping("/addValue")
    @SaCheckPermission("admin.dict.update")
    public Result<Void> addValue(@Validated @Body DictValueAddDTO dto) {
        dictService.addValue(DictMapper.INSTANCE.toDictValueEntity(dto));
        return Result.ok();
    }

    /**
     * 查询指定字典下的全部字典值。
     *
     * @param dictKey 字典键
     * @return 字典值列表
     */
    @ApiOperation("查询字典值列表")
    @Get
    @Mapping("/listValues")
    @SaCheckPermission("admin.dict.query")
    public Result<List<DictValueVO>> listValues(@ApiParam("字典键") @Param("dictKey") String dictKey) {
        return Result.ok(DictMapper.INSTANCE.toDictValueVOList(dictService.listValues(dictKey)));
    }

    /**
     * 更新字典值。
     *
     * @param dto 字典值输入参数
     * @return 更新后的字典值
     */
    @ApiOperation("更新字典值")
    @Post
    @Mapping("/updateValue")
    @SaCheckPermission("admin.dict.update")
    public Result<Void> updateValue(@Validated @Body DictValueUpdateDTO dto) {
        dictService.updateValue(DictMapper.INSTANCE.toDictValueEntity(dto));
        return Result.ok();
    }

    /**
     * 按编号删除字典值。
     *
     * @param dto 编号参数
     * @return 操作结果
     */
    @ApiOperation("删除字典值")
    @Post
    @Mapping("/removeValue")
    @SaCheckPermission("admin.dict.update")
    public Result<Void> removeValue(@Body IdDTO dto) {
        dictService.removeValueById(dto.getId());
        return Result.ok();
    }
}

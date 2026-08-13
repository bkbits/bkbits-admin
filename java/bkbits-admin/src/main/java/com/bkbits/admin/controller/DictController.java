package com.bkbits.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.bkbits.admin.mapper.DictMapper;
import com.bkbits.admin.pojo.DictDTO;
import com.bkbits.admin.pojo.DictValueDTO;
import com.bkbits.admin.pojo.DictValueVO;
import com.bkbits.admin.pojo.DictVO;
import com.bkbits.admin.pojo.IdDTO;
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
     * 新增系统字典。
     *
     * @param dto 字典输入参数
     * @return 新增后的字典
     */
    @ApiOperation("新增系统字典")
    @Post
    @Mapping("/add")
    @SaCheckPermission("admin.dict.add")
    public Result<DictVO> add(@Body DictDTO dto) {
        return Result.ok(DictMapper.INSTANCE.toDictVO(dictService.add(DictMapper.INSTANCE.toDictEntity(dto))));
    }

    /**
     * 按字典键查询字典及其字典值。
     *
     * @param key 字典键
     * @return 字典；不存在时返回 null
     */
    @ApiOperation("按字典键查询字典")
    @Get
    @Mapping("/getByKey")
    @SaCheckPermission("admin.dict.query")
    public Result<DictVO> getByKey(@ApiParam("字典键") @Param("key") String key) {
        return Result.ok(DictMapper.INSTANCE.toDictVO(dictService.getByKey(key)));
    }

    /**
     * 查询全部系统字典。
     *
     * @return 字典列表
     */
    @ApiOperation("查询全部字典")
    @Get
    @Mapping("/list")
    @SaCheckPermission("admin.dict.query")
    public Result<List<DictVO>> list() {
        return Result.ok(DictMapper.INSTANCE.toDictVOList(dictService.list()));
    }

    /**
     * 更新系统字典。
     *
     * @param dto 字典输入参数
     * @return 更新后的字典
     */
    @ApiOperation("更新系统字典")
    @Post
    @Mapping("/update")
    @SaCheckPermission("admin.dict.update")
    public Result<DictVO> update(@Body DictDTO dto) {
        return Result.ok(DictMapper.INSTANCE.toDictVO(dictService.update(DictMapper.INSTANCE.toDictEntity(dto))));
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
    @SaCheckPermission("admin.dictValue.add")
    public Result<DictValueVO> addValue(@Body DictValueDTO dto) {
        return Result.ok(DictMapper.INSTANCE.toDictValueVO(dictService.addValue(DictMapper.INSTANCE.toDictValueEntity(dto))));
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
    @SaCheckPermission("admin.dictValue.query")
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
    @SaCheckPermission("admin.dictValue.update")
    public Result<DictValueVO> updateValue(@Body DictValueDTO dto) {
        return Result.ok(DictMapper.INSTANCE.toDictValueVO(dictService.updateValue(DictMapper.INSTANCE.toDictValueEntity(dto))));
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
    @SaCheckPermission("admin.dictValue.remove")
    public Result<Void> removeValue(@Body IdDTO dto) {
        dictService.removeValueById(dto.getId());
        return Result.ok();
    }

    /**
     * 分页查询系统字典。
     *
     * @param key  字典键（可选，模糊匹配）
     * @param name 字典名称（可选，模糊匹配）
     * @param type 字典类型（可选）
     * @return 分页结果
     */
    @ApiOperation("分页查询系统字典")
    @Get
    @Mapping("/query")
    @SaCheckPermission("admin.dict.query")
    public PageResult<Dict> query(
            @ApiParam("字典键") @Param(value = "key", required = false) String key,
            @ApiParam("字典名称") @Param(value = "name", required = false) String name,
            @ApiParam("字典类型（S=系统字典,U=用户字典）") @Param(value = "type", required = false) String type) {
        return easyEntityQuery.queryable(Dict.class)
                .where(o -> {
                    if (key != null && !key.isBlank()) {
                        o.dictKey().like(key);
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
                .toPageResult(PageQuery.current().toPager(Dict.class));
    }
}

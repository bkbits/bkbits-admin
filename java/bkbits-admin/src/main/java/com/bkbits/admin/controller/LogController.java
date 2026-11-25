package com.bkbits.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.bkbits.admin.pojo.IdDTO;
import com.bkbits.core.PageQuery;
import com.bkbits.core.PageResult;
import com.bkbits.core.Result;
import com.bkbits.logging.dbo.LogLogin;
import com.bkbits.logging.dbo.LogOperation;
import com.bkbits.logging.pojo.LogLoginQueryDTO;
import com.bkbits.logging.pojo.LogOperationQueryDTO;
import com.bkbits.logging.service.LogService;
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
 * 日志控制器。
 */
@Api("日志接口")
@Controller
@Mapping("/api/sys/log")
public class LogController {

    @Inject
    private LogService logService;

    /**
     * 分页查询登录日志。
     *
     * @param dto 查询参数
     * @return 分页结果
     */
    @ApiOperation("分页查询登录日志")
    @Get
    @Mapping("/login/query")
    @SaCheckPermission("admin.loginLog.query")
    public PageResult<LogLogin> queryLoginLog(LogLoginQueryDTO dto) {
        return logService.pageLoginLogs(dto, PageQuery.current());
    }

    /**
     * 按编号查询登录日志。
     *
     * @param id 日志编号
     * @return 登录日志；不存在时返回 null
     */
    @ApiOperation("按编号查询登录日志")
    @Get
    @Mapping("/login/getById")
    @SaCheckPermission("admin.loginLog.query")
    public Result<LogLogin> getLoginLogById(@ApiParam("日志编号") @Param("id") String id) {
        return Result.ok(logService.getLoginLogById(id));
    }

    /**
     * 按编号删除登录日志。
     *
     * @param dto 编号参数
     * @return 操作结果
     */
    @ApiOperation("删除登录日志")
    @Post
    @Mapping("/login/remove")
    @SaCheckPermission("admin.loginLog.remove")
    public Result<Void> removeLoginLog(@Validated @Body IdDTO dto) {
        logService.removeLoginLogById(dto.getId());
        return Result.ok();
    }

    /**
     * 分页查询操作日志。
     *
     * @param dto 查询参数
     * @return 分页结果
     */
    @ApiOperation("分页查询操作日志")
    @Get
    @Mapping("/operation/query")
    @SaCheckPermission("admin.operLog.query")
    public PageResult<LogOperation> queryOperationLog(LogOperationQueryDTO dto) {
        return logService.pageOperationLogs(dto, PageQuery.current());
    }

    /**
     * 按编号查询操作日志。
     *
     * @param id 日志编号
     * @return 操作日志；不存在时返回 null
     */
    @ApiOperation("按编号查询操作日志")
    @Get
    @Mapping("/operation/getById")
    @SaCheckPermission("admin.operLog.query")
    public Result<LogOperation> getOperationLogById(@ApiParam("日志编号") @Param("id") String id) {
        return Result.ok(logService.getOperationLogById(id));
    }

    /**
     * 按编号删除操作日志。
     *
     * @param dto 编号参数
     * @return 操作结果
     */
    @ApiOperation("删除操作日志")
    @Post
    @Mapping("/operation/remove")
    @SaCheckPermission("admin.operLog.remove")
    public Result<Void> removeOperationLog(@Validated @Body IdDTO dto) {
        logService.removeOperationLogById(dto.getId());
        return Result.ok();
    }
}

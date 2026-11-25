package com.bkbits.logging.service;

import com.bkbits.core.PageQuery;
import com.bkbits.core.PageResult;
import com.bkbits.logging.dbo.LogLogin;
import com.bkbits.logging.dbo.LogOperation;
import com.bkbits.logging.pojo.LogLoginQueryDTO;
import com.bkbits.logging.pojo.LogOperationQueryDTO;

/**
 * 日志服务。
 *
 * <p>提供登录日志（{@link LogLogin}）与操作日志（{@link LogOperation}）的写入与查询。</p>
 */
public interface LogService {

    /**
     * 新增登录日志。
     *
     * @param logLogin 登录日志信息
     */
    void addLoginLog(LogLogin logLogin);

    /**
     * 按编号删除登录日志。
     *
     * @param id 日志编号
     */
    void removeLoginLogById(String id);

    /**
     * 按编号查询登录日志。
     *
     * @param id 日志编号
     * @return 登录日志；不存在时返回 {@code null}
     */
    LogLogin getLoginLogById(String id);

    /**
     * 分页查询登录日志，按登录时间倒序。
     *
     * @param query     查询条件，可为 {@code null} 表示不筛选
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    PageResult<LogLogin> pageLoginLogs(LogLoginQueryDTO query, PageQuery pageQuery);

    /**
     * 新增操作日志。
     *
     * @param logOperation 操作日志信息
     */
    void addOperationLog(LogOperation logOperation);

    /**
     * 按编号删除操作日志。
     *
     * @param id 日志编号
     */
    void removeOperationLogById(String id);

    /**
     * 按编号查询操作日志。
     *
     * @param id 日志编号
     * @return 操作日志；不存在时返回 {@code null}
     */
    LogOperation getOperationLogById(String id);

    /**
     * 分页查询操作日志，按创建时间倒序。
     *
     * @param query     查询条件，可为 {@code null} 表示不筛选
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    PageResult<LogOperation> pageOperationLogs(LogOperationQueryDTO query, PageQuery pageQuery);
}

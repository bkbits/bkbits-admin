package com.bkbits.logging.service.impl;

import com.bkbits.core.PageQuery;
import com.bkbits.core.PageResult;
import com.bkbits.logging.dbo.LogLogin;
import com.bkbits.logging.dbo.LogOperation;
import com.bkbits.logging.pojo.LogLoginQueryDTO;
import com.bkbits.logging.pojo.LogOperationQueryDTO;
import com.bkbits.logging.service.LogService;
import com.bkbits.orm.EasyPager;
import com.bkbits.util.ValidUtil;
import com.easy.query.api.proxy.client.EasyEntityQuery;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

@Component
public class LogServiceImpl implements LogService {

    @Inject
    EasyEntityQuery easyEntityQuery;

    @Override
    public void addLoginLog(LogLogin logLogin) {
        ValidUtil.requireNotNull(logLogin, "登录日志不能为空");
        ValidUtil.requireEquals(
                easyEntityQuery.insertable(logLogin).executeRows(),
                1,
                "新增登录日志失败");
    }

    @Override
    public void removeLoginLogById(String id) {
        easyEntityQuery.deletable(LogLogin.class)
                .whereById(ValidUtil.requireString(id, "登录日志编号不能为空"))
                .executeRows(1, "删除登录日志失败");
    }

    @Override
    public LogLogin getLoginLogById(String id) {
        return easyEntityQuery.queryable(LogLogin.class)
                .whereById(ValidUtil.requireString(id, "登录日志编号不能为空"))
                .singleOrNull();
    }

    @Override
    public PageResult<LogLogin> pageLoginLogs(LogLoginQueryDTO query, PageQuery pageQuery) {
        PageQuery checkedPage = ValidUtil.requireNotNull(pageQuery, "分页参数不能为空");
        return easyEntityQuery.queryable(LogLogin.class)
                .whereObject(query != null, query)
                .orderBy(o -> {
                    o.createTime().desc();
                    o.id().desc();
                })
                .toPageResult(EasyPager.of(checkedPage.getPage(), checkedPage.getPageSize()));
    }

    @Override
    public void addOperationLog(LogOperation logOperation) {
        ValidUtil.requireNotNull(logOperation, "操作日志不能为空");
        ValidUtil.requireEquals(
                easyEntityQuery.insertable(logOperation).executeRows(),
                1,
                "新增操作日志失败");
    }

    @Override
    public void removeOperationLogById(String id) {
        easyEntityQuery.deletable(LogOperation.class)
                .whereById(ValidUtil.requireString(id, "操作日志编号不能为空"))
                .executeRows(1, "删除操作日志失败");
    }

    @Override
    public LogOperation getOperationLogById(String id) {
        return easyEntityQuery.queryable(LogOperation.class)
                .whereById(ValidUtil.requireString(id, "操作日志编号不能为空"))
                .singleOrNull();
    }

    @Override
    public PageResult<LogOperation> pageOperationLogs(LogOperationQueryDTO query, PageQuery pageQuery) {
        PageQuery checkedPage = ValidUtil.requireNotNull(pageQuery, "分页参数不能为空");
        return easyEntityQuery.queryable(LogOperation.class)
                .whereObject(query != null, query)
                .orderBy(o -> {
                    o.createTime().desc();
                    o.id().desc();
                })
                .toPageResult(EasyPager.of(checkedPage.getPage(), checkedPage.getPageSize()));
    }
}

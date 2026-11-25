package com.bkbits.admin.controller;

import com.bkbits.admin.service.NotificationService;
import com.bkbits.auth.LoginUser;
import com.bkbits.auth.LoginUtil;
import com.bkbits.core.PageQuery;
import com.bkbits.core.PageResult;
import com.bkbits.dbo.entity.Notification;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;

/**
 * 通知控制器。
 */
@Api("通知接口")
@Controller
@Mapping("/api/notification")
public class NotificationController {

    @Inject
    private NotificationService notificationService;

    /**
     * 分页查询当前登录用户可接收的通知。
     *
     * @return 分页结果
     */
    @ApiOperation("分页查询当前用户通知")
    @Get
    @Mapping("/list")
    public PageResult<Notification> list() {
        LoginUser loginUser = LoginUtil.getLoginUser();
        return notificationService.pageForUser(
                loginUser.getUserId(),
                loginUser.getDeptId(),
                PageQuery.current());
    }
}

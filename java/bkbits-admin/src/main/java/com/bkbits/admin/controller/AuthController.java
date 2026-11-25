package com.bkbits.admin.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.bkbits.admin.AdminParamConstants;
import com.bkbits.admin.config.BkbitsAdminProperties;
import com.bkbits.admin.pojo.LoginDTO;
import com.bkbits.admin.service.ParamService;
import com.bkbits.admin.service.UserService;
import com.bkbits.auth.LoginUser;
import com.bkbits.auth.LoginUtil;
import com.bkbits.core.Result;
import com.bkbits.dbo.constants.BaseConstants;
import com.bkbits.dbo.entity.User;
import com.bkbits.encrypt.IPasswordEncrypt;
import com.bkbits.logging.annotations.Log;
import com.bkbits.logging.dbo.LogLogin;
import com.bkbits.logging.util.LogUtil;
import com.bkbits.util.StringUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.Context;
import org.noear.solon.validation.annotation.Validated;

import java.time.LocalDateTime;

/**
 * 认证控制器。
 */
@Api("认证接口")
@Controller
@Mapping("/api")
public class AuthController implements AdminParamConstants, BaseConstants {

    @Inject
    private IPasswordEncrypt passwordEncrypt;

    @Inject
    private ParamService paramService;

    @Inject
    private UserService userService;

    @Inject
    private BkbitsAdminProperties adminProperties;

    @ApiOperation(value = "获取rsa公钥", notes = "如果平台设置禁用rsa加密，该接口返回404错误")
    @Get
    @SaIgnore
    @Mapping("/publicKey")
    public Result<String> publicKey() {
        String publicKey = passwordEncrypt.getPublicKey();
        if (publicKey == null) {
            return Result.fail(404, "公钥不存在");
        }
        return Result.ok(publicKey);
    }

    @ApiOperation("登录")
    @Post
    @SaIgnore
    @Mapping("/login")
    public Result<LoginUser> login(
            @Validated @Body LoginDTO loginDTO
    ) {
        long startTime = System.currentTimeMillis();
        Context context = Context.current();
        String ip = context.realIp();
        String userAgent = context.userAgent();
        String failReason = null;
        String loginUserId = null;
        boolean succeed = false;

        try {
            String userName = StringUtil.trim(loginDTO.getUsername());
            User user = null;
            if (StringUtil.isNotEmpty(userName)) {
                user = userService.getByUserName(userName);
            }

            if (user == null && adminProperties.isLoginByPhone() &&
                    paramService.getBoolean(PARAM_LOGIN_PHONE, PARAM_LOGIN_PHONE_DEFAULT)) {
                user = userService.getByPhone(loginDTO.getPhone());
            }

            if (user == null && adminProperties.isLoginByEmail() &&
                    paramService.getBoolean(PARAM_LOGIN_EMAIL, PARAM_LOGIN_EMAIL_DEFAULT)) {
                user = userService.getByEmail(loginDTO.getEmail());
            }

            if (user == null) {
                failReason = "用户不存在或密码错误";
                return Result.fail(failReason);
            }

            if (!StringUtil.equals(user.getStatus(), STATUS_ENABLED)) {
                failReason = "用户被停用";
                return Result.fail(failReason);
            }

//            if (paramService.getBoolean(PARAM_LOGIN_CAPTCHA, PARAM_LOGIN_CAPTCHA_DEFAULT)) {
//                //TODO: 验证码验证
//            }

            String password = passwordEncrypt.decrypt(loginDTO.getPassword());
            if (!passwordEncrypt.match(password, user.getPassword())) {
                failReason = "用户不存在或密码错误";
                return Result.fail(failReason);
            }

            LoginUser loginUser = LoginUtil.login(
                    LoginUser.builder()
                            .userId(user.getUserId())
                            .userName(user.getUserName())
                            .loginTime(LocalDateTime.now())
                            .ip(ip)
                            .device("pc")
                            .deptId(user.getLastDeptId())
                            .build());

            succeed = true;
            loginUserId = user.getUserId();
            return Result.ok(loginUser);
        } catch (RuntimeException e) {
            failReason = e.getMessage();
            throw e;
        } finally {
            recordLoginLog(succeed, failReason, ip, userAgent, startTime, loginUserId);
        }
    }

    @ApiOperation("注销登录")
    @Post
    @Mapping("/logout")
    @Log(value = "退出登录", module = "认证", type = Log.LOGOUT)
    public Result<Void> logout() {
        LoginUtil.logout();
        return Result.ok();
    }

    @ApiOperation("获取登录用户")
    @Get
    @Mapping("/loginUser")
    public Result<LoginUser> getLoginUser() {
        return Result.ok(LoginUtil.getLoginUser());
    }

    /**
     * 记录登录日志，异常时忽略，不影响登录结果。
     *
     * @param succeed     是否成功
     * @param failReason  失败原因
     * @param ip          登录 ip
     * @param userAgent   UserAgent
     * @param startTime   开始时间戳（毫秒）
     * @param loginUserId 登录用户 id，失败时为 null
     */
    private void recordLoginLog(boolean succeed, String failReason, String ip, String userAgent,
                                long startTime, String loginUserId) {
        try {
            LogLogin logLogin = new LogLogin();
            logLogin.setSucceed(succeed);
            logLogin.setFailReason(failReason);
            logLogin.setIp(ip);
            logLogin.setUserAgent(userAgent);
            logLogin.setDevice("pc");
            logLogin.setCostTime(Math.toIntExact(System.currentTimeMillis() - startTime));
            logLogin.setCreateBy(loginUserId != null ? loginUserId : LoginUtil.getSystemLoginUserId());
            LogUtil.addLoginLog(logLogin);
        } catch (Throwable ignored) {
            // 写登录日志失败不影响登录返回
        }
    }
}

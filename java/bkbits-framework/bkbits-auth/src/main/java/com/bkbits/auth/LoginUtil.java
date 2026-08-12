package com.bkbits.auth;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.fun.SaFunction;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.session.SaTerminalInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.bkbits.core.PageQuery;
import com.bkbits.core.PageResult;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 登录工具类，基于 sa-token {@link StpUtil} 封装登录、会话与权限校验操作。
 *
 * <p>登录用户的详细信息（{@link LoginUser}）保存于 token 会话中，
 * 可通过 {@link #getLoginUser()} 等方法获取。</p>
 */
@UtilityClass
public class LoginUtil {

    /**
     * 登录用户在会话中的属性键
     */
    private static final String LOGIN_USER_KEY = "LoginUser";

    /**
     * 当前会话登录指定用户，并将用户信息存入会话。
     *
     * @param loginUser 登录用户信息
     */
    public LoginUser login(@NotNull LoginUser loginUser) {
        StpUtil.login(loginUser.getUserId());
        LoginUser stored = LoginUser.builder()
                .token(StpUtil.getTokenValue())
                .userId(loginUser.getUserId())
                .userName(loginUser.getUserName())
                .loginTime(loginUser.getLoginTime())
                .ip(loginUser.getIp())
                .device(loginUser.getDevice())
                .deptId(loginUser.getDeptId())
                .tenantId(loginUser.getTenantId())
                .build();
        StpUtil.getTokenSession().set(LOGIN_USER_KEY, stored);
        return stored;
    }

    /**
     * 当前会话注销登录。
     */
    public void logout() {
        checkLogin();
        StpUtil.logout();
    }

    /**
     * 按 token 将对应会话踢下线。
     *
     * @param token 会话 token
     */
    public void kickout(@NotNull String token) {
        StpUtil.kickoutByTokenValue(token);
    }

    /**
     * 获取当前登录用户 ID，未登录抛出 {@link NotLoginException}。
     *
     * @return 登录用户 ID
     * @throws SaTokenException 会话中不存在登录用户信息时抛出
     */
    public @NotNull String getLoginUserId() {
        checkLogin();
        return StpUtil.getLoginIdAsString();
    }

    /**
     * 获取当前登录用户名，未登录抛出 {@link NotLoginException}。
     *
     * @return 登录用户名
     * @throws SaTokenException 会话中不存在登录用户信息时抛出
     */
    public @NotNull String getLoginUserName() {
        return getLoginUser().getUserName();
    }

    /**
     * 获取当前登录用户，未登录抛出 {@link NotLoginException}。
     *
     * @return 登录用户信息
     * @throws SaTokenException 会话中不存在登录用户信息时抛出
     */
    public @NotNull LoginUser getLoginUser() {
        checkLogin();
        LoginUser loginUser = (LoginUser) StpUtil.getStpLogic()
                .getTokenSession(false)
                .get(LOGIN_USER_KEY);
        if (loginUser == null) {
            throw new SaTokenException("会话中不存在登录用户信息");
        }
        return loginUser;
    }

    /**
     * 更新当前登录用户信息（写入当前 token 会话）。
     *
     * @param loginUser 新的登录用户信息
     */
    public void updateLoginUser(LoginUser loginUser) {
        checkLogin();
        StpUtil.getStpLogic().getTokenSession(false).set(LOGIN_USER_KEY, loginUser);
    }

    /**
     * 按 token 获取登录用户，token 无效或会话中无用户信息时返回 {@code null}。
     *
     * @param token 会话 token
     * @return 登录用户信息或 null
     */
    public @Nullable LoginUser getLoginUserByToken(@NotNull String token) {
        if (token.isBlank()) {
            return null;
        }
        Object loginId;
        try {
            loginId = StpUtil.getLoginIdByToken(token);
        } catch (Exception e) {
            return null;
        }
        if (loginId == null) {
            return null;
        }
        SaSession session = StpUtil.getSessionByLoginId(loginId, false);
        if (session == null) {
            return null;
        }
        return (LoginUser) session.get(LOGIN_USER_KEY);
    }

    /**
     * 按用户 id 获取登录用户列表（同一账号多端登录时可能返回多条）。
     *
     * @param userId 用户 id
     * @param device 登录设备，为空时不限设备
     * @return 登录用户列表；账号未登录或会话中无用户信息时返回 {@code null}
     */
    public @Nullable List<LoginUser> getLoginUserByUserId(@NotNull Object userId, @Nullable String device) {
        List<SaTerminalInfo> infos = StpUtil.getTerminalListByLoginId(userId, device);
        return infos.stream()
                .map(info -> {
                    SaSession session = StpUtil.getStpLogic().getTokenSessionByToken(info.getTokenValue(), false);
                    if (session == null) {
                        return null;
                    }

                    Object loginUser = session.get(LOGIN_USER_KEY);
                    if (!(loginUser instanceof LoginUser)) {
                        return null;
                    }
                    return (LoginUser) loginUser;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 判断当前会话是否已登录。
     *
     * @return 已登录返回 true
     */
    public boolean isLogin() {
        return StpUtil.isLogin();
    }

    /**
     * 校验当前会话是否已登录，未登录抛出 {@link NotLoginException}。
     */
    public void checkLogin() {
        StpUtil.checkLogin();
    }

    /**
     * 临时切换当前会话登录身份，执行完 {@code function} 后自动切回原身份。
     *
     * @param userId   临时切换的账号 id
     * @param function 切换期间执行的操作
     */
    public void switchTo(Object userId, SaFunction function) {
        StpUtil.switchTo(userId, function);
    }

    /**
     * 临时切换当前会话登录身份，需在操作完成后调用 {@link #endSwitch()} 切回。
     *
     * @param userId 临时切换的账号 id
     */
    public void switchTo(Object userId) {
        StpUtil.switchTo(userId);
    }

    /**
     * 结束身份切换，恢复为原登录身份。
     */
    public void endSwitch() {
        StpUtil.endSwitch();
    }

    /**
     * 分页查询在线登录用户。
     *
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    public PageResult<LoginUser> getLoginUser(@NotNull PageQuery pageQuery) {
        List<String> tokenList = StpUtil.searchTokenValue(
                "",
                0,
                -1,
                false
        );

        List<LoginUser> userList = tokenList.stream()
                .skip((pageQuery.getPage() - 1) * pageQuery.getPageSize())
                .limit(pageQuery.getPageSize())
                .map(LoginUtil::getLoginUserByToken)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return PageResult.page(tokenList.size(), userList);
    }

    /**
     * 判断当前登录用户是否同时拥有全部指定权限。
     *
     * @param permissions 权限标识数组
     * @return 全部拥有返回 true
     */
    public boolean hasPermission(String... permissions) {
        return StpUtil.hasPermissionAnd(permissions);
    }

    /**
     * 判断当前登录用户是否拥有任一指定权限。
     *
     * @param permissions 权限标识数组
     * @return 拥有任一返回 true
     */
    public boolean hasPermissionOr(String... permissions) {
        return StpUtil.hasPermissionOr(permissions);
    }

    /**
     * 校验当前登录用户是否同时拥有全部指定权限，不满足抛出异常。
     *
     * @param permissions 权限标识数组
     */
    public void checkPermission(String... permissions) {
        StpUtil.checkPermissionAnd(permissions);
    }

    /**
     * 校验当前登录用户是否拥有任一指定权限，不满足抛出异常。
     *
     * @param permissions 权限标识数组
     */
    public void checkPermissionOr(String... permissions) {
        StpUtil.checkPermissionOr(permissions);
    }

    /**
     * 判断当前登录用户是否同时拥有全部指定角色。
     *
     * @param roles 角色标识数组
     * @return 全部拥有返回 true
     */
    public boolean hasRole(String... roles) {
        return StpUtil.hasRoleAnd(roles);
    }

    /**
     * 判断当前登录用户是否拥有任一指定角色。
     *
     * @param roles 角色标识数组
     * @return 拥有任一返回 true
     */
    public boolean hasRoleOr(String... roles) {
        return StpUtil.hasRoleOr(roles);
    }

    /**
     * 校验当前登录用户是否同时拥有全部指定角色，不满足抛出异常。
     *
     * @param roles 角色标识数组
     */
    public void checkRole(String... roles) {
        StpUtil.checkRoleAnd(roles);
    }

    /**
     * 校验当前登录用户是否拥有任一指定角色，不满足抛出异常。
     *
     * @param roles 角色标识数组
     */
    public void checkRoleOr(String... roles) {
        StpUtil.checkRoleOr(roles);
    }
}

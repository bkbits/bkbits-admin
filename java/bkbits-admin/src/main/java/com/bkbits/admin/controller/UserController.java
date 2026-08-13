package com.bkbits.admin.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.bkbits.admin.mapper.UserMapper;
import com.bkbits.admin.pojo.*;
import com.bkbits.admin.service.PermissionService;
import com.bkbits.admin.service.UserService;
import com.bkbits.auth.LoginUser;
import com.bkbits.auth.LoginUtil;
import com.bkbits.core.PageQuery;
import com.bkbits.core.PageResult;
import com.bkbits.core.Result;
import com.bkbits.dbo.entity.Role;
import com.bkbits.dbo.entity.User;
import com.bkbits.encrypt.IPasswordEncrypt;
import com.bkbits.util.StringUtil;
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
 * 用户控制器。
 */
@Api("用户接口")
@Controller
@Mapping("/api/user")
public class UserController {

    @Inject
    private EasyEntityQuery easyEntityQuery;

    @Inject
    private UserService userService;

    @Inject
    private PermissionService permissionService;

    @Inject
    private IPasswordEncrypt passwordEncrypt;


    /**
     * 分页查询在线用户。
     *
     * @param dto 查询参数
     * @return 分页结果
     */
    @ApiOperation("查询在线用户")
    @Post
    @Mapping("/queryLoginUser")
    @SaCheckPermission("admin.user.queryLoginUser")
    public PageResult<LoginUser> queryLoginUser(@Body UserAddDTO dto) {
        return LoginUtil.pageLoginUser(PageQuery.current());
    }

    /**
     * 按编号查询用户。
     *
     * @param userId 用户编号
     * @return 用户；不存在时返回 null
     */
    @ApiOperation("按编号查询用户")
    @Get
    @Mapping("/getByUserId")
    @SaCheckPermission("admin.user.query")
    public Result<UserVO> getByUserId(@ApiParam("用户编号") @Param("userId") String userId) {
        return Result.ok(UserMapper.INSTANCE.toVO(userService.getByUserId(userId)));
    }

    /**
     * 按用户名查询用户。
     *
     * @param userName 用户名
     * @return 用户；不存在时返回 null
     */
    @ApiOperation("按用户名查询用户")
    @Get
    @Mapping("/getByUserName")
    @SaCheckPermission("admin.user.query")
    public Result<UserVO> getByUserName(@ApiParam("用户名") @Param("userName") String userName) {
        return Result.ok(UserMapper.INSTANCE.toVO(userService.getByUserName(userName)));
    }

    /**
     * 按手机号查询用户。
     *
     * @param phone 手机号
     * @return 用户；不存在时返回 null
     */
    @ApiOperation("按手机号查询用户")
    @Get
    @Mapping("/getByPhone")
    @SaCheckPermission("admin.user.query")
    public Result<UserVO> getByPhone(@ApiParam("手机号") @Param("phone") String phone) {
        return Result.ok(UserMapper.INSTANCE.toVO(userService.getByPhone(phone)));
    }

    /**
     * 按邮箱查询用户。
     *
     * @param email 邮箱
     * @return 用户；不存在时返回 null
     */
    @ApiOperation("按邮箱查询用户")
    @Get
    @Mapping("/getByEmail")
    @SaCheckPermission("admin.user.query")
    public Result<UserVO> getByEmail(@ApiParam("邮箱") @Param("email") String email) {
        return Result.ok(UserMapper.INSTANCE.toVO(userService.getByEmail(email)));
    }

    /**
     * 新增用户。
     *
     * @param dto 用户输入参数
     * @return 新增后的用户
     */
    @ApiOperation("新增用户")
    @Post
    @Mapping("/add")
    @SaCheckPermission("admin.user.add")
    public Result<UserVO> add(@Validated @Body UserAddDTO dto) {
        User user = UserMapper.INSTANCE.toAddEntity(dto);
        String password = passwordEncrypt.decrypt(user.getPassword());
        password = passwordEncrypt.hash(password);
        user.setPassword(password);
        return Result.ok(UserMapper.INSTANCE.toVO(userService.add(user)));
    }

    /**
     * 更新用户。
     *
     * @param dto 用户输入参数
     * @return 更新后的用户
     */
    @ApiOperation("更新用户")
    @Post
    @Mapping("/update")
    @SaCheckPermission("admin.user.update")
    public Result<UserVO> update(@Validated @Body UserUpdateDTO dto) {
        return Result.ok(UserMapper.INSTANCE.toVO(userService.update(UserMapper.INSTANCE.toUpdateEntity(dto))));
    }

    /**
     * 更新当前登录用户密码。
     *
     * @param dto 密码更新参数
     * @return 操作结果
     */
    @ApiOperation("更新密码")
    @Post
    @Mapping("/updateMyPassword")
    public Result<Void> updateMyPassword(@Validated @Body UserUpdateMyPasswordDTO dto) {
        String userId = LoginUtil.getLoginUserId();
        User user = userService.getByUserId(userId);
        if (!passwordEncrypt.match(dto.getOldPassword(), user.getPassword())) {
            return Result.fail("原密码错误");
        }

        userService.updatePassword(userId, dto.getPassword());
        return Result.ok();
    }

    /**
     * 更新当前登录用户密码。
     *
     * @param dto 密码更新参数
     * @return 操作结果
     */
    @ApiOperation("重置密码")
    @SaCheckPermission("admin.user.resetPassword")
    @Post
    @Mapping("/resetPassword")
    public Result<Void> resetPassword(@Validated @Body UserResetPasswordDTO dto) {
        String userId = LoginUtil.getLoginUserId();
        if (StringUtil.equals(dto.getUserId(), userId)) {
            return Result.fail("不能重置自身账号密码");
        }
        userService.updatePassword(dto.getUserId(), dto.getPassword());
        return Result.ok();
    }

    /**
     * 按编号删除用户及其角色关联。
     *
     * @param dto 编号参数
     * @return 操作结果
     */
    @ApiOperation("删除用户")
    @Post
    @Mapping("/remove")
    @SaCheckPermission("admin.user.remove")
    public Result<Void> remove(@Body IdDTO dto) {
        userService.removeById(dto.getId());
        return Result.ok();
    }

    /**
     * 分页查询用户。
     *
     * @param dto 查询参数
     * @return 分页结果
     */
    @ApiOperation("分页查询用户")
    @Get
    @Mapping("/query")
    @SaCheckPermission("admin.user.query")
    public PageResult<User> query(
            @Validated UserQueryDTO dto
    ) {
        return easyEntityQuery.queryable(User.class)
                .where(o -> {
                    o.userId().eq(StringUtil.isNotBlank(dto.getUserId()), dto.getUserId());
                    o.tenantId().eq(StringUtil.isNotBlank(dto.getTenantId()), dto.getTenantId());
                    o.deptId().eq(StringUtil.isNotBlank(dto.getDeptId()), dto.getDeptId());
                    o.userName().like(StringUtil.isNotBlank(dto.getUserName()), dto.getUserName());
                    o.realName().like(StringUtil.isNotBlank(dto.getRealName()), dto.getRealName());
                    o.phone().like(StringUtil.isNotBlank(dto.getPhone()), dto.getPhone());
                    o.email().like(StringUtil.isNotBlank(dto.getEmail()), dto.getEmail());
                    o.sex().eq(StringUtil.isNotBlank(dto.getSex()), dto.getSex());
                    o.status().eq(StringUtil.isNotBlank(dto.getStatus()), dto.getStatus());
                })
                .orderBy(o -> o.createTime().desc())
                .toPageResult(PageQuery.current().toPager(User.class));
    }

    /**
     * 使用给定角色集合替换用户现有的全部角色绑定。
     *
     * @param dto 用户绑定角色参数
     * @return 操作结果
     */
    @ApiOperation("绑定角色到用户")
    @Post
    @Mapping("/bindRole")
    @SaCheckPermission("admin.user.bind")
    public Result<Void> bindRolesToUser(@Validated @Body BindRolesToUserDTO dto) {
        permissionService.bindRolesToUser(dto.getUserId(), dto.getRoleIds());
        return Result.ok();
    }

    /**
     * 查询用户所有角色
     *
     * @param userId 用户id
     * @return 操作结果
     */
    @ApiOperation("查询用户所有角色")
    @Get
    @Mapping("/listRoles")
    @SaCheckPermission("admin.user.bind")
    public Result<List<Role>> listRoles(@ApiParam("用户id") @Param("userId") String userId) {
        return Result.ok(permissionService.listRolesByUserId(userId));
    }
}

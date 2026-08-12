package com.bkbits.admin.controller;

import com.bkbits.admin.mapper.UserMapper;
import com.bkbits.admin.pojo.IdDTO;
import com.bkbits.admin.pojo.UserDTO;
import com.bkbits.admin.pojo.UserVO;
import com.bkbits.admin.service.UserService;
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
 * 用户控制器。
 */
@Controller
@Mapping("/api/user")
public class UserController {


    @Inject
    private UserService userService;

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
    public Result<UserVO> add(@Body UserDTO dto) {
        return Result.ok(UserMapper.INSTANCE.toVO(userService.add(UserMapper.INSTANCE.toEntity(dto))));
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
    public Result<UserVO> getByUserId(@Param("userId") String userId) {
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
    public Result<UserVO> getByUserName(@Param("userName") String userName) {
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
    public Result<UserVO> getByPhone(@Param("phone") String phone) {
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
    public Result<UserVO> getByEmail(@Param("email") String email) {
        return Result.ok(UserMapper.INSTANCE.toVO(userService.getByEmail(email)));
    }

    /**
     * 查询指定租户下的用户。
     *
     * @param tenantId 租户编号
     * @return 用户列表
     */
    @ApiOperation("查询租户下用户")
    @Get
    @Mapping("/listByTenantId")
    @SaCheckPermission("admin.user.query")
    public Result<List<UserVO>> listByTenantId(@Param("tenantId") String tenantId) {
        return Result.ok(UserMapper.INSTANCE.toVOList(userService.listByTenantId(tenantId)));
    }

    /**
     * 查询指定部门下的用户。
     *
     * @param deptId 部门编号
     * @return 用户列表
     */
    @ApiOperation("查询部门下用户")
    @Get
    @Mapping("/listByDeptId")
    @SaCheckPermission("admin.user.query")
    public Result<List<UserVO>> listByDeptId(@Param("deptId") String deptId) {
        return Result.ok(UserMapper.INSTANCE.toVOList(userService.listByDeptId(deptId)));
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
    public Result<UserVO> update(@Body UserDTO dto) {
        return Result.ok(UserMapper.INSTANCE.toVO(userService.update(UserMapper.INSTANCE.toEntity(dto))));
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
}

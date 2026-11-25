package com.bkbits.admin.service;

import com.bkbits.admin.pojo.UserQueryDTO;
import com.bkbits.admin.pojo.UserVO;
import com.bkbits.core.PageQuery;
import com.bkbits.core.PageResult;
import com.bkbits.dbo.entity.User;

import java.util.List;

/**
 * 用户服务。
 */
public interface UserService {

    /**
     * 新增用户，并绑定所属部门。
     *
     * @param user   用户信息
     * @param deptId 部门编号，可为空
     */
    void add(User user, String deptId);

    /**
     * 按编号查询用户及其角色。
     *
     * @param userId 用户编号
     * @return 用户；不存在时返回 {@code null}
     */
    User getByUserId(String userId);

    /**
     * 按用户名查询用户。
     *
     * @param userName 用户名
     * @return 用户；不存在时返回 {@code null}
     */
    User getByUserName(String userName);

    /**
     * 按手机号查询用户。
     *
     * @param phone 手机号
     * @return 用户；不存在时返回 {@code null}
     */
    User getByPhone(String phone);

    /**
     * 按邮箱查询用户。
     *
     * @param email 邮箱
     * @return 用户；不存在时返回 {@code null}
     */
    User getByEmail(String email);

    /**
     * 查询指定部门下的用户。
     *
     * @param deptId 部门编号
     * @return 用户列表
     */
    List<User> listByDeptId(String deptId);

    /**
     * 分页查询用户（含所属部门）。
     *
     * @param query     查询条件，可为 {@code null}
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    PageResult<UserVO> pageUsers(UserQueryDTO query, PageQuery pageQuery);

    /**
     * 查询用户所属部门编号。
     *
     * @param userId 用户编号
     * @return 部门编号；未绑定部门时返回 {@code null}
     */
    String getDeptIdByUserId(String userId);

    /**
     * 更新用户。
     *
     * @param user   待更新的用户信息
     * @param deptId 部门编号；{@code null} 表示不修改，空字符串表示清空
     */
    void update(User user, String deptId);

    /**
     * 修改密码
     *
     * @param userId   用户id
     * @param password 新密码
     */
    void updatePassword(String userId, String password);

    /**
     * 按编号删除用户及其角色关联。
     *
     * @param userId 用户编号
     */
    void removeById(String userId);
}

package com.bkbits.admin.service;

import com.bkbits.dbo.entity.User;

import java.util.List;

/**
 * 用户服务。
 */
public interface UserService {

    /**
     * 新增用户。
     *
     * @param user 用户信息
     * @return 新增后的用户
     */
    User add(User user);

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
     * 查询指定租户下的用户。
     *
     * @param tenantId 租户编号
     * @return 用户列表
     */
    List<User> listByTenantId(String tenantId);

    /**
     * 查询指定部门下的用户。
     *
     * @param deptId 部门编号
     * @return 用户列表
     */
    List<User> listByDeptId(String deptId);

    /**
     * 更新用户。
     *
     * @param user 待更新的用户信息
     * @return 更新后的用户
     */
    User update(User user);

    /**
     * 按编号删除用户及其角色关联。
     *
     * @param userId 用户编号
     */
    void removeById(String userId);
}

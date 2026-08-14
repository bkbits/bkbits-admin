package com.bkbits.admin.service.impl;

import com.bkbits.admin.service.UserService;
import com.bkbits.dbo.entity.User;
import com.bkbits.dbo.entity.UserRoleRel;
import com.bkbits.encrypt.IPasswordEncrypt;
import com.bkbits.util.ValidUtil;
import com.easy.query.api.proxy.client.EasyEntityQuery;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.annotation.Transaction;

import java.util.List;

@Component
public class UserServiceImpl implements UserService {

    @Inject
    private EasyEntityQuery easyEntityQuery;

    @Inject
    private IPasswordEncrypt passwordEncrypt;

    @Override
    public User add(User user) {
        ValidUtil.requireNotNull(user, "用户不能为空");
        if (easyEntityQuery.insertable(user).executeRows() != 1) {
            throw new IllegalStateException("创建用户失败");
        }
        return user;
    }

    @Override
    public User getByUserId(String userId) {
        return easyEntityQuery.queryable(User.class)
                .include(o -> o.roleList())
                .whereById(ValidUtil.requireString(userId, "用户编号不能为空"))
                .singleOrNull();
    }

    @Override
    public User getByUserName(String userName) {
        return easyEntityQuery.queryable(User.class)
                .where(o -> o.userName().eq(ValidUtil.requireString(userName, "用户名不能为空")))
                .singleOrNull();
    }

    @Override
    public User getByPhone(String phone) {
        return easyEntityQuery.queryable(User.class)
                .where(o -> o.phone().eq(ValidUtil.requireString(phone, "手机号不能为空")))
                .singleOrNull();
    }

    @Override
    public User getByEmail(String email) {
        return easyEntityQuery.queryable(User.class)
                .where(o -> o.email().eq(ValidUtil.requireString(email, "邮箱不能为空")))
                .singleOrNull();
    }

    @Override
    public List<User> listByTenantId(String tenantId) {
        return easyEntityQuery.queryable(User.class)
                .where(o -> o.tenantId().eq(ValidUtil.requireString(tenantId, "租户编号不能为空")))
                .orderBy(o -> o.userId().asc())
                .toList();
    }

    @Override
    public List<User> listByDeptId(String deptId) {
        return easyEntityQuery.queryable(User.class)
                .where(o -> o.deptId().eq(ValidUtil.requireString(deptId, "部门编号不能为空")))
                .orderBy(o -> o.userId().asc())
                .toList();
    }

    @Override
    public User update(User user) {
        ValidUtil.requireNotNull(user, "用户不能为空");
        ValidUtil.requireString(user.getUserId(), "用户编号不能为空");
        easyEntityQuery.updatable(user)
                .executeRows(1, "更新用户失败");
        return user;
    }

    @Override
    public void updatePassword(String userId, String password) {
        ValidUtil.requireNotNull(userId, "用户编号不能为空");
        ValidUtil.requireNotNull(password, "密码不能为空");

        String passwordHash = passwordEncrypt.hash(password);

        easyEntityQuery.updatable(User.class)
                .whereById(userId)
                .setColumns(u -> u.password().set(passwordHash))
                .executeRows(1, "找不到用户: " + userId);
    }

    @Override
    @Transaction
    public void removeById(String userId) {
        String checkedUserId = ValidUtil.requireString(userId, "用户编号不能为空");
        easyEntityQuery.deletable(UserRoleRel.class)
                .where(o -> o.userId().eq(checkedUserId))
                .executeRows();
        easyEntityQuery.deletable(User.class)
                .whereById(checkedUserId)
                .executeRows(1, "删除用户失败");
    }

}

package com.bkbits.admin.service.impl;

import com.bkbits.admin.service.UserService;
import com.bkbits.dbo.entity.User;
import com.bkbits.dbo.entity.UserRoleRel;
import com.easy.query.api.proxy.client.EasyEntityQuery;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.annotation.Transaction;

import java.util.List;
import java.util.Objects;

@Component
public class UserServiceImpl implements UserService {

    @Inject
    EasyEntityQuery easyEntityQuery;

    @Override
    public User add(User user) {
        Objects.requireNonNull(user, "用户不能为空");
        if (easyEntityQuery.insertable(user).executeRows() != 1) {
            throw new IllegalStateException("创建用户失败");
        }
        return user;
    }

    @Override
    public User getByUserId(String userId) {
        return easyEntityQuery.queryable(User.class)
                .include(o -> o.roleList())
                .whereById(requireText(userId, "用户编号"))
                .singleOrNull();
    }

    @Override
    public User getByUserName(String userName) {
        return easyEntityQuery.queryable(User.class)
                .where(o -> o.userName().eq(requireText(userName, "用户名")))
                .singleOrNull();
    }

    @Override
    public User getByPhone(String phone) {
        return easyEntityQuery.queryable(User.class)
                .where(o -> o.phone().eq(requireText(phone, "手机号")))
                .singleOrNull();
    }

    @Override
    public User getByEmail(String email) {
        return easyEntityQuery.queryable(User.class)
                .where(o -> o.email().eq(requireText(email, "邮箱")))
                .singleOrNull();
    }

    @Override
    public List<User> listByTenantId(String tenantId) {
        return easyEntityQuery.queryable(User.class)
                .where(o -> o.tenantId().eq(requireText(tenantId, "租户编号")))
                .orderBy(o -> o.userId().asc())
                .toList();
    }

    @Override
    public List<User> listByDeptId(String deptId) {
        return easyEntityQuery.queryable(User.class)
                .where(o -> o.deptId().eq(requireText(deptId, "部门编号")))
                .orderBy(o -> o.userId().asc())
                .toList();
    }

    @Override
    public User update(User user) {
        Objects.requireNonNull(user, "用户不能为空");
        requireText(user.getUserId(), "用户编号");
        easyEntityQuery.updatable(user)
                .executeRows(1, "更新用户失败");
        return user;
    }

    @Override
    @Transaction
    public void removeById(String userId) {
        String checkedUserId = requireText(userId, "用户编号");
        easyEntityQuery.deletable(UserRoleRel.class)
                .where(o -> o.userId().eq(checkedUserId))
                .executeRows();
        easyEntityQuery.deletable(User.class)
                .whereById(checkedUserId)
                .executeRows(1, "删除用户失败");
    }

    private String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + "不能为空");
        }
        return value;
    }
}

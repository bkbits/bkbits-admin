package com.bkbits.admin.service.impl;

import com.bkbits.admin.mapper.UserMapper;
import com.bkbits.admin.pojo.UserQueryDTO;
import com.bkbits.admin.pojo.UserVO;
import com.bkbits.admin.service.UserService;
import com.bkbits.core.PageQuery;
import com.bkbits.core.PageResult;
import com.bkbits.dbo.entity.User;
import com.bkbits.dbo.entity.UserDeptRel;
import com.bkbits.dbo.entity.UserRoleRel;
import com.bkbits.encrypt.IPasswordEncrypt;
import com.bkbits.orm.EasyPager;
import com.bkbits.util.StringUtil;
import com.bkbits.util.ValidUtil;
import com.easy.query.api.proxy.client.EasyEntityQuery;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.annotation.Transaction;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserServiceImpl implements UserService {

    @Inject
    private EasyEntityQuery easyEntityQuery;

    @Inject
    private IPasswordEncrypt passwordEncrypt;

    @Override
    @Transaction
    public void add(User user, String deptId) {
        ValidUtil.requireNotNull(user, "用户不能为空");
        String checkedDeptId = StringUtil.trim(deptId);
        if (StringUtil.isNotEmpty(checkedDeptId)) {
            user.setLastDeptId(checkedDeptId);
        }
        if (easyEntityQuery.insertable(user).executeRows() != 1) {
            throw new IllegalStateException("创建用户失败");
        }
        if (StringUtil.isNotEmpty(checkedDeptId)) {
            UserDeptRel rel = new UserDeptRel();
            rel.setUserId(user.getUserId());
            rel.setDeptId(checkedDeptId);
            easyEntityQuery.insertable(rel).executeRows();
        }
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
    public List<User> listByDeptId(String deptId) {
        String checkedDeptId = ValidUtil.requireString(deptId, "部门编号不能为空");
        List<String> userIds = easyEntityQuery.queryable(UserDeptRel.class)
                .where(r -> r.deptId().eq(checkedDeptId))
                .selectColumn(r -> r.userId())
                .toList();
        if (userIds.isEmpty()) {
            return List.of();
        }
        return easyEntityQuery.queryable(User.class)
                .where(o -> o.userId().in(userIds))
                .orderBy(o -> o.userId().asc())
                .toList();
    }

    @Override
    public PageResult<UserVO> pageUsers(UserQueryDTO query, PageQuery pageQuery) {
        PageQuery checkedPage = ValidUtil.requireNotNull(pageQuery, "分页参数不能为空");
        UserQueryDTO condition = query == null ? new UserQueryDTO() : query;

        String deptId = StringUtil.trim(condition.getDeptId());
        condition.setDeptId(null);
        List<String> deptUserIds = null;
        if (StringUtil.isNotEmpty(deptId)) {
            deptUserIds = easyEntityQuery.queryable(UserDeptRel.class)
                    .where(r -> r.deptId().eq(deptId))
                    .selectColumn(r -> r.userId())
                    .toList();
            if (deptUserIds.isEmpty()) {
                return PageResult.page(0, List.of());
            }
        }

        var queryable = easyEntityQuery.queryable(User.class)
                .whereObject(condition)
                .orderBy(o -> o.createTime().desc());
        if (deptUserIds != null) {
            List<String> userIds = deptUserIds;
            queryable = queryable.where(o -> o.userId().in(userIds));
        }
        PageResult<User> page = queryable.toPageResult(
                EasyPager.of(checkedPage.getPage(), checkedPage.getPageSize()));

        List<User> users = page.getData();
        List<UserVO> rows = UserMapper.INSTANCE.toVOList(users);
        if (!users.isEmpty()) {
            List<String> userIds = users.stream().map(User::getUserId).toList();
            Map<String, String> deptMap = easyEntityQuery.queryable(UserDeptRel.class)
                    .where(r -> r.userId().in(userIds))
                    .toList()
                    .stream()
                    .collect(Collectors.toMap(
                            UserDeptRel::getUserId,
                            UserDeptRel::getDeptId,
                            (first, second) -> first));
            rows.forEach(vo -> vo.setDeptId(deptMap.get(vo.getUserId())));
        }
        return PageResult.page(page.getTotal(), rows);
    }

    @Override
    public String getDeptIdByUserId(String userId) {
        List<String> deptIds = easyEntityQuery.queryable(UserDeptRel.class)
                .where(r -> r.userId().eq(ValidUtil.requireString(userId, "用户编号不能为空")))
                .selectColumn(r -> r.deptId())
                .toList();
        return deptIds.isEmpty() ? null : deptIds.get(0);
    }

    @Override
    @Transaction
    public void update(User user, String deptId) {
        ValidUtil.requireNotNull(user, "用户不能为空");
        ValidUtil.requireString(user.getUserId(), "用户编号不能为空");
        String checkedDeptId = StringUtil.trim(deptId);
        if (StringUtil.isNotEmpty(checkedDeptId)) {
            user.setLastDeptId(checkedDeptId);
        }
        easyEntityQuery.updatable(user)
                .executeRows(1, "更新用户失败");

        if (checkedDeptId != null) {
            easyEntityQuery.deletable(UserDeptRel.class)
                    .where(r -> r.userId().eq(user.getUserId()))
                    .executeRows();
            if (StringUtil.isNotEmpty(checkedDeptId)) {
                UserDeptRel rel = new UserDeptRel();
                rel.setUserId(user.getUserId());
                rel.setDeptId(checkedDeptId);
                easyEntityQuery.insertable(rel).executeRows();
            }
        }
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
        easyEntityQuery.deletable(UserDeptRel.class)
                .where(o -> o.userId().eq(checkedUserId))
                .executeRows();
        easyEntityQuery.deletable(UserRoleRel.class)
                .where(o -> o.userId().eq(checkedUserId))
                .executeRows();
        easyEntityQuery.deletable(User.class)
                .whereById(checkedUserId)
                .executeRows(1, "删除用户失败");
    }

}

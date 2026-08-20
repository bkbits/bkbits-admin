package com.bkbits.admin.service.impl;

import com.bkbits.admin.service.RoleService;
import com.bkbits.dbo.constants.BaseConstants;
import com.bkbits.dbo.entity.*;
import com.bkbits.util.CollectionUtil;
import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.easy.query.core.annotation.EasyQueryTrack;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.annotation.Transaction;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RoleServiceImpl implements RoleService {
    @Inject
    private EasyEntityQuery easyEntityQuery;

    @Override
    public List<Role> getRolesByUserId(String userId, boolean onlyEnabled) {
        return easyEntityQuery.queryable(Role.class)
                .where(r -> {
                    r.userList().any(
                            u -> u.userId().eq(userId)
                    );
                    r.status().eq(onlyEnabled, BaseConstants.STATUS_ENABLED);
                })
                .toList();
    }

    @Override
    public List<Role> getRolesByUserName(String userName, boolean onlyEnabled) {
        return easyEntityQuery.queryable(Role.class)
                .where(r -> {
                    r.userList().any(
                            u -> u.userName().eq(userName)
                    );
                    r.status().eq(onlyEnabled, BaseConstants.STATUS_ENABLED);
                })
                .toList();
    }

    @Override
    public void addRole(Role role) {
        easyEntityQuery.insertable(role).executeRows();
    }

    @Override
    public void updateRole(Role role) {
        easyEntityQuery.updatable(role).executeRows(1, "更新角色失败");
    }

    @Transaction
    @EasyQueryTrack
    @Override
    public void removeRoleByIds(List<String> roleIds) {
        List<Role> roles = easyEntityQuery.queryable(Role.class)
                .include2((context, r) -> {
                    context.query(r.dataPermissionList());
                    context.query(r.dataScopeList());
                    context.query(r.permissionList());
                    context.query(r.userList());
                })
                .where(r -> r.id().in(roleIds))
                .toList();

        easyEntityQuery.savable(roles).removeRoot().executeCommand();
    }

    @Transaction
    @Override
    public void bingUserRole(String userId, List<String> roleIds) {
        easyEntityQuery.deletable(UserRoleRel.class)
                .allowDeleteStatement(true)
                .where(u -> u.userId().eq(userId))
                .executeRows();

        easyEntityQuery.insertable(
                        roleIds.stream()
                                .map(roleId -> {
                                    UserRoleRel rel = new UserRoleRel();
                                    rel.setRoleId(roleId);
                                    rel.setUserId(userId);
                                    return rel;
                                })
                                .collect(Collectors.toList())
                )
                .executeRows();
    }

    @Transaction
    @Override
    public void bindRolePermissions(String roleId, List<String> permissionIds) {
        easyEntityQuery.deletable(RolePermissionRel.class)
                .allowDeleteStatement(true)
                .where(r -> r.roleId().eq(roleId))
                .executeRows();

        easyEntityQuery.insertable(
                        permissionIds.stream()
                                .map(permitId -> {
                                    RolePermissionRel rel = new RolePermissionRel();
                                    rel.setRoleId(roleId);
                                    rel.setPermissionId(permitId);
                                    return rel;
                                })
                                .collect(Collectors.toList())
                )
                .executeRows();
    }

    @Transaction
    @Override
    public void bingRoleDataPermission(String roleId, List<String> dataPermissionIds) {
        easyEntityQuery.insertable(
                        dataPermissionIds.stream()
                                .map(id -> {
                                    RoleDataPermissionRel rel = new RoleDataPermissionRel();
                                    rel.setRoleId(roleId);
                                    rel.setDataPermissionId(id);
                                    return rel;
                                })
                                .collect(Collectors.toList())
                )
                .executeRows();
    }

    @Transaction
    @Override
    public void bindRoleDataScopes(String roleId, List<String> dataScopeList) {
        easyEntityQuery.deletable(RoleDataPermissionRel.class)
                .allowDeleteStatement(true)
                .where(r -> r.roleId().eq(roleId))
                .executeRows();

        if (CollectionUtil.isNotEmpty(dataScopeList)) {
            easyEntityQuery.insertable(
                            dataScopeList.stream()
                                    .map(dataScopeName -> {
                                        RoleDataScope dataScope = new RoleDataScope();
                                        dataScope.setRoleId(roleId);
                                        dataScope.setDataScope(dataScopeName);
                                        return dataScope;
                                    })
                                    .collect(Collectors.toList())
                    )
                    .executeRows();
        }
    }
}

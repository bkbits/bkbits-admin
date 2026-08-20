package com.bkbits.admin.service.impl;

import com.bkbits.admin.service.PermissionService;
import com.bkbits.core.Result;
import com.bkbits.dbo.entity.DataPermission;
import com.bkbits.dbo.entity.Permission;
import com.bkbits.util.CollectionUtil;
import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.easy.query.core.annotation.EasyQueryTrack;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.exception.StatusException;
import org.noear.solon.data.annotation.Transaction;

import java.util.List;

@Component
public class PermissionServiceImpl implements PermissionService {

    @Inject
    private EasyEntityQuery easyEntityQuery;

    @Override
    public Permission getById(String id) {
        return easyEntityQuery.queryable(Permission.class)
                .whereById(id)
                .singleNotNull("权限不存在");
    }

    @Override
    public List<Permission> listTree(Object search) {
        return CollectionUtil.toTree(
                easyEntityQuery.queryable(Permission.class)
                        .include(p -> p.dataPermissionList())
                        .whereObject(search != null, search)
                        .toList()
        );
    }

    @Override
    public List<Permission> listTreeByRoleId(String roleId) {
        return CollectionUtil.toTree(
                easyEntityQuery.queryable(Permission.class)
                        .include(p -> p.dataPermissionList())
                        .where(p -> p.roleList().any(
                                r -> r.id().eq(roleId)
                        ))
                        .toList()
        );
    }

    @Override
    public void addPermission(Permission permission) {
        easyEntityQuery.insertable(permission).executeRows();
    }

    @Override
    public void updatePermission(Permission permission) {
        easyEntityQuery.updatable(permission).executeRows(1, "找不到权限");
    }

    @Transaction
    @EasyQueryTrack
    @Override
    public void removePermissions(List<String> permissionIds) {
        for (String permissionId : permissionIds) {
            Long count = easyEntityQuery.queryable(Permission.class)
                    .where(p -> p.id().eq(permissionId))
                    .selectColumn(p -> p.children().count())
                    .singleNotNull("找不到权限");
            if (count > 0) {
                throw new StatusException("权限 [" + permissionId + "] 子权限不为空", Result.CODE_FAILURE);
            }
        }
        List<Permission> permissions = easyEntityQuery.queryable(Permission.class)
                .include2((context, p) -> {
                    context.query(p.dataPermissionList());
                    context.query(p.roleList());
                })
                .where(p -> p.id().in(permissionIds))
                .toList();

        easyEntityQuery.savable(permissions).removeRoot().executeCommand();
    }

    @Transaction
    @Override
    public void addDataPermission(DataPermission dataPermission) {
        easyEntityQuery.queryable(Permission.class)
                .whereById(dataPermission.getPermissionId())
                .required("找不到需要绑定的权限");
        easyEntityQuery.insertable(dataPermission).executeRows();
    }

    @Override
    public void updateDataPermission(DataPermission dataPermission) {
        easyEntityQuery.updatable(dataPermission)
                .setColumns(p -> p.FETCHER.allFieldsExclude(
                        p.permissionId()
                ))
                .executeRows(1, "找不到数据权限");
    }

    @Transaction
    @EasyQueryTrack
    @Override
    public void removeDataPermission(String dataPermissionId) {
        DataPermission dataPermission = easyEntityQuery.queryable(DataPermission.class)
                .include2((context, d) -> {
                    context.query(d.permission());
                    context.query(d.roleList());
                })
                .singleNotNull();

        easyEntityQuery.savable(dataPermission).removeRoot().executeCommand();
    }
}

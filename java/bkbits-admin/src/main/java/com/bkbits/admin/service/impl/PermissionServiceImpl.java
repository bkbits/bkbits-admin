package com.bkbits.admin.service.impl;

import com.bkbits.admin.service.PermissionService;
import com.bkbits.dbo.constants.BaseConstants;
import com.bkbits.dbo.entity.*;
import com.bkbits.util.CollectionUtil;
import com.bkbits.util.ValidUtil;
import com.easy.query.api.proxy.client.EasyEntityQuery;
import com.easy.query.core.annotation.EasyQueryTrack;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.annotation.Transaction;

import java.util.*;

@Component
public class PermissionServiceImpl implements PermissionService {

    @Inject
    private EasyEntityQuery easyEntityQuery;

    @Override
    public Role addRole(Role role) {
        Objects.requireNonNull(role, "角色不能为空");
        if (easyEntityQuery.insertable(role).executeRows() != 1) {
            throw new IllegalStateException("创建角色失败");
        }
        return role;
    }

    @Override
    public Role getRoleById(String roleId) {
        return easyEntityQuery.queryable(Role.class)
                .include2((ctx, o) -> {
                    ctx.query(o.permissionList());
                    ctx.query(o.dataPermissionList());
                })
                .whereById(ValidUtil.requireString(roleId, "角色编号不能为空"))
                .singleOrNull();
    }

    @Override
    public List<Role> listRolesByTenantId(String tenantId) {
        return easyEntityQuery.queryable(Role.class)
                .where(o -> o.tenantId().eq(ValidUtil.requireString(tenantId, "租户编号不能为空")))
                .orderBy(o -> {
                    o.sort().asc();
                    o.code().asc();
                    o.id().asc();
                })
                .toList();
    }

    @Override
    public Role updateRole(Role role) {
        Objects.requireNonNull(role, "角色不能为空");
        ValidUtil.requireString(role.getId(), "角色编号不能为空");
        easyEntityQuery.updatable(role)
                .executeRows(1, "更新角色失败");
        return role;
    }

    @Override
    @Transaction
    public void removeRoleById(String roleId) {
        String checkedRoleId = ValidUtil.requireString(roleId, "角色编号不能为空");
        easyEntityQuery.deletable(UserRoleRel.class)
                .where(o -> o.roleId().eq(checkedRoleId))
                .executeRows();
        easyEntityQuery.deletable(RolePermissionRel.class)
                .where(o -> o.roleId().eq(checkedRoleId))
                .executeRows();
        easyEntityQuery.deletable(RoleDataPermissionRel.class)
                .where(o -> o.roleId().eq(checkedRoleId))
                .executeRows();
        easyEntityQuery.deletable(Role.class)
                .whereById(checkedRoleId)
                .executeRows(1, "删除角色失败");
    }

    @Override
    public Permission addPermission(Permission permission) {
        Objects.requireNonNull(permission, "权限不能为空");
        if (easyEntityQuery.insertable(permission).executeRows() != 1) {
            throw new IllegalStateException("创建权限失败");
        }
        return permission;
    }

    @Override
    public Permission getPermissionById(String permissionId) {
        return easyEntityQuery.queryable(Permission.class)
                .include(o -> o.dataPermissionList())
                .whereById(ValidUtil.requireString(permissionId, "权限编号不能为空"))
                .singleOrNull();
    }

    @Override
    public Permission updatePermission(Permission permission) {
        Objects.requireNonNull(permission, "权限不能为空");
        ValidUtil.requireString(permission.getId(), "权限编号不能为空");
        easyEntityQuery.updatable(permission)
                .executeRows(1, "更新权限失败");
        return permission;
    }

    @Override
    @EasyQueryTrack
    @Transaction
    public void removePermissionById(String permissionId) {
        String checkedPermissionId = ValidUtil.requireString(permissionId, "权限编号不能为空");
        boolean hasChildren = easyEntityQuery.queryable(Permission.class)
                .where(o -> o.parentId().eq(checkedPermissionId))
                .any();
        if (hasChildren) {
            throw new IllegalStateException("权限存在子权限，不能删除");
        }

        Permission permission = easyEntityQuery.queryable(Permission.class)
                .include2((ctx, p) -> {
                    ctx.query(p.roleList()); //所有关联角色
                    ctx.query(p.dataPermissionList()); // 所有关联数据权限
                    ctx.query(p.dataPermissionList().flatElement().roleList()); // 所有数据权限关联角色
                })
                .whereById(permissionId)
                .firstNotNull("找不到权限");

        easyEntityQuery.savable(permission).removeRoot().executeCommand();
    }

    @Override
    public DataPermission addDataPermission(String menuPermissionId, DataPermission dataPermission) {
        String checkedPermissionId = requireMenuPermission(menuPermissionId);
        Objects.requireNonNull(dataPermission, "数据权限不能为空");
        dataPermission.setPermissionId(checkedPermissionId);
        if (easyEntityQuery.insertable(dataPermission).executeRows() != 1) {
            throw new IllegalStateException("创建数据权限失败");
        }
        return dataPermission;
    }

    @Override
    public List<DataPermission> listDataPermissions(String menuPermissionId) {
        return easyEntityQuery.queryable(DataPermission.class)
                .where(o -> o.permissionId().eq(ValidUtil.requireString(menuPermissionId, "菜单权限编号不能为空")))
                .orderBy(o -> o.id().asc())
                .toList();
    }

    @Override
    public DataPermission updateDataPermission(DataPermission dataPermission) {
        Objects.requireNonNull(dataPermission, "数据权限不能为空");
        String dataPermissionId = ValidUtil.requireString(dataPermission.getId(), "数据权限编号不能为空");
        DataPermission existing = easyEntityQuery.queryable(DataPermission.class)
                .whereById(dataPermissionId)
                .singleOrNull();
        if (existing == null) {
            throw new IllegalArgumentException("数据权限不存在");
        }
        if (dataPermission.getPermissionId() != null) {
            requireMenuPermission(dataPermission.getPermissionId());
        }
        easyEntityQuery.updatable(dataPermission)
                .executeRows(1, "更新数据权限失败");
        return dataPermission;
    }

    @Override
    @Transaction
    public void removeDataPermissionById(String dataPermissionId) {
        String checkedId = ValidUtil.requireString(dataPermissionId, "数据权限编号不能为空");
        easyEntityQuery.deletable(RoleDataPermissionRel.class)
                .where(o -> o.dataPermissionId().eq(checkedId))
                .executeRows();
        easyEntityQuery.deletable(DataPermission.class)
                .whereById(checkedId)
                .executeRows(1, "删除数据权限失败");
    }

    @Override
    @Transaction
    public void bindRolesToUser(String userId, Collection<String> roleIds) {
        String checkedUserId = ValidUtil.requireString(userId, "用户编号不能为空");
        requireUserExists(checkedUserId);
        List<String> checkedRoleIds = CollectionUtil.distinct(roleIds);
        requireAllRolesExist(checkedRoleIds);

        easyEntityQuery.deletable(UserRoleRel.class)
                .where(o -> o.userId().eq(checkedUserId))
                .executeRows();
        if (!checkedRoleIds.isEmpty()) {
            List<UserRoleRel> relations = checkedRoleIds.stream().map(roleId -> {
                UserRoleRel relation = new UserRoleRel();
                relation.setUserId(checkedUserId);
                relation.setRoleId(roleId);
                return relation;
            }).toList();
            easyEntityQuery.insertable(relations).executeRows();
        }
    }

    @Override
    public List<Role> listRolesByUserId(String userId) {
        return easyEntityQuery.queryable(UserRoleRel.class)
                .include(o -> o.role())
                .where(o -> o.userId().eq(ValidUtil.requireString(userId, "用户编号不能为空")))
                .orderBy(o -> o.id().asc())
                .toList()
                .stream()
                .map(UserRoleRel::getRole)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(Role::getSort, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    @Override
    @Transaction
    public void bindPermissionsToRole(String roleId, Collection<String> permissionIds) {
        String checkedRoleId = ValidUtil.requireString(roleId, "角色编号不能为空");
        requireRoleExists(checkedRoleId);
        List<String> checkedPermissionIds = CollectionUtil.distinct(permissionIds);
        requireAllPermissionsExist(checkedPermissionIds);

        easyEntityQuery.deletable(RolePermissionRel.class)
                .where(o -> o.roleId().eq(checkedRoleId))
                .executeRows();
        if (!checkedPermissionIds.isEmpty()) {
            List<RolePermissionRel> relations = checkedPermissionIds.stream().map(permissionId -> {
                RolePermissionRel relation = new RolePermissionRel();
                relation.setRoleId(checkedRoleId);
                relation.setPermissionId(permissionId);
                return relation;
            }).toList();
            easyEntityQuery.insertable(relations).executeRows();
        }
    }

    @Override
    public List<String> listPermissionsByRoleId(String roleId) {
        return easyEntityQuery.queryable(Permission.class)
                .where(p -> p.roleList().any(r -> r.id().eq(roleId)))
                .selectColumn(p -> p.id())
                .toList();
    }

    @Override
    @Transaction
    public void bindDataPermissionsToRole(
            String roleId,
            String menuPermissionId,
            Collection<String> dataPermissionIds) {
        String checkedRoleId = ValidUtil.requireString(roleId, "角色编号不能为空");
        String checkedPermissionId = requireMenuPermission(menuPermissionId);
        requireRoleExists(checkedRoleId);
        List<String> checkedDataPermissionIds = CollectionUtil.distinct(dataPermissionIds);
        requireDataPermissionsBelongToMenu(checkedPermissionId, checkedDataPermissionIds);

        easyEntityQuery.deletable(RoleDataPermissionRel.class)
                .where(o -> o.roleId().eq(checkedRoleId))
                .executeRows();
        if (!checkedDataPermissionIds.isEmpty()) {
            List<RoleDataPermissionRel> relations = checkedDataPermissionIds.stream().map(dataPermissionId -> {
                RoleDataPermissionRel relation = new RoleDataPermissionRel();
                relation.setRoleId(checkedRoleId);
                relation.setDataPermissionId(dataPermissionId);
                return relation;
            }).toList();
            easyEntityQuery.insertable(relations).executeRows();
        }
    }

    @Override
    public List<String> listRoleDataPermissions(String roleId, String menuPermissionId) {
        return easyEntityQuery.queryable(DataPermission.class)
                .where(d -> {
                    d.roleList().any(r -> r.id().eq(roleId));
                    d.permission().id().eq(menuPermissionId);
                })
                .selectColumn(d -> d.id())
                .toList();
    }

    /**
     * 校验菜单权限编号非空，且对应权限存在且为菜单类型，返回校验后的编号。
     */
    private String requireMenuPermission(String permissionId) {
        String checkedId = ValidUtil.requireString(permissionId, "菜单权限编号不能为空");
        Permission permission = easyEntityQuery.queryable(Permission.class)
                .whereById(checkedId)
                .singleOrNull();
        if (permission == null) {
            throw new IllegalArgumentException("菜单权限不存在");
        }
        if (!BaseConstants.PERMISSION_MENU.equals(permission.getType())) {
            throw new IllegalArgumentException("数据权限只能绑定到菜单权限");
        }
        return checkedId;
    }

    /**
     * 校验全部角色编号对应的角色都存在，否则抛出异常。
     */
    private void requireAllRolesExist(List<String> roleIds) {
        if (roleIds.isEmpty()) {
            return;
        }
        long count = easyEntityQuery.queryable(Role.class)
                .whereByIds(roleIds)
                .count();
        if (count != roleIds.size()) {
            throw new IllegalArgumentException("部分角色不存在");
        }
    }

    /**
     * 校验全部权限编号对应的权限都存在，否则抛出异常。
     */
    private void requireAllPermissionsExist(List<String> permissionIds) {
        if (permissionIds.isEmpty()) {
            return;
        }
        long count = easyEntityQuery.queryable(Permission.class)
                .whereByIds(permissionIds)
                .count();
        if (count != permissionIds.size()) {
            throw new IllegalArgumentException("部分权限不存在");
        }
    }

    /**
     * 校验数据权限均存在且归属指定菜单权限，否则抛出异常。
     */
    private void requireDataPermissionsBelongToMenu(String permissionId, List<String> dataPermissionIds) {
        if (dataPermissionIds.isEmpty()) {
            return;
        }
        long count = easyEntityQuery.queryable(DataPermission.class)
                .whereByIds(dataPermissionIds)
                .where(o -> o.permissionId().eq(permissionId))
                .count();
        if (count != dataPermissionIds.size()) {
            throw new IllegalArgumentException("部分数据权限不存在或不属于指定菜单权限");
        }
    }

    /**
     * 校验用户存在，否则抛出异常。
     */
    private void requireUserExists(String userId) {
        if (!easyEntityQuery.queryable(User.class).whereById(userId).any()) {
            throw new IllegalArgumentException("用户不存在");
        }
    }

    /**
     * 校验角色存在，否则抛出异常。
     */
    private void requireRoleExists(String roleId) {
        if (!easyEntityQuery.queryable(Role.class).whereById(roleId).any()) {
            throw new IllegalArgumentException("角色不存在");
        }
    }
}

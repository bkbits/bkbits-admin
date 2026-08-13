package com.bkbits.admin.service.impl;

import com.bkbits.admin.service.PermissionService;
import com.bkbits.dbo.constants.BaseConstants;
import com.bkbits.dbo.entity.*;
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
                .whereById(requireText(roleId, "角色编号"))
                .singleOrNull();
    }

    @Override
    public List<Role> listRolesByTenantId(String tenantId) {
        return easyEntityQuery.queryable(Role.class)
                .where(o -> o.tenantId().eq(requireText(tenantId, "租户编号")))
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
        requireText(role.getId(), "角色编号");
        easyEntityQuery.updatable(role)
                .executeRows(1, "更新角色失败");
        return role;
    }

    @Override
    @Transaction
    public void removeRoleById(String roleId) {
        String checkedRoleId = requireText(roleId, "角色编号");
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
                .whereById(requireText(permissionId, "权限编号"))
                .singleOrNull();
    }

    @Override
    public List<Permission> listPermissions() {
        return easyEntityQuery.queryable(Permission.class)
                .orderBy(o -> {
                    o.parentId().asc();
                    o.sort().asc();
                    o.id().asc();
                })
                .toList();
    }

    @Override
    public Permission updatePermission(Permission permission) {
        Objects.requireNonNull(permission, "权限不能为空");
        requireText(permission.getId(), "权限编号");
        easyEntityQuery.updatable(permission)
                .executeRows(1, "更新权限失败");
        return permission;
    }

    @Override
    @EasyQueryTrack
    @Transaction
    public void removePermissionById(String permissionId) {
        String checkedPermissionId = requireText(permissionId, "权限编号");
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
                .where(o -> o.permissionId().eq(requireText(menuPermissionId, "菜单权限编号")))
                .orderBy(o -> o.id().asc())
                .toList();
    }

    @Override
    public DataPermission updateDataPermission(DataPermission dataPermission) {
        Objects.requireNonNull(dataPermission, "数据权限不能为空");
        String dataPermissionId = requireText(dataPermission.getId(), "数据权限编号");
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
        String checkedId = requireText(dataPermissionId, "数据权限编号");
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
        String checkedUserId = requireText(userId, "用户编号");
        requireUserExists(checkedUserId);
        List<String> checkedRoleIds = distinctIds(roleIds, "角色编号");
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
                .where(o -> o.userId().eq(requireText(userId, "用户编号")))
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
        String checkedRoleId = requireText(roleId, "角色编号");
        requireRoleExists(checkedRoleId);
        List<String> checkedPermissionIds = distinctIds(permissionIds, "权限编号");
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
    public List<Permission> listPermissionsByRoleId(String roleId) {
        return easyEntityQuery.queryable(RolePermissionRel.class)
                .include(o -> o.permission())
                .where(o -> o.roleId().eq(requireText(roleId, "角色编号")))
                .orderBy(o -> o.id().asc())
                .toList()
                .stream()
                .map(RolePermissionRel::getPermission)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(Permission::getSort, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    @Override
    @Transaction
    public void bindDataPermissionsToRole(String roleId, String menuPermissionId,
                                          Collection<String> dataPermissionIds) {
        String checkedRoleId = requireText(roleId, "角色编号");
        String checkedPermissionId = requireMenuPermission(menuPermissionId);
        requireRoleExists(checkedRoleId);
        List<String> checkedDataPermissionIds = distinctIds(dataPermissionIds, "数据权限编号");
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
    public List<RoleDataPermissionRel> listRoleDataPermissions(String roleId, String menuPermissionId) {
        return easyEntityQuery.queryable(RoleDataPermissionRel.class)
                .include(o -> o.dataPermission())
                .where(o -> o.roleId().eq(requireText(roleId, "角色编号")))
                .orderBy(o -> o.id().asc())
                .toList();
    }

    /**
     * 校验菜单权限编号非空，且对应权限存在且为菜单类型，返回校验后的编号。
     */
    private String requireMenuPermission(String permissionId) {
        String checkedId = requireText(permissionId, "菜单权限编号");
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

    /**
     * 逐个校验编号非空并去重，返回去重后的编号集合。
     */
    private List<String> distinctIds(Collection<String> ids, String name) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        LinkedHashSet<String> result = new LinkedHashSet<>();
        for (String id : ids) {
            result.add(requireText(id, name));
        }
        return List.copyOf(result);
    }

    /**
     * 校验文本非空，为空时抛出异常，否则返回原值。
     */
    private String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + "不能为空");
        }
        return value;
    }
}

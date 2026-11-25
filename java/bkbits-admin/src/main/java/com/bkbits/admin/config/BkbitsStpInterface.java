package com.bkbits.admin.config;

import cn.dev33.satoken.stp.StpInterface;
import com.bkbits.dbo.entity.Permission;
import com.bkbits.dbo.entity.Role;
import com.bkbits.dbo.entity.RolePermissionRel;
import com.bkbits.dbo.entity.UserRoleRel;
import com.bkbits.util.CollectionUtil;
import com.bkbits.util.StringUtil;
import com.easy.query.api.proxy.client.EasyEntityQuery;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;

import java.util.List;

/**
 * 权限数据源实现。
 *
 * <p>按用户 -> 角色 -> 权限的关联关系加载权限码与角色码，供 sa-token 注解鉴权使用。
 * 查询不到关联数据时返回空集合（系统用户 {@code "0"} 即属此类）。</p>
 */
@Component
public class BkbitsStpInterface implements StpInterface {

    @Inject
    private EasyEntityQuery easyEntityQuery;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        List<String> roleIds = listRoleIds(String.valueOf(loginId));
        if (CollectionUtil.isEmpty(roleIds)) {
            return List.of();
        }

        List<String> permissionIds = easyEntityQuery.queryable(RolePermissionRel.class)
                .where(r -> r.roleId().in(roleIds))
                .selectColumn(r -> r.permissionId())
                .toList();
        if (CollectionUtil.isEmpty(permissionIds)) {
            return List.of();
        }

        return easyEntityQuery.queryable(Permission.class)
                .where(p -> p.id().in(permissionIds))
                .selectColumn(p -> p.permission())
                .toList()
                .stream()
                .filter(StringUtil::isNotBlank)
                .distinct()
                .toList();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        List<String> roleIds = listRoleIds(String.valueOf(loginId));
        if (CollectionUtil.isEmpty(roleIds)) {
            return List.of();
        }

        return easyEntityQuery.queryable(Role.class)
                .where(r -> r.id().in(roleIds))
                .selectColumn(r -> r.code())
                .toList()
                .stream()
                .filter(StringUtil::isNotBlank)
                .distinct()
                .toList();
    }

    /**
     * 查询用户绑定的角色 id 集合。
     *
     * @param userId 用户 id
     * @return 角色 id 集合
     */
    private List<String> listRoleIds(String userId) {
        return easyEntityQuery.queryable(UserRoleRel.class)
                .where(r -> r.userId().eq(userId))
                .selectColumn(r -> r.roleId())
                .toList();
    }
}

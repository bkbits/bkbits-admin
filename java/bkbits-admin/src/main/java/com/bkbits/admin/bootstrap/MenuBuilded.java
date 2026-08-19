package com.bkbits.admin.bootstrap;

import com.bkbits.dbo.entity.DataPermission;
import com.bkbits.dbo.entity.Permission;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 权限菜单构建结果。
 *
 * <p>由 {@link MenuBuilder#build()} 产出：{@code permissions} 为整棵权限树展平后的全部节点
 * （含顶层目录/菜单及下属菜单/按钮，深度优先，层级仍通过
 * {@link Permission#getChildren()} 表达），可直接交给 easy-query 按序/嵌套插入；
 * {@code dataPermissions} 为全部数据权限的扁平列表，与
 * {@link Permission#getDataPermissionList()} 中的对象为同一实例，便于角色授权绑定。
 */
@Getter
@AllArgsConstructor
public class MenuBuilded {

    /** 全部权限节点（深度优先展平，含顶层与下属菜单/按钮） */
    private final List<Permission> permissions;

    /** 全部数据权限扁平列表 */
    private final List<DataPermission> dataPermissions;
}
package com.bkbits.admin.bootstrap;

import com.bkbits.admin.pojo.DataScopeVO;
import com.bkbits.dbo.entity.Permission;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * 权限菜单构建结果。
 *
 * <p>由 {@link MenuBuilder#build()} 产出：{@code permissions} 为整棵权限树展平后的全部节点
 * （含顶层目录/菜单及下属菜单/按钮，深度优先，层级仍通过
 * {@link Permission#getChildren()} 表达），可直接交给 easy-query 按序/嵌套插入；
 * {@code dataScopes} 为「权限标识 -> 可选数据域」映射，供角色授权时使用。</p>
 */
@Getter
@AllArgsConstructor
public class MenuBuilded {

    /**
     * 全部权限节点（深度优先展平，含顶层与下属菜单/按钮）
     */
    private final List<Permission> permissions;

    /**
     * 权限标识 -> 可选数据域
     */
    private final Map<String, List<DataScopeVO>> dataScopes;
}

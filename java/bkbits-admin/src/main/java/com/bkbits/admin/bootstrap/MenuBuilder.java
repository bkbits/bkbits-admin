package com.bkbits.admin.bootstrap;

import com.bkbits.admin.pojo.DataScopeVO;
import com.bkbits.dbo.constants.BaseConstants;
import com.bkbits.dbo.entity.Permission;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 权限菜单构建器（DSL）。
 *
 * <p>以声明式方式构建权限目录/菜单/按钮树及其数据权限，示例：
 * <pre>{@code
 * MenuBuilded result = MenuBuilder.builder()
 *         .dir().name("系统管理").permission("admin.sys").path("/sys")
 *             .menu().name("菜单管理").permission("admin.sys.menu").component("System/Menu").path("menu")
 *                 .btn().name("菜单查询").permission("admin.sys.menu.query")
 *                     .data().name("仅自身部门").dataScope("OnlySelfDept")
 *                 .end()
 *             .end()
 *         .end()
 *         .build();
 * List<Permission> permissions = result.getPermissions();
 * Map<String, List<DataScopeVO>> dataScopes = result.getDataScopes();
 * }</pre>
 *
 * <p>节点语义：{@code dir=目录(D)}、{@code menu=菜单(M)}、{@code btn=按钮(B)}，节点默认启用；
 * {@code data=数据域}，挂接在菜单/按钮之下（权限类型见 {@link BaseConstants#PERMISSION_DIRECTORY}
 * 等常量）。{@code btn} 与 {@code data} 为叶节点，无需 {@code end()} 闭合，
 * 新建兄弟节点或调用 {@code end()} 时自动收尾；{@code end()} 只用于结束（或略过）目录/菜单层级。
 *
 * <p>树结构通过 {@link Permission#getChildren()} 嵌套表达；节点 id 与 parentId 外键由持久化层
 * （easy-query {@code @Navigate}）维护，构建产物可直接嵌套插入。数据域按所属权限的
 * {@code permission} 标识汇总，仅在内存中使用，不随权限实体持久化。
 */
public class MenuBuilder {

    /**
     * 根节点集合（顶级权限）
     */
    private final List<Permission> roots = new ArrayList<>();

    /**
     * 目录/菜单层级栈
     */
    private final Deque<Permission> stack = new ArrayDeque<>();

    /**
     * 最近创建的按钮（叶节点，不入栈）
     */
    private Permission currentBtn;

    /**
     * 当前数据域；非空表示当前上下文为数据域
     */
    private DataScopeVO currentData;

    /**
     * 已构建的全部数据域（记录所属权限，构建时按权限标识汇总）
     */
    private final List<DataScopeEntry> dataScopeEntries = new ArrayList<>();

    /**
     * 全局序号，用作权限排序
     */
    private int seq = 0;

    private MenuBuilder() {
    }

    /**
     * 创建构建器。
     *
     * @return 构建器
     */
    public static MenuBuilder builder() {
        return new MenuBuilder();
    }

    /**
     * 开始一个目录节点。
     *
     * @return this
     */
    public MenuBuilder dir() {
        return pushNode(BaseConstants.PERMISSION_DIRECTORY);
    }

    /**
     * 开始一个菜单节点。
     *
     * @return this
     */
    public MenuBuilder menu() {
        return pushNode(BaseConstants.PERMISSION_MENU);
    }

    /**
     * 开始一个按钮节点。
     *
     * @return this
     */
    public MenuBuilder btn() {
        return pushNode(BaseConstants.PERMISSION_BUTTON);
    }

    /**
     * 为当前权限节点（按钮或菜单）添加数据域。
     *
     * @return this
     */
    public MenuBuilder data() {
        Permission owner = currentPermission("data 节点必须挂接在菜单/按钮权限之下");
        currentData = new DataScopeVO();
        dataScopeEntries.add(new DataScopeEntry(owner, currentData));
        return this;
    }

    /**
     * 设置当前节点名称。
     *
     * @param name 名称
     * @return this
     */
    public MenuBuilder name(String name) {
        if (currentData != null) {
            currentData.setName(name);
        } else {
            currentPermission("当前没有可设置的节点，请先调用 dir/menu/btn/data").setName(name);
        }
        return this;
    }

    /**
     * 设置当前节点权限标识。
     *
     * @param permission 权限标识，建议用 '.' 分隔
     * @return this
     */
    public MenuBuilder permission(String permission) {
        requirePermission("permission").setPermission(permission);
        return this;
    }

    /**
     * 设置当前节点路径。
     *
     * @param path 路径；/开头为绝对路径，否则为基于父级权限的相对路径
     * @return this
     */
    public MenuBuilder path(String path) {
        requirePermission("path").setPath(path);
        return this;
    }

    /**
     * 设置当前节点组件。
     *
     * @param component 前端组件路径
     * @return this
     */
    public MenuBuilder component(String component) {
        requirePermission("component").setComponent(component);
        return this;
    }

    /**
     * 设置当前数据域的数据域标识。
     *
     * @param dataScope 数据域标识
     * @return this
     */
    public MenuBuilder dataScope(String dataScope) {
        if (currentData == null) {
            throw new IllegalStateException("dataScope 仅支持 data 节点");
        }
        currentData.setDataScope(dataScope);
        return this;
    }

    /**
     * 结束当前层级：收尾叶节点（按钮/数据权限）并结束一个目录/菜单。
     *
     * <p>叶节点无需显式 {@code end()}；本方法仅用于结束（或略过）目录/菜单。
     * 栈已空时静默忽略，便于不必精算 end 数量。
     *
     * @return this
     */
    public MenuBuilder end() {
        currentData = null;
        currentBtn = null;
        if (!stack.isEmpty()) {
            stack.pop();
        }
        return this;
    }

    /**
     * 完成构建：返回全部权限节点与数据域映射。
     *
     * <p>{@code permissions} 为整棵权限树展平后的全部节点（含顶层目录/菜单及下属菜单/按钮，
     * 深度优先，层级仍通过 {@link Permission#getChildren()} 表达），可直接交给 easy-query
     * 按序/嵌套插入；{@code dataScopes} 按权限标识汇总全部数据域。
     *
     * @return 构建结果
     */
    public MenuBuilded build() {
        if (roots.isEmpty()) {
            throw new IllegalStateException("未构建任何权限节点");
        }
        List<Permission> all = new ArrayList<>();
        collectPermissions(roots, all);

        Map<String, List<DataScopeVO>> dataScopes = new LinkedHashMap<>();
        for (DataScopeEntry entry : dataScopeEntries) {
            String permission = entry.owner().getPermission();
            if (permission == null || permission.isBlank()) {
                throw new IllegalStateException("数据域必须挂接在设置了 permission 的权限节点下");
            }
            dataScopes.computeIfAbsent(permission, key -> new ArrayList<>()).add(entry.dataScope());
        }
        return new MenuBuilded(all, dataScopes);
    }

    /**
     * 数据域构建项。
     *
     * @param owner     所属权限节点
     * @param dataScope 数据域
     */
    private record DataScopeEntry(Permission owner, DataScopeVO dataScope) {
    }

    /**
     * 创建权限节点并挂接；目录/菜单入栈，按钮作为叶节点记录为当前按钮。
     *
     * @param type 权限类型（D/M/B）
     * @return this
     */
    private MenuBuilder pushNode(String type) {
        currentData = null;
        currentBtn = null;

        Permission node = new Permission();
        node.setType(type);
        node.setStatus(BaseConstants.STATUS_ENABLED);
        node.setSort(++seq);

        Permission parent = stack.peek();
        if (parent != null) {
            if (parent.getChildren() == null) {
                parent.setChildren(new ArrayList<>());
            }
            parent.getChildren().add(node);
        } else {
            roots.add(node);
        }

        if (BaseConstants.PERMISSION_BUTTON.equals(type)) {
            currentBtn = node;
        } else {
            stack.push(node);
        }
        return this;
    }

    /**
     * 深度优先展平全部权限节点。
     *
     * @param nodes 权限节点集合
     * @param out   输出集合
     */
    private void collectPermissions(List<Permission> nodes, List<Permission> out) {
        for (Permission node : nodes) {
            out.add(node);
            if (node.getChildren() != null) {
                collectPermissions(node.getChildren(), out);
            }
        }
    }

    /**
     * 当前权限节点（优先最近按钮，其次栈顶目录/菜单）。
     *
     * @param errorMsg 无节点时的异常消息
     * @return 当前权限节点
     */
    private Permission currentPermission(String errorMsg) {
        Permission node = currentBtn != null ? currentBtn : stack.peek();
        if (node == null) {
            throw new IllegalStateException(errorMsg);
        }
        return node;
    }

    /**
     * 要求当前上下文为权限节点（非数据权限）。
     *
     * @param property 属性名，用于异常提示
     * @return 当前权限节点
     */
    private Permission requirePermission(String property) {
        if (currentData != null) {
            throw new IllegalStateException("data 节点不支持该属性：" + property);
        }
        return currentPermission("当前没有可设置的节点，请先调用 dir/menu/btn/data");
    }
}
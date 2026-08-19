package com.bkbits.admin.bootstrap;

import com.bkbits.dbo.constants.BaseConstants;
import com.bkbits.dbo.entity.DataPermission;
import com.bkbits.dbo.entity.Permission;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * {@link MenuBuilder} 单元测试。
 */
class MenuBuilderTest {

    @Test
    void buildFullMenuTree() {
        List<Permission> permissions = new ArrayList<>();
        List<DataPermission> dataPermissions = new ArrayList<>();

        MenuBuilder.builder()
                .dir().name("系统管理").permission("admin.sys").path("/sys")
                    .menu().name("菜单管理").permission("admin.sys.menu").component("System/Menu").path("menu")
                        .btn().name("菜单查询").permission("admin.sys.menu.query")
                            .data().name("仅自身部门").dataScope("OnlySelfDept")
                        .end()
                    .end()
                .end()
                .build(permissions, dataPermissions);

        // 单个根目录
        assertEquals(1, permissions.size());
        Permission dir = permissions.get(0);
        assertEquals("D", dir.getType());
        assertEquals("系统管理", dir.getName());
        assertEquals("admin.sys", dir.getPermission());
        assertEquals("/sys", dir.getPath());
        assertEquals(BaseConstants.STATUS_ENABLED, dir.getStatus());
        assertEquals(1, dir.getSort());

        // 目录下挂一个菜单
        assertNotNull(dir.getChildren());
        assertEquals(1, dir.getChildren().size());
        Permission menu = dir.getChildren().get(0);
        assertEquals("M", menu.getType());
        assertEquals("菜单管理", menu.getName());
        assertEquals("admin.sys.menu", menu.getPermission());
        assertEquals("System/Menu", menu.getComponent());
        assertEquals("menu", menu.getPath());

        // 菜单下挂一个按钮
        assertNotNull(menu.getChildren());
        assertEquals(1, menu.getChildren().size());
        Permission btn = menu.getChildren().get(0);
        assertEquals("B", btn.getType());
        assertEquals("菜单查询", btn.getName());
        assertEquals("admin.sys.menu.query", btn.getPermission());

        // 按钮下挂一个数据权限
        assertNotNull(btn.getDataPermissionList());
        assertEquals(1, btn.getDataPermissionList().size());
        DataPermission dp = btn.getDataPermissionList().get(0);
        assertEquals("仅自身部门", dp.getName());
        assertEquals("OnlySelfDept", dp.getDataScope());
        assertEquals(BaseConstants.STATUS_ENABLED, dp.getStatus());

        // 扁平输出：权限仅根、数据权限与树内对象同一实例
        assertEquals(1, dataPermissions.size());
        assertSame(dp, dataPermissions.get(0));
        assertNull(dp.getPermissionId());
    }

    @Test
    void multipleRootsAndSiblingMenu() {
        List<Permission> permissions = new ArrayList<>();
        List<DataPermission> dataPermissions = new ArrayList<>();

        MenuBuilder.builder()
                .dir().name("系统管理").permission("admin.sys").path("/sys")
                    .menu().name("用户管理").permission("admin.sys.user").component("System/User")
                    .end()
                    .menu().name("角色管理").permission("admin.sys.role").component("System/Role")
                .end()
                .end()
                .dir().name("业务管理").permission("admin.biz").path("/biz")
                .end()
                .build(permissions, dataPermissions);

        assertEquals(2, permissions.size());
        assertEquals(2, permissions.get(0).getChildren().size());
        assertEquals(2, permissions.get(0).getChildren().get(0).getSort());
        assertEquals(3, permissions.get(0).getChildren().get(1).getSort());
        assertEquals(4, permissions.get(1).getSort());
        assertEquals(0, dataPermissions.size());
    }

    @Test
    void dataWithoutEndThenNextPermissionAutoCloses() {
        List<Permission> permissions = new ArrayList<>();
        List<DataPermission> dataPermissions = new ArrayList<>();

        // data 后不显式 end，直接再开 btn —— 自动结束上一个 data
        MenuBuilder.builder()
                .menu().name("用户管理").permission("admin.sys.user").component("System/User")
                    .btn().name("用户查询").permission("admin.sys.user.query")
                        .data().name("仅自身部门").dataScope("OnlySelfDept")
                    .btn().name("用户新增").permission("admin.sys.user.add")
                .end()
                .build(permissions, dataPermissions);

        Permission menu = permissions.get(0);
        assertEquals(2, menu.getChildren().size());
        assertEquals(1, menu.getChildren().get(0).getDataPermissionList().size());
        assertNull(menu.getChildren().get(1).getDataPermissionList());
        assertEquals(1, dataPermissions.size());
    }

    @Test
    void buildWithoutAnyNodeThrows() {
        assertThrows(IllegalStateException.class,
                () -> MenuBuilder.builder().build(new ArrayList<>(), new ArrayList<>()));
    }

    @Test
    void extraEndsAreIgnored() {
        // 空栈 end 静默忽略；btn/data 无需 end，多余 end 不报错
        List<Permission> permissions = new ArrayList<>();
        List<DataPermission> dataPermissions = new ArrayList<>();
        MenuBuilder.builder()
                .dir().name("系统管理").permission("admin.sys").path("/sys")
                    .menu().name("菜单管理").permission("admin.sys.menu").component("System/Menu").path("menu")
                        .btn().name("菜单查询").permission("admin.sys.menu.query")
                            .data().name("仅自身部门").dataScope("OnlySelfDept")
                        .end()
                    .end()
                .end()
                .end()
                .build(permissions, dataPermissions);

        // 原示例写法（含结束 btn/menu/dir 的三个 end + 多余一个）也能正确构建
        assertEquals(1, permissions.size());
        assertEquals(1, permissions.get(0).getChildren().size());
        assertEquals(1, permissions.get(0).getChildren().get(0).getChildren().size());
        assertEquals(1, dataPermissions.size());
    }

    @Test
    void noEndRequiredForLeafNodes() {
        // btn/data 不写 end，直接继续构建同级按钮，最后只需一个 end 收尾目录
        List<Permission> permissions = new ArrayList<>();
        List<DataPermission> dataPermissions = new ArrayList<>();
        MenuBuilder.builder()
                .dir().name("系统管理").permission("admin.sys").path("/sys")
                    .menu().name("菜单管理").permission("admin.sys.menu").component("System/Menu")
                        .btn().name("菜单查询").permission("admin.sys.menu.query")
                            .data().name("仅自身部门").dataScope("OnlySelfDept")
                        .btn().name("菜单新增").permission("admin.sys.menu.add")
                .end()
                .build(permissions, dataPermissions);

        Permission menu = permissions.get(0).getChildren().get(0);
        assertEquals(2, menu.getChildren().size());
        assertEquals(1, menu.getChildren().get(0).getDataPermissionList().size());
        assertNull(menu.getChildren().get(1).getDataPermissionList());
        assertEquals(1, dataPermissions.size());
    }

    @Test
    void dataScopeOnPermissionNodeThrows() {
        assertThrows(IllegalStateException.class,
                () -> MenuBuilder.builder().menu().name("用户管理").dataScope("x"));
    }
}
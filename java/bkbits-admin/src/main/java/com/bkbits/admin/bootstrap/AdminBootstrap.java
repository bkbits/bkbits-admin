package com.bkbits.admin.bootstrap;

import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Init;

@Configuration
public class AdminBootstrap {

    @Init
    public void start() {

        MenuBuilder.builder()
                .dir().name("个人中心").path("/my")
                    .menu().name("仪表盘").path("dashboard").component("My/DashBoard")
                    .menu().name("我的信息").path("userinfo").component("My/UserInfo")
                    .menu().name("我的通知").path("notification").component("My/Notification")
                .end()

                .dir().name("系统管理").path("/sys")

                    .menu().name("租户管理").permission("admin.tenant").path("tenant").component("System/Tenant")
                        .data().name("仅自身租户").dataScope("OnlySelfTenant")
                        .data().name("仅自身部门").dataScope("OnlySelfDept")
                        .data().name("子部门").dataScope("ChildDept")
                        .data().name("父部门").dataScope("ParentDept")
                        .data().name("仅自身").dataScope("OnlySelf")
                        .btn().name("租户查询").permission("admin.tenant.query")
                        .btn().name("租户新增").permission("admin.tenant.add")
                        .btn().name("租户编辑").permission("admin.tenant.update")
                        .btn().name("租户删除").permission("admin.tenant.remove")
                    .end()

                    .menu().name("部门管理").permission("admin.dept").path("dept").component("System/Dept")
                        .data().name("仅自身租户").dataScope("OnlySelfTenant")
                        .data().name("仅自身部门").dataScope("OnlySelfDept")
                        .data().name("子部门").dataScope("ChildDept")
                        .data().name("父部门").dataScope("ParentDept")
                        .data().name("仅自身").dataScope("OnlySelf")
                        .btn().name("部门查询").permission("admin.dept.query")
                        .btn().name("部门新增").permission("admin.dept.add")
                        .btn().name("部门编辑").permission("admin.dept.update")
                        .btn().name("部门删除").permission("admin.dept.remove")
                    .end()

                    .menu().name("用户管理").permission("admin.user").path("user").component("System/User")
                        .data().name("仅自身租户").dataScope("OnlySelfTenant")
                        .data().name("仅自身部门").dataScope("OnlySelfDept")
                        .data().name("子部门").dataScope("ChildDept")
                        .data().name("父部门").dataScope("ParentDept")
                        .data().name("仅自身").dataScope("OnlySelf")
                        .btn().name("用户查询").permission("admin.user.query")
                        .btn().name("用户新增").permission("admin.user.add")
                        .btn().name("用户编辑").permission("admin.user.update")
                        .btn().name("用户删除").permission("admin.user.remove")
                    .end()

                    .menu().name("角色管理").permission("admin.role").path("role").component("System/Role")
                        .data().name("仅自身租户").dataScope("OnlySelfTenant")
                        .data().name("仅自身部门").dataScope("OnlySelfDept")
                        .data().name("子部门").dataScope("ChildDept")
                        .data().name("父部门").dataScope("ParentDept")
                        .data().name("仅自身").dataScope("OnlySelf")
                        .btn().name("角色查询").permission("admin.role.query")
                        .btn().name("角色新增").permission("admin.role.add")
                        .btn().name("角色编辑").permission("admin.role.update")
                        .btn().name("角色删除").permission("admin.role.remove")
                    .end()

                    .menu().name("菜单管理").permission("admin.permission").path("permission").component("System/Permission")
                        .data().name("仅自身租户").dataScope("OnlySelfTenant")
                        .data().name("仅自身部门").dataScope("OnlySelfDept")
                        .data().name("子部门").dataScope("ChildDept")
                        .data().name("父部门").dataScope("ParentDept")
                        .data().name("仅自身").dataScope("OnlySelf")
                        .btn().name("菜单查询").permission("admin.permission.query")
                        .btn().name("菜单新增").permission("admin.permission.add")
                        .btn().name("菜单编辑").permission("admin.permission.update")
                        .btn().name("菜单删除").permission("admin.permission.remove")
                    .end()

                    .menu().name("登录日志").permission("admin.loginLog").path("loginLog").component("System/LoginLog")
                        .data().name("仅自身租户").dataScope("OnlySelfTenant")
                        .data().name("仅自身部门").dataScope("OnlySelfDept")
                        .data().name("子部门").dataScope("ChildDept")
                        .data().name("父部门").dataScope("ParentDept")
                        .data().name("仅自身").dataScope("OnlySelf")
                        .btn().name("登录日志查询").permission("admin.loginLog.query")
                        .btn().name("登录日志删除").permission("admin.loginLog.remove")
                    .end()

                    .menu().name("操作日志").permission("admin.operLog").path("operLog").component("System/OperLog")
                        .data().name("仅自身租户").dataScope("OnlySelfTenant")
                        .data().name("仅自身部门").dataScope("OnlySelfDept")
                        .data().name("子部门").dataScope("ChildDept")
                        .data().name("父部门").dataScope("ParentDept")
                        .data().name("仅自身").dataScope("OnlySelf")
                        .btn().name("操作日志查询").permission("admin.operLog.query")
                        .btn().name("操作日志删除").permission("admin.operLog.remove")
                    .end()

                    .menu().name("通知管理").permission("admin.notification").path("notification").component("System/Notification")
                        .data().name("仅自身租户").dataScope("OnlySelfTenant")
                        .data().name("仅自身部门").dataScope("OnlySelfDept")
                        .data().name("子部门").dataScope("ChildDept")
                        .data().name("父部门").dataScope("ParentDept")
                        .data().name("仅自身").dataScope("OnlySelf")
                        .btn().name("通知查询").permission("admin.notification.query")
                        .btn().name("通知新增").permission("admin.notification.add")
                        .btn().name("通知编辑").permission("admin.notification.update")
                        .btn().name("通知删除").permission("admin.notification.remove")
                    .end()

                .end()

                .build();
    }
}
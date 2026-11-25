package com.bkbits.admin.bootstrap;

import com.bkbits.admin.AdminParamConstants;
import com.bkbits.dbo.constants.BaseConstants;
import com.bkbits.dbo.entity.Dict;
import com.bkbits.dbo.entity.DictValue;
import com.bkbits.dbo.entity.Param;
import com.bkbits.dbo.entity.Permission;
import com.bkbits.dbo.entity.Role;
import com.bkbits.dbo.entity.RolePermissionRel;
import com.bkbits.dbo.entity.User;
import com.bkbits.dbo.entity.UserRoleRel;
import com.bkbits.encrypt.IPasswordEncrypt;
import com.bkbits.id.IdUtil;
import com.easy.query.api.proxy.client.EasyEntityQuery;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Inject;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class AdminBootstrap {

    private static final String ADMIN_ROLE_CODE = "admin";
    private static final String ADMIN_ROLE_NAME = "管理员";
    private static final String ADMIN_USER_NAME = "admin";
    private static final String ADMIN_USER_PASSWORD = "admin@123";

    @Inject
    private MenuRegistry menuRegistry;

    @Inject
    private EasyEntityQuery easyEntityQuery;

    @Inject
    private IPasswordEncrypt passwordEncrypt;

    @Init
    public void start() {

        MenuBuilded builded = MenuBuilder.builder()
                .dir().name("个人中心").path("/my")
                    .menu().name("仪表盘").path("dashboard").component("My/DashBoard")
                    .end()
                    .menu().name("我的信息").path("userinfo").component("My/UserInfo")
                    .end()
                    .menu().name("我的通知").path("notification").component("My/Notification")
                .end()
                .end()

                .dir().name("系统管理").path("/sys")

                    .menu().name("部门管理").permission("admin.dept").path("dept").component("System/Dept")
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
                        .data().name("仅自身部门").dataScope("OnlySelfDept")
                        .data().name("子部门").dataScope("ChildDept")
                        .data().name("父部门").dataScope("ParentDept")
                        .data().name("仅自身").dataScope("OnlySelf")
                        .btn().name("用户查询").permission("admin.user.query")
                        .btn().name("用户新增").permission("admin.user.add")
                        .btn().name("用户编辑").permission("admin.user.update")
                        .btn().name("用户删除").permission("admin.user.remove")
                        .btn().name("在线用户查询").permission("admin.user.queryLoginUser")
                        .btn().name("重置密码").permission("admin.user.resetPassword")
                        .btn().name("分配角色").permission("admin.user.bind")
                    .end()

                    .menu().name("角色管理").permission("admin.role").path("role").component("System/Role")
                        .data().name("仅自身部门").dataScope("OnlySelfDept")
                        .data().name("子部门").dataScope("ChildDept")
                        .data().name("父部门").dataScope("ParentDept")
                        .data().name("仅自身").dataScope("OnlySelf")
                        .btn().name("角色查询").permission("admin.role.query")
                        .btn().name("角色新增").permission("admin.role.add")
                        .btn().name("角色编辑").permission("admin.role.update")
                        .btn().name("角色删除").permission("admin.role.remove")
                        .btn().name("分配权限").permission("admin.role.bind")
                    .end()

                    .menu().name("菜单管理").permission("admin.permission").path("permission").component("System/Permission")
                        .data().name("仅自身部门").dataScope("OnlySelfDept")
                        .data().name("子部门").dataScope("ChildDept")
                        .data().name("父部门").dataScope("ParentDept")
                        .data().name("仅自身").dataScope("OnlySelf")
                        .btn().name("菜单查询").permission("admin.permission.query")
                        .btn().name("菜单新增").permission("admin.permission.add")
                        .btn().name("菜单编辑").permission("admin.permission.update")
                        .btn().name("菜单删除").permission("admin.permission.remove")
                    .end()

                    .menu().name("字典管理").permission("admin.dict").path("dict").component("System/Dict")
                        .data().name("仅自身部门").dataScope("OnlySelfDept")
                        .data().name("子部门").dataScope("ChildDept")
                        .data().name("父部门").dataScope("ParentDept")
                        .data().name("仅自身").dataScope("OnlySelf")
                        .btn().name("字典查询").permission("admin.dict.query")
                        .btn().name("字典新增").permission("admin.dict.add")
                        .btn().name("字典编辑").permission("admin.dict.update")
                        .btn().name("字典删除").permission("admin.dict.remove")
                    .end()

                    .menu().name("参数管理").permission("admin.param").path("param").component("System/Param")
                        .data().name("仅自身部门").dataScope("OnlySelfDept")
                        .data().name("子部门").dataScope("ChildDept")
                        .data().name("父部门").dataScope("ParentDept")
                        .data().name("仅自身").dataScope("OnlySelf")
                        .btn().name("参数查询").permission("admin.param.query")
                        .btn().name("参数新增").permission("admin.param.add")
                        .btn().name("参数编辑").permission("admin.param.update")
                        .btn().name("参数删除").permission("admin.param.remove")
                    .end()

                    .menu().name("登录日志").permission("admin.loginLog").path("loginLog").component("System/LoginLog")
                        .data().name("仅自身部门").dataScope("OnlySelfDept")
                        .data().name("子部门").dataScope("ChildDept")
                        .data().name("父部门").dataScope("ParentDept")
                        .data().name("仅自身").dataScope("OnlySelf")
                        .btn().name("登录日志查询").permission("admin.loginLog.query")
                        .btn().name("登录日志删除").permission("admin.loginLog.remove")
                    .end()

                    .menu().name("操作日志").permission("admin.operLog").path("operLog").component("System/OperLog")
                        .data().name("仅自身部门").dataScope("OnlySelfDept")
                        .data().name("子部门").dataScope("ChildDept")
                        .data().name("父部门").dataScope("ParentDept")
                        .data().name("仅自身").dataScope("OnlySelf")
                        .btn().name("操作日志查询").permission("admin.operLog.query")
                        .btn().name("操作日志删除").permission("admin.operLog.remove")
                    .end()

                    .menu().name("通知管理").permission("admin.notification").path("notification").component("System/Notification")
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

        menuRegistry.register(builded.getDataScopes());

        initPermissions(builded.getPermissions());
        Role adminRole = initAdminRole();
        initAdminUser(adminRole);
        initDicts();
        initParams();
    }

    /**
     * 权限表为空时初始化菜单权限。
     *
     * @param permissions 菜单权限（深度优先展平）
     */
    private void initPermissions(List<Permission> permissions) {
        if (easyEntityQuery.queryable(Permission.class).count() > 0) {
            return;
        }

        for (Permission permission : permissions) {
            if (permission.getId() == null) {
                permission.setId(IdUtil.nextId());
            }
        }
        for (Permission permission : permissions) {
            if (permission.getChildren() == null) {
                continue;
            }
            for (Permission child : permission.getChildren()) {
                child.setParentId(permission.getId());
            }
        }

        easyEntityQuery.insertable(permissions).executeRows();
    }

    /**
     * 获取 admin 角色；不存在时创建并绑定全部权限。
     *
     * @return admin 角色
     */
    private Role initAdminRole() {
        Role role = easyEntityQuery.queryable(Role.class)
                .where(r -> r.code().eq(ADMIN_ROLE_CODE))
                .singleOrNull();
        if (role != null) {
            return role;
        }

        role = new Role();
        role.setCode(ADMIN_ROLE_CODE);
        role.setName(ADMIN_ROLE_NAME);
        role.setSort(0);
        role.setStatus(BaseConstants.STATUS_ENABLED);
        easyEntityQuery.insertable(role).executeRows();

        String roleId = role.getId();
        List<RolePermissionRel> rels = easyEntityQuery.queryable(Permission.class)
                .selectColumn(p -> p.id())
                .toList()
                .stream()
                .map(permissionId -> {
                    RolePermissionRel rel = new RolePermissionRel();
                    rel.setRoleId(roleId);
                    rel.setPermissionId(permissionId);
                    return rel;
                })
                .toList();
        if (!rels.isEmpty()) {
            easyEntityQuery.insertable(rels).executeRows();
        }

        return role;
    }

    /**
     * 获取 admin 用户；不存在时创建并绑定 admin 角色。
     *
     * @param adminRole admin 角色
     */
    private void initAdminUser(Role adminRole) {
        User user = easyEntityQuery.queryable(User.class)
                .where(u -> u.userName().eq(ADMIN_USER_NAME))
                .singleOrNull();
        if (user != null) {
            return;
        }

        user = new User();
        user.setUserName(ADMIN_USER_NAME);
        user.setPassword(passwordEncrypt.hash(ADMIN_USER_PASSWORD));
        user.setRealName(ADMIN_ROLE_NAME);
        user.setSex("U");
        user.setStatus(BaseConstants.STATUS_ENABLED);
        easyEntityQuery.insertable(user).executeRows();

        UserRoleRel rel = new UserRoleRel();
        rel.setUserId(user.getUserId());
        rel.setRoleId(adminRole.getId());
        easyEntityQuery.insertable(rel).executeRows();
    }

    /**
     * 按 {@link BaseConstants} 初始化系统字典；字典键已存在时跳过。
     */
    private void initDicts() {
        seedDict("gender", "性别", 1, "用户性别",
                dictValue("M", "男", 1, "#1677ff"),
                dictValue("F", "女", 2, "#eb2f96"),
                dictValue("U", "未知", 3, "#8c8c8c"));
        seedDict("status", "通用状态", 2, "启用/禁用",
                dictValue("E", "启用", 1, "#52c41a"),
                dictValue("D", "禁用", 2, "#ff4d4f"));
        seedDict("permission_type", "权限类型", 3, "目录/菜单/按钮",
                dictValue("D", "目录", 1, "#722ed1"),
                dictValue("M", "菜单", 2, "#1677ff"),
                dictValue("B", "按钮", 3, "#13c2c2"));
        seedDict("data_permission_target", "数据权限目标", 4, "数据权限绑定目标",
                dictValue("A", "绑定所有", 1, "#1677ff"),
                dictValue("R", "角色绑定", 2, "#52c41a"),
                dictValue("P", "权限绑定", 3, "#faad14"));
        seedDict("notification_type", "通知类型", 5, "站内消息/部门通知/用户通知",
                dictValue("M", "站内消息", 1, "#1677ff"),
                dictValue("D", "部门通知", 2, "#52c41a"),
                dictValue("U", "用户通知", 3, "#faad14"));
        seedDict("param_type", "参数类型", 6, "系统参数/用户参数",
                dictValue("S", "系统参数", 1, "#1677ff"),
                dictValue("U", "用户参数", 2, "#52c41a"));
        seedDict("dict_type", "字典类型", 7, "系统字典/用户字典",
                dictValue("S", "系统字典", 1, "#1677ff"),
                dictValue("U", "用户字典", 2, "#52c41a"));
        seedDict("label_type", "标签类型", 8, "成功/信息/警告/错误",
                dictValue("S", "成功", 1, "#52c41a"),
                dictValue("I", "信息", 2, "#1677ff"),
                dictValue("W", "警告", 3, "#faad14"),
                dictValue("E", "错误", 4, "#ff4d4f"));
    }

    /**
     * 按 {@link AdminParamConstants} 初始化系统参数；参数键已存在时跳过。
     */
    private void initParams() {
        seedParam(AdminParamConstants.PARAM_LOGIN_PHONE, "手机号登录", 1,
                String.valueOf(AdminParamConstants.PARAM_LOGIN_PHONE_DEFAULT),
                "是否允许使用手机号登录");
        seedParam(AdminParamConstants.PARAM_LOGIN_EMAIL, "邮箱登录", 2,
                String.valueOf(AdminParamConstants.PARAM_LOGIN_EMAIL_DEFAULT),
                "是否允许使用邮箱登录");
        seedParam(AdminParamConstants.PARAM_LOGIN_CAPTCHA, "验证码登录", 3,
                String.valueOf(AdminParamConstants.PARAM_LOGIN_CAPTCHA_DEFAULT),
                "是否开启登录验证码");
    }

    /**
     * 字典不存在时创建字典及其字典值。
     *
     * @param dictKey 字典键
     * @param name    字典名称
     * @param sort    排序
     * @param remark  备注
     * @param values  字典值
     */
    private void seedDict(String dictKey, String name, int sort, String remark, DictValueSeed... values) {
        boolean exists = easyEntityQuery.queryable(Dict.class)
                .where(d -> d.dictKey().eq(dictKey))
                .any();
        if (exists) {
            return;
        }

        Dict dict = new Dict();
        dict.setDictKey(dictKey);
        dict.setName(name);
        dict.setSort(sort);
        dict.setType(BaseConstants.DICT_TYPE_SYSTEM);
        dict.setRemark(remark);
        easyEntityQuery.insertable(dict).executeRows();

        List<DictValue> dictValues = new ArrayList<>(values.length);
        for (DictValueSeed seed : values) {
            DictValue dictValue = new DictValue();
            dictValue.setDictId(dict.getId());
            dictValue.setValueKey(seed.valueKey());
            dictValue.setName(seed.name());
            dictValue.setSort(seed.sort());
            dictValue.setValue(seed.valueKey());
            dictValue.setColor(seed.color());
            dictValues.add(dictValue);
        }
        easyEntityQuery.insertable(dictValues).executeRows();
    }

    /**
     * 参数不存在时创建系统参数。
     *
     * @param paramKey 参数键
     * @param name     参数名称
     * @param sort     排序
     * @param value    参数值
     * @param remark   备注
     */
    private void seedParam(String paramKey, String name, int sort, String value, String remark) {
        boolean exists = easyEntityQuery.queryable(Param.class)
                .where(p -> p.paramKey().eq(paramKey))
                .any();
        if (exists) {
            return;
        }

        Param param = new Param();
        param.setParamKey(paramKey);
        param.setName(name);
        param.setSort(sort);
        param.setValue(value);
        param.setType(BaseConstants.PARAM_TYPE_SYSTEM);
        param.setRemark(remark);
        easyEntityQuery.insertable(param).executeRows();
    }

    private DictValueSeed dictValue(String valueKey, String name, int sort, String color) {
        return new DictValueSeed(valueKey, name, sort, color);
    }

    /**
     * 字典值种子。
     *
     * @param valueKey 值键
     * @param name     值名称
     * @param sort     排序
     * @param color    颜色
     */
    private record DictValueSeed(String valueKey, String name, int sort, String color) {
    }
}
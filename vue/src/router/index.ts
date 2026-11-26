import { createRouter, createWebHistory, type RouteRecordRaw } from "vue-router";

/** 权限 component 字段 → 路由路径 映射（与后端权限表 component 字段对应） */
export const MENU_ROUTE_MAP: Record<string, string> = {
  "My/DashBoard": "/dashboard",
  "My/UserInfo": "/profile",
  "My/Notification": "",
  "System/Dept": "/system/dept",
  "System/User": "/system/user",
  "System/Role": "/system/role",
  "System/Permission": "/system/permission",
  "System/LoginLog": "/system/login-log",
  "System/OperLog": "/system/operation-log",
  "System/Dict": "/system/dict",
  "System/Param": "/system/param",
};

const routes: RouteRecordRaw[] = [
  {
    path: "/login",
    name: "login",
    component: () => import("@/views/Login.vue"),
    meta: { title: "登录" },
  },
  {
    path: "/register",
    name: "register",
    component: () => import("@/views/Register.vue"),
    meta: { title: "注册" },
  },
  {
    path: "/",
    component: () => import("@/layouts/AdminLayout.vue"),
    redirect: "/dashboard",
    children: [
      {
        path: "dashboard",
        name: "dashboard",
        component: () => import("@/views/Dashboard.vue"),
        meta: { title: "仪表盘" },
      },
      {
        path: "profile",
        name: "profile",
        component: () => import("@/views/Profile.vue"),
        meta: { title: "我的信息" },
      },
      {
        path: "system/user",
        name: "user-list",
        component: () => import("@/views/system/UserList.vue"),
        meta: { title: "用户管理" },
      },
      {
        path: "system/online-user",
        name: "online-user",
        component: () => import("@/views/system/OnlineUser.vue"),
        meta: { title: "在线用户" },
      },
      {
        path: "system/role",
        name: "role-list",
        component: () => import("@/views/system/RoleList.vue"),
        meta: { title: "角色管理" },
      },
      {
        path: "system/permission",
        name: "permission-list",
        component: () => import("@/views/system/PermissionList.vue"),
        meta: { title: "菜单管理" },
      },
      {
        path: "system/dept",
        name: "dept-list",
        component: () => import("@/views/system/DeptList.vue"),
        meta: { title: "部门管理" },
      },

      {
        path: "system/param",
        name: "param-list",
        component: () => import("@/views/system/ParamList.vue"),
        meta: { title: "参数管理" },
      },
      {
        path: "system/dict",
        name: "dict-list",
        component: () => import("@/views/system/DictList.vue"),
        meta: { title: "字典管理" },
      },
      {
        path: "system/login-log",
        name: "login-log",
        component: () => import("@/views/system/LoginLog.vue"),
        meta: { title: "登录日志" },
      },
      {
        path: "system/operation-log",
        name: "operation-log",
        component: () => import("@/views/system/OperationLog.vue"),
        meta: { title: "操作日志" },
      },
    ],
  },
  {
    path: "/:pathMatch(.*)*",
    name: "not-found",
    component: () => import("@/views/NotFound.vue"),
    meta: { title: "页面不存在" },
  },
];

export const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.afterEach((to) => {
  const appTitle = import.meta.env.VITE_TITLE;
  const title = typeof to.meta.title === "string" ? to.meta.title : appTitle;
  document.title = `${title} - ${appTitle}`;
});

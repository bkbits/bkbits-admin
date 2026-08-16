/**
 * mock 接口路由表：实现后端 Swagger 全部接口 + 少量前端扩展接口。
 * 仅运行于 Vite dev/preview 服务的 Node 环境。
 */
import type { Dept, Dict, Permission, Role, Tenant, User } from "../api/types";
import type { MockContext } from "./helpers";
import { fail, filterRows, now, ok, paginate } from "./helpers";
import { db, type MockUser } from "./store";

export type MockHandler = (
  ctx: MockContext,
) => Record<string, unknown> | Promise<Record<string, unknown>>;

export interface MockRoute {
  method: string;
  path: string;
  handler: MockHandler;
}

export const routes: MockRoute[] = [];

function route(method: string, path: string, handler: MockHandler) {
  routes.push({ method, path, handler });
}

/* ------------------------------ 通用辅助 ------------------------------ */

/** 从 token 反查当前登录用户（不存在返回 undefined） */
function currentUser(ctx: MockContext): MockUser | undefined {
  if (!ctx.token) return undefined;
  const userId = db.tokens.get(ctx.token);
  if (!userId) return undefined;
  return db.users.find((u) => u.userId === userId);
}

/** 当前用户名（用于日志） */
function currentUserName(ctx: MockContext): string {
  return currentUser(ctx)?.userName ?? "anonymous";
}

/** 记录操作日志 */
function logOperation(ctx: MockContext, module: string, action: string, method = "POST") {
  const req = ctx.req;
  db.operationLogs.unshift({
    id: String(Date.now()),
    userName: currentUserName(ctx),
    module,
    action,
    method,
    url: req.url?.split("?")[0] ?? "",
    ip: req.socket.remoteAddress?.replace("::ffff:", "") ?? "127.0.0.1",
    duration: 10 + Math.floor(Math.random() * 150),
    status: "success",
    createTime: now(),
  });
}

/** 去除 password 字段的用户对象 */
function toPublicUser(u: MockUser): User {
  const { password: _p, ...rest } = u;
  return rest;
}

/** 实体转 VO（保留字段子集由调用方决定，这里做浅拷贝） */
function toVO<T>(obj: T): T {
  return { ...obj };
}

/** 组装部门树（用于 getById 返回 children） */
function deptChildren(deptId: string): Dept[] {
  return db.depts
    .filter((d) => d.parentId === deptId)
    .map((d) => ({ ...d, children: deptChildren(d.deptId ?? "") }));
}

/* ------------------------------ 认证 ------------------------------ */

route("POST", "/api/login", (ctx) => {
  const { username, phone, email, password } = ctx.body as Record<string, string>;
  const user = db.users.find(
    (u) =>
      u.status === "E" &&
      (u.userName === username ||
        (phone ? u.phone === phone : false) ||
        (email ? u.email === email : false)),
  );
  if (!user || user.password !== password) {
    db.loginLogs.unshift({
      id: String(Date.now()),
      userName: username ?? phone ?? email ?? "",
      ip: ctx.req.socket.remoteAddress?.replace("::ffff:", "") ?? "127.0.0.1",
      device: (ctx.req.headers["user-agent"] ?? "unknown").slice(0, 60),
      loginTime: now(),
      success: false,
      message: "用户名或密码错误",
    });
    return fail("用户名或密码错误", 400);
  }
  const token = `mock-token-${user.userId}-${Date.now()}`;
  db.tokens.set(token, user.userId ?? "");
  db.onlineUsers = db.onlineUsers.filter((o) => o.userId !== user.userId);
  db.onlineUsers.unshift({
    token,
    userId: user.userId,
    userName: user.userName,
    loginTime: now(),
    ip: ctx.req.socket.remoteAddress?.replace("::ffff:", "") ?? "127.0.0.1",
    device: (ctx.req.headers["user-agent"] ?? "unknown").slice(0, 60),
    deptId: user.deptId,
    tenantId: user.tenantId,
  });
  db.loginLogs.unshift({
    id: String(Date.now() + 1),
    userName: user.userName ?? "",
    ip: ctx.req.socket.remoteAddress?.replace("::ffff:", "") ?? "127.0.0.1",
    device: (ctx.req.headers["user-agent"] ?? "unknown").slice(0, 60),
    loginTime: now(),
    success: true,
    message: "登录成功",
  });
  const online = db.onlineUsers.find((o) => o.userId === user.userId);
  return ok(online, "登录成功");
});

route("POST", "/api/register", (ctx) => {
  const body = ctx.body as Record<string, string>;
  if (!body.username || !body.password) {
    return fail("用户名与密码不能为空", 400);
  }
  if (db.users.some((u) => u.userName === body.username)) {
    return fail("用户名已存在", 400);
  }
  db.users.push({
    userId: String(Number(db.users[db.users.length - 1]?.userId ?? "10000") + 1),
    userName: body.username,
    password: body.password,
    email: body.email,
    phone: body.phone,
    realName: body.realName ?? body.username,
    sex: body.sex ?? "M",
    status: "E",
    tenantId: "1",
    deptId: "101",
    createBy: body.username,
    createTime: now(),
    updateBy: body.username,
    updateTime: now(),
  });
  db.loginLogs.unshift({
    id: String(Date.now() + 2),
    userName: body.username,
    ip: ctx.req.socket.remoteAddress?.replace("::ffff:", "") ?? "127.0.0.1",
    device: (ctx.req.headers["user-agent"] ?? "unknown").slice(0, 60),
    loginTime: now(),
    success: true,
    message: "注册成功",
  });
  return ok(null, "注册成功");
});

route("POST", "/api/logout", (ctx) => {
  if (ctx.token) {
    const userId = db.tokens.get(ctx.token);
    db.tokens.delete(ctx.token);
    if (userId) {
      db.onlineUsers = db.onlineUsers.filter((o) => o.userId !== userId);
    }
  }
  return ok(null, "注销成功");
});

route("GET", "/api/loginUser", (ctx) => {
  const user = currentUser(ctx);
  if (!user) return fail("未登录或登录已过期", 401);
  const online = db.onlineUsers.find((o) => o.userId === user.userId);
  return ok(online ?? null);
});

route("GET", "/api/publicKey", () =>
  ok(
    "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAu1SU1LfVLPHCozMxH2Mo\n4lgOEePzNm0tRgeLezV6ffAt0gunVTLw7onLRnrq0/IzW7yWR7QkrmBL7jTKEn5u\n+qKhbwKfBstIs+bMY2Zkp18gnTxKLxoS2tFczGkPLPgizskuemMghRniWaoLcyeh\nkd3qqGElvW/VDL5AaWTg0nLVkjRo9z+40RQzuVaE8AkAFmxZzow3x+VJYKdjykkJ\n0iT9wCS0DRTXu269V264Vf/3jvredZiKRkgwlL9xNAwxXFg0x/XFw005UWVRIkdg\ncKWTjpBP2dPwVZ4WWC+9aGVd+Gyn1o0CLelf4rEjGoXbAAEgAqeGUxrcIlbjXfbc\nmwIDAQAB\n-----END PUBLIC KEY-----",
  ),
);

/* ------------------------------ 用户 ------------------------------ */

route("GET", "/api/user/query", (ctx) => {
  const { page, pageSize, ...query } = ctx.query;
  const rows = filterRows(db.users.map(toPublicUser), query);
  return ok(paginate(rows, page, pageSize));
});

function findUserBy(key: string, value: string) {
  const user = db.users.find((u) => {
    const v = u[key as keyof MockUser];
    return typeof v === "string" && v === value;
  });
  if (!user) return fail("用户不存在", 404);
  return ok(toVO(toPublicUser(user)));
}

route("GET", "/api/user/getByUserId", (ctx) => findUserBy("userId", ctx.query.userId ?? ""));
route("GET", "/api/user/getByUserName", (ctx) => findUserBy("userName", ctx.query.userName ?? ""));
route("GET", "/api/user/getByEmail", (ctx) => findUserBy("email", ctx.query.email ?? ""));
route("GET", "/api/user/getByPhone", (ctx) => findUserBy("phone", ctx.query.phone ?? ""));

route("POST", "/api/user/add", (ctx) => {
  const body = ctx.body as Record<string, string>;
  if (!body.userName) return fail("用户名不能为空", 400);
  if (db.users.some((u) => u.userName === body.userName)) return fail("用户名已存在", 400);
  db.users.push({
    userId: String(Number(db.users[db.users.length - 1]?.userId ?? "10000") + 1),
    userName: body.userName,
    password: body.password ?? "123456",
    email: body.email,
    phone: body.phone,
    realName: body.realName,
    sex: body.sex,
    status: body.status ?? "E",
    tenantId: body.tenantId ?? "1",
    deptId: body.deptId ?? "101",
    createBy: currentUserName(ctx),
    createTime: now(),
    updateBy: currentUserName(ctx),
    updateTime: now(),
  });
  logOperation(ctx, "用户管理", `新增用户 ${body.userName}`);
  return ok(null, "新增用户成功");
});

route("POST", "/api/user/update", (ctx) => {
  const body = ctx.body as Record<string, string>;
  const user = db.users.find((u) => u.userId === body.userId);
  if (!user) return fail("用户不存在", 404);
  Object.assign(user, {
    userName: body.userName ?? user.userName,
    email: body.email ?? user.email,
    phone: body.phone ?? user.phone,
    realName: body.realName ?? user.realName,
    sex: body.sex ?? user.sex,
    status: body.status ?? user.status,
    tenantId: body.tenantId ?? user.tenantId,
    deptId: body.deptId ?? user.deptId,
    updateBy: currentUserName(ctx),
    updateTime: now(),
  });
  if (body.password) user.password = body.password;
  logOperation(ctx, "用户管理", `修改用户 ${user.userName}`);
  return ok(null, "更新用户成功");
});

route("POST", "/api/user/remove", (ctx) => {
  const { id } = ctx.body as { id: string };
  const user = db.users.find((u) => u.userId === id);
  if (!user) return fail("用户不存在", 404);
  if (user.userName === "admin") return fail("内置管理员账号不允许删除", 400);
  db.users = db.users.filter((u) => u.userId !== id);
  db.roleBindings.delete(id);
  db.onlineUsers = db.onlineUsers.filter((o) => o.userId !== id);
  logOperation(ctx, "用户管理", `删除用户 ${user.userName}`);
  return ok(null, "删除用户成功");
});

route("POST", "/api/user/resetPassword", (ctx) => {
  const { userId, password } = ctx.body as { userId: string; password: string };
  const user = db.users.find((u) => u.userId === userId);
  if (!user) return fail("用户不存在", 404);
  if (!password) return fail("新密码不能为空", 400);
  user.password = password;
  user.updateBy = currentUserName(ctx);
  user.updateTime = now();
  logOperation(ctx, "用户管理", `重置用户 ${user.userName} 密码`);
  return ok(null, "重置密码成功");
});

route("POST", "/api/user/bindRole", (ctx) => {
  const { userId, roleIds } = ctx.body as { userId: string; roleIds: string[] };
  const user = db.users.find((u) => u.userId === userId);
  if (!user) return fail("用户不存在", 404);
  db.roleBindings.set(userId, roleIds ?? []);
  logOperation(ctx, "用户管理", `绑定用户 ${user.userName} 角色`);
  return ok(null, "绑定角色成功");
});

route("GET", "/api/user/listRoles", (ctx) => {
  const { userId } = ctx.query;
  const roleIds = db.roleBindings.get(userId ?? "") ?? [];
  return ok(db.roles.filter((r) => roleIds.includes(r.id ?? "")));
});

route("POST", "/api/user/queryLoginUser", (ctx) => {
  const body = (ctx.body ?? {}) as Record<string, string>;
  const page = body.page ?? ctx.query.page;
  const pageSize = body.pageSize ?? ctx.query.pageSize;
  const { page: _p, pageSize: _ps, ...query } = { ...ctx.query, ...body };
  const rows = filterRows(db.onlineUsers, query);
  return ok(paginate(rows, page, pageSize));
});

route("POST", "/api/user/updateMyPassword", (ctx) => {
  const user = currentUser(ctx);
  if (!user) return fail("未登录或登录已过期", 401);
  const { oldPassword, password } = ctx.body as { oldPassword: string; password: string };
  if (user.password !== oldPassword) return fail("原密码错误", 400);
  if (!password) return fail("新密码不能为空", 400);
  user.password = password;
  user.updateBy = user.userName;
  user.updateTime = now();
  logOperation(ctx, "个人中心", "修改密码");
  return ok(null, "密码修改成功");
});

/* ------------------------------ 角色 ------------------------------ */

route("GET", "/api/role/query", (ctx) => {
  const { page, pageSize, ...query } = ctx.query;
  const rows = filterRows(db.roles, query);
  return ok(paginate(rows, page, pageSize));
});

route("GET", "/api/role/getById", (ctx) => {
  const role = db.roles.find((r) => r.id === ctx.query.id);
  if (!role) return fail("角色不存在", 404);
  return ok(toVO(role));
});

route("POST", "/api/role/add", (ctx) => {
  const body = ctx.body as Record<string, string>;
  if (!body.name) return fail("角色名不能为空", 400);
  if (db.roles.some((r) => r.code === body.code)) return fail("角色代码已存在", 400);
  const role: Role = {
    id: String(Number(db.roles[db.roles.length - 1]?.id ?? "20000") + 1),
    tenantId: "1",
    code: body.code ?? "",
    name: body.name,
    sort: Number(body.sort ?? 0),
    status: body.status ?? "E",
    createBy: currentUserName(ctx),
    createTime: now(),
    updateBy: currentUserName(ctx),
    updateTime: now(),
  };
  db.roles.push(role);
  logOperation(ctx, "角色管理", `新增角色 ${role.name}`);
  return ok(null, "新增角色成功");
});

route("POST", "/api/role/update", (ctx) => {
  const body = ctx.body as Record<string, string>;
  const role = db.roles.find((r) => r.id === body.id);
  if (!role) return fail("角色不存在", 404);
  Object.assign(role, {
    code: body.code ?? role.code,
    name: body.name ?? role.name,
    sort: Number(body.sort ?? role.sort),
    status: body.status ?? role.status,
    updateBy: currentUserName(ctx),
    updateTime: now(),
  });
  logOperation(ctx, "角色管理", `修改角色 ${role.name}`);
  return ok(null, "更新角色成功");
});

route("POST", "/api/role/remove", (ctx) => {
  const { id } = ctx.body as { id: string };
  const role = db.roles.find((r) => r.id === id);
  if (!role) return fail("角色不存在", 404);
  for (const roleIds of db.roleBindings.values()) {
    if (roleIds.includes(id)) return fail(`角色「${role.name}」已绑定用户，无法删除`, 400);
  }
  db.roles = db.roles.filter((r) => r.id !== id);
  db.permissionBindings.delete(id);
  logOperation(ctx, "角色管理", `删除角色 ${role.name}`);
  return ok(null, "删除角色成功");
});

route("POST", "/api/role/bindPermissions", (ctx) => {
  const { roleId, permissionIds } = ctx.body as { roleId: string; permissionIds: string[] };
  if (!db.roles.some((r) => r.id === roleId)) return fail("角色不存在", 404);
  db.permissionBindings.set(roleId, permissionIds ?? []);
  logOperation(ctx, "角色管理", "绑定角色权限");
  return ok(null, "绑定权限成功");
});

route("POST", "/api/role/bindDataPermissions", (ctx) => {
  const { roleId, permissionId, dataPermissionIds } = ctx.body as {
    roleId: string;
    permissionId: string;
    dataPermissionIds: string[];
  };
  if (!db.roles.some((r) => r.id === roleId)) return fail("角色不存在", 404);
  db.dataPermissionBindings.set(`${roleId}::${permissionId}`, dataPermissionIds ?? []);
  logOperation(ctx, "角色管理", "绑定角色数据权限");
  return ok(null, "绑定数据权限成功");
});

route("GET", "/api/role/listPermissionIds", (ctx) =>
  ok(db.permissionBindings.get(ctx.query.roleId ?? "") ?? []),
);

route("GET", "/api/role/listDataPermissionIds", (ctx) =>
  ok(
    db.dataPermissionBindings.get(`${ctx.query.roleId ?? ""}::${ctx.query.permissionId ?? ""}`) ??
      [],
  ),
);

/* ------------------------------ 权限 ------------------------------ */

route("GET", "/api/permission/list", (ctx) => {
  const query = ctx.query;
  const rows = filterRows(db.permissions, query, ["permission", "name"]);
  return ok(rows);
});

route("GET", "/api/permission/getById", (ctx) => {
  const permission = db.permissions.find((p) => p.id === ctx.query.id);
  if (!permission) return fail("权限不存在", 404);
  return ok(toVO(permission));
});

route("POST", "/api/permission/add", (ctx) => {
  const body = ctx.body as Record<string, string>;
  const permission: Permission = {
    id: String(Number(db.permissions[db.permissions.length - 1]?.id ?? "0") + 1),
    parentId: body.parentId ?? "0",
    type: body.type ?? "MENU",
    permission: body.permission ?? "",
    name: body.name ?? "",
    sort: Number(body.sort ?? 0),
    component: body.component ?? "",
    status: body.status ?? "E",
    createBy: currentUserName(ctx),
    createTime: now(),
    updateBy: currentUserName(ctx),
    updateTime: now(),
  };
  db.permissions.push(permission);
  logOperation(ctx, "权限管理", `新增权限 ${permission.name}`);
  return ok(null, "新增权限成功");
});

route("POST", "/api/permission/update", (ctx) => {
  const body = ctx.body as Record<string, string>;
  const permission = db.permissions.find((p) => p.id === body.id);
  if (!permission) return fail("权限不存在", 404);
  Object.assign(permission, {
    parentId: body.parentId ?? permission.parentId,
    type: body.type ?? permission.type,
    permission: body.permission ?? permission.permission,
    name: body.name ?? permission.name,
    sort: Number(body.sort ?? permission.sort),
    component: body.component ?? permission.component,
    status: body.status ?? permission.status,
    updateBy: currentUserName(ctx),
    updateTime: now(),
  });
  logOperation(ctx, "权限管理", `修改权限 ${permission.name}`);
  return ok(null, "更新权限成功");
});

route("POST", "/api/permission/remove", (ctx) => {
  const { id } = ctx.body as { id: string };
  const permission = db.permissions.find((p) => p.id === id);
  if (!permission) return fail("权限不存在", 404);
  if (db.permissions.some((p) => p.parentId === id)) {
    return fail("存在子权限，请先删除子权限", 400);
  }
  db.permissions = db.permissions.filter((p) => p.id !== id);
  for (const [roleId, ids] of db.permissionBindings) {
    db.permissionBindings.set(
      roleId,
      ids.filter((pid) => pid !== id),
    );
  }
  logOperation(ctx, "权限管理", `删除权限 ${permission.name}`);
  return ok(null, "删除权限成功");
});

/* ------------------------------ 部门 ------------------------------ */

route("GET", "/api/dept/query", (ctx) => {
  const { page, pageSize, ...query } = ctx.query;
  const rows = filterRows(db.depts, query);
  return ok(paginate(rows, page, pageSize));
});

route("GET", "/api/dept/getById", (ctx) => {
  const dept = db.depts.find((d) => d.deptId === ctx.query.deptId);
  if (!dept) return fail("部门不存在", 404);
  return ok({ ...dept, children: deptChildren(dept.deptId ?? "") });
});

route("POST", "/api/dept/add", (ctx) => {
  const body = ctx.body as Record<string, string>;
  if (!body.name) return fail("部门名称不能为空", 400);
  const dept: Dept = {
    deptId: String(Number(db.depts[db.depts.length - 1]?.deptId ?? "100") + 1),
    parentId: body.parentId ?? "0",
    tenantId: body.tenantId ?? "1",
    name: body.name,
    sort: Number(body.sort ?? 0),
    status: body.status ?? "E",
    createBy: currentUserName(ctx),
    createTime: now(),
    updateBy: currentUserName(ctx),
    updateTime: now(),
  };
  db.depts.push(dept);
  logOperation(ctx, "部门管理", `新增部门 ${dept.name}`);
  return ok(null, "新增部门成功");
});

route("POST", "/api/dept/update", (ctx) => {
  const body = ctx.body as Record<string, string>;
  const dept = db.depts.find((d) => d.deptId === body.deptId);
  if (!dept) return fail("部门不存在", 404);
  Object.assign(dept, {
    parentId: body.parentId ?? dept.parentId,
    tenantId: body.tenantId ?? dept.tenantId,
    name: body.name ?? dept.name,
    sort: Number(body.sort ?? dept.sort),
    status: body.status ?? dept.status,
    updateBy: currentUserName(ctx),
    updateTime: now(),
  });
  logOperation(ctx, "部门管理", `修改部门 ${dept.name}`);
  return ok(null, "更新部门成功");
});

route("POST", "/api/dept/remove", (ctx) => {
  const { id } = ctx.body as { id: string };
  const dept = db.depts.find((d) => d.deptId === id);
  if (!dept) return fail("部门不存在", 404);
  if (db.depts.some((d) => d.parentId === id)) return fail("存在子部门，请先删除子部门", 400);
  if (db.users.some((u) => u.deptId === id)) return fail("部门下存在用户，无法删除", 400);
  db.depts = db.depts.filter((d) => d.deptId !== id);
  logOperation(ctx, "部门管理", `删除部门 ${dept.name}`);
  return ok(null, "删除部门成功");
});

/* ------------------------------ 租户 ------------------------------ */

route("GET", "/api/tenant/query", (ctx) => {
  const { page, pageSize, ...query } = ctx.query;
  const rows = filterRows(db.tenants, query);
  return ok(paginate(rows, page, pageSize));
});

route("GET", "/api/tenant/getById", (ctx) => {
  const tenant = db.tenants.find((t) => t.id === ctx.query.id);
  if (!tenant) return fail("租户不存在", 404);
  return ok(toVO(tenant));
});

route("POST", "/api/tenant/add", (ctx) => {
  const body = ctx.body as Record<string, string>;
  if (!body.name) return fail("租户名称不能为空", 400);
  const tenant: Tenant = {
    id: String(Number(db.tenants[db.tenants.length - 1]?.id ?? "0") + 1),
    type: body.type ?? "NORMAL",
    name: body.name,
    status: body.status ?? "E",
    createBy: currentUserName(ctx),
    createTime: now(),
    updateBy: currentUserName(ctx),
    updateTime: now(),
  };
  db.tenants.push(tenant);
  logOperation(ctx, "租户管理", `新增租户 ${tenant.name}`);
  return ok(null, "新增租户成功");
});

route("POST", "/api/tenant/update", (ctx) => {
  const body = ctx.body as Record<string, string>;
  const tenant = db.tenants.find((t) => t.id === body.id);
  if (!tenant) return fail("租户不存在", 404);
  Object.assign(tenant, {
    type: body.type ?? tenant.type,
    name: body.name ?? tenant.name,
    status: body.status ?? tenant.status,
    updateBy: currentUserName(ctx),
    updateTime: now(),
  });
  logOperation(ctx, "租户管理", `修改租户 ${tenant.name}`);
  return ok(null, "更新租户成功");
});

route("POST", "/api/tenant/remove", (ctx) => {
  const { id } = ctx.body as { id: string };
  const tenant = db.tenants.find((t) => t.id === id);
  if (!tenant) return fail("租户不存在", 404);
  if (tenant.type === "SYSTEM") return fail("系统租户不允许删除", 400);
  if (db.users.some((u) => u.tenantId === id)) return fail("租户下存在用户，无法删除", 400);
  db.tenants = db.tenants.filter((t) => t.id !== id);
  logOperation(ctx, "租户管理", `删除租户 ${tenant.name}`);
  return ok(null, "删除租户成功");
});

/* ------------------------------ 系统参数 ------------------------------ */

route("GET", "/api/param/query", (ctx) => {
  const { page, pageSize, ...query } = ctx.query;
  const rows = filterRows(db.params, query, ["paramKey", "name"]);
  return ok(paginate(rows, page, pageSize));
});

route("GET", "/api/param/getById", (ctx) => {
  const param = db.params.find((p) => p.id === ctx.query.id);
  if (!param) return fail("参数不存在", 404);
  return ok(toVO(param));
});

route("GET", "/api/param/getByKey", (ctx) => {
  const param = db.params.find((p) => p.paramKey === ctx.query.paramKey);
  if (!param) return fail("参数不存在", 404);
  return ok(toVO(param));
});

route("POST", "/api/param/add", (ctx) => {
  const body = ctx.body as Record<string, string>;
  if (!body.paramKey) return fail("参数键不能为空", 400);
  if (db.params.some((p) => p.paramKey === body.paramKey)) return fail("参数键已存在", 400);
  db.params.push({
    id: String(Number(db.params[db.params.length - 1]?.id ?? "40000") + 1),
    paramKey: body.paramKey,
    name: body.name ?? "",
    sort: Number(body.sort ?? 0),
    value: body.value ?? "",
    type: body.type ?? "BUSINESS",
    remark: body.remark,
    createBy: currentUserName(ctx),
    createTime: now(),
    updateBy: currentUserName(ctx),
    updateTime: now(),
  });
  logOperation(ctx, "系统参数", `新增参数 ${body.paramKey}`);
  return ok(null, "新增参数成功");
});

route("POST", "/api/param/update", (ctx) => {
  const body = ctx.body as Record<string, string>;
  const param = db.params.find((p) => p.id === body.id);
  if (!param) return fail("参数不存在", 404);
  Object.assign(param, {
    paramKey: body.paramKey ?? param.paramKey,
    name: body.name ?? param.name,
    sort: Number(body.sort ?? param.sort),
    value: body.value ?? param.value,
    type: body.type ?? param.type,
    remark: body.remark ?? param.remark,
    updateBy: currentUserName(ctx),
    updateTime: now(),
  });
  logOperation(ctx, "系统参数", `修改参数 ${param.paramKey}`);
  return ok(null, "更新参数成功");
});

route("POST", "/api/param/remove", (ctx) => {
  const { id } = ctx.body as { id: string };
  const param = db.params.find((p) => p.id === id);
  if (!param) return fail("参数不存在", 404);
  db.params = db.params.filter((p) => p.id !== id);
  logOperation(ctx, "系统参数", `删除参数 ${param.paramKey}`);
  return ok(null, "删除参数成功");
});

/* ------------------------------ 系统字典 ------------------------------ */

function dictToVO(dict: Dict): Dict {
  return { ...dict, valueList: db.dictValues.filter((v) => v.dictId === dict.id) };
}

route("GET", "/api/dict/query", (ctx) => {
  const { page, pageSize, ...query } = ctx.query;
  const rows = filterRows(db.dicts.map(dictToVO), query, ["dictKey", "name"]);
  return ok(paginate(rows, page, pageSize));
});

route("GET", "/api/dict/getByKey", (ctx) => {
  const dict = db.dicts.find((d) => d.dictKey === ctx.query.dictKey);
  if (!dict) return fail("字典不存在", 404);
  return ok(dictToVO(dict));
});

route("POST", "/api/dict/add", (ctx) => {
  const body = ctx.body as Record<string, string>;
  if (!body.dictKey) return fail("字典键不能为空", 400);
  if (db.dicts.some((d) => d.dictKey === body.dictKey)) return fail("字典键已存在", 400);
  db.dicts.push({
    id: String(Number(db.dicts[db.dicts.length - 1]?.id ?? "50000") + 1),
    dictKey: body.dictKey,
    name: body.name ?? "",
    sort: Number(body.sort ?? 0),
    type: body.type ?? "BUSINESS",
    remark: body.remark,
    createBy: currentUserName(ctx),
    createTime: now(),
    updateBy: currentUserName(ctx),
    updateTime: now(),
  });
  logOperation(ctx, "系统字典", `新增字典 ${body.dictKey}`);
  return ok(null, "新增字典成功");
});

route("POST", "/api/dict/update", (ctx) => {
  const body = ctx.body as Record<string, string>;
  const dict = db.dicts.find((d) => d.id === body.id);
  if (!dict) return fail("字典不存在", 404);
  Object.assign(dict, {
    dictKey: body.dictKey ?? dict.dictKey,
    name: body.name ?? dict.name,
    sort: Number(body.sort ?? dict.sort),
    type: body.type ?? dict.type,
    remark: body.remark ?? dict.remark,
    updateBy: currentUserName(ctx),
    updateTime: now(),
  });
  logOperation(ctx, "系统字典", `修改字典 ${dict.dictKey}`);
  return ok(null, "更新字典成功");
});

route("POST", "/api/dict/remove", (ctx) => {
  const { id } = ctx.body as { id: string };
  const dict = db.dicts.find((d) => d.id === id);
  if (!dict) return fail("字典不存在", 404);
  if (db.dictValues.some((v) => v.dictId === id))
    return fail("字典下存在字典值，请先删除字典值", 400);
  db.dicts = db.dicts.filter((d) => d.id !== id);
  logOperation(ctx, "系统字典", `删除字典 ${dict.dictKey}`);
  return ok(null, "删除字典成功");
});

route("GET", "/api/dict/value/list", (ctx) => {
  const dict = db.dicts.find((d) => d.dictKey === ctx.query.dictKey);
  if (!dict) return fail("字典不存在", 404);
  const rows = db.dictValues
    .filter((v) => v.dictId === dict.id)
    .sort((a, b) => (a.sort ?? 0) - (b.sort ?? 0));
  return ok(rows.map((v) => ({ ...v })));
});

route("POST", "/api/dict/value/add", (ctx) => {
  const body = ctx.body as Record<string, string>;
  const dict = db.dicts.find((d) => d.id === body.dictId);
  if (!dict) return fail("字典不存在", 404);
  db.dictValues.push({
    id: String(Number(db.dictValues[db.dictValues.length - 1]?.id ?? "51000") + 1),
    dictId: body.dictId,
    valueKey: body.valueKey ?? "",
    name: body.name ?? "",
    sort: Number(body.sort ?? 0),
    value: body.value ?? "",
    type: body.type,
    color: body.color,
    remark: body.remark,
  });
  logOperation(ctx, "系统字典", `新增字典值 ${dict.dictKey}.${body.valueKey}`);
  return ok(null, "新增字典值成功");
});

route("POST", "/api/dict/value/update", (ctx) => {
  const body = ctx.body as Record<string, string>;
  const value = db.dictValues.find((v) => v.id === body.id);
  if (!value) return fail("字典值不存在", 404);
  Object.assign(value, {
    valueKey: body.valueKey ?? value.valueKey,
    name: body.name ?? value.name,
    sort: Number(body.sort ?? value.sort),
    value: body.value ?? value.value,
    type: body.type ?? value.type,
    color: body.color ?? value.color,
    remark: body.remark ?? value.remark,
  });
  logOperation(ctx, "系统字典", `修改字典值 ${value.valueKey}`);
  return ok(null, "更新字典值成功");
});

route("POST", "/api/dict/value/remove", (ctx) => {
  const { id } = ctx.body as { id: string };
  const value = db.dictValues.find((v) => v.id === id);
  if (!value) return fail("字典值不存在", 404);
  db.dictValues = db.dictValues.filter((v) => v.id !== id);
  logOperation(ctx, "系统字典", `删除字典值 ${value.valueKey}`);
  return ok(null, "删除字典值成功");
});

/* ------------------------------ 数据权限 ------------------------------ */

route("GET", "/api/dataPermission/list", (ctx) => {
  const rows = db.dataPermissions.filter((d) => d.permissionId === ctx.query.permissionId);
  return ok(rows);
});

route("POST", "/api/dataPermission/add", (ctx) => {
  const body = ctx.body as Record<string, string>;
  db.dataPermissions.push({
    id: String(Number(db.dataPermissions[db.dataPermissions.length - 1]?.id ?? "30000") + 1),
    permissionId: body.permissionId ?? "",
    dataScope: body.dataScope ?? "ALL",
    status: body.status ?? "E",
    createBy: currentUserName(ctx),
    createTime: now(),
    updateBy: currentUserName(ctx),
    updateTime: now(),
  });
  logOperation(ctx, "权限管理", "新增数据权限");
  return ok(null, "新增数据权限成功");
});

route("POST", "/api/dataPermission/update", (ctx) => {
  const body = ctx.body as Record<string, string>;
  const dp = db.dataPermissions.find((d) => d.id === body.id);
  if (!dp) return fail("数据权限不存在", 404);
  Object.assign(dp, {
    dataScope: body.dataScope ?? dp.dataScope,
    status: body.status ?? dp.status,
    updateBy: currentUserName(ctx),
    updateTime: now(),
  });
  logOperation(ctx, "权限管理", "修改数据权限");
  return ok(null, "更新数据权限成功");
});

route("POST", "/api/dataPermission/remove", (ctx) => {
  const { id } = ctx.body as { id: string };
  const dp = db.dataPermissions.find((d) => d.id === id);
  if (!dp) return fail("数据权限不存在", 404);
  db.dataPermissions = db.dataPermissions.filter((d) => d.id !== id);
  for (const [key, ids] of db.dataPermissionBindings) {
    db.dataPermissionBindings.set(
      key,
      ids.filter((pid) => pid !== id),
    );
  }
  logOperation(ctx, "权限管理", "删除数据权限");
  return ok(null, "删除数据权限成功");
});

/* ------------------------------ 文件 ------------------------------ */

route("POST", "/api/file/upload", (ctx) => {
  const hash = ctx.query.hash ?? "unknown";
  const filename = ctx.files[0]?.filename ?? `file-${Date.now()}`;
  const file = {
    id: String(Number(db.files[db.files.length - 1]?.id ?? "80000") + 1),
    path: `/upload/${now().slice(0, 7).replace("-", "/")}/${filename}`,
    contentType:
      (ctx.req.headers["content-type"] ?? "").split(";")[0] ?? "application/octet-stream",
    fileSize: 0,
    fileName: filename,
    hash,
    createBy: currentUserName(ctx),
    createTime: now(),
  };
  db.files.push(file);
  return ok(file, "上传成功");
});

route("POST", "/api/file/task/create", (ctx) => {
  const body = ctx.body as Record<string, unknown>;
  const fileSize = Number(body.fileSize ?? 0);
  const pieceSize = Number(body.pieceSize ?? 5 * 1024 * 1024);
  const pieceHashes = (body.pieceHashes as string[] | undefined) ?? [];
  const pieceCount = Math.max(pieceHashes.length, Math.ceil(fileSize / pieceSize));
  const taskId = `task-${Date.now()}`;
  db.uploadTasks.set(taskId, {
    fileName: typeof body.fileName === "string" ? body.fileName : "file.bin",
    contentType:
      typeof body.contentType === "string" ? body.contentType : "application/octet-stream",
    pieceCount,
    uploaded: new Set<number>(),
  });
  return ok({ taskId, pieceCount, pieceSize }, "创建上传任务成功");
});

route("POST", "/api/file/task/upload", (ctx) => {
  const { taskId, fileIndex } = ctx.query;
  const task = db.uploadTasks.get(taskId ?? "");
  if (!task) return fail("上传任务不存在", 404);
  const index = Number(fileIndex);
  if (Number.isNaN(index) || index < 0 || index >= task.pieceCount) {
    return fail("分片编号超出范围", 400);
  }
  task.uploaded.add(index);
  return ok(null, `分片 ${index + 1}/${task.pieceCount} 上传成功`);
});

route("POST", "/api/file/task/finish", (ctx) => {
  const { taskId } = ctx.body as { taskId: string };
  const task = db.uploadTasks.get(taskId ?? "");
  if (!task) return fail("上传任务不存在", 404);
  if (task.uploaded.size < task.pieceCount) {
    return fail(`尚有 ${task.pieceCount - task.uploaded.size} 个分片未上传`, 400);
  }
  db.uploadTasks.delete(taskId);
  const file = {
    id: String(Number(db.files[db.files.length - 1]?.id ?? "80000") + 1),
    path: `/upload/${now().slice(0, 7).replace("-", "/")}/${task.fileName}`,
    contentType: task.contentType,
    fileSize: 0,
    fileName: task.fileName,
    hash: `merged-${taskId}`,
    createBy: currentUserName(ctx),
    createTime: now(),
  };
  db.files.push(file);
  return ok(file, "文件上传完成");
});

/* ------------------------------ 日志（mock 扩展） ------------------------------ */

route("GET", "/api/log/login/query", (ctx) => {
  const { page, pageSize, ...query } = ctx.query;
  const rows = filterRows(db.loginLogs, query, ["userName"]);
  return ok(paginate(rows, page, pageSize));
});

route("GET", "/api/log/operation/query", (ctx) => {
  const { page, pageSize, ...query } = ctx.query;
  const rows = filterRows(db.operationLogs, query, ["userName", "module"]);
  return ok(paginate(rows, page, pageSize));
});

/* ------------------------------ 通知公告（mock 扩展） ------------------------------ */

route("GET", "/api/notification/list", (ctx) => {
  const { page, pageSize, ...query } = ctx.query;
  const rows = filterRows(
    db.notifications.sort((a, b) => (b.publishTime ?? "").localeCompare(a.publishTime ?? "")),
    query,
    ["title", "content"],
  );
  return ok(paginate(rows, page, pageSize));
});

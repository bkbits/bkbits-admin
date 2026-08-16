import { defineGet, definePost, defineQuery } from "../define";
import type {
  BindDataPermissionsToRoleDTO,
  BindPermissionsToRoleDTO,
  IdDTO,
  PageData,
  PageParams,
  Role,
  RoleAddDTO,
  RoleUpdateDTO,
} from "../types";

/** 角色分页查询条件（mock 扩展：后端 Swagger 未提供角色分页查询接口） */
export interface RoleQueryDTO extends PageParams {
  code?: string;
  name?: string;
  status?: string;
}

/**
 * 分页查询角色（mock 扩展接口：后端 Swagger 无角色列表查询接口，
 * 依据 AGENTS.md「角色管理」补充，mock 层实现）。
 */
export const queryRole = defineQuery<RoleQueryDTO, PageData<Role>>("/api/role/query");

/** 按编号查询角色 */
export const getRoleById = defineGet<{ id: string }, Role>("/api/role/getById");

/** 新增角色 */
export const addRole = definePost<RoleAddDTO, unknown>("/api/role/add");

/** 更新角色 */
export const updateRole = definePost<RoleUpdateDTO, unknown>("/api/role/update");

/** 删除角色 */
export const removeRole = definePost<IdDTO, unknown>("/api/role/remove");

/** 绑定权限到角色 */
export const bindRolePermissions = definePost<BindPermissionsToRoleDTO, unknown>(
  "/api/role/bindPermissions",
);

/** 绑定数据权限到角色 */
export const bindRoleDataPermissions = definePost<BindDataPermissionsToRoleDTO, unknown>(
  "/api/role/bindDataPermissions",
);

/** 查询角色绑定的权限 id */
export const listRolePermissionIds = defineGet<{ roleId: string }, string[]>(
  "/api/role/listPermissionIds",
);

/** 查询角色菜单权限的数据权限关联 id */
export const listRoleDataPermissionIds = defineGet<
  { roleId: string; permissionId: string },
  string[]
>("/api/role/listDataPermissionIds");

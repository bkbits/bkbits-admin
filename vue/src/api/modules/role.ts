import { defineGet, definePost, defineQuery } from "../define";
import type {
  BindDataPermissionsToRoleDTO,
  BindPermissionsToRoleDTO,
  IdDTO,
  PageParams,
  Permission,
  Role,
  RoleAddDTO,
  RoleUpdateDTO,
  Result,
} from "../types";
import { flattenPermissionTree } from "./permission";

/** 角色分页查询条件 */
export interface RoleQueryDTO extends PageParams {
  code?: string;
  name?: string;
  status?: string;
}

/** 分页查询角色 */
export const queryRole = defineQuery<RoleQueryDTO, Role>("/api/sys/role/query");

/** 按编号查询角色 */
export const getRoleById = defineGet<{ id: string }, Role>("/api/sys/role/getById");

/** 新增角色 */
export const addRole = definePost<RoleAddDTO, unknown>("/api/sys/role/add");

/** 更新角色 */
export const updateRole = definePost<RoleUpdateDTO, unknown>("/api/sys/role/update");

/** 删除角色 */
export const removeRole = definePost<IdDTO, unknown>("/api/sys/role/remove");

/** 绑定权限到角色 */
export const bindRolePermissions = definePost<BindPermissionsToRoleDTO, unknown>(
  "/api/sys/role/bindPermissions",
);

/** 绑定数据权限到角色 */
export const bindRoleDataPermissions = definePost<BindDataPermissionsToRoleDTO, unknown>(
  "/api/sys/role/bindDataPermissions",
);

/** 查询角色已绑定的权限（后端返回权限树） */
export const listRolePermissions = defineGet<{ roleId: string }, Permission[]>(
  "/api/sys/role/permissions",
);

/** 查询角色绑定的权限 id（后端返回权限树，这里展平后取 id） */
export async function listRolePermissionIds(params: { roleId: string }): Promise<Result<string[]>> {
  const result = await listRolePermissions(params);
  return {
    ...result,
    data: flattenPermissionTree(result.data ?? []).map((p) => p.id ?? ""),
  };
}

/** 查询角色菜单权限绑定的数据域 */
export const listRoleDataScopes = defineGet<{ roleId: string; permissionId: string }, string[]>(
  "/api/sys/role/listDataScopes",
);

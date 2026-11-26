import { http } from "@/api/http";
import { defineGet, definePost } from "@/api/define";
import type {
  IdDTO,
  Permission,
  PermissionAddDTO,
  PermissionQueryDTO,
  PermissionUpdateDTO,
  Result,
} from "@/api/types";

/** 深度展平权限树（后端 list 接口返回树结构，页面按平铺数据使用） */
export function flattenPermissionTree(list: Permission[]): Permission[] {
  return list.flatMap((p) => [
    { ...p, children: undefined },
    ...flattenPermissionTree(p.children ?? []),
  ]);
}

/** 查询全部权限（后端返回树结构，这里展平） */
export async function listPermission(params?: PermissionQueryDTO): Promise<Result<Permission[]>> {
  const result = await http.get<Permission[]>("/sys/permission/list", { params });
  return { ...result, data: flattenPermissionTree(result.data ?? []) };
}

/** 按编号查询权限 */
export const getPermissionById = defineGet<{ id: string }, Permission>("/sys/permission/getById");

/** 新增权限 */
export const addPermission = definePost<PermissionAddDTO, unknown>("/sys/permission/add");

/** 更新权限 */
export const updatePermission = definePost<PermissionUpdateDTO, unknown>("/sys/permission/update");

/** 删除权限 */
export const removePermission = definePost<IdDTO, unknown>("/sys/permission/remove");

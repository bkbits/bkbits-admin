import { defineGet, definePost } from "../define";
import type {
  IdDTO,
  Permission,
  PermissionAddDTO,
  PermissionQueryDTO,
  PermissionUpdateDTO,
} from "../types";

/** 查询全部权限（按查询条件过滤） */
export const listPermission = defineGet<PermissionQueryDTO, Permission[]>("/api/permission/list");

/** 按编号查询权限 */
export const getPermissionById = defineGet<{ id: string }, Permission>("/api/permission/getById");

/** 新增权限 */
export const addPermission = definePost<PermissionAddDTO, unknown>("/api/permission/add");

/** 更新权限 */
export const updatePermission = definePost<PermissionUpdateDTO, unknown>("/api/permission/update");

/** 删除权限 */
export const removePermission = definePost<IdDTO, unknown>("/api/permission/remove");

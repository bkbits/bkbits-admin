import { defineGet, definePost } from "../define";
import type {
  DataPermission,
  DataPermissionAddDTO,
  DataPermissionUpdateDTO,
  IdDTO,
} from "../types";

/** 查询菜单权限下数据权限 */
export const listDataPermission = defineGet<{ permissionId: string }, DataPermission[]>(
  "/api/dataPermission/list",
);

/** 为菜单权限添加数据权限 */
export const addDataPermission = definePost<DataPermissionAddDTO, unknown>(
  "/api/dataPermission/add",
);

/** 更新数据权限 */
export const updateDataPermission = definePost<DataPermissionUpdateDTO, unknown>(
  "/api/dataPermission/update",
);

/** 删除数据权限 */
export const removeDataPermission = definePost<IdDTO, unknown>("/api/dataPermission/remove");

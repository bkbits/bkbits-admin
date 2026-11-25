import { defineGet } from "../define";
import type { DataScopeVO } from "../types";

/** 查询菜单权限可选数据域 */
export const listDataScopes = defineGet<{ permissionId: string }, DataScopeVO[]>(
  "/api/sys/permission/dataScope/list",
);

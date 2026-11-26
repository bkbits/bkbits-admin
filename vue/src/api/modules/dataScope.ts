import { defineGet } from "@/api/define";
import type { DataScopeVO } from "@/api/types";

/** 查询菜单权限可选数据域 */
export const listDataScopes = defineGet<{ permissionId: string }, DataScopeVO[]>(
  "/sys/permission/dataScope/list",
);

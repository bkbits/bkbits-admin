import { defineGet, definePost, defineQuery } from "../define";
import type {
  IdDTO,
  PageData,
  PageParams,
  Tenant,
  TenantAddDTO,
  TenantQueryDTO,
  TenantUpdateDTO,
  TenantVO,
} from "../types";

/** 分页查询租户 */
export const queryTenant = defineQuery<TenantQueryDTO & PageParams, PageData<Tenant>>(
  "/api/tenant/query",
);

/** 按编号查询租户 */
export const getTenantById = defineGet<{ id: string }, TenantVO>("/api/tenant/getById");

/** 新增租户 */
export const addTenant = definePost<TenantAddDTO, unknown>("/api/tenant/add");

/** 更新租户 */
export const updateTenant = definePost<TenantUpdateDTO, unknown>("/api/tenant/update");

/** 删除租户 */
export const removeTenant = definePost<IdDTO, unknown>("/api/tenant/remove");

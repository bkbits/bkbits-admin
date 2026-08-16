import { defineGet, definePost, defineQuery } from "../define";
import type {
  IdDTO,
  PageData,
  PageParams,
  ParamAddDTO,
  ParamQueryDTO,
  ParamUpdateDTO,
  ParamVO,
} from "../types";

/** 分页查询系统参数 */
export const queryParam = defineQuery<ParamQueryDTO & PageParams, PageData<ParamVO>>(
  "/api/param/query",
);

/** 按编号查询系统参数 */
export const getParamById = defineGet<{ id: string }, ParamVO>("/api/param/getById");

/** 按参数键查询系统参数 */
export const getParamByKey = defineGet<{ paramKey: string }, ParamVO>("/api/param/getByKey");

/** 新增系统参数 */
export const addParam = definePost<ParamAddDTO, unknown>("/api/param/add");

/** 更新系统参数 */
export const updateParam = definePost<ParamUpdateDTO, unknown>("/api/param/update");

/** 删除系统参数 */
export const removeParam = definePost<IdDTO, unknown>("/api/param/remove");

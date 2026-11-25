import { defineGet, definePost, defineQuery } from "../define";
import type {
  IdDTO,
  PageParams,
  ParamAddDTO,
  ParamQueryDTO,
  ParamUpdateDTO,
  ParamVO,
} from "../types";

/** 分页查询系统参数 */
export const queryParam = defineQuery<ParamQueryDTO & PageParams, ParamVO>("/api/sys/param/query");

/** 按编号查询系统参数 */
export const getParamById = defineGet<{ id: string }, ParamVO>("/api/sys/param/getById");

/** 按参数键查询系统参数 */
export const getParamByKey = defineGet<{ paramKey: string }, ParamVO>("/api/sys/param/getByKey");

/** 新增系统参数 */
export const addParam = definePost<ParamAddDTO, unknown>("/api/sys/param/add");

/** 更新系统参数 */
export const updateParam = definePost<ParamUpdateDTO, unknown>("/api/sys/param/update");

/** 删除系统参数 */
export const removeParam = definePost<IdDTO, unknown>("/api/sys/param/remove");

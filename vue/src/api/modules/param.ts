import { defineGet, definePost, defineQuery } from "@/api/define";
import type {
  IdDTO,
  PageQuery,
  ParamAddDTO,
  ParamQueryDTO,
  ParamUpdateDTO,
  ParamVO,
} from "@/api/types";

/** 分页查询系统参数 */
export const queryParam = defineQuery<PageQuery<ParamQueryDTO>, ParamVO>("/sys/param/query");

/** 按编号查询系统参数 */
export const getParamById = defineGet<{ id: string }, ParamVO>("/sys/param/getById");

/** 按参数键查询系统参数 */
export const getParamByKey = defineGet<{ paramKey: string }, ParamVO>("/sys/param/getByKey");

/** 新增系统参数 */
export const addParam = definePost<ParamAddDTO, unknown>("/sys/param/add");

/** 更新系统参数 */
export const updateParam = definePost<ParamUpdateDTO, unknown>("/sys/param/update");

/** 删除系统参数 */
export const removeParam = definePost<IdDTO, unknown>("/sys/param/remove");

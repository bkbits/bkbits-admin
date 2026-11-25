import { defineGet, definePost, defineQuery } from "../define";
import type {
  Dict,
  DictAddDTO,
  DictQueryDTO,
  DictUpdateDTO,
  DictValueAddDTO,
  DictValueUpdateDTO,
  DictValueVO,
  DictVO,
  IdDTO,
  PageParams,
} from "../types";

/** 分页查询系统字典 */
export const queryDict = defineQuery<DictQueryDTO & PageParams, Dict>("/api/sys/dict/query");

/** 按字典键查询字典 */
export const getDictByKey = defineGet<{ dictKey: string }, DictVO>("/api/sys/dict/getByKey");

/** 新增系统字典 */
export const addDict = definePost<DictAddDTO, unknown>("/api/sys/dict/add");

/** 更新系统字典 */
export const updateDict = definePost<DictUpdateDTO, unknown>("/api/sys/dict/update");

/** 删除系统字典 */
export const removeDict = definePost<IdDTO, unknown>("/api/sys/dict/remove");

/** 查询字典值列表 */
export const listDictValue = defineGet<{ dictKey: string }, DictValueVO[]>(
  "/api/sys/dict/value/list",
);

/** 新增字典值 */
export const addDictValue = definePost<DictValueAddDTO, unknown>("/api/sys/dict/value/add");

/** 更新字典值 */
export const updateDictValue = definePost<DictValueUpdateDTO, unknown>(
  "/api/sys/dict/value/update",
);

/** 删除字典值 */
export const removeDictValue = definePost<IdDTO, unknown>("/api/sys/dict/value/remove");

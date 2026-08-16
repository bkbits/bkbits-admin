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
  PageData,
  PageParams,
} from "../types";

/** 分页查询系统字典 */
export const queryDict = defineQuery<DictQueryDTO & PageParams, PageData<Dict>>("/api/dict/query");

/** 按字典键查询字典 */
export const getDictByKey = defineGet<{ dictKey: string }, DictVO>("/api/dict/getByKey");

/** 新增系统字典 */
export const addDict = definePost<DictAddDTO, unknown>("/api/dict/add");

/** 更新系统字典 */
export const updateDict = definePost<DictUpdateDTO, unknown>("/api/dict/update");

/** 删除系统字典 */
export const removeDict = definePost<IdDTO, unknown>("/api/dict/remove");

/** 查询字典值列表 */
export const listDictValue = defineGet<{ dictKey: string }, DictValueVO[]>("/api/dict/value/list");

/** 新增字典值 */
export const addDictValue = definePost<DictValueAddDTO, unknown>("/api/dict/value/add");

/** 更新字典值 */
export const updateDictValue = definePost<DictValueUpdateDTO, unknown>("/api/dict/value/update");

/** 删除字典值 */
export const removeDictValue = definePost<IdDTO, unknown>("/api/dict/value/remove");

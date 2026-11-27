import { defineGet, definePost, defineQuery } from "@/api/define";
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
  PageQuery,
} from "@/api/types";

/** 分页查询系统字典 */
export const queryDict = defineQuery<PageQuery<DictQueryDTO>, Dict>("/sys/dict/query");

/** 按字典键查询字典 */
export const getDictByKey = defineGet<{ dictKey: string }, DictVO>("/sys/dict/getByKey");

/** 新增系统字典 */
export const addDict = definePost<DictAddDTO, unknown>("/sys/dict/add");

/** 更新系统字典 */
export const updateDict = definePost<DictUpdateDTO, unknown>("/sys/dict/update");

/** 删除系统字典 */
export const removeDict = definePost<IdDTO, unknown>("/sys/dict/remove");

/** 查询字典值列表 */
export const listDictValue = defineGet<{ dictKey: string }, DictValueVO[]>("/sys/dict/value/list");

/** 新增字典值 */
export const addDictValue = definePost<DictValueAddDTO, unknown>("/sys/dict/value/add");

/** 更新字典值 */
export const updateDictValue = definePost<DictValueUpdateDTO, unknown>("/sys/dict/value/update");

/** 删除字典值 */
export const removeDictValue = definePost<IdDTO, unknown>("/sys/dict/value/remove");

import { defineGet, definePost, defineQuery } from "@/api/define";
import type {
  Dept,
  DeptAddDTO,
  DeptQueryDTO,
  DeptUpdateDTO,
  DeptVO,
  IdDTO,
  PageParams,
} from "@/api/types";

/** 分页查询部门 */
export const queryDept = defineQuery<DeptQueryDTO & PageParams, Dept>("/sys/dept/query");

/** 按编号查询部门 */
export const getDeptById = defineGet<{ deptId: string }, DeptVO>("/sys/dept/getById");

/** 新增部门 */
export const addDept = definePost<DeptAddDTO, unknown>("/sys/dept/add");

/** 更新部门 */
export const updateDept = definePost<DeptUpdateDTO, unknown>("/sys/dept/update");

/** 删除部门 */
export const removeDept = definePost<IdDTO, unknown>("/sys/dept/remove");

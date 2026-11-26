import { http } from "@/api/http";
import type { PageData, Result } from "@/api/types";

/**
 * 将后端分页响应适配为 { rows, total }：
 * - 后端实时契约：`{ ok, code, total, data: [...] }`（total 在根级，data 为数组）
 * - mock 兼容：`{ ok, code, data: { total, rows } }`
 */
function toPageResult<Row>(result: Result<Row[] | PageData<Row>>): Result<PageData<Row>> {
  const data = result.data;
  if (Array.isArray(data)) {
    return { ...result, data: { rows: data, total: result.total ?? data.length } };
  }
  if (data && Array.isArray(data.rows)) {
    return {
      ...result,
      data: { rows: data.rows, total: data.total ?? result.total ?? data.rows.length },
    };
  }
  return { ...result, data: { rows: [], total: result.total ?? 0 } };
}

/**
 * defineQuery —— GET 分页查询（query 参数），返回统一的分页数据体。
 * 例：const queryDept = defineQuery<DeptQueryDTO & PageParams, Dept>("/sys/dept/query");
 */
export function defineQuery<Params, Row>(url: string) {
  return async (params?: Params): Promise<Result<PageData<Row>>> =>
    toPageResult(await http.get<Row[] | PageData<Row>>(url, { params }));
}

/**
 * definePagePost —— POST 分页查询（JSON body），返回统一的分页数据体。
 * 例：const queryLoginUser = definePagePost<UserQueryDTO & PageParams, LoginUser>("/sys/user/queryLoginUser");
 */
export function definePagePost<Body, Row>(url: string) {
  return async (data?: Body): Promise<Result<PageData<Row>>> =>
    toPageResult(await http.post<Row[] | PageData<Row>>(url, data));
}

/**
 * defineGet —— GET 获取（query 参数），用于按编号/键查询单条数据接口。
 * 例：const getDept = defineGet<{ deptId: string }, DeptVO>("/sys/dept/getById");
 */
export function defineGet<Params, T>(url: string) {
  return (params?: Params): Promise<Result<T>> => http.get<T>(url, { params });
}

/**
 * definePost —— POST JSON 提交，用于新增/修改/删除等写操作接口。
 * 例：const addDept = definePost<DeptAddDTO, unknown>("/sys/dept/add");
 */
export function definePost<Body, T = unknown>(url: string) {
  return (data?: Body): Promise<Result<T>> => http.post<T>(url, data);
}

/**
 * defineMultipart —— POST multipart/form-data 上传，用于文件上传接口。
 * query 参数通过第二个参数传入，文件放入 FormData。
 * 例：const upload = defineMultipart<{ hash: string }, UploadFile>("/file/upload");
 *     upload(formData, { hash: "..." })
 */
export function defineMultipart<Query, T = unknown>(url: string) {
  return (form: FormData, query?: Query): Promise<Result<T>> =>
    http.post<T>(url, form, {
      params: query,
      headers: { "Content-Type": "multipart/form-data" },
    });
}

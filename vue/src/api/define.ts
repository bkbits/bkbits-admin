import { http } from "./http";
import type { Result } from "./types";

/**
 * defineQuery —— GET 查询（query 参数），用于分页/列表查询接口。
 * 例：const queryDept = defineQuery<DeptQueryDTO & PageParams, PageData<Dept>>("/api/dept/query");
 */
export function defineQuery<Params, T>(url: string) {
  return (params?: Params): Promise<Result<T>> => http.get<T>(url, { params });
}

/**
 * defineGet —— GET 获取（query 参数），用于按编号/键查询单条数据接口。
 * 例：const getDept = defineGet<{ deptId: string }, DeptVO>("/api/dept/getById");
 */
export function defineGet<Params, T>(url: string) {
  return (params?: Params): Promise<Result<T>> => http.get<T>(url, { params });
}

/**
 * definePost —— POST JSON 提交，用于新增/修改/删除等写操作接口。
 * 例：const addDept = definePost<DeptAddDTO, unknown>("/api/dept/add");
 */
export function definePost<Body, T = unknown>(url: string) {
  return (data?: Body): Promise<Result<T>> => http.post<T>(url, data);
}

/**
 * defineMultipart —— POST multipart/form-data 上传，用于文件上传接口。
 * query 参数通过第二个参数传入，文件放入 FormData。
 * 例：const upload = defineMultipart<{ hash: string }, UploadFile>("/api/file/upload");
 *     upload(formData, { hash: "..." })
 */
export function defineMultipart<Query, T = unknown>(url: string) {
  return (form: FormData, query?: Query): Promise<Result<T>> =>
    http.post<T>(url, form, {
      params: query,
      headers: { "Content-Type": "multipart/form-data" },
    });
}

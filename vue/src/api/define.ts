import { http } from "@/api/http";
import type { PageResult, Result } from "@/api/types";

/**
 * 补全分页总条数（后端在根级返回 total，data 为当前页数据）。
 */
function toPageResult<Row>(result: Result<Row[]>): PageResult<Row> {
  return { ...result, total: result.total ?? result.data?.length ?? 0 };
}

/**
 * defineQuery —— GET 分页查询（query 参数），返回分页数据体。
 * 例：const queryDept = defineQuery<PageQuery<DeptQueryDTO>, Dept>("/sys/dept/query");
 */
export function defineQuery<Query, Row>(url: string) {
  return async (params?: Query): Promise<PageResult<Row>> =>
    toPageResult(await http.get<Row[]>(url, { params }));
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

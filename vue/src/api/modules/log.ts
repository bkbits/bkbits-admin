import { defineGet, definePost, defineQuery } from "@/api/define";
import type { IdDTO, PageQuery } from "@/api/types";

/** 登录日志 */
export interface LoginLogVO {
  /** 日志编号 */
  id: string;
  /** 登录是否成功 */
  succeed?: boolean;
  /** 失败原因 */
  failReason?: string;
  /** 登录ip */
  ip?: string;
  /** 登录设备 */
  device?: string;
  /** UserAgent */
  userAgent?: string;
  /** 花费时间（毫秒） */
  costTime?: number;
  /** 登录者 */
  createBy?: string;
  /** 登录时间 */
  createTime?: string;
}

/** 登录日志查询条件 */
export interface LoginLogQueryDTO {
  /** 登录者 */
  createBy?: string;
  /** 登录是否成功 */
  succeed?: boolean;
  /** 登录ip */
  ip?: string;
  /** 登录设备 */
  device?: string;
  /** 登录时间起（含） */
  createTimeBegin?: string;
  /** 登录时间止（含） */
  createTimeEnd?: string;
}

/** 操作日志 */
export interface OperationLogVO {
  /** 日志编号 */
  id: string;
  /** 日志名称 */
  name?: string;
  /** 日志类型 */
  type?: string;
  /** 模块 */
  module?: string;
  /** 函数路径 */
  method?: string;
  /** 请求参数 */
  args?: string;
  /** 返回结果 */
  result?: string;
  /** 操作IP */
  ip?: string;
  /** UserAgent记录 */
  userAgent?: string;
  /** 访问的url */
  url?: string;
  /** 花费时间（毫秒） */
  costTime?: number;
  /** 备注 */
  remark?: string;
  /** 创建者 */
  createBy?: string;
  /** 创建时间 */
  createTime?: string;
}

/** 操作日志查询条件 */
export interface OperationLogQueryDTO {
  /** 日志名称 */
  name?: string;
  /** 日志类型 */
  type?: string;
  /** 模块 */
  module?: string;
  /** 函数路径 */
  method?: string;
  /** 操作IP */
  ip?: string;
  /** 访问的url */
  url?: string;
  /** 创建者 */
  createBy?: string;
  /** 创建时间起（含） */
  createTimeBegin?: string;
  /** 创建时间止（含） */
  createTimeEnd?: string;
}

/* ------------------------------ 登录日志 ------------------------------ */

/** 分页查询登录日志 */
export const queryLoginLog = defineQuery<PageQuery<LoginLogQueryDTO>, LoginLogVO>(
  "/sys/log/login/query",
);

/** 按编号查询登录日志 */
export const getLoginLogById = defineGet<{ id: string }, LoginLogVO>("/sys/log/login/getById");

/** 按编号删除登录日志 */
export const removeLoginLog = definePost<IdDTO, unknown>("/sys/log/login/remove");

/* ------------------------------ 操作日志 ------------------------------ */

/** 分页查询操作日志 */
export const queryOperationLog = defineQuery<PageQuery<OperationLogQueryDTO>, OperationLogVO>(
  "/sys/log/operation/query",
);

/** 按编号查询操作日志 */
export const getOperationLogById = defineGet<{ id: string }, OperationLogVO>(
  "/sys/log/operation/getById",
);

/** 按编号删除操作日志 */
export const removeOperationLog = definePost<IdDTO, unknown>("/sys/log/operation/remove");

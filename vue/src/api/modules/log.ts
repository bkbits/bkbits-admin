import { defineQuery } from "../define";
import type { PageData, PageParams } from "../types";

/**
 * 登录日志 / 操作日志（mock 扩展接口：后端 Swagger 未提供日志接口，
 * 依据 AGENTS.md「登录日志」「操作日志」菜单补充，mock 层实现）。
 */

/** 登录日志 */
export interface LoginLogVO {
  id: string;
  userName: string;
  ip: string;
  device: string;
  loginTime: string;
  success: boolean;
  message: string;
}

/** 操作日志 */
export interface OperationLogVO {
  id: string;
  userName: string;
  module: string;
  action: string;
  method: string;
  url: string;
  ip: string;
  duration: number;
  status: "success" | "error";
  createTime: string;
}

/** 分页查询登录日志 */
export const queryLoginLog = defineQuery<{ userName?: string } & PageParams, PageData<LoginLogVO>>(
  "/api/log/login/query",
);

/** 分页查询操作日志 */
export const queryOperationLog = defineQuery<
  { userName?: string; module?: string } & PageParams,
  PageData<OperationLogVO>
>("/api/log/operation/query");

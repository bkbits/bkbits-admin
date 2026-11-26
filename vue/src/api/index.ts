/**
 * API 统一出口。
 * - defineQuery / definePagePost / defineGet / definePost / defineMultipart：接口定义五件套
 * - http：axios 实例
 * - modules/*：按业务模块组织的接口定义（含 mock 扩展，见各文件注释）
 */
export { http } from "@/api/http";
export { defineGet, defineMultipart, definePagePost, definePost, defineQuery } from "@/api/define";
export type { EmptyResult } from "@/api/modules/auth";

export * as authApi from "@/api/modules/auth";
export * as userApi from "@/api/modules/user";
export * as roleApi from "@/api/modules/role";
export * as permissionApi from "@/api/modules/permission";
export * as deptApi from "@/api/modules/dept";

export * as paramApi from "@/api/modules/param";
export * as dictApi from "@/api/modules/dict";
export * as dataScopeApi from "@/api/modules/dataScope";
export * as fileApi from "@/api/modules/file";
export * as logApi from "@/api/modules/log";
export * as notificationApi from "@/api/modules/notification";

export * from "@/api/types";

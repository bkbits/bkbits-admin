/**
 * API 统一出口。
 * - defineQuery / defineGet / definePost / defineMultipart：接口定义四件套
 * - http：axios 实例
 * - modules/*：按业务模块组织的接口定义（含 mock 扩展，见各文件注释）
 */
export { http, TOKEN_KEY } from "./http";
export { defineGet, defineMultipart, definePost, defineQuery } from "./define";
export type { EmptyResult } from "./modules/auth";

export * as authApi from "./modules/auth";
export * as userApi from "./modules/user";
export * as roleApi from "./modules/role";
export * as permissionApi from "./modules/permission";
export * as deptApi from "./modules/dept";
export * as tenantApi from "./modules/tenant";
export * as paramApi from "./modules/param";
export * as dictApi from "./modules/dict";
export * as dataPermissionApi from "./modules/dataPermission";
export * as fileApi from "./modules/file";
export * as logApi from "./modules/log";
export * as notificationApi from "./modules/notification";

export * from "./types";

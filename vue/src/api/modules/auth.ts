import { defineGet, definePost } from "../define";
import type { LoginDTO, LoginUser, PageData, PageParams, Result, UserQueryDTO } from "../types";

/** 登录 */
export const login = definePost<LoginDTO, LoginUser>("/api/login");

/** 注销登录 */
export const logout = definePost<Record<string, never>, unknown>("/api/logout");

/** 获取当前登录用户 */
export const getLoginUser = defineGet<Record<string, never>, LoginUser>("/api/loginUser");

/** 获取 RSA 公钥（用于登录密码加密） */
export const getPublicKey = defineGet<Record<string, never>, string>("/api/publicKey");

/** 注册入参（mock 扩展：在登录 DTO 基础上增加姓名） */
export interface RegisterDTO extends LoginDTO {
  realName?: string;
}

/**
 * 注册（mock 扩展接口：后端 Swagger 未提供注册接口，
 * 依据 AGENTS.md 菜单「注册」补充，mock 层实现）。
 */
export const register = definePost<RegisterDTO, unknown>("/api/register");

/** 分页查询在线用户（登录用户） */
export const queryLoginUser = definePost<UserQueryDTO & PageParams, PageData<LoginUser>>(
  "/api/user/queryLoginUser",
);

/** 空响应类型 */
export type EmptyResult = Result<unknown>;

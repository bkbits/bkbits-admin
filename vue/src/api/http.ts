import axios, { type AxiosRequestConfig } from "axios";
import { message } from "antdv-next";
import type { Result } from "@/api/types";

/** 创建 axios 实例 */
const instance = axios.create({
  baseURL: import.meta.env.VITE_API,
  timeout: 15000,
});

/** 登录失效：清理本地令牌并跳转登录页（并发请求只处理一次） */
let redirectingToLogin = false;

function handleUnauthorized() {
  if (redirectingToLogin || window.location.pathname === "/login") return;
  redirectingToLogin = true;
  message.error("登录已过期，请重新登录");
  const { pathname, search, hash } = window.location;
  const redirect = encodeURIComponent(pathname + search + hash);
  window.location.replace(`/login?redirect=${redirect}`);
}

// 响应拦截：解包 AxiosResponse，统一处理业务错误
instance.interceptors.response.use(
  (response) => {
    const result = response.data as Result<unknown> | undefined;
    if (result && typeof result === "object" && "ok" in result) {
      if (result.code === 401) {
        // 后端以 HTTP 200 + code 401 表示登录失效，跳登录页，不再提示业务消息
        handleUnauthorized();
      } else if (result.ok === false) {
        message.error(result.message || "操作失败");
      }
    }
    return response.data;
  },
  (error) => {
    if (error.response?.status === 401) {
      handleUnauthorized();
      return Promise.reject(error);
    }
    const msg =
      (error.response?.data as Result | undefined)?.message ?? error.message ?? "网络请求失败";
    message.error(msg);
    return Promise.reject(error);
  },
);

/**
 * 类型化 http 客户端：请求结果直接为后端 Result<T> 包装对象。
 */
export interface Http {
  get<T>(url: string, config?: AxiosRequestConfig): Promise<Result<T>>;
  post<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<Result<T>>;
}

export const http = instance as unknown as Http;

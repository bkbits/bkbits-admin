import axios, { type AxiosRequestConfig } from "axios";
import { message } from "antdv-next";
import type { Result } from "./types";

/** 本地存储 token 的键名 */
export const TOKEN_KEY = "bkbits-admin-token";

/** 创建 axios 实例 */
const instance = axios.create({
  baseURL: "/",
  timeout: 15000,
});

// 请求拦截：自动附带登录 token
instance.interceptors.request.use((config) => {
  const token = localStorage.getItem(TOKEN_KEY);
  if (token) {
    config.headers.Authorization = token;
  }
  return config;
});

// 响应拦截：解包 AxiosResponse，统一处理业务错误
instance.interceptors.response.use(
  (response) => response.data,
  (error) => {
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

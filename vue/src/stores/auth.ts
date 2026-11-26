import { defineStore } from "pinia";
import { ref } from "vue";
import * as authApi from "@/api/modules/auth";
import type { LoginDTO, LoginUser } from "@/api/types";

/** 认证状态：当前登录用户（凭证由后端通过 cookie 维护） */
export const useAuthStore = defineStore("auth", () => {
  const loginUser = ref<LoginUser | null>(null);

  /** 登录 */
  async function login(form: LoginDTO) {
    const result = await authApi.login(form);
    if (!result.ok) {
      throw new Error(result.message || "登录失败");
    }
    const data = result.data;
    if (!data) {
      throw new Error("登录失败：未返回登录用户信息");
    }
    loginUser.value = data;
  }

  /** 拉取当前登录用户信息（页面刷新后恢复会话） */
  async function fetchLoginUser() {
    const result = await authApi.getLoginUser();
    loginUser.value = result.data ?? null;
  }

  /** 注销登录 */
  async function logout() {
    try {
      await authApi.logout();
    } catch {
      // 忽略注销接口异常，继续清理本地状态
    }
    loginUser.value = null;
  }

  return { loginUser, login, fetchLoginUser, logout };
});

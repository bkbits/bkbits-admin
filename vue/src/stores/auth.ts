import { defineStore } from "pinia";
import { ref } from "vue";
import * as authApi from "../api/modules/auth";
import { TOKEN_KEY } from "../api/http";
import type { LoginDTO, LoginUser } from "../api/types";

/** 认证状态：token + 当前登录用户 */
export const useAuthStore = defineStore("auth", () => {
  const token = ref<string>(localStorage.getItem(TOKEN_KEY) ?? "");
  const loginUser = ref<LoginUser | null>(null);

  /** 登录 */
  async function login(form: LoginDTO) {
    const result = await authApi.login(form);
    token.value = result.data.token ?? "";
    loginUser.value = result.data;
    localStorage.setItem(TOKEN_KEY, token.value);
  }

  /** 拉取当前登录用户信息（页面刷新后恢复会话） */
  async function fetchLoginUser() {
    if (!token.value) return;
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
    token.value = "";
    loginUser.value = null;
    localStorage.removeItem(TOKEN_KEY);
  }

  return { token, loginUser, login, fetchLoginUser, logout };
});

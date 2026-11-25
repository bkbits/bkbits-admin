<script setup lang="ts">
import { reactive, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { message } from "antdv-next";
import AuthLayout from "../layouts/AuthLayout.vue";
import { useAuthStore } from "../stores/auth";
import { encryptPassword } from "../utils/password";

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

const form = reactive({
  username: "admin",
  password: "admin@123",
  remember: true,
});

const loading = ref(false);

async function handleLogin() {
  if (!form.username || !form.password) {
    message.warning("请输入用户名与密码");
    return;
  }
  loading.value = true;
  try {
    let password: string;
    try {
      password = await encryptPassword(form.password);
    } catch (error) {
      message.error(error instanceof Error ? error.message : "密码加密失败，请重试");
      return;
    }
    await authStore.login({ username: form.username, password });
    message.success("登录成功");
    const redirect = (route.query.redirect as string) || "/dashboard";
    router.push(redirect);
  } catch (error) {
    // HTTP 错误已由 http 拦截器提示，这里只提示业务错误（如用户名或密码错误）
    if (error instanceof Error && !("isAxiosError" in error)) {
      message.error(error.message);
    }
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <AuthLayout title="登录">
    <a-form name="login" :model="form" layout="vertical" @finish="handleLogin">
      <a-form-item label="用户名" name="username">
        <a-input
          v-model:value="form.username"
          size="large"
          autocomplete="username"
          placeholder="用户名 / 手机号 / 邮箱"
        />
      </a-form-item>
      <a-form-item label="密码" name="password">
        <a-input-password
          v-model:value="form.password"
          size="large"
          autocomplete="current-password"
          placeholder="请输入密码"
        />
      </a-form-item>
      <div class="login-options">
        <a-checkbox v-model:checked="form.remember">记住我</a-checkbox>
        <router-link to="/register" class="auth-link">注册账号</router-link>
      </div>
      <a-button type="primary" size="large" block html-type="submit" :loading="loading">
        登录
      </a-button>
    </a-form>
  </AuthLayout>
</template>

<style scoped>
.login-options {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  margin: 4px 0 24px;
}
</style>

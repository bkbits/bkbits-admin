<script setup lang="ts">
import { reactive, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { message } from "antdv-next";
import { useAuthStore } from "../stores/auth";

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();

const form = reactive({
  username: "admin",
  password: "123456",
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
    await authStore.login({ username: form.username, password: form.password });
    message.success("登录成功");
    const redirect = (route.query.redirect as string) || "/dashboard";
    router.push(redirect);
  } catch {
    // 错误提示已在 http 拦截器中统一处理
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <div class="login-page">
    <div class="login-card">
      <div class="login-head">
        <img src="/favicon.svg" alt="logo" class="login-logo" />
        <h1 class="login-title">bkbits-admin</h1>
        <p class="login-subtitle">后台管理系统</p>
      </div>
      <a-form :model="form" layout="vertical" @finish="handleLogin">
        <a-form-item label="用户名" name="username">
          <a-input
            v-model:value="form.username"
            size="large"
            placeholder="用户名 / 手机号 / 邮箱"
          />
        </a-form-item>
        <a-form-item label="密码" name="password">
          <a-input-password v-model:value="form.password" size="large" placeholder="请输入密码" />
        </a-form-item>
        <a-form-item>
          <div class="login-options">
            <a-checkbox v-model:checked="form.remember">记住我</a-checkbox>
            <router-link to="/register" class="login-link">注册账号</router-link>
          </div>
        </a-form-item>
        <a-button type="primary" size="large" block html-type="submit" :loading="loading">
          登录
        </a-button>
      </a-form>
      <div class="login-tip">默认账号：admin / 123456（mock 数据）</div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.login-page {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
  background: linear-gradient(135deg, #1677ff 0%, #0958d9 45%, #002c8c 100%);
}

.login-card {
  width: 380px;
  padding: 40px 36px 28px;
  background: var(--ant-color-bg-container, #fff);
  border-radius: 12px;
  box-shadow: 0 12px 40px rgb(0 0 0 / 25%);
}

.login-head {
  text-align: center;
  margin-bottom: 24px;
}

.login-logo {
  width: 48px;
  height: 48px;
}

.login-title {
  margin: 12px 0 4px;
  font-size: 22px;
  font-weight: 700;
  color: var(--ant-color-text, rgb(0 0 0 / 88%));
}

.login-subtitle {
  margin: 0;
  color: var(--ant-color-text-secondary, rgb(0 0 0 / 55%));
}

.login-options {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}

.login-link {
  color: #1677ff;
}

.login-tip {
  margin-top: 16px;
  text-align: center;
  font-size: 12px;
  color: var(--ant-color-text-tertiary, rgb(0 0 0 / 45%));
}
</style>

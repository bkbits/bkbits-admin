<script setup lang="ts">
import { reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { message } from "antdv-next";
import * as authApi from "../api/modules/auth";

const router = useRouter();

const form = reactive({
  username: "",
  password: "",
  confirmPassword: "",
  realName: "",
  email: "",
  phone: "",
});

const loading = ref(false);

async function handleRegister() {
  if (!form.username || !form.password) {
    message.warning("请输入用户名与密码");
    return;
  }
  if (form.password !== form.confirmPassword) {
    message.warning("两次输入的密码不一致");
    return;
  }
  loading.value = true;
  try {
    const result = await authApi.register({
      username: form.username,
      password: form.password,
      realName: form.realName || undefined,
      email: form.email || undefined,
      phone: form.phone || undefined,
    });
    if (result.ok) {
      message.success("注册成功，请登录");
      router.push("/login");
    } else {
      message.error(result.message);
    }
  } catch {
    // 错误提示已在 http 拦截器中统一处理
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <div class="register-page">
    <div class="register-card">
      <div class="register-head">
        <img src="/favicon.svg" alt="logo" class="register-logo" />
        <h1 class="register-title">注册账号</h1>
        <p class="register-subtitle">bkbits-admin 后台管理系统</p>
      </div>
      <a-form :model="form" layout="vertical" @finish="handleRegister">
        <a-form-item label="用户名" required>
          <a-input v-model:value="form.username" size="large" placeholder="登录用户名" />
        </a-form-item>
        <a-form-item label="姓名">
          <a-input v-model:value="form.realName" size="large" placeholder="真实姓名" />
        </a-form-item>
        <a-form-item label="密码" required>
          <a-input-password v-model:value="form.password" size="large" placeholder="请输入密码" />
        </a-form-item>
        <a-form-item label="确认密码" required>
          <a-input-password
            v-model:value="form.confirmPassword"
            size="large"
            placeholder="请再次输入密码"
          />
        </a-form-item>
        <a-form-item label="邮箱">
          <a-input v-model:value="form.email" size="large" placeholder="邮箱地址" />
        </a-form-item>
        <a-form-item label="手机号">
          <a-input v-model:value="form.phone" size="large" placeholder="手机号码" />
        </a-form-item>
        <a-button type="primary" size="large" block html-type="submit" :loading="loading">
          注册
        </a-button>
      </a-form>
      <div class="register-back">
        已有账号？<router-link to="/login" class="register-link">去登录</router-link>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.register-page {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 24px 0;
  background: linear-gradient(135deg, #1677ff 0%, #0958d9 45%, #002c8c 100%);
}

.register-card {
  width: 420px;
  padding: 36px 36px 24px;
  background: var(--ant-color-bg-container, #fff);
  border-radius: 12px;
  box-shadow: 0 12px 40px rgb(0 0 0 / 25%);
}

.register-head {
  text-align: center;
  margin-bottom: 20px;
}

.register-logo {
  width: 44px;
  height: 44px;
}

.register-title {
  margin: 10px 0 4px;
  font-size: 20px;
  font-weight: 700;
  color: var(--ant-color-text, rgb(0 0 0 / 88%));
}

.register-subtitle {
  margin: 0;
  color: var(--ant-color-text-secondary, rgb(0 0 0 / 55%));
}

.register-back {
  margin-top: 12px;
  text-align: center;
  font-size: 13px;
  color: var(--ant-color-text-secondary, rgb(0 0 0 / 55%));
}

.register-link {
  color: #1677ff;
}
</style>

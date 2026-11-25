<script setup lang="ts">
import { reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { message } from "antdv-next";
import * as authApi from "../api/modules/auth";
import AuthLayout from "../layouts/AuthLayout.vue";
import { encryptPassword } from "../utils/password";

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
      password: await encryptPassword(form.password),
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
  } catch (error) {
    // 错误提示已在 http 拦截器中统一处理；加密失败单独提示
    if (error instanceof Error && !("isAxiosError" in error)) {
      message.error(error.message);
    }
  } finally {
    loading.value = false;
  }
}
</script>

<template>
  <AuthLayout title="注册账号">
    <a-form name="register" :model="form" layout="vertical" @finish="handleRegister">
      <a-form-item label="用户名" name="username" required>
        <a-input
          v-model:value="form.username"
          size="large"
          autocomplete="username"
          placeholder="登录用户名"
        />
      </a-form-item>
      <div class="register-fields">
        <a-form-item label="密码" name="password" required>
          <a-input-password
            v-model:value="form.password"
            size="large"
            autocomplete="new-password"
            placeholder="请输入密码"
          />
        </a-form-item>
        <a-form-item label="确认密码" name="confirmPassword" required>
          <a-input-password
            v-model:value="form.confirmPassword"
            size="large"
            autocomplete="new-password"
            placeholder="请再次输入密码"
          />
        </a-form-item>
      </div>
      <fieldset class="register-profile">
        <legend>个人资料（选填）</legend>
        <div class="register-fields">
          <a-form-item label="姓名" name="realName">
            <a-input
              v-model:value="form.realName"
              size="large"
              autocomplete="name"
              placeholder="真实姓名"
            />
          </a-form-item>
          <a-form-item label="手机号" name="phone">
            <a-input
              v-model:value="form.phone"
              size="large"
              autocomplete="tel"
              inputmode="tel"
              placeholder="手机号码"
            />
          </a-form-item>
        </div>
        <a-form-item label="邮箱" name="email">
          <a-input
            v-model:value="form.email"
            size="large"
            autocomplete="email"
            inputmode="email"
            placeholder="邮箱地址"
          />
        </a-form-item>
      </fieldset>
      <a-button type="primary" size="large" block html-type="submit" :loading="loading">
        注册
      </a-button>
    </a-form>
    <p class="register-back">
      已有账号？<router-link to="/login" class="auth-link">去登录</router-link>
    </p>
  </AuthLayout>
</template>

<style scoped>
.register-fields {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  column-gap: 16px;
}

.register-profile {
  min-width: 0;
  margin: 6px 0 8px;
  padding: 0;
  border: 0;
}

.register-profile legend {
  box-sizing: border-box;
  width: 100%;
  margin-bottom: 12px;
  padding: 12px 0 0;
  border-top: 1px solid var(--ui-border, #e0e5ee);
  color: var(--ui-muted, #5b6578);
  font-size: 13px;
  font-weight: 600;
}

.register-back {
  margin: 18px 0 0;
  text-align: center;
  color: var(--ui-muted, #5b6578);
}

@media (max-width: 480px) {
  .register-fields {
    grid-template-columns: minmax(0, 1fr);
  }
}
</style>

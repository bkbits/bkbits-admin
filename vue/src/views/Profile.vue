<script setup lang="ts">
import { computed, onMounted, reactive, ref } from "vue";
import { message } from "antdv-next";
import { userApi } from "../api";
import { deptApi } from "../api";
import { tenantApi } from "../api";
import type { UserVO } from "../api/types";
import { SEX_OPTIONS, STATUS_OPTIONS, dictLabel } from "../utils/dict";
import { useAuthStore } from "../stores/auth";

const authStore = useAuthStore();

const profile = ref<UserVO | null>(null);
const loading = ref(false);
const editing = ref(false);

const form = reactive({
  userId: "",
  userName: "",
  realName: "",
  email: "",
  phone: "",
  sex: "M",
  status: "E",
  tenantId: "",
  deptId: "",
});

const deptOptions = ref<{ label: string; value: string }[]>([]);
const tenantOptions = ref<{ label: string; value: string }[]>([]);

async function loadProfile() {
  const userId = authStore.loginUser?.userId;
  if (!userId) return;
  loading.value = true;
  try {
    const [userResult, deptResult, tenantResult] = await Promise.all([
      userApi.getUserByUserId({ userId }),
      deptApi.queryDept({ page: 1, pageSize: 100 }),
      tenantApi.queryTenant({ page: 1, pageSize: 100 }),
    ]);
    const user = userResult.data;
    if (user) {
      profile.value = user;
      Object.assign(form, {
        userId: user.userId,
        userName: user.userName,
        realName: user.realName,
        email: user.email,
        phone: user.phone,
        sex: user.sex ?? "M",
        status: user.status ?? "E",
        tenantId: user.tenantId ?? "",
        deptId: user.deptId ?? "",
      });
    }
    deptOptions.value = (deptResult.data?.rows ?? []).map((d) => ({
      label: d.name ?? "",
      value: d.deptId ?? "",
    }));
    tenantOptions.value = (tenantResult.data?.rows ?? []).map((t) => ({
      label: t.name ?? "",
      value: t.id ?? "",
    }));
  } finally {
    loading.value = false;
  }
}

onMounted(loadProfile);

async function handleSave() {
  if (!form.realName) {
    message.warning("请填写姓名");
    return;
  }
  const result = await userApi.updateUser({
    userId: form.userId,
    userName: form.userName,
    realName: form.realName,
    email: form.email,
    phone: form.phone,
    sex: form.sex,
    status: form.status,
    tenantId: form.tenantId,
    deptId: form.deptId,
  });
  if (result.ok) {
    message.success("保存成功");
    editing.value = false;
    await authStore.fetchLoginUser();
    await loadProfile();
  } else {
    message.error(result.message);
  }
}

const descItems = computed(() => {
  const p = profile.value;
  if (!p) return [];
  return [
    { label: "用户名", value: p.userName ?? "-" },
    { label: "姓名", value: p.realName ?? "-" },
    { label: "邮箱", value: p.email ?? "-" },
    { label: "手机号", value: p.phone ?? "-" },
    { label: "性别", value: dictLabel(SEX_OPTIONS, p.sex) },
    { label: "状态", value: dictLabel(STATUS_OPTIONS, p.status) },
    {
      label: "租户",
      value: tenantOptions.value.find((t) => t.value === p.tenantId)?.label ?? "-",
    },
    {
      label: "部门",
      value: deptOptions.value.find((d) => d.value === p.deptId)?.label ?? "-",
    },
  ];
});
</script>

<template>
  <div class="profile-page">
    <a-row :gutter="[16, 16]">
      <a-col :xs="24" :lg="8">
        <a-card variant="borderless" :loading="loading">
          <div class="profile-head">
            <a-avatar :size="72" class="profile-avatar">
              {{ (form.realName || form.userName || "U").charAt(0) }}
            </a-avatar>
            <div class="profile-name">{{ form.realName || form.userName }}</div>
            <div class="profile-role">@{{ form.userName }}</div>
          </div>
          <a-divider style="margin: 16px 0" />
          <a-descriptions :column="1" size="small">
            <a-descriptions-item v-for="item in descItems" :key="item.label" :label="item.label">
              {{ item.value }}
            </a-descriptions-item>
          </a-descriptions>
        </a-card>
      </a-col>

      <a-col :xs="24" :lg="16">
        <a-card variant="borderless" title="编辑个人信息">
          <template #extra>
            <a-button v-if="!editing" type="primary" @click="editing = true">编辑</a-button>
            <template v-else>
              <a-button style="margin-right: 8px" @click="editing = false">取消</a-button>
              <a-button type="primary" :loading="loading" @click="handleSave">保存</a-button>
            </template>
          </template>
          <a-form :model="form" layout="vertical" :disabled="!editing">
            <a-row :gutter="16">
              <a-col :xs="24" :md="12">
                <a-form-item label="用户名">
                  <a-input v-model:value="form.userName" disabled />
                </a-form-item>
              </a-col>
              <a-col :xs="24" :md="12">
                <a-form-item label="姓名" required>
                  <a-input v-model:value="form.realName" placeholder="请输入姓名" />
                </a-form-item>
              </a-col>
              <a-col :xs="24" :md="12">
                <a-form-item label="邮箱">
                  <a-input v-model:value="form.email" placeholder="请输入邮箱" />
                </a-form-item>
              </a-col>
              <a-col :xs="24" :md="12">
                <a-form-item label="手机号">
                  <a-input v-model:value="form.phone" placeholder="请输入手机号" />
                </a-form-item>
              </a-col>
              <a-col :xs="24" :md="12">
                <a-form-item label="性别">
                  <a-radio-group v-model:value="form.sex" :options="SEX_OPTIONS" />
                </a-form-item>
              </a-col>
              <a-col :xs="24" :md="12">
                <a-form-item label="状态">
                  <a-select v-model:value="form.status" :options="STATUS_OPTIONS" />
                </a-form-item>
              </a-col>
              <a-col :xs="24" :md="12">
                <a-form-item label="所属租户">
                  <a-select v-model:value="form.tenantId" :options="tenantOptions" />
                </a-form-item>
              </a-col>
              <a-col :xs="24" :md="12">
                <a-form-item label="所属部门">
                  <a-select v-model:value="form.deptId" :options="deptOptions" />
                </a-form-item>
              </a-col>
            </a-row>
          </a-form>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<style lang="scss" scoped>
.profile-head {
  text-align: center;
}

.profile-avatar {
  background: linear-gradient(135deg, #1677ff, #0958d9);
  color: #fff;
  font-size: 28px;
}

.profile-name {
  margin-top: 12px;
  font-size: 18px;
  font-weight: 600;
  color: var(--ant-color-text, rgb(0 0 0 / 88%));
}

.profile-role {
  margin-top: 2px;
  color: var(--ant-color-text-secondary, rgb(0 0 0 / 55%));
}
</style>

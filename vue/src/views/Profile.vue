<script setup lang="ts">
import { computed, reactive, ref, watch } from "vue";
import { message } from "antdv-next";
import { Pencil } from "@lucide/vue";
import { deptApi, userApi } from "../api";
import type { UserVO } from "../api/types";
import { SEX_OPTIONS, STATUS_OPTIONS, dictColor, dictLabel } from "../utils/dict";
import { useAuthStore } from "../stores/auth";

const authStore = useAuthStore();

const profile = ref<UserVO | null>(null);
const loading = ref(false);
const editing = ref(false);
const saving = ref(false);

const form = reactive({
  userId: "",
  userName: "",
  realName: "",
  email: "",
  phone: "",
  sex: "M",
  status: "E",
  deptId: undefined as string | undefined,
});

const deptOptions = ref<{ label: string; value: string }[]>([]);
const userName = computed(() => profile.value?.userName?.trim() || authStore.loginUser?.userName);
const displayName = computed(() => profile.value?.realName?.trim() || userName.value);
const department = computed(
  () => deptOptions.value.find((dept) => dept.value === profile.value?.deptId)?.label,
);

function resetForm() {
  const user = profile.value;
  Object.assign(form, {
    userId: user?.userId || authStore.loginUser?.userId || "",
    userName: userName.value || "",
    realName: user?.realName ?? "",
    email: user?.email ?? "",
    phone: user?.phone ?? "",
    sex: user?.sex || "M",
    status: user?.status || "E",
    deptId: user?.deptId || undefined,
  });
}

async function loadProfile() {
  const userId = authStore.loginUser?.userId;
  if (!userId) return;
  loading.value = true;
  const [user, departments] = await Promise.allSettled([
    userApi.getUserByUserId({ userId }),
    deptApi.queryDept({ page: 1, pageSize: 100 }),
  ]);
  if (userId !== authStore.loginUser?.userId) return;
  if (user.status === "fulfilled" && user.value.ok && user.value.data) {
    profile.value = user.value.data;
    resetForm();
  }
  if (departments.status === "fulfilled" && departments.value.ok) {
    deptOptions.value = (departments.value.data?.rows ?? []).flatMap((dept) =>
      dept.deptId ? [{ label: dept.name || dept.deptId, value: dept.deptId }] : [],
    );
  }
  loading.value = false;
}

watch(
  () => authStore.loginUser?.userId,
  () => {
    profile.value = null;
    editing.value = false;
    loading.value = false;
    void loadProfile();
  },
  { immediate: true },
);

function handleCancel() {
  resetForm();
  editing.value = false;
}

async function handleSave() {
  if (!editing.value || !form.userId || saving.value) return;
  if (!form.realName.trim()) {
    message.warning("请填写姓名");
    return;
  }
  saving.value = true;
  const data = { ...form, deptId: form.deptId ?? "" };
  try {
    const result = await userApi.updateUser(data);
    if (data.userId !== authStore.loginUser?.userId) return;
    if (result.ok) {
      message.success("保存成功");
      profile.value = { ...profile.value, ...data };
      editing.value = false;
      await Promise.allSettled([authStore.fetchLoginUser(), loadProfile()]);
    }
  } finally {
    saving.value = false;
  }
}
</script>

<template>
  <div class="profile-page">
    <div class="profile-grid">
      <section class="profile-panel" aria-labelledby="profile-name">
        <div class="identity-head">
          <a-avatar :size="64" class="profile-avatar" aria-hidden="true">
            {{ displayName?.charAt(0).toLocaleUpperCase() || "U" }}
          </a-avatar>
          <div class="identity-name">
            <h2 id="profile-name">{{ displayName || "—" }}</h2>
            <p v-if="userName && userName !== displayName">@{{ userName }}</p>
          </div>
        </div>

        <dl class="profile-details" :aria-busy="loading">
          <div>
            <dt>所属部门</dt>
            <dd>{{ department || profile?.deptId || "—" }}</dd>
          </div>
          <div>
            <dt>邮箱</dt>
            <dd>{{ profile?.email?.trim() || "—" }}</dd>
          </div>
          <div>
            <dt>手机号</dt>
            <dd>{{ profile?.phone?.trim() || "—" }}</dd>
          </div>
          <div>
            <dt>账号状态</dt>
            <dd>
              <a-tag v-if="profile?.status" :color="dictColor(STATUS_OPTIONS, profile.status)">
                {{ dictLabel(STATUS_OPTIONS, profile.status) }}
              </a-tag>
              <span v-else>—</span>
            </dd>
          </div>
        </dl>
      </section>

      <section class="profile-panel" aria-labelledby="profile-form-title">
        <div class="form-header">
          <h2 id="profile-form-title">基本资料</h2>
          <div v-if="profile" class="form-actions">
            <a-button v-if="!editing" type="primary" :disabled="loading" @click="editing = true">
              <template #icon><Pencil :size="15" aria-hidden="true" /></template>
              编辑
            </a-button>
            <template v-else>
              <a-button :disabled="saving" @click="handleCancel">取消</a-button>
              <a-button type="primary" html-type="submit" form="profile-form" :loading="saving">
                保存
              </a-button>
            </template>
          </div>
        </div>
        <div class="form-body" :aria-busy="loading || saving">
          <a-form
            v-if="profile"
            id="profile-form"
            name="profile-form"
            :model="form"
            layout="vertical"
            :disabled="!editing || saving"
            @finish="handleSave"
          >
            <div class="form-grid">
              <a-form-item name="userName" label="用户名">
                <a-input v-model:value="form.userName" placeholder="—" disabled />
              </a-form-item>
              <a-form-item name="realName" label="姓名" required>
                <a-input
                  v-model:value="form.realName"
                  :placeholder="editing ? '请输入姓名' : '—'"
                />
              </a-form-item>
              <a-form-item name="email" label="邮箱">
                <a-input v-model:value="form.email" :placeholder="editing ? '请输入邮箱' : '—'" />
              </a-form-item>
              <a-form-item name="phone" label="手机号">
                <a-input v-model:value="form.phone" :placeholder="editing ? '请输入手机号' : '—'" />
              </a-form-item>
              <a-form-item name="sex" label="性别">
                <a-radio-group v-model:value="form.sex" :options="SEX_OPTIONS" />
              </a-form-item>
              <a-form-item name="status" label="状态">
                <a-select v-model:value="form.status" :options="STATUS_OPTIONS" />
              </a-form-item>
              <a-form-item name="deptId" label="所属部门">
                <a-select
                  v-model:value="form.deptId"
                  :options="deptOptions"
                  :placeholder="editing ? '请选择部门' : '—'"
                />
              </a-form-item>
            </div>
          </a-form>
          <a-skeleton v-else-if="loading" active />
          <p v-else class="profile-empty">暂无个人资料</p>
        </div>
      </section>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.profile-page {
  min-width: 0;
  container-type: inline-size;
  color: var(--ui-text, #182033);
}

.profile-grid {
  display: grid;
  grid-template-columns: minmax(250px, 1fr) minmax(0, 2.2fr);
  align-items: start;
  gap: 16px;
}

.profile-panel {
  min-width: 0;
  border: 1px solid var(--ui-border, #e7e9f1);
  border-radius: 12px;
  background: var(--ui-surface, #fff);
  box-shadow: var(--ui-shadow, 0 1px 3px rgb(24 32 51 / 5%));
}

.identity-head {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 21px 18px;
  border-radius: 11px 11px 0 0;
  background: var(--ui-accent-soft, #eeedff);
}

.profile-avatar {
  flex: none;
  border: 2px solid var(--ui-surface, #fff);
  background: #4b45d4;
  color: #fff;
  font-size: 24px;
  font-weight: 650;
}

.identity-name {
  min-width: 0;
  overflow-wrap: anywhere;

  h2 {
    margin: 0;
    color: inherit;
    font-size: 19px;
    font-weight: 650;
  }

  p {
    margin: 3px 0 0;
    color: var(--ui-muted, #626b80);
  }
}

.profile-details {
  margin: 0;
  padding: 4px 18px;
  font-size: 13px;

  > div {
    display: grid;
    grid-template-columns: auto minmax(0, 1fr);
    align-items: baseline;
    gap: 12px;
    padding: 12px 0;
  }

  > div + div {
    border-top: 1px solid var(--ui-border, #e7e9f1);
  }

  dt {
    color: var(--ui-muted, #626b80);
  }

  dd {
    margin: 0;
    text-align: end;
    overflow-wrap: anywhere;
  }

  :deep(.ant-tag) {
    margin-inline-end: 0;
  }
}

.form-header {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 8px 12px;
  min-height: 61px;
  padding: 12px 18px;
  border-bottom: 1px solid var(--ui-border, #e7e9f1);

  h2 {
    margin: 0;
    color: inherit;
    font-size: 16px;
    font-weight: 650;
  }
}

.form-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;

  svg {
    vertical-align: middle;
  }
}

.form-body {
  padding: 18px;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px 20px;

  :deep(.ant-form-item) {
    min-width: 0;
    margin-bottom: 0;
  }

  :deep(.ant-form-item-label > label) {
    color: var(--ui-text, #182033);
    font-weight: 500;
  }

  :deep(.ant-input:disabled),
  :deep(.ant-select-disabled .ant-select-selector) {
    border-color: var(--ui-border, #e7e9f1);
    background: var(--ui-canvas, #f5f6fa);
    color: var(--ui-text, #182033);
  }

  :deep(.ant-input:disabled::placeholder),
  :deep(.ant-select-disabled .ant-select-selection-placeholder) {
    color: var(--ui-muted, #626b80);
  }

  :deep(.ant-radio-wrapper-disabled) {
    color: var(--ui-muted, #626b80);
  }
}

.profile-empty {
  margin: 6px 0;
  color: var(--ui-muted, #626b80);
}

@container (max-width: 720px) {
  .profile-grid {
    grid-template-columns: minmax(0, 1fr);
  }
}

@container (max-width: 480px) {
  .form-grid {
    grid-template-columns: minmax(0, 1fr);
  }
}
</style>

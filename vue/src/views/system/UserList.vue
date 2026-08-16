<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { message, Modal } from "antdv-next";
import { PlusOutlined } from "@ant-design/icons-vue";
import { deptApi } from "../../api";
import { roleApi } from "../../api";
import { tenantApi } from "../../api";
import { userApi } from "../../api";
import type { User } from "../../api/types";
import { SEX_OPTIONS, STATUS_OPTIONS, dictColor, dictLabel } from "../../utils/dict";

/* ------------------------------ 列表 ------------------------------ */

const loading = ref(false);
const rows = ref<User[]>([]);
const total = ref(0);

const query = reactive({
  page: 1,
  pageSize: 10,
  userName: "",
  realName: "",
  phone: "",
  status: undefined as string | undefined,
});

async function loadList() {
  loading.value = true;
  try {
    const result = await userApi.queryUser({ ...query });
    rows.value = result.data?.rows ?? [];
    total.value = result.data?.total ?? 0;
  } finally {
    loading.value = false;
  }
}

function handleSearch() {
  query.page = 1;
  void loadList();
}

function handleReset() {
  query.userName = "";
  query.realName = "";
  query.phone = "";
  query.status = undefined;
  handleSearch();
}

onMounted(() => {
  void loadOptions();
  void loadList();
});

/* ------------------------------ 部门/租户选项 ------------------------------ */

const deptOptions = ref<{ label: string; value: string }[]>([]);
const tenantOptions = ref<{ label: string; value: string }[]>([]);

async function loadOptions() {
  const [deptResult, tenantResult] = await Promise.all([
    deptApi.queryDept({ page: 1, pageSize: 100 }),
    tenantApi.queryTenant({ page: 1, pageSize: 100 }),
  ]);
  deptOptions.value = (deptResult.data?.rows ?? []).map((d) => ({
    label: d.name ?? "",
    value: d.deptId ?? "",
  }));
  tenantOptions.value = (tenantResult.data?.rows ?? []).map((t) => ({
    label: t.name ?? "",
    value: t.id ?? "",
  }));
}

/* ------------------------------ 新增/编辑 ------------------------------ */

const modalVisible = ref(false);
const modalMode = ref<"add" | "edit">("add");
const submitting = ref(false);

const form = reactive({
  userId: "",
  userName: "",
  password: "",
  realName: "",
  email: "",
  phone: "",
  sex: "M",
  status: "E",
  tenantId: undefined as string | undefined,
  deptId: undefined as string | undefined,
});

function openAdd() {
  modalMode.value = "add";
  Object.assign(form, {
    userId: "",
    userName: "",
    password: "",
    realName: "",
    email: "",
    phone: "",
    sex: "M",
    status: "E",
    tenantId: undefined,
    deptId: undefined,
  });
  modalVisible.value = true;
}

function openEdit(record: User) {
  modalMode.value = "edit";
  Object.assign(form, {
    userId: record.userId ?? "",
    userName: record.userName ?? "",
    password: "",
    realName: record.realName ?? "",
    email: record.email ?? "",
    phone: record.phone ?? "",
    sex: record.sex ?? "M",
    status: record.status ?? "E",
    tenantId: record.tenantId,
    deptId: record.deptId,
  });
  modalVisible.value = true;
}

async function handleSubmit() {
  if (!form.userName) {
    message.warning("请输入用户名");
    return;
  }
  if (modalMode.value === "add" && !form.password) {
    message.warning("请输入密码");
    return;
  }
  submitting.value = true;
  try {
    const result =
      modalMode.value === "add"
        ? await userApi.addUser({
            userName: form.userName,
            password: form.password,
            email: form.email,
            phone: form.phone,
            realName: form.realName,
            sex: form.sex,
            status: form.status,
            tenantId: form.tenantId,
            deptId: form.deptId,
          })
        : await userApi.updateUser({
            userId: form.userId,
            userName: form.userName,
            email: form.email,
            phone: form.phone,
            realName: form.realName,
            sex: form.sex,
            status: form.status,
            tenantId: form.tenantId,
            deptId: form.deptId,
          });
    if (result.ok) {
      message.success(result.message);
      modalVisible.value = false;
      void loadList();
    } else {
      message.error(result.message);
    }
  } finally {
    submitting.value = false;
  }
}

/* ------------------------------ 绑定角色 ------------------------------ */

const bindVisible = ref(false);
const bindSubmitting = ref(false);
const bindUserId = ref("");
const bindRoleIds = ref<string[]>([]);
const roleOptions = ref<{ label: string; value: string }[]>([]);

function openBindRole(record: User) {
  bindUserId.value = record.userId ?? "";
  bindRoleIds.value = [];
  roleOptions.value = [];
  bindVisible.value = true;
  void loadRoleOptions();
}

async function loadRoleOptions() {
  const [currentResult, allResult] = await Promise.all([
    userApi.listUserRoles({ userId: bindUserId.value }),
    roleApi.queryRole({ page: 1, pageSize: 100 }),
  ]);
  bindRoleIds.value = (currentResult.data ?? []).map((r) => r.id ?? "");
  roleOptions.value = (allResult.data?.rows ?? []).map((r) => ({
    label: r.name ?? "",
    value: r.id ?? "",
  }));
}

async function handleBindSubmit() {
  bindSubmitting.value = true;
  try {
    const result = await userApi.bindUserRole({
      userId: bindUserId.value,
      roleIds: bindRoleIds.value,
    });
    if (result.ok) {
      message.success(result.message);
      bindVisible.value = false;
    } else {
      message.error(result.message);
    }
  } finally {
    bindSubmitting.value = false;
  }
}

/* ------------------------------ 重置密码 ------------------------------ */

const pwdVisible = ref(false);
const pwdSubmitting = ref(false);
const pwdUserId = ref("");
const newPassword = ref("");

function openResetPwd(record: User) {
  pwdUserId.value = record.userId ?? "";
  newPassword.value = "";
  pwdVisible.value = true;
}

async function handlePwdSubmit() {
  if (!newPassword.value) {
    message.warning("请输入新密码");
    return;
  }
  pwdSubmitting.value = true;
  try {
    const result = await userApi.resetUserPassword({
      userId: pwdUserId.value,
      password: newPassword.value,
    });
    if (result.ok) {
      message.success(result.message);
      pwdVisible.value = false;
    } else {
      message.error(result.message);
    }
  } finally {
    pwdSubmitting.value = false;
  }
}

/* ------------------------------ 删除 ------------------------------ */

function handleRemove(record: User) {
  Modal.confirm({
    title: "删除用户",
    content: `确定删除用户「${record.userName}」吗？`,
    okText: "删除",
    okType: "danger",
    cancelText: "取消",
    onOk: async () => {
      const result = await userApi.removeUser({ id: record.userId ?? "" });
      if (result.ok) {
        message.success(result.message);
        void loadList();
      } else {
        message.error(result.message);
      }
    },
  });
}
</script>

<template>
  <div class="page-container">
    <a-card variant="borderless" class="search-card">
      <a-form layout="inline" :model="query">
        <a-form-item label="用户名">
          <a-input v-model:value="query.userName" placeholder="请输入用户名" allow-clear />
        </a-form-item>
        <a-form-item label="姓名">
          <a-input v-model:value="query.realName" placeholder="请输入姓名" allow-clear />
        </a-form-item>
        <a-form-item label="手机号">
          <a-input v-model:value="query.phone" placeholder="请输入手机号" allow-clear />
        </a-form-item>
        <a-form-item label="状态">
          <a-select
            v-model:value="query.status"
            :options="STATUS_OPTIONS"
            placeholder="全部"
            allow-clear
            style="width: 140px"
          />
        </a-form-item>
        <a-form-item>
          <a-space>
            <a-button type="primary" @click="handleSearch">查询</a-button>
            <a-button @click="handleReset">重置</a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>

    <a-card variant="borderless">
      <div class="table-toolbar">
        <a-button type="primary" @click="openAdd">
          <template #icon><PlusOutlined /></template>
          新增用户
        </a-button>
      </div>
      <a-table
        :columns="[
          { title: '用户名', dataIndex: 'userName', key: 'userName' },
          { title: '姓名', dataIndex: 'realName', key: 'realName', width: 120 },
          { title: '性别', dataIndex: 'sex', key: 'sex', width: 90 },
          { title: '手机号', dataIndex: 'phone', key: 'phone', width: 130 },
          { title: '邮箱', dataIndex: 'email', key: 'email', width: 180 },
          { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
          { title: '所属部门', key: 'deptName', width: 140 },
          { title: '所属租户', key: 'tenantName', width: 140 },
          { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 180 },
          { title: '操作', key: 'action', width: 200 },
        ]"
        :data-source="rows"
        :loading="loading"
        :pagination="{
          current: query.page,
          pageSize: query.pageSize,
          total,
          showSizeChanger: true,
          showTotal: (t: number) => `共 ${t} 条`,
        }"
        row-key="userId"
        @change="
          (p: { current?: number; pageSize?: number }) => {
            query.page = p.current ?? 1;
            query.pageSize = p.pageSize ?? 10;
            void loadList();
          }
        "
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'sex'">
            <a-tag :color="dictColor(SEX_OPTIONS, record.sex)">
              {{ dictLabel(SEX_OPTIONS, record.sex) }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="dictColor(STATUS_OPTIONS, record.status)">
              {{ dictLabel(STATUS_OPTIONS, record.status) }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'deptName'">
            {{ deptOptions.find((d) => d.value === record.deptId)?.label ?? "-" }}
          </template>
          <template v-else-if="column.key === 'tenantName'">
            {{ tenantOptions.find((t) => t.value === record.tenantId)?.label ?? "-" }}
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a @click="openBindRole(record)">绑定角色</a>
              <a @click="openEdit(record)">修改</a>
              <a @click="openResetPwd(record)">重置密码</a>
              <a style="color: #ff4d4f" @click="handleRemove(record)">删除</a>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:open="modalVisible"
      :title="modalMode === 'add' ? '新增用户' : '修改用户'"
      :confirm-loading="submitting"
      ok-text="确定"
      cancel-text="取消"
      @ok="handleSubmit"
    >
      <a-form :model="form" layout="vertical" class="modal-form">
        <a-form-item label="用户名" required>
          <a-input
            v-model:value="form.userName"
            placeholder="请输入用户名"
            :disabled="modalMode === 'edit'"
          />
        </a-form-item>
        <a-form-item v-if="modalMode === 'add'" label="密码" required>
          <a-input-password v-model:value="form.password" placeholder="请输入密码" />
        </a-form-item>
        <a-form-item label="姓名">
          <a-input v-model:value="form.realName" placeholder="请输入姓名" />
        </a-form-item>
        <a-form-item label="邮箱">
          <a-input v-model:value="form.email" placeholder="请输入邮箱" />
        </a-form-item>
        <a-form-item label="手机号">
          <a-input v-model:value="form.phone" placeholder="请输入手机号" />
        </a-form-item>
        <a-form-item label="性别">
          <a-radio-group v-model:value="form.sex" :options="SEX_OPTIONS" />
        </a-form-item>
        <a-form-item label="状态">
          <a-radio-group v-model:value="form.status" :options="STATUS_OPTIONS" />
        </a-form-item>
        <a-form-item label="所属租户">
          <a-select
            v-model:value="form.tenantId"
            :options="tenantOptions"
            placeholder="请选择租户"
            allow-clear
          />
        </a-form-item>
        <a-form-item label="所属部门">
          <a-select
            v-model:value="form.deptId"
            :options="deptOptions"
            placeholder="请选择部门"
            allow-clear
          />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="bindVisible"
      title="绑定角色"
      :confirm-loading="bindSubmitting"
      ok-text="确定"
      cancel-text="取消"
      @ok="handleBindSubmit"
    >
      <a-checkbox-group
        v-model:value="bindRoleIds"
        :options="roleOptions"
        class="role-checkbox-group"
      />
    </a-modal>

    <a-modal
      v-model:open="pwdVisible"
      title="重置密码"
      :confirm-loading="pwdSubmitting"
      ok-text="确定"
      cancel-text="取消"
      @ok="handlePwdSubmit"
    >
      <a-form layout="vertical" class="modal-form">
        <a-form-item label="新密码" required>
          <a-input-password v-model:value="newPassword" placeholder="请输入新密码" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<style lang="scss" scoped>
.modal-form {
  padding-top: 8px;
}

.role-checkbox-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding-top: 8px;
}
</style>

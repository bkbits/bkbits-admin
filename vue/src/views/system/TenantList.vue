<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { message, Modal } from "antdv-next";
import { PlusOutlined } from "@ant-design/icons-vue";
import { tenantApi } from "../../api";
import type { Tenant } from "../../api/types";
import { STATUS_OPTIONS, TENANT_TYPE_OPTIONS, dictColor, dictLabel } from "../../utils/dict";

/* ------------------------------ 列表 ------------------------------ */

const loading = ref(false);
const rows = ref<Tenant[]>([]);
const total = ref(0);

const query = reactive({
  page: 1,
  pageSize: 10,
  name: "",
  type: undefined as string | undefined,
  status: undefined as string | undefined,
});

async function loadList() {
  loading.value = true;
  try {
    const result = await tenantApi.queryTenant({ ...query });
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
  query.name = "";
  query.type = undefined;
  query.status = undefined;
  handleSearch();
}

onMounted(loadList);

/* ------------------------------ 新增/编辑 ------------------------------ */

const modalVisible = ref(false);
const modalMode = ref<"add" | "edit">("add");
const submitting = ref(false);

const form = reactive({
  id: "",
  type: "SYSTEM",
  name: "",
  status: "E",
});

function openAdd() {
  modalMode.value = "add";
  Object.assign(form, { id: "", type: "SYSTEM", name: "", status: "E" });
  modalVisible.value = true;
}

function openEdit(record: Tenant) {
  modalMode.value = "edit";
  Object.assign(form, {
    id: record.id ?? "",
    type: record.type ?? "SYSTEM",
    name: record.name ?? "",
    status: record.status ?? "E",
  });
  modalVisible.value = true;
}

async function handleSubmit() {
  if (!form.name) {
    message.warning("请输入租户名称");
    return;
  }
  submitting.value = true;
  try {
    const result =
      modalMode.value === "add"
        ? await tenantApi.addTenant({ type: form.type, name: form.name, status: form.status })
        : await tenantApi.updateTenant({
            id: form.id,
            type: form.type,
            name: form.name,
            status: form.status,
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

/* ------------------------------ 删除 ------------------------------ */

function handleRemove(record: Tenant) {
  Modal.confirm({
    title: "删除租户",
    content: `确定删除租户「${record.name}」吗？`,
    okText: "删除",
    okType: "danger",
    cancelText: "取消",
    onOk: async () => {
      const result = await tenantApi.removeTenant({ id: record.id ?? "" });
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
        <a-form-item label="租户名">
          <a-input v-model:value="query.name" placeholder="请输入租户名" allow-clear />
        </a-form-item>
        <a-form-item label="类型">
          <a-select
            v-model:value="query.type"
            :options="TENANT_TYPE_OPTIONS"
            placeholder="全部"
            allow-clear
            style="width: 140px"
          />
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
        <a-button type="primary" @click="openAdd()">
          <template #icon><PlusOutlined /></template>
          新增租户
        </a-button>
      </div>
      <a-table
        :columns="[
          { title: '租户名', dataIndex: 'name', key: 'name' },
          { title: '类型', dataIndex: 'type', key: 'type', width: 120 },
          { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
          { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 180 },
          { title: '操作', key: 'action', width: 160 },
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
        row-key="id"
        @change="
          (p: { current?: number; pageSize?: number }) => {
            query.page = p.current ?? 1;
            query.pageSize = p.pageSize ?? 10;
            void loadList();
          }
        "
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'type'">
            <a-tag :color="dictColor(TENANT_TYPE_OPTIONS, record.type)">
              {{ dictLabel(TENANT_TYPE_OPTIONS, record.type) }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="dictColor(STATUS_OPTIONS, record.status)">
              {{ dictLabel(STATUS_OPTIONS, record.status) }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a @click="openEdit(record)">修改</a>
              <a style="color: #ff4d4f" @click="handleRemove(record)">删除</a>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:open="modalVisible"
      :title="modalMode === 'add' ? '新增租户' : '修改租户'"
      :confirm-loading="submitting"
      ok-text="确定"
      cancel-text="取消"
      @ok="handleSubmit"
    >
      <a-form :model="form" layout="vertical" class="modal-form">
        <a-form-item label="租户名称" required>
          <a-input v-model:value="form.name" placeholder="请输入租户名称" />
        </a-form-item>
        <a-form-item label="类型">
          <a-radio-group v-model:value="form.type" :options="TENANT_TYPE_OPTIONS" />
        </a-form-item>
        <a-form-item label="状态">
          <a-radio-group v-model:value="form.status" :options="STATUS_OPTIONS" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<style lang="scss" scoped>
.modal-form {
  padding-top: 8px;
}
</style>

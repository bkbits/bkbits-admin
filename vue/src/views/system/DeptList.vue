<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { message, Modal } from "antdv-next";
import { PlusOutlined } from "@ant-design/icons-vue";
import { deptApi } from "../../api";
import { tenantApi } from "../../api";
import type { Dept } from "../../api/types";
import { STATUS_OPTIONS, dictColor, dictLabel } from "../../utils/dict";

/* ------------------------------ 列表 ------------------------------ */

const loading = ref(false);
const rows = ref<Dept[]>([]);
const total = ref(0);

const query = reactive({
  page: 1,
  pageSize: 10,
  name: "",
  status: undefined as string | undefined,
});

async function loadList() {
  loading.value = true;
  try {
    const result = await deptApi.queryDept({ ...query });
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
  query.status = undefined;
  handleSearch();
}

onMounted(loadList);

/* ------------------------------ 新增/编辑 ------------------------------ */

const modalVisible = ref(false);
const modalMode = ref<"add" | "edit">("add");
const submitting = ref(false);

const form = reactive({
  deptId: "",
  parentId: "0",
  name: "",
  sort: 0,
  status: "E",
  tenantId: "1",
});

const parentOptions = ref<{ label: string; value: string }[]>([]);
const tenantOptions = ref<{ label: string; value: string }[]>([]);

async function loadOptions() {
  const [deptResult, tenantResult] = await Promise.all([
    deptApi.queryDept({ page: 1, pageSize: 100 }),
    tenantApi.queryTenant({ page: 1, pageSize: 100 }),
  ]);
  parentOptions.value = [{ label: "顶级部门", value: "0" }].concat(
    (deptResult.data?.rows ?? []).map((d) => ({ label: d.name ?? "", value: d.deptId ?? "" })),
  );
  tenantOptions.value = (tenantResult.data?.rows ?? []).map((t) => ({
    label: t.name ?? "",
    value: t.id ?? "",
  }));
}

function openAdd(parentId = "0") {
  modalMode.value = "add";
  Object.assign(form, { deptId: "", parentId, name: "", sort: 0, status: "E", tenantId: "1" });
  modalVisible.value = true;
  void loadOptions();
}

function openEdit(record: Dept) {
  modalMode.value = "edit";
  Object.assign(form, {
    deptId: record.deptId,
    parentId: record.parentId ?? "0",
    name: record.name,
    sort: record.sort ?? 0,
    status: record.status ?? "E",
    tenantId: record.tenantId ?? "1",
  });
  modalVisible.value = true;
  void loadOptions();
}

async function handleSubmit() {
  if (!form.name) {
    message.warning("请输入部门名称");
    return;
  }
  submitting.value = true;
  try {
    const result =
      modalMode.value === "add"
        ? await deptApi.addDept({
            parentId: form.parentId,
            name: form.name,
            sort: form.sort,
            status: form.status,
            tenantId: form.tenantId,
          })
        : await deptApi.updateDept({
            deptId: form.deptId,
            parentId: form.parentId,
            name: form.name,
            sort: form.sort,
            status: form.status,
            tenantId: form.tenantId,
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

function handleRemove(record: Dept) {
  Modal.confirm({
    title: "删除部门",
    content: `确定删除部门「${record.name}」吗？`,
    okText: "删除",
    okType: "danger",
    cancelText: "取消",
    onOk: async () => {
      const result = await deptApi.removeDept({ id: record.deptId ?? "" });
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
        <a-form-item label="部门名称">
          <a-input v-model:value="query.name" placeholder="请输入部门名称" allow-clear />
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
          新增部门
        </a-button>
      </div>
      <a-table
        :columns="[
          { title: '部门名称', dataIndex: 'name', key: 'name' },
          { title: '上级部门', key: 'parentName' },
          { title: '排序', dataIndex: 'sort', key: 'sort', width: 90 },
          { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
          { title: '所属租户', key: 'tenantName', width: 140 },
          { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 180 },
          { title: '操作', key: 'action', width: 180 },
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
        row-key="deptId"
        @change="
          (p: { current?: number; pageSize?: number }) => {
            query.page = p.current ?? 1;
            query.pageSize = p.pageSize ?? 10;
            void loadList();
          }
        "
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'parentName'">
            {{ parentOptions.find((p) => p.value === record.parentId)?.label ?? "-" }}
          </template>
          <template v-else-if="column.key === 'tenantName'">
            {{ tenantOptions.find((t) => t.value === record.tenantId)?.label ?? "-" }}
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="dictColor(STATUS_OPTIONS, record.status)">
              {{ dictLabel(STATUS_OPTIONS, record.status) }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a @click="openAdd(record.deptId)">新增子部门</a>
              <a @click="openEdit(record)">修改</a>
              <a style="color: #ff4d4f" @click="handleRemove(record)">删除</a>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:open="modalVisible"
      :title="modalMode === 'add' ? '新增部门' : '修改部门'"
      :confirm-loading="submitting"
      ok-text="确定"
      cancel-text="取消"
      @ok="handleSubmit"
    >
      <a-form :model="form" layout="vertical" class="modal-form">
        <a-form-item label="上级部门">
          <a-select v-model:value="form.parentId" :options="parentOptions" />
        </a-form-item>
        <a-form-item label="部门名称" required>
          <a-input v-model:value="form.name" placeholder="请输入部门名称" />
        </a-form-item>
        <a-form-item label="所属租户">
          <a-select v-model:value="form.tenantId" :options="tenantOptions" />
        </a-form-item>
        <a-form-item label="排序">
          <a-input-number v-model:value="form.sort" :min="0" style="width: 100%" />
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

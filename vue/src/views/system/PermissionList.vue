<script setup lang="ts">
import { computed, onMounted, reactive, ref } from "vue";
import { message, Modal } from "antdv-next";
import { PlusOutlined } from "@ant-design/icons-vue";
import { permissionApi } from "../../api";
import type { Permission } from "../../api/types";
import { PERMISSION_TYPE_OPTIONS, STATUS_OPTIONS, dictColor, dictLabel } from "../../utils/dict";

/* ------------------------------ 列表 ------------------------------ */

const loading = ref(false);
const rows = ref<Permission[]>([]);
const treeRows = ref<Permission[]>([]);

const query = reactive({
  name: "",
  status: undefined as string | undefined,
});

/** 平铺权限组装成树：根为 parentId === '0'（parentId 可能为 undefined，按 '0' 处理），children 字段供表格树形展示 */
function buildTableTree(parentId: string): Permission[] {
  return rows.value
    .filter((p) => (p.parentId ?? "0") === parentId)
    .map((p) => ({ ...p, children: buildTableTree(p.id ?? "") }));
}

async function loadList() {
  loading.value = true;
  try {
    const result = await permissionApi.listPermission({ ...query });
    rows.value = result.data ?? [];
    treeRows.value = buildTableTree("0");
  } finally {
    loading.value = false;
  }
}

function handleSearch() {
  void loadList();
}

function handleReset() {
  query.name = "";
  query.status = undefined;
  handleSearch();
}

onMounted(loadList);

/* ------------------------------ 新增/编辑 ------------------------------ */

interface TreeNode {
  key: string;
  title: string;
  children?: TreeNode[];
}

const modalVisible = ref(false);
const modalMode = ref<"add" | "edit">("add");
const submitting = ref(false);

const form = reactive({
  id: "",
  parentId: "0",
  type: "MENU",
  name: "",
  permission: "",
  component: "",
  sort: 0,
  status: "E",
});

/** 平铺权限组装成树：供上级权限选择器使用 */
function buildTree(parentId: string): TreeNode[] {
  return rows.value
    .filter((p) => (p.parentId ?? "0") === parentId)
    .map((p) => ({
      key: p.id ?? "",
      title: p.name ?? "",
      children: buildTree(p.id ?? ""),
    }));
}

const parentTreeData = computed<TreeNode[]>(() => [
  { key: "0", title: "顶级权限", children: buildTree("0") },
]);

function openAdd(parentId = "0") {
  modalMode.value = "add";
  Object.assign(form, {
    id: "",
    parentId,
    type: "MENU",
    name: "",
    permission: "",
    component: "",
    sort: 0,
    status: "E",
  });
  modalVisible.value = true;
}

function openEdit(record: Permission) {
  modalMode.value = "edit";
  Object.assign(form, {
    id: record.id ?? "",
    parentId: record.parentId ?? "0",
    type: record.type ?? "MENU",
    name: record.name ?? "",
    permission: record.permission ?? "",
    component: record.component ?? "",
    sort: record.sort ?? 0,
    status: record.status ?? "E",
  });
  modalVisible.value = true;
}

async function handleSubmit() {
  if (!form.name) {
    message.warning("请输入权限名称");
    return;
  }
  submitting.value = true;
  try {
    const result =
      modalMode.value === "add"
        ? await permissionApi.addPermission({
            parentId: form.parentId,
            type: form.type,
            permission: form.permission,
            name: form.name,
            sort: form.sort,
            component: form.component,
            status: form.status,
          })
        : await permissionApi.updatePermission({
            id: form.id,
            parentId: form.parentId,
            type: form.type,
            permission: form.permission,
            name: form.name,
            sort: form.sort,
            component: form.component,
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

function handleRemove(record: Permission) {
  Modal.confirm({
    title: "删除权限",
    content: `确定删除权限「${record.name}」吗？`,
    okText: "删除",
    okType: "danger",
    cancelText: "取消",
    onOk: async () => {
      const result = await permissionApi.removePermission({ id: record.id ?? "" });
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
        <a-form-item label="权限名称">
          <a-input v-model:value="query.name" placeholder="请输入权限名称" allow-clear />
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
          新增权限
        </a-button>
      </div>
      <a-table
        :columns="[
          { title: '权限名称', dataIndex: 'name', key: 'name' },
          { title: '类型', dataIndex: 'type', key: 'type', width: 100 },
          { title: '权限标识', dataIndex: 'permission', key: 'permission', width: 200 },
          { title: '组件', dataIndex: 'component', key: 'component', width: 140 },
          { title: '排序', dataIndex: 'sort', key: 'sort', width: 90 },
          { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
          { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 180 },
          { title: '操作', key: 'action', width: 180 },
        ]"
        :data-source="treeRows"
        :loading="loading"
        :pagination="false"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'type'">
            <a-tag :color="dictColor(PERMISSION_TYPE_OPTIONS, record.type)">
              {{ dictLabel(PERMISSION_TYPE_OPTIONS, record.type) }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag :color="dictColor(STATUS_OPTIONS, record.status)">
              {{ dictLabel(STATUS_OPTIONS, record.status) }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a @click="openAdd(record.id)">新增子项</a>
              <a @click="openEdit(record)">修改</a>
              <a style="color: #ff4d4f" @click="handleRemove(record)">删除</a>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:open="modalVisible"
      :title="modalMode === 'add' ? '新增权限' : '修改权限'"
      :confirm-loading="submitting"
      ok-text="确定"
      cancel-text="取消"
      @ok="handleSubmit"
    >
      <a-form :model="form" layout="vertical" class="modal-form">
        <a-form-item label="上级权限">
          <a-tree-select
            v-model:value="form.parentId"
            :tree-data="parentTreeData"
            :field-names="{ label: 'title', value: 'key', children: 'children' }"
            tree-default-expand-all
            placeholder="请选择上级权限"
          />
        </a-form-item>
        <a-form-item label="权限类型">
          <a-radio-group v-model:value="form.type" :options="PERMISSION_TYPE_OPTIONS" />
        </a-form-item>
        <a-form-item label="权限名称" required>
          <a-input v-model:value="form.name" placeholder="请输入权限名称" />
        </a-form-item>
        <a-form-item label="权限标识">
          <a-input v-model:value="form.permission" placeholder="请输入权限标识，如 system:role" />
        </a-form-item>
        <a-form-item label="组件">
          <a-input v-model:value="form.component" placeholder="请输入前端组件名" />
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

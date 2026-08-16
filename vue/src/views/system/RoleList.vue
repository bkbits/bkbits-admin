<script setup lang="ts">
import { computed, onMounted, reactive, ref } from "vue";
import { message, Modal } from "antdv-next";
import { PlusOutlined } from "@ant-design/icons-vue";
import { roleApi } from "../../api";
import { permissionApi } from "../../api";
import { dataPermissionApi } from "../../api";
import type { DataPermission, Permission, Role } from "../../api/types";
import { DATA_SCOPE_OPTIONS, STATUS_OPTIONS, dictColor, dictLabel } from "../../utils/dict";

/* ------------------------------ 列表 ------------------------------ */

const loading = ref(false);
const rows = ref<Role[]>([]);
const total = ref(0);

const query = reactive({
  page: 1,
  pageSize: 10,
  name: "",
  code: "",
  status: undefined as string | undefined,
});

async function loadList() {
  loading.value = true;
  try {
    const result = await roleApi.queryRole({ ...query });
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
  query.code = "";
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
  code: "",
  name: "",
  sort: 0,
  status: "E",
});

function openAdd() {
  modalMode.value = "add";
  Object.assign(form, { id: "", code: "", name: "", sort: 0, status: "E" });
  modalVisible.value = true;
}

function openEdit(record: Role) {
  modalMode.value = "edit";
  Object.assign(form, {
    id: record.id ?? "",
    code: record.code ?? "",
    name: record.name ?? "",
    sort: record.sort ?? 0,
    status: record.status ?? "E",
  });
  modalVisible.value = true;
}

async function handleSubmit() {
  if (!form.code) {
    message.warning("请输入角色代码");
    return;
  }
  if (!form.name) {
    message.warning("请输入角色名");
    return;
  }
  submitting.value = true;
  try {
    const result =
      modalMode.value === "add"
        ? await roleApi.addRole({
            code: form.code,
            name: form.name,
            sort: form.sort,
            status: form.status,
          })
        : await roleApi.updateRole({
            id: form.id,
            code: form.code,
            name: form.name,
            sort: form.sort,
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

function handleRemove(record: Role) {
  Modal.confirm({
    title: "删除角色",
    content: `确定删除角色「${record.name}」吗？`,
    okText: "删除",
    okType: "danger",
    cancelText: "取消",
    onOk: async () => {
      const result = await roleApi.removeRole({ id: record.id ?? "" });
      if (result.ok) {
        message.success(result.message);
        void loadList();
      } else {
        message.error(result.message);
      }
    },
  });
}

/* ------------------------------ 绑定权限 ------------------------------ */

interface TreeNode {
  key: string;
  title: string;
  children?: TreeNode[];
}

const permissionVisible = ref(false);
const permissionLoading = ref(false);
const permissionSubmitting = ref(false);
const permissionRoleId = ref("");
const allPermissions = ref<Permission[]>([]);
const checkedIds = ref<string[]>([]);

/** 平铺权限组装成树：根为 parentId === '0'（parentId 可能为 undefined，按 '0' 处理） */
function buildTree(parentId: string): TreeNode[] {
  return allPermissions.value
    .filter((p) => (p.parentId ?? "0") === parentId)
    .map((p) => ({
      key: p.id ?? "",
      title: p.name ?? "",
      children: buildTree(p.id ?? ""),
    }));
}

const treeData = computed<TreeNode[]>(() => buildTree("0"));

function handleTreeCheck(
  keys: { checked: (string | number)[]; halfChecked: (string | number)[] } | (string | number)[],
) {
  checkedIds.value = (Array.isArray(keys) ? keys : keys.checked).map(String);
}

async function openPermission(record: Role) {
  permissionRoleId.value = record.id ?? "";
  permissionVisible.value = true;
  permissionLoading.value = true;
  try {
    const [allResult, idsResult] = await Promise.all([
      permissionApi.listPermission({}),
      roleApi.listRolePermissionIds({ roleId: record.id ?? "" }),
    ]);
    allPermissions.value = allResult.data ?? [];
    checkedIds.value = idsResult.data ?? [];
  } finally {
    permissionLoading.value = false;
  }
}

async function handlePermissionSubmit() {
  permissionSubmitting.value = true;
  try {
    const result = await roleApi.bindRolePermissions({
      roleId: permissionRoleId.value,
      permissionIds: checkedIds.value,
    });
    if (result.ok) {
      message.success(result.message);
      permissionVisible.value = false;
    } else {
      message.error(result.message);
    }
  } finally {
    permissionSubmitting.value = false;
  }
}

/* ------------------------------ 绑定数据权限 ------------------------------ */

const dataPermissionVisible = ref(false);
const dataPermissionLoading = ref(false);
const dataPermissionSubmitting = ref(false);
const dataPermissionRoleId = ref("");
const menuPermissions = ref<Permission[]>([]);
const currentMenuId = ref("");
const dataPermissions = ref<DataPermission[]>([]);
const checkedDataPermissionIds = ref<string[]>([]);

const dataPermissionOptions = computed(() =>
  dataPermissions.value.map((d) => ({
    label: `${dictLabel(DATA_SCOPE_OPTIONS, d.dataScope)} - ${dictLabel(STATUS_OPTIONS, d.status)}`,
    value: d.id ?? "",
  })),
);

async function openDataPermission(record: Role) {
  dataPermissionRoleId.value = record.id ?? "";
  currentMenuId.value = "";
  checkedDataPermissionIds.value = [];
  dataPermissions.value = [];
  dataPermissionVisible.value = true;
  dataPermissionLoading.value = true;
  try {
    const result = await permissionApi.listPermission({});
    menuPermissions.value = (result.data ?? []).filter((p) => p.type === "MENU");
    if (menuPermissions.value.length > 0) {
      await selectMenuPermission(menuPermissions.value[0].id ?? "");
    }
  } finally {
    dataPermissionLoading.value = false;
  }
}

async function selectMenuPermission(permissionId: string) {
  currentMenuId.value = permissionId;
  dataPermissionLoading.value = true;
  try {
    const [dpResult, boundResult] = await Promise.all([
      dataPermissionApi.listDataPermission({ permissionId }),
      roleApi.listRoleDataPermissionIds({ roleId: dataPermissionRoleId.value, permissionId }),
    ]);
    dataPermissions.value = dpResult.data ?? [];
    checkedDataPermissionIds.value = boundResult.data ?? [];
  } finally {
    dataPermissionLoading.value = false;
  }
}

async function handleDataPermissionSubmit() {
  if (!currentMenuId.value) {
    message.warning("请选择菜单权限");
    return;
  }
  dataPermissionSubmitting.value = true;
  try {
    const result = await roleApi.bindRoleDataPermissions({
      roleId: dataPermissionRoleId.value,
      permissionId: currentMenuId.value,
      dataPermissionIds: checkedDataPermissionIds.value,
    });
    if (result.ok) {
      message.success(result.message);
      dataPermissionVisible.value = false;
    } else {
      message.error(result.message);
    }
  } finally {
    dataPermissionSubmitting.value = false;
  }
}
</script>

<template>
  <div class="page-container">
    <a-card variant="borderless" class="search-card">
      <a-form layout="inline" :model="query">
        <a-form-item label="角色名">
          <a-input v-model:value="query.name" placeholder="请输入角色名" allow-clear />
        </a-form-item>
        <a-form-item label="角色代码">
          <a-input v-model:value="query.code" placeholder="请输入角色代码" allow-clear />
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
          新增角色
        </a-button>
      </div>
      <a-table
        :columns="[
          { title: '角色代码', dataIndex: 'code', key: 'code', width: 140 },
          { title: '角色名', dataIndex: 'name', key: 'name' },
          { title: '排序', dataIndex: 'sort', key: 'sort', width: 90 },
          { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
          { title: '创建时间', dataIndex: 'createTime', key: 'createTime', width: 180 },
          { title: '操作', key: 'action', width: 260 },
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
          <template v-if="column.key === 'status'">
            <a-tag :color="dictColor(STATUS_OPTIONS, record.status)">
              {{ dictLabel(STATUS_OPTIONS, record.status) }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space wrap>
              <a @click="openPermission(record)">绑定权限</a>
              <a @click="openDataPermission(record)">绑定数据权限</a>
              <a @click="openEdit(record)">修改</a>
              <a style="color: #ff4d4f" @click="handleRemove(record)">删除</a>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:open="modalVisible"
      :title="modalMode === 'add' ? '新增角色' : '修改角色'"
      :confirm-loading="submitting"
      ok-text="确定"
      cancel-text="取消"
      @ok="handleSubmit"
    >
      <a-form :model="form" layout="vertical" class="modal-form">
        <a-form-item label="角色代码" required>
          <a-input v-model:value="form.code" placeholder="请输入角色代码" />
        </a-form-item>
        <a-form-item label="角色名" required>
          <a-input v-model:value="form.name" placeholder="请输入角色名" />
        </a-form-item>
        <a-form-item label="排序">
          <a-input-number v-model:value="form.sort" :min="0" style="width: 100%" />
        </a-form-item>
        <a-form-item label="状态">
          <a-radio-group v-model:value="form.status" :options="STATUS_OPTIONS" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-modal
      v-model:open="permissionVisible"
      title="绑定权限"
      :confirm-loading="permissionSubmitting"
      ok-text="确定"
      cancel-text="取消"
      width="480px"
      @ok="handlePermissionSubmit"
    >
      <div v-if="permissionLoading" class="modal-loading">加载中...</div>
      <div v-else class="permission-tree">
        <a-tree
          checkable
          :tree-data="treeData"
          :checked-keys="checkedIds"
          :default-expand-all="true"
          @check="handleTreeCheck"
        />
      </div>
    </a-modal>

    <a-modal
      v-model:open="dataPermissionVisible"
      title="绑定数据权限"
      :confirm-loading="dataPermissionSubmitting"
      ok-text="确定"
      cancel-text="取消"
      width="640px"
      @ok="handleDataPermissionSubmit"
    >
      <div v-if="dataPermissionLoading" class="modal-loading">加载中...</div>
      <div v-else class="data-permission-body">
        <div class="menu-list">
          <div
            v-for="m in menuPermissions"
            :key="m.id"
            class="menu-item"
            :class="{ active: m.id === currentMenuId }"
            @click="selectMenuPermission(m.id ?? '')"
          >
            {{ m.name }}
          </div>
        </div>
        <div class="scope-list">
          <a-empty v-if="dataPermissions.length === 0" description="该菜单暂无数据权限" />
          <a-checkbox-group
            v-else
            v-model:value="checkedDataPermissionIds"
            :options="dataPermissionOptions"
          />
        </div>
      </div>
    </a-modal>
  </div>
</template>

<style lang="scss" scoped>
.modal-form {
  padding-top: 8px;
}

.modal-loading {
  padding: 24px 0;
  text-align: center;
  color: rgba(0, 0, 0, 0.45);
}

.permission-tree {
  max-height: 420px;
  overflow: auto;
  padding: 8px 4px;
}

.data-permission-body {
  display: flex;
  gap: 16px;
  min-height: 220px;

  .menu-list {
    width: 200px;
    flex-shrink: 0;
    border-right: 1px solid rgba(5, 5, 5, 0.06);
    padding-right: 12px;

    .menu-item {
      padding: 8px 12px;
      border-radius: 6px;
      cursor: pointer;
      color: rgba(0, 0, 0, 0.88);
      transition: background-color 0.2s;

      &:hover {
        background-color: rgba(0, 0, 0, 0.04);
      }

      &.active {
        color: #1677ff;
        background-color: rgba(22, 119, 255, 0.1);
      }
    }
  }

  .scope-list {
    flex: 1;
    padding: 8px 4px;
  }
}
</style>

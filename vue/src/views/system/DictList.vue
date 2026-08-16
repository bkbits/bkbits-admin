<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { message, Modal } from "antdv-next";
import { PlusOutlined } from "@ant-design/icons-vue";
import { dictApi } from "../../api";
import type { Dict, DictValueVO } from "../../api/types";
import { SYSTEM_BUSINESS_OPTIONS, dictColor, dictLabel } from "../../utils/dict";

/** 字典值类型（与 mock 种子数据一致，对应 antd Tag 颜色） */
const VALUE_TYPE_OPTIONS = [
  { value: "success", label: "成功" },
  { value: "error", label: "错误" },
  { value: "warning", label: "警告" },
  { value: "primary", label: "主要" },
  { value: "default", label: "默认" },
];

/* ------------------------------ 列表 ------------------------------ */

const loading = ref(false);
const rows = ref<Dict[]>([]);
const total = ref(0);

const query = reactive({
  page: 1,
  pageSize: 10,
  dictKey: "",
  name: "",
  type: undefined as string | undefined,
});

async function loadList() {
  loading.value = true;
  try {
    const result = await dictApi.queryDict({ ...query });
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
  query.dictKey = "";
  query.name = "";
  query.type = undefined;
  handleSearch();
}

onMounted(loadList);

/* ------------------------------ 新增/编辑 ------------------------------ */

const modalVisible = ref(false);
const modalMode = ref<"add" | "edit">("add");
const submitting = ref(false);

const form = reactive({
  id: "",
  dictKey: "",
  name: "",
  type: "SYSTEM",
  sort: 0,
  remark: "",
});

function openAdd() {
  modalMode.value = "add";
  Object.assign(form, { id: "", dictKey: "", name: "", type: "SYSTEM", sort: 0, remark: "" });
  modalVisible.value = true;
}

function openEdit(record: Dict) {
  modalMode.value = "edit";
  Object.assign(form, {
    id: record.id ?? "",
    dictKey: record.dictKey ?? "",
    name: record.name ?? "",
    type: record.type ?? "SYSTEM",
    sort: record.sort ?? 0,
    remark: record.remark ?? "",
  });
  modalVisible.value = true;
}

async function handleSubmit() {
  if (!form.dictKey) {
    message.warning("请输入字典键");
    return;
  }
  if (!form.name) {
    message.warning("请输入字典名");
    return;
  }
  submitting.value = true;
  try {
    const result =
      modalMode.value === "add"
        ? await dictApi.addDict({
            dictKey: form.dictKey,
            name: form.name,
            sort: form.sort,
            type: form.type,
            remark: form.remark,
          })
        : await dictApi.updateDict({
            id: form.id,
            dictKey: form.dictKey,
            name: form.name,
            sort: form.sort,
            type: form.type,
            remark: form.remark,
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

function handleRemove(record: Dict) {
  Modal.confirm({
    title: "删除字典",
    content: `确定删除字典「${record.name}」吗？`,
    okText: "删除",
    okType: "danger",
    cancelText: "取消",
    onOk: async () => {
      const result = await dictApi.removeDict({ id: record.id ?? "" });
      if (result.ok) {
        message.success(result.message);
        void loadList();
      } else {
        message.error(result.message);
      }
    },
  });
}

/* ------------------------------ 字典值管理 ------------------------------ */

const drawerVisible = ref(false);
const currentDict = ref<Dict | null>(null);
const valueLoading = ref(false);
const valueRows = ref<DictValueVO[]>([]);

async function loadDictValues() {
  const dictKey = currentDict.value?.dictKey;
  if (!dictKey) return;
  valueLoading.value = true;
  try {
    const result = await dictApi.listDictValue({ dictKey });
    valueRows.value = result.data ?? [];
  } finally {
    valueLoading.value = false;
  }
}

function openValue(record: Dict) {
  currentDict.value = record;
  drawerVisible.value = true;
  void loadDictValues();
}

/* ------------------------------ 字典值新增/编辑 ------------------------------ */

const valueModalVisible = ref(false);
const valueModalMode = ref<"add" | "edit">("add");
const valueSubmitting = ref(false);

const valueForm = reactive({
  id: "",
  valueKey: "",
  name: "",
  value: "",
  sort: 0,
  type: "success",
  color: "#1677ff",
  remark: "",
});

function openValueAdd() {
  valueModalMode.value = "add";
  Object.assign(valueForm, {
    id: "",
    valueKey: "",
    name: "",
    value: "",
    sort: 0,
    type: "success",
    color: "#1677ff",
    remark: "",
  });
  valueModalVisible.value = true;
}

function openValueEdit(record: DictValueVO) {
  valueModalMode.value = "edit";
  Object.assign(valueForm, {
    id: record.id ?? "",
    valueKey: record.valueKey ?? "",
    name: record.name ?? "",
    value: record.value ?? "",
    sort: record.sort ?? 0,
    type: record.type ?? "success",
    color: record.color ?? "#1677ff",
    remark: record.remark ?? "",
  });
  valueModalVisible.value = true;
}

async function handleValueSubmit() {
  if (!valueForm.valueKey) {
    message.warning("请输入值键");
    return;
  }
  if (!valueForm.name) {
    message.warning("请输入名称");
    return;
  }
  valueSubmitting.value = true;
  try {
    const base = {
      valueKey: valueForm.valueKey,
      name: valueForm.name,
      sort: valueForm.sort,
      value: valueForm.value,
      type: valueForm.type,
      color: valueForm.color,
      remark: valueForm.remark,
    };
    const result =
      valueModalMode.value === "add"
        ? await dictApi.addDictValue({ dictId: currentDict.value?.id ?? "", ...base })
        : await dictApi.updateDictValue({ id: valueForm.id, ...base });
    if (result.ok) {
      message.success(result.message);
      valueModalVisible.value = false;
      void loadDictValues();
    } else {
      message.error(result.message);
    }
  } finally {
    valueSubmitting.value = false;
  }
}

/* ------------------------------ 字典值删除 ------------------------------ */

function handleValueRemove(record: DictValueVO) {
  Modal.confirm({
    title: "删除字典值",
    content: `确定删除字典值「${record.name}」吗？`,
    okText: "删除",
    okType: "danger",
    cancelText: "取消",
    onOk: async () => {
      const result = await dictApi.removeDictValue({ id: record.id ?? "" });
      if (result.ok) {
        message.success(result.message);
        void loadDictValues();
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
        <a-form-item label="字典键">
          <a-input v-model:value="query.dictKey" placeholder="请输入字典键" allow-clear />
        </a-form-item>
        <a-form-item label="字典名">
          <a-input v-model:value="query.name" placeholder="请输入字典名" allow-clear />
        </a-form-item>
        <a-form-item label="类型">
          <a-select
            v-model:value="query.type"
            :options="SYSTEM_BUSINESS_OPTIONS"
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
          新增字典
        </a-button>
      </div>
      <a-table
        :columns="[
          { title: '字典键', dataIndex: 'dictKey', key: 'dictKey', width: 160 },
          { title: '字典名', dataIndex: 'name', key: 'name', width: 160 },
          { title: '类型', dataIndex: 'type', key: 'type', width: 120 },
          { title: '排序', dataIndex: 'sort', key: 'sort', width: 80 },
          { title: '备注', dataIndex: 'remark', key: 'remark', width: 160 },
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
            <a-tag :color="dictColor(SYSTEM_BUSINESS_OPTIONS, record.type)">
              {{ dictLabel(SYSTEM_BUSINESS_OPTIONS, record.type) }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a @click="openValue(record)">字典值</a>
              <a @click="openEdit(record)">修改</a>
              <a style="color: #ff4d4f" @click="handleRemove(record)">删除</a>
            </a-space>
          </template>
        </template>
      </a-table>
    </a-card>

    <a-modal
      v-model:open="modalVisible"
      :title="modalMode === 'add' ? '新增字典' : '修改字典'"
      :confirm-loading="submitting"
      ok-text="确定"
      cancel-text="取消"
      @ok="handleSubmit"
    >
      <a-form :model="form" layout="vertical" class="modal-form">
        <a-form-item label="字典键" required>
          <a-input
            v-model:value="form.dictKey"
            placeholder="请输入字典键"
            :disabled="modalMode === 'edit'"
          />
        </a-form-item>
        <a-form-item label="字典名" required>
          <a-input v-model:value="form.name" placeholder="请输入字典名" />
        </a-form-item>
        <a-form-item label="类型">
          <a-radio-group v-model:value="form.type" :options="SYSTEM_BUSINESS_OPTIONS" />
        </a-form-item>
        <a-form-item label="排序">
          <a-input-number v-model:value="form.sort" :min="0" style="width: 100%" />
        </a-form-item>
        <a-form-item label="备注">
          <a-textarea v-model:value="form.remark" :rows="2" placeholder="请输入备注" />
        </a-form-item>
      </a-form>
    </a-modal>

    <a-drawer
      v-model:open="drawerVisible"
      :title="`字典值管理 - ${currentDict?.name ?? ''}`"
      width="640"
    >
      <div class="table-toolbar">
        <a-button type="primary" @click="openValueAdd()">
          <template #icon><PlusOutlined /></template>
          新增字典值
        </a-button>
      </div>
      <a-table
        :columns="[
          { title: '值键', dataIndex: 'valueKey', key: 'valueKey', width: 120 },
          { title: '名称', dataIndex: 'name', key: 'name', width: 120 },
          { title: '值', dataIndex: 'value', key: 'value', width: 100 },
          { title: '排序', dataIndex: 'sort', key: 'sort', width: 70 },
          { title: '类型', dataIndex: 'type', key: 'type', width: 90 },
          { title: '颜色', dataIndex: 'color', key: 'color', width: 120 },
          { title: '备注', dataIndex: 'remark', key: 'remark', width: 120 },
          { title: '操作', key: 'action', width: 130 },
        ]"
        :data-source="valueRows"
        :loading="valueLoading"
        :pagination="false"
        row-key="id"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'type'">
            <a-tag :color="record.type || 'default'">
              {{ dictLabel(VALUE_TYPE_OPTIONS, record.type) }}
            </a-tag>
          </template>
          <template v-else-if="column.key === 'color'">
            <span class="color-cell">
              <span class="color-dot" :style="{ background: record.color || 'transparent' }"></span>
              {{ record.color || "-" }}
            </span>
          </template>
          <template v-else-if="column.key === 'action'">
            <a-space>
              <a @click="openValueEdit(record)">修改</a>
              <a style="color: #ff4d4f" @click="handleValueRemove(record)">删除</a>
            </a-space>
          </template>
        </template>
      </a-table>

      <a-modal
        v-model:open="valueModalVisible"
        :title="valueModalMode === 'add' ? '新增字典值' : '修改字典值'"
        :confirm-loading="valueSubmitting"
        ok-text="确定"
        cancel-text="取消"
        @ok="handleValueSubmit"
      >
        <a-form :model="valueForm" layout="vertical" class="modal-form">
          <a-form-item label="值键" required>
            <a-input v-model:value="valueForm.valueKey" placeholder="请输入值键" />
          </a-form-item>
          <a-form-item label="名称" required>
            <a-input v-model:value="valueForm.name" placeholder="请输入名称" />
          </a-form-item>
          <a-form-item label="值">
            <a-input v-model:value="valueForm.value" placeholder="请输入值" />
          </a-form-item>
          <a-form-item label="排序">
            <a-input-number v-model:value="valueForm.sort" :min="0" style="width: 100%" />
          </a-form-item>
          <a-form-item label="类型">
            <a-select v-model:value="valueForm.type" :options="VALUE_TYPE_OPTIONS" />
          </a-form-item>
          <a-form-item label="颜色">
            <a-input v-model:value="valueForm.color" type="color" style="width: 100%" />
          </a-form-item>
          <a-form-item label="备注">
            <a-textarea v-model:value="valueForm.remark" :rows="2" placeholder="请输入备注" />
          </a-form-item>
        </a-form>
      </a-modal>
    </a-drawer>
  </div>
</template>

<style lang="scss" scoped>
.modal-form {
  padding-top: 8px;
}

.color-cell {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.color-dot {
  display: inline-block;
  width: 14px;
  height: 14px;
  border-radius: 3px;
  border: 1px solid #d9d9d9;
}
</style>

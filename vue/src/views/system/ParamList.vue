<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { message, Modal } from "antdv-next";
import { PlusOutlined } from "@ant-design/icons-vue";
import { paramApi } from "../../api";
import type { ParamVO } from "../../api/types";
import { SYSTEM_BUSINESS_OPTIONS, dictColor, dictLabel } from "../../utils/dict";

/* ------------------------------ 列表 ------------------------------ */

const loading = ref(false);
const rows = ref<ParamVO[]>([]);
const total = ref(0);

const query = reactive({
  page: 1,
  pageSize: 10,
  paramKey: "",
  name: "",
  type: undefined as string | undefined,
});

async function loadList() {
  loading.value = true;
  try {
    const result = await paramApi.queryParam({ ...query });
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
  query.paramKey = "";
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
  paramKey: "",
  name: "",
  value: "",
  type: "SYSTEM",
  sort: 0,
  remark: "",
});

function openAdd() {
  modalMode.value = "add";
  Object.assign(form, {
    id: "",
    paramKey: "",
    name: "",
    value: "",
    type: "SYSTEM",
    sort: 0,
    remark: "",
  });
  modalVisible.value = true;
}

function openEdit(record: ParamVO) {
  modalMode.value = "edit";
  Object.assign(form, {
    id: record.id ?? "",
    paramKey: record.paramKey ?? "",
    name: record.name ?? "",
    value: record.value ?? "",
    type: record.type ?? "SYSTEM",
    sort: record.sort ?? 0,
    remark: record.remark ?? "",
  });
  modalVisible.value = true;
}

async function handleSubmit() {
  if (!form.paramKey) {
    message.warning("请输入参数键");
    return;
  }
  if (!form.name) {
    message.warning("请输入参数名称");
    return;
  }
  submitting.value = true;
  try {
    const result =
      modalMode.value === "add"
        ? await paramApi.addParam({
            paramKey: form.paramKey,
            name: form.name,
            sort: form.sort,
            value: form.value,
            type: form.type,
            remark: form.remark,
          })
        : await paramApi.updateParam({
            id: form.id,
            paramKey: form.paramKey,
            name: form.name,
            sort: form.sort,
            value: form.value,
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

function handleRemove(record: ParamVO) {
  Modal.confirm({
    title: "删除系统参数",
    content: `确定删除系统参数「${record.name}」吗？`,
    okText: "删除",
    okType: "danger",
    cancelText: "取消",
    onOk: async () => {
      const result = await paramApi.removeParam({ id: record.id ?? "" });
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
        <a-form-item label="参数键">
          <a-input v-model:value="query.paramKey" placeholder="请输入参数键" allow-clear />
        </a-form-item>
        <a-form-item label="参数名">
          <a-input v-model:value="query.name" placeholder="请输入参数名" allow-clear />
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
          新增参数
        </a-button>
      </div>
      <a-table
        :columns="[
          { title: '参数键', dataIndex: 'paramKey', key: 'paramKey', width: 160 },
          { title: '参数名', dataIndex: 'name', key: 'name', width: 160 },
          { title: '参数值', dataIndex: 'value', key: 'value' },
          { title: '类型', dataIndex: 'type', key: 'type', width: 120 },
          { title: '排序', dataIndex: 'sort', key: 'sort', width: 80 },
          { title: '备注', dataIndex: 'remark', key: 'remark', width: 160 },
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
            <a-tag :color="dictColor(SYSTEM_BUSINESS_OPTIONS, record.type)">
              {{ dictLabel(SYSTEM_BUSINESS_OPTIONS, record.type) }}
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
      :title="modalMode === 'add' ? '新增参数' : '修改参数'"
      :confirm-loading="submitting"
      ok-text="确定"
      cancel-text="取消"
      @ok="handleSubmit"
    >
      <a-form :model="form" layout="vertical" class="modal-form">
        <a-form-item label="参数键" required>
          <a-input
            v-model:value="form.paramKey"
            placeholder="请输入参数键"
            :disabled="modalMode === 'edit'"
          />
        </a-form-item>
        <a-form-item label="参数名" required>
          <a-input v-model:value="form.name" placeholder="请输入参数名" />
        </a-form-item>
        <a-form-item label="参数值">
          <a-input v-model:value="form.value" placeholder="请输入参数值" />
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
  </div>
</template>

<style lang="scss" scoped>
.modal-form {
  padding-top: 8px;
}
</style>

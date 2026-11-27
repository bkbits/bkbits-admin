<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { message, Modal } from "antdv-next";
import { Trash2 } from "@lucide/vue";
import RowAction from "@/components/RowAction.vue";
import BkCard from "@/components/BkCard.vue";
import BkContainer from "@/components/BkContainer.vue";
import BkSearch from "@/components/BkSearch.vue";
import { queryOperationLog, removeOperationLog } from "@/api/modules/log";
import type { OperationLogVO } from "@/api/modules/log";

/* ------------------------------ 列表 ------------------------------ */

const MODULE_OPTIONS = [
  { label: "部门管理", value: "部门管理" },
  { label: "用户管理", value: "用户管理" },
  { label: "角色管理", value: "角色管理" },
  { label: "菜单管理", value: "菜单管理" },
  { label: "字典管理", value: "字典管理" },
  { label: "参数管理", value: "参数管理" },
  { label: "认证", value: "认证" },
];

const loading = ref(false);
const rows = ref<OperationLogVO[]>([]);
const total = ref(0);

const query = reactive({
  page: 1,
  pageSize: 10,
  name: "",
  type: "",
  module: undefined as string | undefined,
  createBy: "",
  createTimeBegin: undefined as string | undefined,
  createTimeEnd: undefined as string | undefined,
});

async function loadList() {
  loading.value = true;
  try {
    const result = await queryOperationLog({ ...query });
    rows.value = result.data ?? [];
    total.value = result.total ?? 0;
  } finally {
    loading.value = false;
  }
}

function handleSearch() {
  query.page = 1;
  void loadList();
}

onMounted(loadList);
function handleRemove(record: OperationLogVO) {
  Modal.confirm({
    title: "删除操作日志",
    content: "确定删除该操作日志吗？",
    okText: "删除",
    okType: "danger",
    cancelText: "取消",
    onOk: async () => {
      const result = await removeOperationLog({ id: record.id });
      if (result.ok) {
        message.success(result.message);
        void loadList();
      }
    },
  });
}

onMounted(loadList);
</script>

<template>
  <BkContainer>
    <BkCard>
      <BkSearch v-model="query" :loading="loading" @search="handleSearch">
        <a-form-item name="name" label="名称">
          <a-input v-model:value="query.name" placeholder="请输入日志名称" allow-clear />
        </a-form-item>
        <a-form-item name="module" label="模块">
          <a-select
            v-model:value="query.module"
            :options="MODULE_OPTIONS"
            placeholder="全部"
            allow-clear
          />
        </a-form-item>
        <a-form-item name="type" label="类型">
          <a-input v-model:value="query.type" placeholder="请输入类型" allow-clear />
        </a-form-item>
        <a-form-item name="createBy" label="操作人">
          <a-input v-model:value="query.createBy" placeholder="请输入操作人" allow-clear />
        </a-form-item>
        <a-form-item name="createTimeBegin" label="开始时间">
          <a-date-picker
            v-model:value="query.createTimeBegin"
            show-time
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="开始时间"
          />
        </a-form-item>
        <a-form-item name="createTimeEnd" label="结束时间">
          <a-date-picker
            v-model:value="query.createTimeEnd"
            show-time
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="结束时间"
          />
        </a-form-item>
      </BkSearch>
    </BkCard>

    <BkCard title="操作记录">
      <a-table
        :columns="[
          { title: '名称', dataIndex: 'name', key: 'name', width: 160 },
          { title: '模块', dataIndex: 'module', key: 'module', width: 110 },
          { title: '类型', dataIndex: 'type', key: 'type', width: 100 },
          { title: '方法', dataIndex: 'method', key: 'method', width: 220, ellipsis: true },
          { title: 'URL', dataIndex: 'url', key: 'url', ellipsis: true },
          { title: 'IP', dataIndex: 'ip', key: 'ip', width: 140 },
          { title: '耗时(ms)', dataIndex: 'costTime', key: 'costTime', width: 100 },
          { title: '结果', dataIndex: 'result', key: 'result', ellipsis: true },
          { title: '操作人', dataIndex: 'createBy', key: 'createBy', width: 120 },
          { title: '时间', dataIndex: 'createTime', key: 'createTime', width: 180 },
          { title: '操作', key: 'action', width: 90, fixed: 'right' },
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
        :scroll="{ x: 'max-content' }"
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'action'">
            <RowAction :icon="Trash2" danger @click="handleRemove(record)">删除</RowAction>
          </template>
        </template>
      </a-table>
    </BkCard>
  </BkContainer>
</template>

<style lang="scss" scoped></style>

<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { message, Modal } from "antdv-next";
import { Trash2 } from "@lucide/vue";
import RowAction from "../../components/RowAction.vue";
import { queryLoginLog, removeLoginLog } from "../../api/modules/log";
import type { LoginLogVO } from "../../api/modules/log";

/* ------------------------------ 列表 ------------------------------ */

const loading = ref(false);
const rows = ref<LoginLogVO[]>([]);
const total = ref(0);

const query = reactive({
  page: 1,
  pageSize: 10,
  createBy: "",
  ip: "",
  device: "",
  succeed: undefined as boolean | undefined,
  createTimeBegin: undefined as string | undefined,
  createTimeEnd: undefined as string | undefined,
});

const RESULT_OPTIONS = [
  { label: "成功", value: true },
  { label: "失败", value: false },
];

async function loadList() {
  loading.value = true;
  try {
    const result = await queryLoginLog({ ...query });
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
  query.createBy = "";
  query.ip = "";
  query.device = "";
  query.succeed = undefined;
  query.createTimeBegin = undefined;
  query.createTimeEnd = undefined;
  handleSearch();
}

function handleRemove(record: LoginLogVO) {
  Modal.confirm({
    title: "删除登录日志",
    content: "确定删除该登录日志吗？",
    okText: "删除",
    okType: "danger",
    cancelText: "取消",
    onOk: async () => {
      const result = await removeLoginLog({ id: record.id });
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
  <div class="page-container">
    <a-card variant="borderless" class="search-card">
      <a-form layout="vertical" class="filter-form" :model="query" @finish="handleSearch">
        <a-form-item name="createBy" label="登录者">
          <a-input v-model:value="query.createBy" placeholder="请输入登录者" allow-clear />
        </a-form-item>
        <a-form-item name="ip" label="IP">
          <a-input v-model:value="query.ip" placeholder="请输入IP" allow-clear />
        </a-form-item>
        <a-form-item name="device" label="设备">
          <a-input v-model:value="query.device" placeholder="请输入设备" allow-clear />
        </a-form-item>
        <a-form-item name="succeed" label="结果">
          <a-select
            v-model:value="query.succeed"
            :options="RESULT_OPTIONS"
            placeholder="全部"
            allow-clear
          />
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
        <a-form-item>
          <a-space>
            <a-button type="primary" html-type="submit">查询</a-button>
            <a-button @click="handleReset">重置</a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>

    <a-card variant="borderless">
      <div class="table-toolbar">
        <h2 class="table-heading">登录记录</h2>
      </div>
      <a-table
        :columns="[
          { title: '登录者', dataIndex: 'createBy', key: 'createBy', width: 120 },
          { title: 'IP', dataIndex: 'ip', key: 'ip', width: 140 },
          { title: '设备', dataIndex: 'device', key: 'device', width: 140 },
          { title: '登录时间', dataIndex: 'createTime', key: 'createTime', width: 180 },
          { title: '结果', dataIndex: 'succeed', key: 'succeed', width: 90 },
          { title: '失败原因', dataIndex: 'failReason', key: 'failReason', ellipsis: true },
          { title: '操作', key: 'action', width: 90 },
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
          <template v-if="column.key === 'succeed'">
            <a-tag v-if="record.succeed" color="success">成功</a-tag>
            <a-tag v-else color="error">失败</a-tag>
          </template>
          <template v-else-if="column.key === 'action'">
            <RowAction :icon="Trash2" danger @click="handleRemove(record)">删除</RowAction>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<style lang="scss" scoped></style>

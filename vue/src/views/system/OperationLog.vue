<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { queryOperationLog } from "../../api/modules/log";
import type { OperationLogVO } from "../../api/modules/log";

/* ------------------------------ 列表 ------------------------------ */

const MODULE_OPTIONS = [
  { label: "用户管理", value: "用户管理" },
  { label: "角色管理", value: "角色管理" },
  { label: "权限管理", value: "权限管理" },
  { label: "部门管理", value: "部门管理" },
  { label: "租户管理", value: "租户管理" },
  { label: "系统参数", value: "系统参数" },
  { label: "系统字典", value: "系统字典" },
];

const loading = ref(false);
const rows = ref<OperationLogVO[]>([]);
const total = ref(0);

const query = reactive({
  page: 1,
  pageSize: 10,
  userName: "",
  module: undefined as string | undefined,
});

async function loadList() {
  loading.value = true;
  try {
    const result = await queryOperationLog({ ...query });
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
  query.module = undefined;
  handleSearch();
}

onMounted(loadList);
</script>

<template>
  <div class="page-container">
    <a-card variant="borderless" class="search-card">
      <a-form layout="inline" :model="query">
        <a-form-item label="用户名">
          <a-input v-model:value="query.userName" placeholder="请输入用户名" allow-clear />
        </a-form-item>
        <a-form-item label="模块">
          <a-select
            v-model:value="query.module"
            :options="MODULE_OPTIONS"
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
      <a-table
        :columns="[
          { title: '用户名', dataIndex: 'userName', key: 'userName' },
          { title: '模块', dataIndex: 'module', key: 'module' },
          { title: '操作', dataIndex: 'action', key: 'action' },
          { title: '请求方式', dataIndex: 'method', key: 'method', width: 110 },
          { title: 'URL', dataIndex: 'url', key: 'url', ellipsis: true },
          { title: 'IP', dataIndex: 'ip', key: 'ip' },
          { title: '耗时(ms)', dataIndex: 'duration', key: 'duration', width: 100 },
          { title: '状态', dataIndex: 'status', key: 'status', width: 100 },
          { title: '操作时间', dataIndex: 'createTime', key: 'createTime', width: 180 },
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
          <template v-if="column.key === 'method'">
            <a-tag v-if="record.method === 'GET'" color="blue">{{ record.method }}</a-tag>
            <a-tag v-else color="green">{{ record.method }}</a-tag>
          </template>
          <template v-else-if="column.key === 'status'">
            <a-tag v-if="record.status === 'success'" color="success">成功</a-tag>
            <a-tag v-else color="error">失败</a-tag>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<style lang="scss" scoped></style>

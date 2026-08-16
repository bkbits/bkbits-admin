<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { queryLoginLog } from "../../api/modules/log";
import type { LoginLogVO } from "../../api/modules/log";

/* ------------------------------ 列表 ------------------------------ */

const loading = ref(false);
const rows = ref<LoginLogVO[]>([]);
const total = ref(0);

const query = reactive({
  page: 1,
  pageSize: 10,
  userName: "",
});

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
  query.userName = "";
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
          { title: 'IP', dataIndex: 'ip', key: 'ip' },
          { title: '设备', dataIndex: 'device', key: 'device' },
          { title: '登录时间', dataIndex: 'loginTime', key: 'loginTime', width: 180 },
          { title: '结果', dataIndex: 'success', key: 'success', width: 100 },
          { title: '消息', dataIndex: 'message', key: 'message', ellipsis: true },
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
          <template v-if="column.key === 'success'">
            <a-tag v-if="record.success" color="success">成功</a-tag>
            <a-tag v-else color="error">失败</a-tag>
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<style lang="scss" scoped></style>

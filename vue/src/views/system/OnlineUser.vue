<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { message } from "antdv-next";
import { ReloadOutlined } from "@ant-design/icons-vue";
import { deptApi } from "../../api";
import { tenantApi } from "../../api";
import { userApi } from "../../api";
import type { LoginUser } from "../../api/types";

/* ------------------------------ 列表 ------------------------------ */

const loading = ref(false);
const rows = ref<LoginUser[]>([]);
const total = ref(0);

const query = reactive({
  page: 1,
  pageSize: 10,
  userName: "",
});

async function loadList() {
  loading.value = true;
  try {
    const result = await userApi.queryOnlineUser({
      userName: query.userName,
      page: query.page,
      pageSize: query.pageSize,
    });
    if (result.ok) {
      rows.value = result.data?.rows ?? [];
      total.value = result.data?.total ?? 0;
    } else {
      message.error(result.message);
    }
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

function handleRefresh() {
  void loadList();
}

onMounted(() => {
  void loadOptions();
  void loadList();
});

/* ------------------------------ 部门/租户选项 ------------------------------ */

const deptOptions = ref<{ label: string; value: string }[]>([]);
const tenantOptions = ref<{ label: string; value: string }[]>([]);

async function loadOptions() {
  const [deptResult, tenantResult] = await Promise.all([
    deptApi.queryDept({ page: 1, pageSize: 100 }),
    tenantApi.queryTenant({ page: 1, pageSize: 100 }),
  ]);
  deptOptions.value = (deptResult.data?.rows ?? []).map((d) => ({
    label: d.name ?? "",
    value: d.deptId ?? "",
  }));
  tenantOptions.value = (tenantResult.data?.rows ?? []).map((t) => ({
    label: t.name ?? "",
    value: t.id ?? "",
  }));
}
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
            <a-button @click="handleRefresh">
              <template #icon><ReloadOutlined /></template>
              刷新
            </a-button>
          </a-space>
        </a-form-item>
      </a-form>
    </a-card>

    <a-card variant="borderless">
      <a-table
        :columns="[
          { title: '用户名', dataIndex: 'userName', key: 'userName' },
          { title: '登录时间', dataIndex: 'loginTime', key: 'loginTime', width: 180 },
          { title: 'IP', dataIndex: 'ip', key: 'ip', width: 150 },
          { title: '设备', dataIndex: 'device', key: 'device', width: 150 },
          { title: '所属部门', key: 'deptName', width: 140 },
          { title: '所属租户', key: 'tenantName', width: 140 },
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
        row-key="userId"
        @change="
          (p: { current?: number; pageSize?: number }) => {
            query.page = p.current ?? 1;
            query.pageSize = p.pageSize ?? 10;
            void loadList();
          }
        "
      >
        <template #bodyCell="{ column, record }">
          <template v-if="column.key === 'deptName'">
            {{ deptOptions.find((d) => d.value === record.deptId)?.label ?? "-" }}
          </template>
          <template v-else-if="column.key === 'tenantName'">
            {{ tenantOptions.find((t) => t.value === record.tenantId)?.label ?? "-" }}
          </template>
        </template>
      </a-table>
    </a-card>
  </div>
</template>

<style lang="scss" scoped></style>

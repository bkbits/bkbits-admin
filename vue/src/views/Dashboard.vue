<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import {
  Users,
  UserRound,
  ShieldCheck,
  Building2,
  Globe,
  SlidersHorizontal,
  BookMarked,
  Wifi,
} from "lucide-vue-next";
import { userApi } from "../api";
import { roleApi } from "../api";
import { deptApi } from "../api";
import { tenantApi } from "../api";
import { paramApi } from "../api";
import { dictApi } from "../api";
import { logApi } from "../api";
import type { LoginLogVO } from "../api/modules/log";
import { useAuthStore } from "../stores/auth";

const router = useRouter();
const authStore = useAuthStore();

const stats = reactive({
  user: 0,
  role: 0,
  dept: 0,
  tenant: 0,
  param: 0,
  dict: 0,
  online: 0,
});

const recentLogs = ref<LoginLogVO[]>([]);
const loading = ref(true);

async function loadStats() {
  const [users, roles, depts, tenants, params, dicts, online, logs] = await Promise.all([
    userApi.queryUser({ page: 1, pageSize: 1 }),
    roleApi.queryRole({ page: 1, pageSize: 1 }),
    deptApi.queryDept({ page: 1, pageSize: 1 }),
    tenantApi.queryTenant({ page: 1, pageSize: 1 }),
    paramApi.queryParam({ page: 1, pageSize: 1 }),
    dictApi.queryDict({ page: 1, pageSize: 1 }),
    userApi.queryOnlineUser({ page: 1, pageSize: 1 }),
    logApi.queryLoginLog({ page: 1, pageSize: 5 }),
  ]);
  stats.user = users.data?.total ?? 0;
  stats.role = roles.data?.total ?? 0;
  stats.dept = depts.data?.total ?? 0;
  stats.tenant = tenants.data?.total ?? 0;
  stats.param = params.data?.total ?? 0;
  stats.dict = dicts.data?.total ?? 0;
  stats.online = online.data?.total ?? 0;
  recentLogs.value = logs.data?.rows ?? [];
  loading.value = false;
}

onMounted(loadStats);

const greeting = (() => {
  const hour = new Date().getHours();
  if (hour < 6) return "夜深了";
  if (hour < 12) return "早上好";
  if (hour < 14) return "中午好";
  if (hour < 18) return "下午好";
  return "晚上好";
})();

const cards = [
  { key: "user", title: "用户总数", icon: Users, color: "#1677ff", path: "/system/user" },
  { key: "role", title: "角色总数", icon: ShieldCheck, color: "#722ed1", path: "/system/role" },
  { key: "dept", title: "部门总数", icon: Building2, color: "#13c2c2", path: "/system/dept" },
  { key: "tenant", title: "租户总数", icon: Globe, color: "#fa8c16", path: "/system/tenant" },
  {
    key: "param",
    title: "系统参数",
    icon: SlidersHorizontal,
    color: "#eb2f96",
    path: "/system/param",
  },
  { key: "dict", title: "系统字典", icon: BookMarked, color: "#52c41a", path: "/system/dict" },
  { key: "online", title: "在线用户", icon: Wifi, color: "#faad14", path: "/system/online-user" },
];
</script>

<template>
  <div class="dashboard">
    <a-card variant="borderless" class="welcome-card">
      <div class="welcome">
        <a-avatar :size="56" class="welcome-avatar">
          {{ (authStore.loginUser?.realName || authStore.loginUser?.userName || "U").charAt(0) }}
        </a-avatar>
        <div>
          <div class="welcome-title">
            {{ greeting }}，{{ authStore.loginUser?.realName || authStore.loginUser?.userName }}
          </div>
          <div class="welcome-sub">
            欢迎使用 bkbits-admin 后台管理系统，当前所有数据由 mock 服务提供。
          </div>
        </div>
      </div>
    </a-card>

    <a-row :gutter="[16, 16]" class="stats-row">
      <a-col v-for="card in cards" :key="card.key" :xs="12" :sm="12" :md="8" :lg="6" :xl="6">
        <a-card variant="borderless" hoverable class="stat-card" @click="router.push(card.path)">
          <a-skeleton :loading="loading" active>
            <div class="stat">
              <div class="stat-icon" :style="{ background: `${card.color}1a`, color: card.color }">
                <component :is="card.icon" :size="22" />
              </div>
              <div class="stat-info">
                <div class="stat-title">{{ card.title }}</div>
                <div class="stat-value">{{ stats[card.key as keyof typeof stats] }}</div>
              </div>
            </div>
          </a-skeleton>
        </a-card>
      </a-col>
    </a-row>

    <a-row :gutter="[16, 16]">
      <a-col :xs="24" :lg="12">
        <a-card variant="borderless" title="最近登录日志" class="panel-card">
          <template #extra>
            <a @click="router.push('/system/login-log')">查看全部</a>
          </template>
          <a-table
            :data-source="recentLogs"
            :columns="[
              { title: '用户名', dataIndex: 'userName', key: 'userName' },
              { title: 'IP', dataIndex: 'ip', key: 'ip' },
              { title: '登录时间', dataIndex: 'loginTime', key: 'loginTime' },
              { title: '结果', dataIndex: 'success', key: 'success' },
            ]"
            :pagination="false"
            size="small"
            row-key="id"
          >
            <template #bodyCell="{ column, record }">
              <template v-if="column.key === 'success'">
                <a-tag :color="record.success ? 'success' : 'error'">
                  {{ record.success ? "成功" : "失败" }}
                </a-tag>
              </template>
            </template>
          </a-table>
        </a-card>
      </a-col>
      <a-col :xs="24" :lg="12">
        <a-card variant="borderless" title="快捷入口" class="panel-card">
          <div class="quick-links">
            <div
              v-for="card in cards.slice(0, 6)"
              :key="card.key"
              class="quick-link"
              @click="router.push(card.path)"
            >
              <component :is="card.icon" :size="18" :color="card.color" />
              <span>{{ card.title }}</span>
            </div>
          </div>
          <a-divider style="margin: 12px 0" />
          <div class="tips">
            <UserRound :size="14" />
            提示：登录后可体验用户、角色、权限、部门、租户、参数、字典、日志等完整功能。
          </div>
        </a-card>
      </a-col>
    </a-row>
  </div>
</template>

<style lang="scss" scoped>
.welcome-card {
  margin-bottom: 16px;
}

.welcome {
  display: flex;
  align-items: center;
  gap: 16px;
}

.welcome-avatar {
  background: linear-gradient(135deg, #1677ff, #0958d9);
  color: #fff;
  font-size: 22px;
}

.welcome-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--ant-color-text, rgb(0 0 0 / 88%));
}

.welcome-sub {
  margin-top: 4px;
  color: var(--ant-color-text-secondary, rgb(0 0 0 / 55%));
}

.stats-row {
  margin-bottom: 16px;
}

.stat {
  display: flex;
  align-items: center;
  gap: 14px;
}

.stat-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 46px;
  height: 46px;
  border-radius: 10px;
}

.stat-title {
  font-size: 13px;
  color: var(--ant-color-text-secondary, rgb(0 0 0 / 55%));
}

.stat-value {
  font-size: 22px;
  font-weight: 600;
  color: var(--ant-color-text, rgb(0 0 0 / 88%));
}

.panel-card {
  height: 100%;
}

.quick-links {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}

.quick-link {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px;
  border: 1px solid var(--ant-color-border-secondary, #f0f0f0);
  border-radius: 8px;
  cursor: pointer;

  &:hover {
    border-color: #1677ff;
    color: #1677ff;
  }
}

.tips {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--ant-color-text-secondary, rgb(0 0 0 / 55%));
  font-size: 13px;
}
</style>

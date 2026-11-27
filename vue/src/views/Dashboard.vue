<script setup lang="ts">
import { onMounted, reactive, ref } from "vue";
import {
  ArrowRight,
  BookMarked,
  Building2,
  CalendarDays,
  ChevronRight,
  ClipboardList,
  KeyRound,
  ShieldCheck,
  SlidersHorizontal,
  Users,
  Wifi,
} from "@lucide/vue";
import { deptApi, dictApi, logApi, paramApi, roleApi, userApi } from "@/api";
import type { LoginLogVO } from "@/api/modules/log";
import { useAuthStore } from "@/stores/auth";

const authStore = useAuthStore();
const today = new Date();
const dateLabel = new Intl.DateTimeFormat("zh-CN", { dateStyle: "full" }).format(today);

const greeting = (() => {
  const hour = today.getHours();
  if (hour < 6) return "夜深了";
  if (hour < 12) return "早上好";
  if (hour < 14) return "中午好";
  if (hour < 18) return "下午好";
  return "晚上好";
})();

const cards = [
  { key: "user", title: "用户总数", icon: Users, path: "/system/user", query: userApi.queryUser },
  {
    key: "role",
    title: "角色总数",
    icon: ShieldCheck,
    path: "/system/role",
    query: roleApi.queryRole,
  },
  {
    key: "dept",
    title: "部门总数",
    icon: Building2,
    path: "/system/dept",
    query: deptApi.queryDept,
  },
  {
    key: "online",
    title: "在线用户",
    icon: Wifi,
    path: "/system/online-user",
    query: userApi.queryOnlineUser,
  },
  {
    key: "param",
    title: "系统参数",
    icon: SlidersHorizontal,
    path: "/system/param",
    query: paramApi.queryParam,
  },
  {
    key: "dict",
    title: "系统字典",
    icon: BookMarked,
    path: "/system/dict",
    query: dictApi.queryDict,
  },
];

const quickLinks = [
  { title: "用户管理", icon: Users, path: "/system/user" },
  { title: "部门管理", icon: Building2, path: "/system/dept" },
  { title: "菜单管理", icon: KeyRound, path: "/system/permission" },
  { title: "操作日志", icon: ClipboardList, path: "/system/operation-log" },
];

const stats = reactive<Partial<Record<string, number>>>({});
const recentLogs = ref<LoginLogVO[]>([]);
const loading = ref(true);
const logsUnavailable = ref(false);

onMounted(async () => {
  const [logs] = await Promise.allSettled([
    logApi.queryLoginLog({ page: 1, pageSize: 5 }),
    ...cards.map(async ({ key, query }) => {
      const result = await query({ page: 1, pageSize: 1 });
      if (result.ok) stats[key] = result.total;
    }),
  ]);
  if (logs.status === "fulfilled" && logs.value.ok) {
    recentLogs.value = logs.value.data ?? [];
  } else {
    logsUnavailable.value = true;
  }
  loading.value = false;
});
</script>

<template>
  <div class="dashboard">
    <section class="welcome" aria-labelledby="welcome-title">
      <div class="welcome-content">
        <p class="welcome-date">
          <CalendarDays :size="16" aria-hidden="true" />
          {{ dateLabel }}
        </p>
        <h2 id="welcome-title">
          {{ greeting
          }}<template v-if="authStore.loginUser"
            >，{{ authStore.loginUser.userName || authStore.loginUser.userId }}</template
          >
        </h2>
      </div>
      <router-link class="welcome-link" to="/profile">
        个人信息
        <ArrowRight :size="16" aria-hidden="true" />
      </router-link>
    </section>

    <div class="stats-grid" :aria-busy="loading">
      <router-link v-for="card in cards" :key="card.key" :to="card.path" class="stat-card">
        <div class="stat-top">
          <span class="stat-icon">
            <component :is="card.icon" :size="19" aria-hidden="true" />
          </span>
          <ChevronRight class="link-arrow" :size="16" aria-hidden="true" />
        </div>
        <div class="stat-title">{{ card.title }}</div>
        <div class="stat-value">{{ stats[card.key]?.toLocaleString("zh-CN") ?? "—" }}</div>
      </router-link>
    </div>

    <div class="dashboard-panels">
      <section class="panel" aria-labelledby="recent-log-title">
        <div class="panel-header">
          <h2 id="recent-log-title">最近登录日志</h2>
          <router-link class="panel-link" to="/system/login-log">
            查看全部<span class="visually-hidden">登录日志</span>
            <ChevronRight :size="15" aria-hidden="true" />
          </router-link>
        </div>
        <div class="log-scroll" tabindex="0" role="region" aria-labelledby="recent-log-title">
          <table class="log-table" :aria-busy="loading">
            <thead>
              <tr>
                <th scope="col">登录者</th>
                <th scope="col">IP</th>
                <th scope="col">登录时间</th>
                <th scope="col">结果</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="log in recentLogs" :key="log.id">
                <td>{{ log.createBy || "—" }}</td>
                <td>{{ log.ip || "—" }}</td>
                <td>{{ log.createTime || "—" }}</td>
                <td>
                  <a-tag
                    :color="
                      log.succeed === true ? 'success' : log.succeed === false ? 'error' : undefined
                    "
                  >
                    {{ log.succeed === true ? "成功" : log.succeed === false ? "失败" : "未知" }}
                  </a-tag>
                </td>
              </tr>
              <tr v-if="!recentLogs.length">
                <td class="log-empty" colspan="4">
                  {{ loading ? "加载中…" : logsUnavailable ? "登录日志暂不可用" : "暂无登录记录" }}
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>

      <nav class="panel" aria-labelledby="quick-links-title">
        <div class="panel-header">
          <h2 id="quick-links-title">快捷入口</h2>
        </div>
        <div class="quick-links">
          <router-link
            v-for="link in quickLinks"
            :key="link.path"
            :to="link.path"
            class="quick-link"
          >
            <span class="quick-icon">
              <component :is="link.icon" :size="18" aria-hidden="true" />
            </span>
            <span>{{ link.title }}</span>
            <ChevronRight class="link-arrow" :size="16" aria-hidden="true" />
          </router-link>
        </div>
      </nav>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.dashboard {
  display: grid;
  gap: 16px;
  min-width: 0;
  container-type: inline-size;
  color: var(--ui-text, #182033);
}

.welcome {
  position: relative;
  isolation: isolate;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 16px 24px;
  overflow: hidden;
  padding: 23px clamp(18px, 3cqi, 30px);
  border-radius: 14px;
  background: linear-gradient(110deg, #3933b5, #4b45d4 72%, #5650de);
  color: #fff;

  &::before,
  &::after {
    position: absolute;
    z-index: -1;
    pointer-events: none;
    content: "";
  }

  &::before {
    inset: -90px 7% auto auto;
    width: 220px;
    aspect-ratio: 1;
    border: 1px solid rgb(255 255 255 / 18%);
    border-radius: 50%;
    box-shadow:
      0 0 0 26px rgb(255 255 255 / 6%),
      0 0 0 52px rgb(255 255 255 / 4%);
  }

  &::after {
    inset: 13px 16px auto auto;
    width: 84px;
    height: 70px;
    background: radial-gradient(circle, rgb(255 255 255 / 24%) 1px, transparent 1.3px) 0 0 / 12px
      12px;
  }

  h2 {
    margin: 0;
    color: inherit;
    font-size: clamp(22px, 3cqi, 28px);
    font-weight: 650;
    line-height: 1.3;
    overflow-wrap: anywhere;
  }
}

.welcome-content {
  min-width: 0;
}

.welcome-date,
.welcome-link,
.panel-link {
  display: inline-flex;
  align-items: center;
  gap: 7px;
}

.welcome-date {
  margin: 0 0 8px;
  color: #eeedff;
  font-size: 13px;
}

.welcome-link {
  padding: 8px 12px;
  border: 1px solid rgb(255 255 255 / 32%);
  border-radius: 8px;
  background: rgb(255 255 255 / 9%);
  color: #fff;
  font-weight: 600;
  white-space: nowrap;

  &:hover {
    background: rgb(255 255 255 / 20%);
    color: #fff;
  }
}

.stats-grid,
.dashboard-panels {
  display: grid;
  gap: 14px;
}

.stats-grid {
  grid-template-columns: repeat(6, minmax(0, 1fr));
}

.stat-card,
.panel {
  min-width: 0;
  border: 1px solid var(--ui-border, #e7e9f1);
  border-radius: 12px;
  background: var(--ui-surface, #fff);
  box-shadow: var(--ui-shadow, 0 1px 3px rgb(24 32 51 / 5%));
}

.stat-card {
  display: block;
  padding: 14px;
  color: inherit;

  &:hover {
    border-color: var(--ui-accent, #4b45d4);
    color: inherit;
  }
}

.stat-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 6px;
  margin-bottom: 10px;
}

.stat-icon,
.quick-icon {
  display: grid;
  flex: none;
  place-items: center;
  width: 36px;
  height: 36px;
  border-radius: 9px;
  background: var(--ui-accent-soft, #eeedff);
  color: var(--ui-accent, #4b45d4);
}

.link-arrow,
.stat-title {
  color: var(--ui-muted, #626b80);
}

.stat-title {
  font-size: 13px;
}

.stat-value {
  margin-top: 1px;
  font-size: 24px;
  font-weight: 650;
  font-variant-numeric: tabular-nums;
  line-height: 1.25;
  overflow-wrap: anywhere;
}

.dashboard-panels {
  align-items: start;
  grid-template-columns: minmax(0, 2.2fr) minmax(230px, 1fr);
}

.panel-header {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 6px 12px;
  padding: 14px 16px;

  h2 {
    margin: 0;
    color: inherit;
    font-size: 16px;
    font-weight: 650;
  }
}

.panel-link {
  color: var(--ui-accent, #4b45d4);
  font-size: 13px;
  white-space: nowrap;

  &:hover {
    color: var(--ui-accent, #4b45d4);
    text-decoration: underline;
  }
}

.log-scroll {
  margin: 0 16px 12px;
  overflow-x: auto;
}

.log-table {
  width: 100%;
  min-width: 420px;
  border-collapse: collapse;
  text-align: left;
  font-size: 13px;
  font-variant-numeric: tabular-nums;

  th,
  td {
    padding: 9px 10px;
    overflow-wrap: anywhere;
  }

  th {
    background: var(--ui-canvas, #f5f6fa);
    color: var(--ui-muted, #626b80);
    font-weight: 600;
    white-space: nowrap;
  }

  th:first-child {
    border-radius: 6px 0 0 6px;
  }

  th:last-child {
    border-radius: 0 6px 6px 0;
  }

  tr + tr {
    border-top: 1px solid var(--ui-border, #e7e9f1);
  }

  td:last-child {
    white-space: nowrap;
  }

  :deep(.ant-tag) {
    margin-inline-end: 0;
  }
}

.log-table .log-empty {
  padding: 26px 10px;
  color: var(--ui-muted, #626b80);
  text-align: center;
}

.quick-links {
  display: grid;
  gap: 7px;
  padding: 0 12px 12px;
}

.quick-link {
  display: grid;
  grid-template-columns: auto minmax(0, 1fr) auto;
  align-items: center;
  gap: 10px;
  padding: 6px 8px;
  border: 1px solid var(--ui-border, #e7e9f1);
  border-radius: 8px;
  color: inherit;
  font-weight: 500;

  &:hover {
    border-color: var(--ui-accent, #4b45d4);
    color: var(--ui-accent, #4b45d4);
  }
}

.quick-icon {
  width: 32px;
  height: 32px;
  border-radius: 7px;
}

.dashboard a:focus-visible,
.log-scroll:focus-visible {
  outline: 2px solid var(--ui-accent, #4b45d4);
  outline-offset: 2px;
}

.dashboard .welcome-link:focus-visible {
  outline-color: #fff;
}

.visually-hidden {
  position: absolute;
  width: 1px;
  height: 1px;
  overflow: hidden;
  clip-path: inset(50%);
  white-space: nowrap;
}

@container (max-width: 940px) {
  .stats-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@container (max-width: 720px) {
  .dashboard-panels {
    grid-template-columns: minmax(0, 1fr);
  }
}

@container (max-width: 480px) {
  .stats-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>

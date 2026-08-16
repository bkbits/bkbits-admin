<script setup lang="ts">
import { computed, h, onMounted, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import {
  Bell,
  BookMarked,
  Building2,
  Globe,
  IdCard,
  KeyRound,
  LayoutDashboard,
  LogIn,
  LogOut,
  Menu as MenuIcon,
  Moon,
  MoreHorizontal,
  ScrollText,
  Search,
  Settings,
  ShieldCheck,
  SlidersHorizontal,
  Sun,
  UserRound,
  Users,
  Wifi,
} from "lucide-vue-next";
import { message, type MenuProps } from "antdv-next";
import { permissionApi } from "../api";
import { notificationApi } from "../api";
import { userApi } from "../api";
import type { Notification, Permission } from "../api/types";
import { MENU_ROUTE_MAP } from "../router";
import { useAppStore } from "../stores/app";
import { useAuthStore } from "../stores/auth";
import { useTabsStore } from "../stores/tabs";

const route = useRoute();
const router = useRouter();
const appStore = useAppStore();
const authStore = useAuthStore();

/* ------------------------------ 菜单 ------------------------------ */

/** permission 标识 → 图标 */
const ICONS: Record<string, typeof Settings> = {
  personal: UserRound,
  dashboard: LayoutDashboard,
  profile: IdCard,
  system: Settings,
  "system:user": Users,
  "system:onlineUser": Wifi,
  "system:role": ShieldCheck,
  "system:permission": KeyRound,
  "system:dept": Building2,
  "system:tenant": Globe,
  "system:param": SlidersHorizontal,
  "system:dict": BookMarked,
  "system:loginLog": LogIn,
  "system:operationLog": ScrollText,
};

const flatPermissions = ref<Permission[]>([]);

/** 菜单树（CATALOG → 子菜单，MENU → 菜单项，过滤 BUTTON） */
const menuItems = computed<MenuProps["items"]>(() => {
  const nodes = flatPermissions.value.filter((p) => p.status === "E" && p.type !== "BUTTON");
  const build = (parentId: string): NonNullable<MenuProps["items"]> =>
    nodes
      .filter((n) => (n.parentId ?? "0") === parentId)
      .sort((a, b) => (a.sort ?? 0) - (b.sort ?? 0))
      .map((n) => {
        const icon = ICONS[n.permission ?? ""] ?? Settings;
        if (n.type === "CATALOG") {
          return {
            key: `catalog-${n.id}`,
            icon: () => h(icon),
            label: n.name,
            children: build(n.id ?? ""),
          };
        }
        const path = MENU_ROUTE_MAP[n.component ?? ""] ?? "";
        return {
          key: path,
          icon: () => h(icon),
          label: n.name,
          disabled: !path,
        };
      });
  return build("0");
});

/** 查找当前路径对应权限在菜单树中的祖先链（含自身，自顶向下） */
function findMenuChain(path: string): Permission[] {
  const current = flatPermissions.value.find((p) => MENU_ROUTE_MAP[p.component ?? ""] === path);
  if (!current) return [];
  const chain: Permission[] = [current];
  let cursor = current;
  while (cursor.parentId && cursor.parentId !== "0") {
    const parent = flatPermissions.value.find((p) => p.id === cursor.parentId);
    if (!parent) break;
    chain.unshift(parent);
    cursor = parent;
  }
  return chain;
}

/** 面包屑：当前路由对应的菜单链 */
const breadcrumbs = computed<string[]>(() => {
  const chain = findMenuChain(route.path);
  if (chain.length === 0) return [String(route.meta.title ?? "")];
  return chain.map((c) => c.name ?? "");
});

/** 侧边栏当前展开的目录 key */
const openKeys = ref<string[]>([]);

/** 根据当前路由自动展开上级目录（保留用户手动展开的其它目录） */
function syncOpenKeys() {
  const keys = findMenuChain(route.path)
    .filter((p) => p.type === "CATALOG")
    .map((p) => `catalog-${p.id ?? ""}`);
  openKeys.value = [...new Set([...openKeys.value, ...keys])];
}

/** 用户手动展开/收起目录时同步状态 */
function onOpenChange(keys: string[]) {
  openKeys.value = keys;
}

// 路由变化（含浏览器前进/后退、刷新后首次进入）时自动展开上级目录
watch(() => route.path, syncOpenKeys);

/** 菜单搜索候选（目录 + 菜单） */
const searchOptions = computed(() =>
  flatPermissions.value
    .filter((p) => p.status === "E" && p.type !== "BUTTON")
    .map((p) => ({
      value:
        p.type === "MENU" ? (MENU_ROUTE_MAP[p.component ?? ""] ?? p.name ?? "") : (p.name ?? ""),
      label: p.name ?? "",
      path: p.type === "MENU" ? MENU_ROUTE_MAP[p.component ?? ""] : undefined,
    })),
);

function onMenuClick({ key }: { key: string | number }) {
  const k = String(key);
  if (k.startsWith("/")) router.push(k);
}

function onSearchSelect(value: string) {
  const item = searchOptions.value.find((o) => o.value === value);
  if (item?.path) router.push(item.path);
}

/* ------------------------------ 通知 ------------------------------ */

const notifications = ref<Notification[]>([]);

async function loadNotifications() {
  const result = await notificationApi.listNotification({ page: 1, pageSize: 20 });
  notifications.value = result.data?.rows ?? [];
}

/* ------------------------------ 用户 ------------------------------ */

const userName = computed(
  () => authStore.loginUser?.realName || authStore.loginUser?.userName || "用户",
);
const userInitial = computed(() => userName.value.charAt(0).toUpperCase());

async function handleLogout() {
  await authStore.logout();
  message.success("已退出登录");
  router.push("/login");
}

/* ------------------------------ 修改密码 ------------------------------ */

const passwordVisible = ref(false);
const passwordForm = ref({ oldPassword: "", password: "", confirmPassword: "" });
const passwordSubmitting = ref(false);

function openPasswordModal() {
  passwordForm.value = { oldPassword: "", password: "", confirmPassword: "" };
  passwordVisible.value = true;
}

async function submitPassword() {
  if (!passwordForm.value.oldPassword || !passwordForm.value.password) {
    message.warning("请填写原密码与新密码");
    return;
  }
  if (passwordForm.value.password !== passwordForm.value.confirmPassword) {
    message.warning("两次输入的新密码不一致");
    return;
  }
  passwordSubmitting.value = true;
  try {
    const result = await userApi.updateMyPassword({
      oldPassword: passwordForm.value.oldPassword,
      password: passwordForm.value.password,
    });
    if (result.ok) {
      message.success("密码修改成功，请重新登录");
      passwordVisible.value = false;
      await handleLogout();
    } else {
      message.error(result.message);
    }
  } finally {
    passwordSubmitting.value = false;
  }
}

/* ------------------------------ 用户下拉菜单 ------------------------------ */

const userMenuItems = computed<NonNullable<MenuProps["items"]>>(() => [
  { key: "profile", icon: () => h(IdCard, { size: 15 }), label: "个人信息" },
  { key: "password", icon: () => h(KeyRound, { size: 15 }), label: "修改密码" },
  { type: "divider" },
  { key: "logout", icon: () => h(LogOut, { size: 15 }), label: "退出登录", danger: true },
]);

function onUserMenuClick({ key }: { key: string | number }) {
  if (key === "profile") {
    router.push("/profile");
  } else if (key === "password") {
    openPasswordModal();
  } else if (key === "logout") {
    void handleLogout();
  }
}

/* ------------------------------ 标签页栏 ------------------------------ */

const tabsStore = useTabsStore();

/** 当前路由变化时打开对应标签页 */
watch(
  () => route.path,
  (path) => {
    const title = typeof route.meta.title === "string" ? route.meta.title : "未命名";
    tabsStore.openTab(path, title);
  },
  { immediate: true },
);

/** 切换标签页 */
function onTabChange(key: string | number) {
  const path = String(key);
  if (path !== route.path) router.push(path);
}

/** 关闭标签页（点击标签上的关闭按钮） */
function onTabEdit(targetKey: string | number | MouseEvent, action: "add" | "remove") {
  if (action !== "remove") return;
  const path = String(targetKey);
  const next = tabsStore.closeTab(path);
  if (path === route.path && next) {
    router.push(next);
  }
}

/** 标签页更多操作（关闭其他 / 关闭全部） */
const tabsActionItems: NonNullable<MenuProps["items"]> = [
  { key: "closeOthers", label: "关闭其他" },
  { key: "closeAll", label: "关闭全部" },
];

function onTabsAction({ key }: { key: string | number }) {
  if (key === "closeOthers") {
    const path = tabsStore.closeOthers(route.path);
    if (path !== route.path) router.push(path);
  } else if (key === "closeAll") {
    const path = tabsStore.closeAll();
    if (path !== route.path) router.push(path);
  }
}

/* ------------------------------ 初始化 ------------------------------ */

onMounted(async () => {
  await authStore.fetchLoginUser();
  try {
    const result = await permissionApi.listPermission();
    flatPermissions.value = result.data ?? [];
  } catch {
    // 菜单加载失败时忽略，保留空菜单
  }
  // 菜单数据就绪后，按当前路由展开上级目录
  syncOpenKeys();
  void loadNotifications();
});

const copyright = ref("© 2025 bkbits-admin");
</script>

<template>
  <a-layout class="admin-layout">
    <!-- 左侧边栏 -->
    <a-layout-sider
      v-model:collapsed="appStore.collapsed"
      :trigger="null"
      collapsible
      :width="224"
      class="admin-sider"
    >
      <div class="sider-logo" @click="router.push('/dashboard')">
        <img src="/favicon.svg" alt="logo" class="sider-logo-img" />
        <span v-if="!appStore.collapsed" class="sider-logo-title">bkbits-admin</span>
      </div>
      <a-menu
        theme="dark"
        mode="inline"
        :selected-keys="[route.path]"
        :open-keys="openKeys"
        :items="menuItems"
        class="sider-menu"
        @click="onMenuClick"
        @open-change="onOpenChange"
      />
    </a-layout-sider>

    <a-layout class="admin-main">
      <!-- 顶部标题栏 -->
      <a-layout-header class="admin-header">
        <div class="header-left">
          <a-button type="text" class="header-icon-btn" @click="appStore.toggleCollapsed()">
            <MenuIcon :size="18" />
          </a-button>
          <a-breadcrumb class="header-breadcrumb">
            <a-breadcrumb-item v-for="(item, index) in breadcrumbs" :key="index">
              {{ item }}
            </a-breadcrumb-item>
          </a-breadcrumb>
        </div>

        <div class="header-right">
          <!-- 搜索栏 -->
          <a-auto-complete
            class="header-search"
            :options="searchOptions"
            placeholder="搜索菜单"
            allow-clear
            @select="onSearchSelect"
          >
            <template #suffixIcon>
              <Search :size="15" />
            </template>
          </a-auto-complete>

          <!-- 主题切换 -->
          <a-tooltip :title="appStore.isDark ? '切换为亮色主题' : '切换为暗色主题'">
            <a-button type="text" class="header-icon-btn" @click="appStore.toggleTheme()">
              <Sun v-if="appStore.isDark" :size="18" />
              <Moon v-else :size="18" />
            </a-button>
          </a-tooltip>

          <!-- 通知下拉 -->
          <a-popover placement="bottomRight" trigger="click">
            <a-badge :count="notifications.length" :offset="[-4, 4]">
              <a-button type="text" class="header-icon-btn">
                <Bell :size="18" />
              </a-button>
            </a-badge>
            <template #content>
              <div class="notification-menu">
                <div class="notification-head">通知公告</div>
                <div v-for="item in notifications" :key="item.id" class="notification-item">
                  <div class="notification-title">{{ item.title }}</div>
                  <div class="notification-content">{{ item.content }}</div>
                  <div class="notification-time">{{ item.publishTime }}</div>
                </div>
                <div v-if="notifications.length === 0" class="notification-empty">暂无通知</div>
              </div>
            </template>
          </a-popover>

          <!-- 用户信息下拉 -->
          <a-dropdown
            placement="bottomRight"
            :trigger="['click']"
            :menu="{ items: userMenuItems, onClick: onUserMenuClick }"
          >
            <div class="header-user">
              <a-avatar class="header-avatar" :size="30">{{ userInitial }}</a-avatar>
              <span class="header-username">{{ userName }}</span>
            </div>
          </a-dropdown>
        </div>
      </a-layout-header>

      <!-- 标签页栏 -->
      <div class="admin-tabs-bar">
        <a-tabs
          type="editable-card"
          hide-add
          size="small"
          :active-key="route.path"
          class="admin-tabs"
          @change="onTabChange"
          @edit="onTabEdit"
        >
          <a-tab-pane
            v-for="tab in tabsStore.tabs"
            :key="tab.path"
            :tab="tab.title"
            :closable="tab.closable"
          />
        </a-tabs>
        <a-dropdown :trigger="['click']" :menu="{ items: tabsActionItems, onClick: onTabsAction }">
          <a-button type="text" size="small" class="tabs-action-btn" title="标签页操作">
            <MoreHorizontal :size="14" />
          </a-button>
        </a-dropdown>
      </div>

      <!-- 内容区域 -->
      <a-layout-content class="admin-content">
        <router-view />
      </a-layout-content>

      <!-- 脚注 -->
      <a-layout-footer class="admin-footer">{{ copyright }}</a-layout-footer>
    </a-layout>
  </a-layout>

  <!-- 修改密码 -->
  <a-modal
    v-model:open="passwordVisible"
    title="修改密码"
    :confirm-loading="passwordSubmitting"
    ok-text="确定"
    cancel-text="取消"
    @ok="submitPassword"
  >
    <a-form :model="passwordForm" layout="vertical" class="password-form">
      <a-form-item label="原密码" required>
        <a-input-password v-model:value="passwordForm.oldPassword" placeholder="请输入原密码" />
      </a-form-item>
      <a-form-item label="新密码" required>
        <a-input-password v-model:value="passwordForm.password" placeholder="请输入新密码" />
      </a-form-item>
      <a-form-item label="确认新密码" required>
        <a-input-password
          v-model:value="passwordForm.confirmPassword"
          placeholder="请再次输入新密码"
        />
      </a-form-item>
    </a-form>
  </a-modal>
</template>

<style lang="scss" scoped>
.admin-layout {
  height: 100vh;
}

.admin-sider {
  box-shadow: 2px 0 8px rgb(0 0 0 / 6%);
  z-index: 10;
}

.sider-logo {
  display: flex;
  align-items: center;
  gap: 10px;
  height: 56px;
  padding: 0 18px;
  cursor: pointer;
  overflow: hidden;
  white-space: nowrap;
}

.sider-logo-img {
  width: 26px;
  height: 26px;
  flex-shrink: 0;
}

.sider-logo-title {
  color: #fff;
  font-size: 16px;
  font-weight: 600;
}

.sider-menu {
  border-inline-end: none;
}

.admin-main {
  min-width: 0;
}

.admin-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 56px;
  padding: 0 16px;
  background: var(--ant-color-bg-container, #fff);
  border-bottom: 1px solid var(--ant-color-border-secondary, #f0f0f0);
  line-height: 56px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.header-icon-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: var(--ant-color-text, rgb(0 0 0 / 88%));
}

.header-breadcrumb {
  white-space: nowrap;
}

.header-search {
  width: 220px;
}

.header-user {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 8px;
  cursor: pointer;
  border-radius: 6px;

  &:hover {
    background: var(--ant-color-fill-quaternary, rgb(0 0 0 / 4%));
  }
}

.header-avatar {
  background: #1677ff;
  color: #fff;
}

.header-username {
  font-size: 14px;
  color: var(--ant-color-text, rgb(0 0 0 / 88%));
}

.menu-item-icon {
  vertical-align: -3px;
  margin-right: 6px;
}

.notification-menu {
  width: 320px;
  max-height: 420px;
  overflow-y: auto;
  padding: 4px 0;
}

.notification-head {
  padding: 10px 16px;
  font-weight: 600;
  border-bottom: 1px solid var(--ant-color-border-secondary, #f0f0f0);
}

.notification-item {
  height: auto !important;
  padding: 10px 16px !important;
  white-space: normal;
}

.notification-title {
  font-weight: 500;
}

.notification-content {
  margin-top: 2px;
  font-size: 12px;
  color: var(--ant-color-text-secondary, rgb(0 0 0 / 65%));
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.notification-time {
  margin-top: 4px;
  font-size: 11px;
  color: var(--ant-color-text-tertiary, rgb(0 0 0 / 45%));
}

.notification-empty {
  padding: 24px 16px;
  text-align: center;
  color: var(--ant-color-text-tertiary, rgb(0 0 0 / 45%));
}

.admin-tabs-bar {
  display: flex;
  align-items: center;
  background: var(--ant-color-bg-container, #fff);
  border-bottom: 1px solid var(--ant-color-border-secondary, #f0f0f0);
  padding: 4px 8px 0;
}

.admin-tabs {
  flex: 1;
  min-width: 0;

  :deep(.ant-tabs-nav) {
    margin-bottom: 4px;
  }

  :deep(.ant-tabs-tab) {
    border-radius: 6px 6px 0 0;
  }
}

.tabs-action-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  margin-left: 4px;
  color: var(--ant-color-text-secondary, rgb(0 0 0 / 65%));
}

.admin-content {
  padding: 16px;
  overflow: auto;
}

.admin-footer {
  padding: 10px 16px;
  text-align: center;
  color: var(--ant-color-text-tertiary, rgb(0 0 0 / 45%));
  font-size: 13px;
}

.password-form {
  padding-top: 12px;
}
</style>

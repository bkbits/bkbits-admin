<script setup lang="ts">
import { computed, h, nextTick, onMounted, onUnmounted, ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import {
  Bell,
  BookMarked,
  Building2,
  ChevronDown,
  Gauge,
  IdCard,
  KeyRound,
  LogIn,
  LogOut,
  Menu as MenuIcon,
  Moon,
  MoreHorizontal,
  PanelLeftClose,
  PanelLeftOpen,
  ScrollText,
  Search,
  Settings,
  ShieldCheck,
  SlidersHorizontal,
  Sun,
  Users,
  Wifi,
} from "@lucide/vue";
import { message, type MenuProps } from "antdv-next";
import { permissionApi } from "@/api";
import { notificationApi } from "@/api";
import { userApi } from "@/api";
import type { Notification, Permission } from "@/api/types";
import { MENU_ROUTE_MAP } from "@/router";
import { useAppStore } from "@/stores/app";
import { useAuthStore } from "@/stores/auth";
import { useTabsStore } from "@/stores/tabs";

const route = useRoute();
const router = useRouter();
const appStore = useAppStore();
const authStore = useAuthStore();
const appTitle = import.meta.env.VITE_TITLE;
const mobileQuery = window.matchMedia("(max-width: 760px)");
const isMobile = ref(mobileQuery.matches);
const mobileMenuOpen = ref(false);
const sidebarCollapsed = computed(() => !isMobile.value && appStore.collapsed);
const menuButton = ref<HTMLButtonElement>();

function closeMobileMenu(restoreFocus = false) {
  mobileMenuOpen.value = false;
  if (restoreFocus) menuButton.value?.focus();
}

function syncViewport(event: MediaQueryListEvent) {
  isMobile.value = event.matches;
  closeMobileMenu();
}

async function toggleSidebar() {
  if (!isMobile.value) {
    appStore.toggleCollapsed();
    return;
  }
  if (mobileMenuOpen.value) {
    closeMobileMenu(true);
    return;
  }
  mobileMenuOpen.value = true;
  await nextTick();
  document.querySelector<HTMLAnchorElement>("#admin-sidebar .sider-logo")?.focus();
}

onMounted(() => mobileQuery.addEventListener("change", syncViewport));
onUnmounted(() => mobileQuery.removeEventListener("change", syncViewport));

/* ------------------------------ 菜单 ------------------------------ */

const sidebarMenuTheme = {
  components: {
    Menu: {
      itemHoverColor: "var(--ui-accent)",
      itemHoverBg: "var(--ui-canvas)",
      itemActiveBg: "var(--ui-accent-soft)",
      itemSelectedBg: "var(--ui-accent-soft)",
      itemSelectedColor: "var(--ui-accent)",
      subMenuItemSelectedColor: "var(--ui-accent)",
      subMenuItemBg: "var(--ui-surface)",
      popupBg: "var(--ui-surface)",
    },
  },
};

/** 权限码 → 图标（后端权限码为 admin.xxx；目录无权限码时回退 Settings） */
const ICONS: Record<string, typeof Settings> = {
  "admin.dept": Building2,
  "admin.user": Users,
  "admin.role": ShieldCheck,
  "admin.permission": KeyRound,
  "admin.dict": BookMarked,
  "admin.param": SlidersHorizontal,
  "admin.loginLog": LogIn,
  "admin.operLog": ScrollText,
  "admin.notification": Bell,
};

const ROUTE_ICONS: Record<string, typeof Settings> = {
  "/dashboard": Gauge,
  "/profile": IdCard,
  "/system/online-user": Wifi,
};

const flatPermissions = ref<Permission[]>([]);

/** 菜单树（D 目录 → 子菜单，M 菜单 → 菜单项，过滤 B 按钮） */
const menuItems = computed<MenuProps["items"]>(() => {
  const nodes = flatPermissions.value.filter((p) => p.status === "E" && p.type !== "B");
  const build = (parentId: string): NonNullable<MenuProps["items"]> =>
    nodes
      .filter((n) => (n.parentId ?? "0") === parentId)
      .sort((a, b) => (a.sort ?? 0) - (b.sort ?? 0))
      .map((n) => {
        const path = MENU_ROUTE_MAP[n.component ?? ""] ?? "";
        const icon = ICONS[n.permission ?? ""] ?? ROUTE_ICONS[path] ?? Settings;
        if (n.type === "D") {
          return {
            key: `catalog-${n.id}`,
            icon: () => h(icon),
            label: n.name,
            children: build(n.id ?? ""),
          };
        }
        return {
          key: path || `menu-${n.id}`,
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

const pageTitle = computed(() => breadcrumbs.value.at(-1) || String(route.meta.title ?? ""));
const pageIcon = computed(() => {
  const permission = findMenuChain(route.path).at(-1)?.permission ?? "";
  return ICONS[permission] ?? ROUTE_ICONS[route.path] ?? Settings;
});

/** 侧边栏当前展开的目录 key */
const openKeys = ref<string[]>([]);

/** 根据当前路由自动展开上级目录（保留用户手动展开的其它目录） */
function syncOpenKeys() {
  const keys = findMenuChain(route.path)
    .filter((p) => p.type === "D")
    .map((p) => `catalog-${p.id ?? ""}`);
  openKeys.value = [...new Set([...openKeys.value, ...keys])];
}

/** 用户手动展开/收起目录时同步状态 */
function onOpenChange(keys: string[]) {
  openKeys.value = keys;
}

// 路由变化（含浏览器前进/后退、刷新后首次进入）时自动展开上级目录
watch(
  () => route.path,
  () => {
    syncOpenKeys();
    closeMobileMenu();
  },
);

/** 菜单搜索候选 */
const searchValue = ref("");
const searchOptions = computed(() =>
  flatPermissions.value.flatMap((permission) => {
    const path = MENU_ROUTE_MAP[permission.component ?? ""];
    return permission.status === "E" && permission.type === "M" && path
      ? [{ value: path, label: permission.name ?? path }]
      : [];
  }),
);

function onMenuClick({ key }: { key: string | number }) {
  const path = String(key);
  if (!path.startsWith("/")) return;
  closeMobileMenu(isMobile.value);
  router.push(path);
}

function onSearchSelect(path: string) {
  void router.push(path).then(() => {
    searchValue.value = "";
  });
}

/* ------------------------------ 通知 ------------------------------ */

const notifications = ref<Notification[]>([]);

async function loadNotifications() {
  const result = await notificationApi.listNotification({ page: 1, pageSize: 20 });
  notifications.value = result.data ?? [];
}

/* ------------------------------ 用户 ------------------------------ */

const userName = computed(
  () => authStore.loginUser?.userName || authStore.loginUser?.userId || "用户",
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

const copyright = `© ${new Date().getFullYear()} bkbits-admin`;
</script>

<template>
  <a-layout class="admin-layout" @keydown.esc="mobileMenuOpen && closeMobileMenu(true)">
    <div v-if="mobileMenuOpen" class="sidebar-backdrop" @click="closeMobileMenu(true)" />
    <!-- 左侧边栏 -->
    <a-layout-sider
      id="admin-sidebar"
      aria-label="主导航"
      :collapsed="sidebarCollapsed"
      :theme="appStore.theme"
      :trigger="null"
      :width="228"
      :collapsed-width="72"
      :inert="isMobile && !mobileMenuOpen"
      :class="{ 'is-open': mobileMenuOpen }"
      class="admin-sider"
    >
      <router-link
        to="/dashboard"
        class="sider-logo"
        :aria-label="`${appTitle} 首页`"
        @click="closeMobileMenu(isMobile)"
      >
        <span class="sider-logo-mark"><img src="/favicon.svg" alt="" /></span>
        <span v-if="!sidebarCollapsed" class="sider-logo-title">{{ appTitle }}</span>
      </router-link>
      <a-config-provider :theme="sidebarMenuTheme">
        <a-menu
          mode="inline"
          :selected-keys="[route.path]"
          :open-keys="openKeys"
          :items="menuItems"
          class="sider-menu"
          @click="onMenuClick"
          @open-change="onOpenChange"
        />
      </a-config-provider>
    </a-layout-sider>

    <a-layout class="admin-main">
      <!-- 顶部标题栏 -->
      <a-layout-header class="admin-header">
        <div class="header-left">
          <button
            ref="menuButton"
            type="button"
            class="header-icon-btn sidebar-trigger"
            :aria-label="(isMobile ? mobileMenuOpen : !sidebarCollapsed) ? '收起导航' : '展开导航'"
            :aria-expanded="isMobile ? mobileMenuOpen : !sidebarCollapsed"
            aria-controls="admin-sidebar"
            @click="toggleSidebar"
          >
            <MenuIcon v-if="isMobile" :size="19" aria-hidden="true" />
            <PanelLeftOpen v-else-if="sidebarCollapsed" :size="19" aria-hidden="true" />
            <PanelLeftClose v-else :size="19" aria-hidden="true" />
          </button>
          <a-breadcrumb class="header-breadcrumb">
            <a-breadcrumb-item v-for="(item, index) in breadcrumbs" :key="index">
              {{ item }}
            </a-breadcrumb-item>
          </a-breadcrumb>
        </div>

        <div class="header-right">
          <!-- 搜索栏 -->
          <a-auto-complete
            v-model:value="searchValue"
            class="header-search"
            :options="searchOptions"
            default-active-first-option
            :show-search="{
              filterOption: (input: string, option: any) =>
                String(option?.label ?? '').includes(input.trim()),
            }"
            @select="onSearchSelect"
          >
            <a-input placeholder="搜索菜单" aria-label="搜索菜单" allow-clear>
              <template #prefix><Search :size="16" aria-hidden="true" /></template>
            </a-input>
          </a-auto-complete>

          <!-- 主题切换 -->
          <a-tooltip :title="appStore.isDark ? '切换为亮色主题' : '切换为暗色主题'">
            <a-button
              type="text"
              class="header-icon-btn"
              :aria-label="appStore.isDark ? '切换为亮色主题' : '切换为暗色主题'"
              @click="appStore.toggleTheme()"
            >
              <Sun v-if="appStore.isDark" :size="18" />
              <Moon v-else :size="18" />
            </a-button>
          </a-tooltip>

          <!-- 通知下拉 -->
          <a-popover placement="bottomRight" trigger="click">
            <a-badge :count="notifications.length" :offset="[-4, 4]">
              <a-button type="text" class="header-icon-btn" aria-label="通知公告">
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
            <button type="button" class="header-user" aria-label="账号菜单">
              <a-avatar class="header-avatar" :size="32">{{ userInitial }}</a-avatar>
              <span class="header-username">{{ userName }}</span>
              <ChevronDown :size="14" aria-hidden="true" />
            </button>
          </a-dropdown>
        </div>
      </a-layout-header>

      <!-- 标签页栏 -->
      <div class="admin-tabs-bar" :inert="mobileMenuOpen">
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
          <a-button type="text" size="small" class="tabs-action-btn" aria-label="标签页操作">
            <MoreHorizontal :size="14" />
          </a-button>
        </a-dropdown>
      </div>

      <!-- 内容区域 -->
      <a-layout-content class="admin-content" :inert="mobileMenuOpen">
        <div class="admin-page">
          <header class="page-heading">
            <span class="page-heading-icon">
              <component :is="pageIcon" :size="23" aria-hidden="true" />
            </span>
            <h1>{{ pageTitle }}</h1>
          </header>
          <router-view />
          <footer class="admin-footer">{{ copyright }}</footer>
        </div>
      </a-layout-content>
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
  height: 100dvh;
  background: var(--ui-canvas);
}

.admin-sider {
  z-index: 10;
  background: var(--ui-surface);
  border-inline-end: 1px solid var(--ui-border);

  :deep(.ant-layout-sider-children) {
    display: flex;
    flex-direction: column;
  }
}

.sider-logo {
  display: flex;
  flex: none;
  align-items: center;
  gap: 10px;
  height: 64px;
  margin: 0 12px 12px;
  padding: 0 6px;
  border-bottom: 1px solid var(--ui-border);
  white-space: nowrap;
  text-decoration: none;
}

.sider-logo-mark {
  display: grid;
  width: 36px;
  height: 36px;
  flex: none;
  place-items: center;
  border: 1px solid var(--ui-border);
  border-radius: 10px;
  background: var(--ui-accent-soft);

  img {
    width: 21px;
    height: 21px;
  }
}

.sider-logo-title {
  min-width: 0;
  overflow: hidden;
  color: var(--ui-text);
  font-size: 19px;
  font-weight: 730;
  text-overflow: ellipsis;
}

// a-menu 的 class 落在内部 ul 上，该 ul 不带作用域属性，故用 :deep 匹配
:deep(.sider-menu) {
  flex: 1;
  min-height: 0;
  overflow-y: auto;
  overscroll-behavior: contain;
  background: transparent;
  border-inline-end: 0;

  .ant-menu-item,
  .ant-menu-submenu-title {
    width: calc(100% - 16px);
    height: 38px;
    margin: 3px 8px;
  }
  .ant-menu-item-selected {
    font-weight: 650;
  }
}

.admin-main {
  min-width: 0;
  background: var(--ui-canvas);
}

.admin-header,
.admin-tabs-bar {
  display: flex;
  align-items: center;
  flex: none;
  background: var(--ui-surface);
  border-bottom: 1px solid var(--ui-border);
}

.admin-header {
  justify-content: space-between;
  height: 60px;
  gap: 12px;
  padding: 0 24px;
  line-height: normal;
}

.header-left,
.header-right {
  display: flex;
  align-items: center;
  min-width: 0;
  gap: 8px;
}

.header-breadcrumb {
  margin-inline-start: 4px;
  white-space: nowrap;
}

.header-icon-btn,
.header-user {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 36px;
  flex: none;
  border: 0;
  border-radius: 8px;
  color: var(--ui-muted);
  background: transparent;
  cursor: pointer;

  &:hover {
    color: var(--ui-accent);
    background: var(--ui-accent-soft);
  }
}

.header-icon-btn {
  width: 36px;
  padding: 0;
}
.header-search {
  width: 214px;
  margin-inline-end: 2px;
}
.header-search :deep(.ant-input-affix-wrapper) {
  background: var(--ui-canvas);
  border-color: var(--ui-border);
}

.header-user {
  gap: 7px;
  margin-inline-start: 3px;
  padding: 3px 7px;
}

.header-avatar {
  background: var(--ui-accent-soft);
  color: var(--ui-accent);
  font-weight: 700;
}

.header-username {
  max-width: 112px;
  overflow: hidden;
  color: var(--ui-text);
  font-weight: 600;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.notification-menu {
  width: min(320px, calc(100vw - 48px));
  max-height: 420px;
  overflow-y: auto;
}

.notification-head,
.notification-item {
  padding: 10px 12px;
  border-bottom: 1px solid var(--ui-border);
}
.notification-head,
.notification-title {
  font-weight: 650;
}
.notification-content,
.notification-time,
.notification-empty {
  color: var(--ui-muted);
  font-size: 12px;
}
.notification-content {
  display: -webkit-box;
  margin-top: 3px;
  overflow: hidden;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow-wrap: anywhere;
}
.notification-time {
  margin-top: 4px;
}
.notification-empty {
  padding: 24px 12px;
  text-align: center;
}

.admin-tabs-bar {
  gap: 6px;
  padding: 6px 24px;
}
.admin-tabs {
  flex: 1;
  min-width: 0;

  :deep(.ant-tabs-nav) {
    margin: 0;
  }
  :deep(.ant-tabs-nav::before) {
    border-bottom: 0;
  }
  :deep(.ant-tabs-tab) {
    margin-inline: 0 5px;
    border: 1px solid transparent;
    border-radius: 6px;
    padding: 4px 10px;
    background: var(--ui-canvas);
    color: var(--ui-muted);
  }
  :deep(.ant-tabs-tab-active) {
    background: var(--ui-accent-soft);
    font-weight: 600;
  }
  :deep(.ant-tabs-tab-remove) {
    color: var(--ui-muted);
  }
}

.tabs-action-btn {
  display: inline-flex;
  align-items: center;
  color: var(--ui-muted);
}

.admin-content {
  min-height: 0;
  overflow: auto;
}
.admin-page {
  display: flex;
  flex-direction: column;
  gap: 20px;
  max-width: 1760px;
  min-height: 100%;
  margin-inline: auto;
  padding: 22px 24px 0;
}

.page-heading {
  display: flex;
  align-items: center;
  gap: 12px;

  h1 {
    margin: 0;
    color: var(--ui-text);
    font-size: 25px;
    font-weight: 720;
    line-height: 1.35;
  }
}

.page-heading-icon {
  display: grid;
  width: 42px;
  height: 42px;
  flex: none;
  place-items: center;
  border-radius: 11px;
  background: var(--ui-accent-soft);
  color: var(--ui-accent);
}

.admin-footer {
  margin-top: auto;
  padding: 4px 0 16px;
  color: var(--ui-muted);
  font-size: 12px;
  text-align: center;
}

.password-form {
  padding-top: 12px;
}

@media (max-width: 1000px) {
  .header-search {
    width: 150px;
  }
}

@media (max-width: 760px) {
  .admin-header,
  .admin-tabs-bar {
    padding-inline: 12px;
  }
  .admin-header {
    gap: 4px;
  }
  .header-right {
    gap: 2px;
  }
  .header-search {
    width: min(28vw, 170px);
  }
  .admin-page {
    gap: 14px;
    padding: 14px 12px 0;
  }
  .page-heading h1 {
    font-size: 22px;
  }
  .admin-sider {
    position: fixed;
    inset: 60px auto 0 0;
    z-index: 21;
    transform: translateX(-100%);
    transition: transform 180ms ease;

    &.is-open {
      transform: translateX(0);
    }
  }
  .sidebar-backdrop {
    position: fixed;
    inset: 60px 0 0;
    z-index: 20;
    background: rgb(8 12 24 / 52%);
  }
}

@media (max-width: 580px) {
  .header-breadcrumb {
    display: none;
  }
}

@media (max-width: 460px) {
  .header-search {
    width: min(34vw, 135px);
  }
  .header-username {
    display: none;
  }
  .header-user {
    gap: 3px;
    padding-inline: 3px;
  }
}

@media (prefers-reduced-motion: reduce) {
  .admin-sider {
    transition: none;
  }
}
</style>

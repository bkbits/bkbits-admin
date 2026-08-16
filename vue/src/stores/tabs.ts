import { defineStore } from "pinia";
import { ref, watch } from "vue";

/** 标签页 */
export interface TabItem {
  /** 路由路径（唯一标识） */
  path: string;
  /** 显示标题 */
  title: string;
  /** 是否允许关闭 */
  closable: boolean;
}

/** 固定首页 */
export const HOME_PATH = "/dashboard";
const HOME_TITLE = "仪表盘";

const STORAGE_KEY = "bkbits-admin-tabs";

/** 读取持久化的标签页 */
function loadTabs(): TabItem[] {
  try {
    const raw = sessionStorage.getItem(STORAGE_KEY);
    if (!raw) return [];
    const list = JSON.parse(raw) as TabItem[];
    if (!Array.isArray(list)) return [];
    return list.filter(
      (t) => typeof t.path === "string" && typeof t.title === "string" && !!t.path,
    );
  } catch {
    return [];
  }
}

/** 顶部标签页栏状态（会话内持久化） */
export const useTabsStore = defineStore("tabs", () => {
  const tabs = ref<TabItem[]>([
    ...loadTabs(),
    { path: HOME_PATH, title: HOME_TITLE, closable: false },
  ]);

  // 持久化（首页不落盘，恢复时自动补上）
  watch(
    tabs,
    (list) => {
      sessionStorage.setItem(STORAGE_KEY, JSON.stringify(list.filter((t) => t.path !== HOME_PATH)));
    },
    { deep: true },
  );

  /** 打开标签页（已存在则忽略） */
  function openTab(path: string, title: string) {
    if (!path) return;
    if (!tabs.value.some((t) => t.path === path)) {
      tabs.value.push({ path, title, closable: path !== HOME_PATH });
    }
  }

  /** 关闭标签页，返回关闭后应激活的路径 */
  function closeTab(path: string): string | null {
    if (path === HOME_PATH) return null;
    const index = tabs.value.findIndex((t) => t.path === path);
    if (index === -1) return null;
    const next = tabs.value[index + 1] ?? tabs.value[index - 1];
    tabs.value = tabs.value.filter((t) => t.path !== path);
    return next?.path ?? HOME_PATH;
  }

  /** 关闭其他标签页（保留当前与首页），返回应激活的路径 */
  function closeOthers(path: string): string {
    tabs.value = tabs.value.filter((t) => t.path === path || t.path === HOME_PATH);
    return path;
  }

  /** 关闭全部（保留首页） */
  function closeAll(): string {
    tabs.value = tabs.value.filter((t) => t.path === HOME_PATH);
    return HOME_PATH;
  }

  return { tabs, openTab, closeTab, closeOthers, closeAll };
});

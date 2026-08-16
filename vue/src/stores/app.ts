import { defineStore } from "pinia";
import { computed, ref } from "vue";

export type ThemeMode = "light" | "dark";

const THEME_KEY = "bkbits-admin-theme";

/** 应用级状态：主题、侧边栏折叠 */
export const useAppStore = defineStore("app", () => {
  const theme = ref<ThemeMode>((localStorage.getItem(THEME_KEY) as ThemeMode) || "light");
  const collapsed = ref(false);

  const isDark = computed(() => theme.value === "dark");

  function toggleTheme() {
    theme.value = theme.value === "light" ? "dark" : "light";
    localStorage.setItem(THEME_KEY, theme.value);
  }

  function toggleCollapsed() {
    collapsed.value = !collapsed.value;
  }

  return { theme, collapsed, isDark, toggleTheme, toggleCollapsed };
});

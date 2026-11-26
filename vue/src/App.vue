<script setup lang="ts">
import { computed, watchEffect } from "vue";
import { ConfigProvider, theme as antdTheme } from "antdv-next";
import zhCN from "antdv-next/locale/zh_CN";
import { useAppStore } from "@/stores/app";

const appStore = useAppStore();

const themeConfig = computed(() => ({
  algorithm: appStore.theme === "dark" ? antdTheme.darkAlgorithm : antdTheme.defaultAlgorithm,
  token: {
    colorPrimary: appStore.isDark ? "#a6a0ff" : "#4b45d4",
    colorTextLightSolid: appStore.isDark ? "#181735" : "#fff",
    colorBgLayout: appStore.isDark ? "#141722" : "#f5f6fa",
    colorBgContainer: appStore.isDark ? "#1e2230" : "#fff",
    colorText: appStore.isDark ? "#edf0fc" : "#182033",
    colorTextSecondary: appStore.isDark ? "#aeb7cd" : "#626b80",
    colorBorderSecondary: appStore.isDark ? "#333a4e" : "#e7e9f1",
    borderRadius: 8,
    controlHeight: 36,
  },
  components: {
    Table: {
      headerBg: appStore.isDark ? "#24293a" : "#f7f8fc",
      rowHoverBg: appStore.isDark ? "#272c3e" : "#f6f6ff",
      cellPaddingBlock: 12,
      cellPaddingInline: 14,
    },
  },
}));

watchEffect(() => {
  document.documentElement.dataset.theme = appStore.theme;
  ConfigProvider.config({ theme: themeConfig.value, locale: zhCN });
});
</script>

<template>
  <a-config-provider
    :theme="themeConfig"
    :locale="zhCN"
    :button="{ autoInsertSpace: false }"
    :table="{ scroll: { x: 'max-content' } }"
  >
    <router-view />
  </a-config-provider>
</template>

<script setup lang="ts">
import { computed } from "vue";
import { theme as antdTheme } from "antdv-next";
import { useAppStore } from "./stores/app";

const appStore = useAppStore();

const themeConfig = computed(() => ({
  algorithm: appStore.theme === "dark" ? antdTheme.darkAlgorithm : antdTheme.defaultAlgorithm,
  token: {
    colorPrimary: "#1677ff",
    borderRadius: 6,
  },
}));
</script>

<template>
  <a-config-provider :theme="themeConfig">
    <router-view />
  </a-config-provider>
</template>

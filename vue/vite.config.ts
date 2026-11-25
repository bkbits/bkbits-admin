import { defineConfig } from "vite-plus";
import vue from "@vitejs/plugin-vue";
import { mockServer } from "./src/mock";

/** 仅当 VITE_USE_MOCK=true 时启用 mock，默认由 /api 代理到 8088 真实后端 */
const useMock = process.env.VITE_USE_MOCK === "true";

export default defineConfig({
  plugins: [vue(), ...(useMock ? [mockServer()] : [])],
  resolve: {
    alias: {
      "@": new URL("./src", import.meta.url).pathname,
    },
  },
  server: {
    port: 5173,
    proxy: {
      "/api": {
        target: "http://localhost:8088",
        changeOrigin: true,
      },
    },
  },
  fmt: {},
  lint: {
    jsPlugins: [{ name: "vite-plus", specifier: "vite-plus/oxlint-plugin" }],
    rules: { "vite-plus/prefer-vite-plus-imports": "error" },
    options: { typeAware: true, typeCheck: true },
  },
});

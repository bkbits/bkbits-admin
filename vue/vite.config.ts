import { defineConfig } from "vite-plus";
import vue from "@vitejs/plugin-vue";
import { mockServer } from "./src/mock";

export default defineConfig({
  plugins: [vue(), mockServer()],
  resolve: {
    alias: {
      "@": new URL("./src", import.meta.url).pathname,
    },
  },
  server: {
    port: 5173,
    proxy: {
      // 后端启动后取消 mockServer 插件即可切换为真实接口
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

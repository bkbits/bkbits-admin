/// <reference types="vite/client" />

/**
 * 自定义环境变量（对应 vue/.env 中的 VITE_ 前缀变量）。
 * 内置变量 MODE/DEV/PROD/BASE_URL/SSR 由 vite/client 提供，此处仅扩展业务变量。
 */
interface ImportMetaEnv {
  /** 接口请求前缀，例如 /api */
  readonly VITE_API: string;
  /** 应用标题名 */
  readonly VITE_TITLE: string;
}

declare module "*.vue" {
  import type { DefineComponent } from "vue";
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  const component: DefineComponent<Record<string, unknown>, Record<string, unknown>, any>;
  export default component;
}

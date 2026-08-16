/**
 * 字典映射工具：与 mock 种子数据（src/mock/store.ts）保持一致，
 * 页面开发阶段直接使用，后端联调后可从 /api/dict/value/list 动态加载。
 */

export interface DictOption {
  value: string;
  label: string;
  color?: string;
}

/** 通用状态 */
export const STATUS_OPTIONS: DictOption[] = [
  { value: "E", label: "启用", color: "success" },
  { value: "D", label: "禁用", color: "error" },
];

/** 性别 */
export const SEX_OPTIONS: DictOption[] = [
  { value: "M", label: "男", color: "blue" },
  { value: "F", label: "女", color: "magenta" },
];

/** 租户类型 */
export const TENANT_TYPE_OPTIONS: DictOption[] = [
  { value: "SYSTEM", label: "系统租户", color: "warning" },
  { value: "NORMAL", label: "普通租户" },
];

/** 数据域（数据权限范围） */
export const DATA_SCOPE_OPTIONS: DictOption[] = [
  { value: "ALL", label: "全部数据" },
  { value: "DEPT", label: "本部门及以下" },
  { value: "DEPT_ONLY", label: "仅本部门" },
  { value: "SELF", label: "仅本人" },
];

/** 权限类型 */
export const PERMISSION_TYPE_OPTIONS: DictOption[] = [
  { value: "CATALOG", label: "目录", color: "purple" },
  { value: "MENU", label: "菜单", color: "blue" },
  { value: "BUTTON", label: "按钮", color: "cyan" },
];

/** 字典/参数类型 */
export const SYSTEM_BUSINESS_OPTIONS: DictOption[] = [
  { value: "SYSTEM", label: "系统内置", color: "gold" },
  { value: "BUSINESS", label: "业务自定义" },
];

/** 按 value 取 label */
export function dictLabel(options: DictOption[], value?: string): string {
  return options.find((o) => o.value === value)?.label ?? value ?? "-";
}

/** 按 value 取颜色（用于 Tag） */
export function dictColor(options: DictOption[], value?: string): string | undefined {
  return options.find((o) => o.value === value)?.color;
}

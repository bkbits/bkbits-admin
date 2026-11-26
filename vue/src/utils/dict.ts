/**
 * 字典映射工具：与 mock 种子数据（src/mock/store.ts）保持一致，
 * 页面开发阶段直接使用，后端联调后可从 /dict/value/list 动态加载。
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
  { value: "U", label: "未知", color: "default" },
];

/** 权限类型 */
export const PERMISSION_TYPE_OPTIONS: DictOption[] = [
  { value: "D", label: "目录", color: "purple" },
  { value: "M", label: "菜单", color: "blue" },
  { value: "B", label: "按钮", color: "cyan" },
];

/** 字典/参数类型 */
export const SYSTEM_BUSINESS_OPTIONS: DictOption[] = [
  { value: "S", label: "系统", color: "gold" },
  { value: "U", label: "用户" },
];

/** 按 value 取 label */
export function dictLabel(options: DictOption[], value?: string): string {
  return options.find((o) => o.value === value)?.label ?? value ?? "-";
}

/** 按 value 取颜色（用于 Tag） */
export function dictColor(options: DictOption[], value?: string): string | undefined {
  return options.find((o) => o.value === value)?.color;
}

/**
 * mock 请求上下文与通用工具（仅运行于 Vite dev/preview 服务的 Node 环境）。
 */
import type { IncomingMessage, ServerResponse } from "node:http";

/** 请求上下文 */
export interface MockContext {
  req: IncomingMessage;
  res: ServerResponse;
  /** URL query 参数（原始字符串，与后端 Swagger 定义一致） */
  query: Record<string, string>;
  /** 解析后的请求体 */
  body: Record<string, unknown>;
  /** multipart 中提取的文件信息 */
  files: { field: string; filename: string }[];
  /** 当前登录 token（Authorization 头） */
  token?: string;
}

/** 成功响应 */
export function ok<T>(data: T, message = "操作成功"): Record<string, unknown> {
  return { ok: true, code: 200, message, data };
}

/** 失败响应 */
export function fail(message: string, code = 500): Record<string, unknown> {
  return { ok: false, code, message, data: null };
}

/** 读取请求体（返回原始字节，空请求返回空数组） */
export function readBody(req: IncomingMessage): Promise<Uint8Array> {
  return new Promise((resolve) => {
    const chunks: Uint8Array[] = [];
    req.on("data", (chunk: Uint8Array) => chunks.push(chunk));
    req.on("end", () => {
      const total = chunks.reduce((sum, c) => sum + c.length, 0);
      const merged = new Uint8Array(total);
      let offset = 0;
      for (const chunk of chunks) {
        merged.set(chunk, offset);
        offset += chunk.length;
      }
      resolve(merged);
    });
    req.on("error", () => resolve(new Uint8Array(0)));
  });
}

/** 从 multipart 字节流中提取 filename（仅做轻量提取，mock 不落盘） */
export function extractFilenames(body: Uint8Array): string[] {
  const text = new TextDecoder("latin1").decode(body);
  const names: string[] = [];
  const re = /filename="([^"]*)"/g;
  let m = re.exec(text);
  while (m) {
    names.push(m[1]);
    m = re.exec(text);
  }
  return names;
}

/** 当前时间字符串（yyyy-MM-dd HH:mm:ss） */
export function now(): string {
  const d = new Date();
  const p = (n: number) => String(n).padStart(2, "0");
  return `${d.getFullYear()}-${p(d.getMonth() + 1)}-${p(d.getDate())} ${p(d.getHours())}:${p(d.getMinutes())}:${p(d.getSeconds())}`;
}

/** 简单分页工具（page/pageSize 兼容字符串与数字） */
export function paginate<T>(rows: T[], page?: string | number, pageSize?: string | number) {
  const p = Math.max(1, Number(page) || 1);
  const size = Math.max(1, Number(pageSize) || 10);
  const start = (p - 1) * size;
  return { total: rows.length, rows: rows.slice(start, start + size) };
}

/** 安全字符串化（对象转 JSON，避免 [object Object]） */
function stringify(value: unknown): string {
  if (typeof value === "string" || typeof value === "number" || typeof value === "boolean") {
    return String(value);
  }
  return JSON.stringify(value);
}

/** 字符串过滤：字段缺失或空则不过滤；字符串包含匹配，其余精确匹配 */
export function filterRows<T>(
  rows: T[],
  query: Record<string, string>,
  likeFields: string[] = [
    "name",
    "userName",
    "realName",
    "code",
    "dictKey",
    "paramKey",
    "permission",
    "title",
  ],
): T[] {
  return rows.filter((item) => {
    const row = item as Record<string, unknown>;
    return Object.entries(query).every(([key, value]) => {
      if (value === undefined || value === null || value === "") return true;
      const rowValue = row[key];
      if (rowValue === undefined || rowValue === null) return true;
      if (likeFields.includes(key)) {
        return stringify(rowValue).toLowerCase().includes(String(value).toLowerCase());
      }
      return stringify(rowValue) === String(value);
    });
  });
}

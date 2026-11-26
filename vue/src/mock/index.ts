/**
 * bkbits-admin mock 服务器插件。
 * 在 Vite dev / preview 服务器中拦截 /api/* 请求，
 * 以内存数据实现后端 Swagger 全部接口（后端暂无数据，前端联调使用）。
 *
 * 注意：本目录被 vite.config.ts 在加载配置阶段静态引入，此时 resolve.alias 尚未生效，
 * 因此目录内的 import 必须使用相对路径，不要改成 @ 别名。
 */
import type { IncomingMessage, ServerResponse } from "node:http";
import type { Connect } from "vite-plus";
import type { Plugin } from "vite-plus";
import {
  extractFilenames,
  fail,
  readBody,
  readCookie,
  TOKEN_NAME,
  type MockContext,
} from "./helpers";
import { routes } from "./router";

/** 无需登录即可访问的接口 */
const PUBLIC_PATHS = new Set(["/login", "/register", "/publicKey"]);

async function handleMockRequest(req: IncomingMessage, res: ServerResponse): Promise<boolean> {
  const url = new URL(req.url ?? "/", "http://localhost");
  // 前端请求统一带 /api 前缀（与后端一致），mock 路由表不含该前缀
  if (!url.pathname.startsWith("/api/")) return false;
  const pathname = url.pathname.slice(4);

  const method = (req.method ?? "GET").toUpperCase();
  const route = routes.find((r) => r.method === method && r.path === pathname);
  if (!route) {
    // 已匹配 /api 前缀但无对应路由：返回 404 包装
    res.setHeader("Content-Type", "application/json; charset=utf-8");
    res.statusCode = 200;
    res.end(JSON.stringify(fail(`mock 未实现接口：${method} ${pathname}`, 404)));
    return true;
  }

  // 鉴权（白名单除外）：token 由后端写入 bkbits-token cookie，前端不显式管理
  const token = readCookie(req, TOKEN_NAME);
  if (!PUBLIC_PATHS.has(pathname) && !token) {
    res.setHeader("Content-Type", "application/json; charset=utf-8");
    res.statusCode = 200;
    res.end(JSON.stringify(fail("未登录或登录已过期", 401)));
    return true;
  }

  const query: Record<string, string> = {};
  url.searchParams.forEach((value, key) => {
    query[key] = value;
  });

  const raw = await readBody(req);
  const contentType = String(req.headers["content-type"] ?? "");
  let body: Record<string, unknown> = {};
  const files: { field: string; filename: string }[] = [];
  if (raw.length > 0) {
    if (contentType.includes("multipart/form-data")) {
      for (const filename of extractFilenames(raw)) {
        files.push({ field: "file", filename });
      }
    } else if (contentType.includes("application/x-www-form-urlencoded")) {
      const search = new URLSearchParams(new TextDecoder().decode(raw));
      search.forEach((value, key) => {
        body[key] = value;
      });
    } else {
      try {
        body = JSON.parse(new TextDecoder().decode(raw)) as Record<string, unknown>;
      } catch {
        body = {};
      }
    }
  }

  const ctx: MockContext = { req, res, query, body, files, token };
  let result: Record<string, unknown>;
  try {
    result = (await route.handler(ctx)) as Record<string, unknown>;
  } catch (err) {
    result = fail(err instanceof Error ? err.message : "mock 服务内部错误", 500);
  }

  res.setHeader("Content-Type", "application/json; charset=utf-8");
  res.statusCode = 200;
  res.end(JSON.stringify(result));
  return true;
}

/** mock 服务器插件：dev 与 preview 环境生效 */
export function mockServer(): Plugin {
  const middleware: Connect.NextHandleFunction = (req, res, next) => {
    handleMockRequest(req as IncomingMessage, res as ServerResponse)
      .then((handled) => {
        if (!handled) next();
      })
      .catch(() => {
        next();
      });
  };
  return {
    name: "bkbits-admin-mock-server",
    configureServer(server) {
      server.middlewares.use(middleware);
    },
    configurePreviewServer(server) {
      server.middlewares.use(middleware);
    },
  };
}

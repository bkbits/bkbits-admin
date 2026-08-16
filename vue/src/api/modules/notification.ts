import { defineGet } from "../define";
import type { Notification, PageData, PageParams } from "../types";

/**
 * 通知公告（mock 扩展接口：后端 Swagger 仅有 Notification 实体，
 * 未提供通知接口，依据 AGENTS.md 顶栏「通知下拉」补充，mock 层实现）。
 */

/** 分页查询通知 */
export const listNotification = defineGet<PageParams, PageData<Notification>>(
  "/api/notification/list",
);

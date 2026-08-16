import { defineMultipart, definePost } from "../define";
import type {
  UploadFile,
  UploadTaskCreateDTO,
  UploadTaskCreateVO,
  UploadTaskFinishDTO,
} from "../types";

/** 小文件上传（multipart/form-data） */
export const uploadFile = defineMultipart<{ hash: string }, UploadFile>("/api/file/upload");

/** 创建大文件上传任务 */
export const createUploadTask = definePost<UploadTaskCreateDTO, UploadTaskCreateVO>(
  "/api/file/task/create",
);

/** 上传文件分片（multipart/form-data，query: taskId/fileIndex） */
export const uploadTaskPiece = defineMultipart<{ taskId: string; fileIndex: number }, unknown>(
  "/api/file/task/upload",
);

/** 完成大文件上传任务 */
export const finishUploadTask = definePost<UploadTaskFinishDTO, UploadFile>(
  "/api/file/task/finish",
);

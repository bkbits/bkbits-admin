import { defineMultipart, definePost } from "@/api/define";
import type {
  UploadFile,
  UploadTaskCreateDTO,
  UploadTaskCreateVO,
  UploadTaskFinishDTO,
} from "@/api/types";

/** 小文件上传（multipart/form-data） */
export const uploadFile = defineMultipart<{ hash: string }, UploadFile>("/file/upload");

/** 创建大文件上传任务 */
export const createUploadTask = definePost<UploadTaskCreateDTO, UploadTaskCreateVO>(
  "/file/task/create",
);

/** 上传文件分片（multipart/form-data，query: taskId/fileIndex） */
export const uploadTaskPiece = defineMultipart<{ taskId: string; fileIndex: number }, unknown>(
  "/file/task/upload",
);

/** 完成大文件上传任务 */
export const finishUploadTask = definePost<UploadTaskFinishDTO, UploadFile>("/file/task/finish");

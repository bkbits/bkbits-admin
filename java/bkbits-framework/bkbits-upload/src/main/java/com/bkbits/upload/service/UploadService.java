package com.bkbits.upload.service;

import com.bkbits.upload.UploadFile;
import com.bkbits.upload.pojo.UploadTaskCreateDTO;
import com.bkbits.upload.pojo.UploadTaskCreateVO;
import org.noear.solon.core.handle.UploadedFile;

/**
 * 文件上传服务接口。
 */
public interface UploadService {

    /**
     * 小文件上传。
     *
     * @param file 上传文件
     * @param hash 文件哈希（SHA-256 hex）
     * @return 落库后的文件记录
     */
    UploadFile upload(UploadedFile file, String hash);

    /**
     * 创建大文件上传任务（任务与分片信息落库）。
     */
    UploadTaskCreateVO createTask(UploadTaskCreateDTO req);

    /**
     * 上传文件分片。
     */
    void uploadPiece(String taskId, long fileIndex, UploadedFile file);

    /**
     * 完成大文件上传任务（合并分片并落库）。
     *
     * @return 落库后的文件记录
     */
    UploadFile finishTask(String taskId);
}

package com.bkbits.upload.controller;

import com.bkbits.core.Result;
import com.bkbits.upload.UploadFile;
import com.bkbits.upload.pojo.UploadTaskCreateDTO;
import com.bkbits.upload.pojo.UploadTaskFinishDTO;
import com.bkbits.upload.exception.UploadException;
import com.bkbits.upload.service.UploadService;
import com.bkbits.upload.pojo.UploadTaskCreateVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.noear.solon.annotation.Body;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Param;
import org.noear.solon.annotation.Post;
import org.noear.solon.core.handle.UploadedFile;

/**
 * 文件上传接口。
 */
@Api("文件上传接口")
@Controller
@Mapping("/api/file")
public class FileController {

    @Inject
    private UploadService uploadService;

    /**
     * 小文件上传（multipart：file 文件、hash 文件哈希）。
     */
    @ApiOperation("小文件上传")
    @Post
    @Mapping("/upload")
    public Result<UploadFile> upload(@ApiParam("文件") @Param("file") UploadedFile file,
                                     @ApiParam("文件哈希（SHA-256 hex）") @Param("hash") String hash) {
        try {
            return Result.ok(uploadService.upload(file, hash));
        } catch (UploadException e) {
            return Result.fail(400, e.getMessage());
        }
    }

    /**
     * 创建大文件上传任务。
     */
    @ApiOperation("创建大文件上传任务")
    @Post
    @Mapping("/task/create")
    public Result<UploadTaskCreateVO> createTask(@Body UploadTaskCreateDTO req) {
        try {
            return Result.ok(uploadService.createTask(req));
        } catch (UploadException e) {
            return Result.fail(400, e.getMessage());
        }
    }

    /**
     * 上传文件分片（multipart：taskId 任务 id、fileIndex 分片序号、file 分片文件）。
     */
    @ApiOperation("上传文件分片")
    @Post
    @Mapping("/task/upload")
    public Result<Void> uploadPiece(@ApiParam("任务 id") @Param("taskId") String taskId,
                                    @ApiParam("分片序号（从 0 开始）") @Param("fileIndex") long fileIndex,
                                    @ApiParam("分片文件") @Param("file") UploadedFile file) {
        try {
            uploadService.uploadPiece(taskId, fileIndex, file);
            return Result.ok();
        } catch (UploadException e) {
            return Result.fail(400, e.getMessage());
        }
    }

    /**
     * 完成大文件上传任务（合并分片并落库）。
     */
    @ApiOperation("完成大文件上传任务")
    @Post
    @Mapping("/task/finish")
    public Result<UploadFile> finish(@Body UploadTaskFinishDTO req) {
        try {
            return Result.ok(uploadService.finishTask(req.getTaskId()));
        } catch (UploadException e) {
            return Result.fail(400, e.getMessage());
        }
    }
}

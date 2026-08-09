package com.bkbits.upload.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 创建大文件上传任务结果。
 */
@ApiModel("创建大文件上传任务结果")
@Data
@AllArgsConstructor
public class UploadTaskCreateVO {

    @ApiModelProperty("上传任务 id")
    private String taskId;

    @ApiModelProperty("分片数量")
    private long pieceCount;

    @ApiModelProperty("分片大小（字节）")
    private long pieceSize;
}

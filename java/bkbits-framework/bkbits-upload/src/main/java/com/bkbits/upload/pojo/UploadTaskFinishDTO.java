package com.bkbits.upload.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 完成大文件上传任务请求。
 */
@ApiModel("完成大文件上传任务请求")
@Data
public class UploadTaskFinishDTO {

    @ApiModelProperty(value = "上传任务 id", required = true)
    private String taskId;
}

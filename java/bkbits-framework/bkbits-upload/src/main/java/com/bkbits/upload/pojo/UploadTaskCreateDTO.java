package com.bkbits.upload.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 创建大文件上传任务请求。
 */
@ApiModel("创建大文件上传任务请求")
@Data
public class UploadTaskCreateDTO {

    @ApiModelProperty(value = "文件哈希（SHA-256 hex）", required = true)
    private String fileHash;

    @ApiModelProperty(value = "文件大小（字节）", required = true)
    private Long fileSize;

    @ApiModelProperty("文件类型")
    private String contentType;

    @ApiModelProperty(value = "文件名称", required = true)
    private String fileName;

    @ApiModelProperty(value = "每个分片的文件大小（字节），最后一片可能不足", required = true)
    private Long pieceSize;

    @ApiModelProperty(value = "每个分片的文件哈希（SHA-256 hex），数量需与分片数一致", required = true)
    private List<String> pieceHashes;
}

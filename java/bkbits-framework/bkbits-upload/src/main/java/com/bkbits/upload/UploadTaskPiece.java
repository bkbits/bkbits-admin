package com.bkbits.upload;

import com.bkbits.orm.ICreateBy;
import com.bkbits.orm.IGenId;
import com.bkbits.upload.proxy.UploadTaskPieceProxy;
import com.easy.query.core.annotation.Column;
import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import lombok.Data;

import java.time.LocalDateTime;

@Table
@EntityProxy
@Data
public class UploadTaskPiece implements ProxyEntityAvailable<UploadTaskPiece, UploadTaskPieceProxy>, IGenId, ICreateBy {
    // 任务id
    @Column(primaryKey = true)
    private String id;

    // 上传任务id
    private String uploadId;

    // 文件序号, 从0开始
    private Long fileIndex;

    // 任务状态，依赖字典 uploadStatus, S=成功完成,W=等待上传
    private String uploadStatus;

    // 保存路径
    private String path;

    // 文件大小
    private Long fileSize;

    // 文件哈希
    private String hash;

    // 创建者
    private String createBy;

    // 创建时间
    private LocalDateTime createTime;
}

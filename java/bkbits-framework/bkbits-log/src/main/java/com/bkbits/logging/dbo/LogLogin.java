package com.bkbits.logging.dbo;

import com.bkbits.logging.dbo.proxy.LogLoginProxy;
import com.bkbits.orm.ICreateBy;
import com.bkbits.orm.IGenId;
import com.easy.query.core.annotation.Column;
import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;

@ApiModel("登录日志记录")
@Data
@Table
@EntityProxy
@FieldNameConstants
public class LogLogin implements IGenId, ICreateBy, ProxyEntityAvailable<LogLogin, LogLoginProxy> {
    @ApiModelProperty("日志编号")
    @Column(primaryKey = true)
    private String id;

    @ApiModelProperty("登录是否成功")
    private Boolean succeed;

    @ApiModelProperty("失败原因")
    private String failReason;

    @ApiModelProperty("登录ip")
    private String ip;

    @ApiModelProperty("登录设备")
    private String device;

    @ApiModelProperty("UserAgent")
    private String userAgent;

    @ApiModelProperty("花费时间")
    private Integer costTime;

    @ApiModelProperty("登陆者")
    private String createBy;

    @ApiModelProperty("登录时间")
    private LocalDateTime createTime;
}

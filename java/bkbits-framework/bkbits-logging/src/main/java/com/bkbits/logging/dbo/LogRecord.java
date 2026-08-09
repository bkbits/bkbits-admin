package com.bkbits.logging.dbo;

import com.bkbits.logging.dbo.proxy.LogRecordProxy;
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

@ApiModel("日志记录")
@Data
@Table("log")
@EntityProxy
@FieldNameConstants
public class LogRecord implements IGenId, ICreateBy, ProxyEntityAvailable<LogRecord, LogRecordProxy> {
    @ApiModelProperty("日志编号")
    @Column(primaryKey = true)
    private String id;

    @ApiModelProperty("日志名称")
    private String name;

    @ApiModelProperty("日志类型")
    private String type;

    @ApiModelProperty("模块")
    private String module;

    @ApiModelProperty("函数路径")
    private String method;

    @ApiModelProperty("请求参数")
    private String args;

    @ApiModelProperty("返回结果")
    private String result;

    @ApiModelProperty("操作IP")
    private String ip;

    @ApiModelProperty("UserAgent记录")
    private String userAgent;

    @ApiModelProperty("访问的url")
    private String url;

    @ApiModelProperty("花费时间")
    private Integer costTime;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建者")
    private String createBy;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;
}

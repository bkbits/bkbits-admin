package com.bkbits.logging.pojo;

import com.easy.query.core.annotation.EasyWhereCondition;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志查询参数。
 * {@link com.bkbits.logging.dbo.LogOperation }
 *
 * @easy-query-dto schema: request
 */
@ApiModel("操作日志查询参数")
@Data
public class LogOperationQueryDTO {

    @ApiModelProperty("日志名称")
    @EasyWhereCondition
    private String name;

    @ApiModelProperty("日志类型")
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String type;

    @ApiModelProperty("模块")
    @EasyWhereCondition
    private String module;

    @ApiModelProperty("函数路径")
    @EasyWhereCondition
    private String method;

    @ApiModelProperty("操作IP")
    @EasyWhereCondition
    private String ip;

    @ApiModelProperty("访问的url")
    @EasyWhereCondition
    private String url;

    @ApiModelProperty("创建者")
    @EasyWhereCondition
    private String createBy;

    @ApiModelProperty("创建时间起（含）")
    @EasyWhereCondition(type = EasyWhereCondition.Condition.RANGE_LEFT_CLOSED, propName = "createTime")
    private LocalDateTime createTimeBegin;

    @ApiModelProperty("创建时间止（含）")
    @EasyWhereCondition(type = EasyWhereCondition.Condition.RANGE_RIGHT_CLOSED, propName = "createTime")
    private LocalDateTime createTimeEnd;
}

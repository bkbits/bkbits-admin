package com.bkbits.logging.pojo;

import com.easy.query.core.annotation.EasyWhereCondition;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录日志查询参数。
 * {@link com.bkbits.logging.dbo.LogLogin }
 *
 * @easy-query-dto schema: request
 */
@ApiModel("登录日志查询参数")
@Data
public class LogLoginQueryDTO {

    @ApiModelProperty("登录是否成功")
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private Boolean succeed;

    @ApiModelProperty("登录者")
    @EasyWhereCondition
    private String createBy;

    @ApiModelProperty("登录ip")
    @EasyWhereCondition
    private String ip;

    @ApiModelProperty("登录设备")
    @EasyWhereCondition
    private String device;

    @ApiModelProperty("登录时间起（含）")
    @EasyWhereCondition(type = EasyWhereCondition.Condition.RANGE_LEFT_CLOSED, propName = "createTime")
    private LocalDateTime createTimeBegin;

    @ApiModelProperty("登录时间止（含）")
    @EasyWhereCondition(type = EasyWhereCondition.Condition.RANGE_RIGHT_CLOSED, propName = "createTime")
    private LocalDateTime createTimeEnd;
}

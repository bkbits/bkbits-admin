package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 租户输出参数。
 */
@Data
@ApiModel("租户输出参数")
public class TenantVO {

    @ApiModelProperty("租户编号")
    private String id;

    @ApiModelProperty("租户类型（S=系统租户,U=用户租户,T=租户模板）")
    private String type;

    @ApiModelProperty("租户名称")
    private String name;

    @ApiModelProperty("状态（E=启用,D=禁用）")
    private String status;

    @ApiModelProperty("创建人")
    private String createBy;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新人")
    private String updateBy;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
}

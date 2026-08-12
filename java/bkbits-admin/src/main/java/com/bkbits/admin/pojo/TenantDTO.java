package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 租户输入参数。
 */
@Data
@ApiModel("租户输入参数")
public class TenantDTO {

    @ApiModelProperty("租户编号；更新时必填")
    private String id;

    @ApiModelProperty("租户类型（S=系统租户,U=用户租户,T=租户模板）")
    private String type;

    @ApiModelProperty("租户名称")
    private String name;

    @ApiModelProperty("状态（E=启用,D=禁用）")
    private String status;
}

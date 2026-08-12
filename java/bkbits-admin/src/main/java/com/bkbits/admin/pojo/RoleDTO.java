package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 角色输入参数。
 */
@Data
@ApiModel("角色输入参数")
public class RoleDTO {

    @ApiModelProperty("角色编号；更新时必填")
    private String id;

    @ApiModelProperty("所属租户id")
    private String tenantId;

    @ApiModelProperty("角色代码")
    private String code;

    @ApiModelProperty("角色名")
    private String name;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("状态（E=启用,D=禁用）")
    private String status;
}

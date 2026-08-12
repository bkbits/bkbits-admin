package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 部门输入参数。
 */
@Data
@ApiModel("部门输入参数")
public class DeptDTO {

    @ApiModelProperty("部门编号；更新时必填")
    private String deptId;

    @ApiModelProperty("父级部门编号；为空表示顶级部门")
    private String parentId;

    @ApiModelProperty("所属租户id")
    private String tenantId;

    @ApiModelProperty("部门名称")
    private String name;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("状态（E=启用,D=禁用）")
    private String status;
}

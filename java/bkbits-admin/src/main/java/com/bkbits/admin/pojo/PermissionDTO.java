package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 权限输入参数。
 */
@Data
@ApiModel("权限输入参数")
public class PermissionDTO {

    @ApiModelProperty("权限编号；更新时必填")
    private String id;

    @ApiModelProperty("父级权限编号；为空表示顶级权限")
    private String parentId;

    @ApiModelProperty("权限类型（D=目录,M=菜单,B=按钮）")
    private String type;

    @ApiModelProperty("权限（用 . 作为分隔符）")
    private String permission;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("组件")
    private String component;

    @ApiModelProperty("状态（E=启用,D=禁用）")
    private String status;
}

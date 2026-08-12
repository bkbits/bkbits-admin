package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 数据权限输入参数。
 */
@Data
@ApiModel("数据权限输入参数")
public class DataPermissionDTO {

    @ApiModelProperty("数据权限编号；更新时必填")
    private String id;

    @ApiModelProperty("菜单权限编号；新增时必填，将写入数据权限的关联权限id")
    private String menuPermissionId;

    @ApiModelProperty("数据域")
    private String dataScope;

    @ApiModelProperty("状态（E=启用,D=禁用）")
    private String status;
}

package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 数据权限输出参数。
 */
@Data
@ApiModel("数据权限输出参数")
public class DataPermissionVO {

    @ApiModelProperty("数据权限编号")
    private String id;

    @ApiModelProperty("关联权限id")
    private String permissionId;

    @ApiModelProperty("数据域")
    private String dataScope;

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

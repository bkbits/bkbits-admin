package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色输出参数。
 */
@Data
@ApiModel("角色输出参数")
public class RoleVO {

    @ApiModelProperty("角色编号")
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

    @ApiModelProperty("创建人")
    private String createBy;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新人")
    private String updateBy;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("角色权限列表")
    private List<PermissionVO> permissionList;

    @ApiModelProperty("数据权限关联列表")
    private List<RoleDataPermissionRelVO> dataPermissionList;
}

package com.bkbits.admin.pojo;

import com.bkbits.dbo.entity.DataPermission;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 角色数据权限关联输出参数。
 */
@Data
@ApiModel("角色数据权限关联输出参数")
public class RoleDataPermissionRelVO {

    @ApiModelProperty("主键id")
    private String id;

    @ApiModelProperty("关联角色id")
    private String roleId;

    @ApiModelProperty("关联权限id")
    private String permissionId;

    @ApiModelProperty("关联数据权限id")
    private String dataPermissionId;

    @ApiModelProperty("关联数据权限")
    private DataPermission dataPermission;
}

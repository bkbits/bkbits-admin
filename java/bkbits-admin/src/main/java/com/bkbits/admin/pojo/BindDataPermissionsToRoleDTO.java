package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 角色绑定数据权限参数。
 */
@Data
@ApiModel("角色绑定数据权限参数")
public class BindDataPermissionsToRoleDTO {

    @ApiModelProperty("角色编号")
    private String roleId;

    @ApiModelProperty("菜单权限编号")
    private String menuPermissionId;

    @ApiModelProperty("数据权限编号集合；为空时清空绑定")
    private List<String> dataPermissionIds;
}

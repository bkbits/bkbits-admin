package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.noear.solon.validation.annotation.NotBlank;

import java.util.List;

/**
 * 角色绑定权限参数。
 * {@link com.bkbits.dbo.entity.RolePermissionRel }
 *
 * @author lkq
 * @easy-query-dto schema: request
 */
@Data
@ApiModel("角色绑定权限参数")
public class BindPermissionsToRoleDTO {

    @ApiModelProperty("角色编号")
    @NotBlank
    private String roleId;

    @SuppressWarnings("EasyQueryFieldMissMatch")
    @ApiModelProperty("权限编号集合；为空时清空绑定")
    private List<String> permissionIds;
}

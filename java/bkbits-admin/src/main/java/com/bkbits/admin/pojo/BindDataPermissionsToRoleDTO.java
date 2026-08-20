package com.bkbits.admin.pojo;

import com.bkbits.dbo.entity.RoleDataPermissionRel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.noear.solon.validation.annotation.NotBlank;

import java.util.List;

/**
 * 角色绑定数据权限参数。
 * {@link RoleDataPermissionRel }
 *
 * @author lkq
 * @easy-query-dto schema: request
 */
@Data
@ApiModel("角色绑定数据权限参数")
public class BindDataPermissionsToRoleDTO {

    @ApiModelProperty("角色编号")
    @NotBlank
    private String roleId;

    @SuppressWarnings("EasyQueryFieldMissMatch")
    @ApiModelProperty("菜单权限编号")
    @NotBlank
    private String permissionId;

    @SuppressWarnings("EasyQueryFieldMissMatch")
    @ApiModelProperty("数据权限编号集合；为空时清空绑定")
    private List<String> dataPermissionIds;
}

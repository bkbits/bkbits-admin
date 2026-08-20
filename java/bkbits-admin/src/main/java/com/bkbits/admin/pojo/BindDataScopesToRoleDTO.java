package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.noear.solon.validation.annotation.NotBlank;

import java.util.List;

/**
 * 角色绑定数据域参数。
 * {@link com.bkbits.dbo.entity.RoleDataScope }
 *
 * @easy-query-dto schema: request
 */
@Data
@ApiModel("角色绑定数据域参数")
public class BindDataScopesToRoleDTO {

    @ApiModelProperty("角色编号")
    @NotBlank
    private String roleId;

    @SuppressWarnings("EasyQueryFieldMissMatch")
    @ApiModelProperty("数据域集合；为空时清空绑定")
    private List<String> dataScopes;
}
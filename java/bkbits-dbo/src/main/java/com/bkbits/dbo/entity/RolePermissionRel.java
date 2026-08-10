package com.bkbits.dbo.entity;

import com.bkbits.dbo.entity.proxy.RolePermissionRelProxy;
import com.bkbits.orm.IGenId;
import com.easy.query.core.annotation.Column;
import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Navigate;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.enums.RelationTypeEnum;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@ApiModel("角色权限关联")
@Data
@FieldNameConstants
@Table
@EntityProxy
public class RolePermissionRel implements IGenId, ProxyEntityAvailable<RolePermissionRel, RolePermissionRelProxy> {

    @ApiModelProperty("主键id")
    @Column(primaryKey = true)
    private String id;

    @ApiModelProperty("关联角色id")
    private String roleId;

    @ApiModelProperty("关联权限id")
    private String permissionId;

    @ApiModelProperty("关联角色")
    @Navigate(
            value = RelationTypeEnum.ManyToOne,
            selfProperty = RolePermissionRel.Fields.roleId,
            targetProperty = Role.Fields.id
    )
    private Role role;

    @ApiModelProperty("关联权限")
    @Navigate(
            value = RelationTypeEnum.ManyToOne,
            selfProperty = RolePermissionRel.Fields.permissionId,
            targetProperty = Permission.Fields.id
    )
    private Permission permission;
}

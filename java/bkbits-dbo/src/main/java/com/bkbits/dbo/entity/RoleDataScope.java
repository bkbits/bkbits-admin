package com.bkbits.dbo.entity;

import com.bkbits.dbo.entity.proxy.RoleDataScopeProxy;
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

@ApiModel("角色数据权限")
@Data
@FieldNameConstants
@Table
@EntityProxy
public class RoleDataScope implements IGenId, ProxyEntityAvailable<RoleDataScope, RoleDataScopeProxy> {
    @ApiModelProperty("主键id")
    @Column(primaryKey = true)
    private String id;

    @ApiModelProperty("角色id")
    private String roleId;

    @ApiModelProperty("数据域")
    private String dataScope;

    @ApiModelProperty("角色")
    @Navigate(
            value = RelationTypeEnum.ManyToOne,
            selfProperty = RoleDataScope.Fields.roleId,
            targetProperty = Role.Fields.id
    )
    private Role role;
}

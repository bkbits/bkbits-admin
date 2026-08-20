package com.bkbits.dbo.entity;

import com.bkbits.dbo.entity.proxy.RoleDataPermissionRelProxy;
import com.bkbits.orm.IGenId;
import com.easy.query.core.annotation.Column;
import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@ApiModel("角色数据权限关联")
@Data
@FieldNameConstants
@Table
@EntityProxy
public class RoleDataPermissionRel implements IGenId, ProxyEntityAvailable<RoleDataPermissionRel, RoleDataPermissionRelProxy> {

    @Column(primaryKey = true)
    @ApiModelProperty("主键id")
    private String id;

    @ApiModelProperty("关联角色id")
    private String roleId;

    @ApiModelProperty("关联数据权限id")
    private String dataPermissionId;
}

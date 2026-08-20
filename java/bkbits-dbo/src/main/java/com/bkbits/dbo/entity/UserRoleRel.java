package com.bkbits.dbo.entity;

import com.bkbits.dbo.entity.proxy.UserRoleRelProxy;
import com.bkbits.orm.IGenId;
import com.easy.query.core.annotation.Column;
import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@ApiModel("用户角色关联")
@Data
@FieldNameConstants
@Table
@EntityProxy
public class UserRoleRel implements IGenId, ProxyEntityAvailable<UserRoleRel, UserRoleRelProxy> {

    @ApiModelProperty("主键id")
    @Column(primaryKey = true)
    private String id;

    @ApiModelProperty("关联用户id")
    private String userId;

    @ApiModelProperty("关联角色id")
    private String roleId;
}

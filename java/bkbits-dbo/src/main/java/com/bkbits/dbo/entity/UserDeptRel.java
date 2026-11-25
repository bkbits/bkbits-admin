package com.bkbits.dbo.entity;

import com.bkbits.dbo.entity.proxy.UserDeptRelProxy;
import com.bkbits.orm.IGenId;
import com.easy.query.core.annotation.Column;
import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@ApiModel("用户部门关系表")
@Data
@FieldNameConstants
@Table
@EntityProxy
public class UserDeptRel implements IGenId, ProxyEntityAvailable<UserDeptRel, UserDeptRelProxy> {
    @ApiModelProperty("主键id")
    @Column(primaryKey = true)
    private String id;

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("部门id")
    private String deptId;
}

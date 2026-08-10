package com.bkbits.dbo.entity;

import com.bkbits.dbo.entity.proxy.DataPermissionProxy;
import com.bkbits.orm.ICreateBy;
import com.bkbits.orm.IGenId;
import com.bkbits.orm.IUpdateBy;
import com.easy.query.core.annotation.Column;
import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;

@ApiModel("数据权限")
@Data
@FieldNameConstants
@Table
@EntityProxy
public class DataPermission implements IGenId, ICreateBy, IUpdateBy, ProxyEntityAvailable<DataPermission, DataPermissionProxy> {

    @Column(primaryKey = true)
    @ApiModelProperty("主键id")
    private String id;

    @ApiModelProperty("关联权限id")
    private String permissionId;

    @ApiModelProperty("数据域")
    private String dataScope;

    @ApiModelProperty(value = "状态", notes = "E=启用,D=禁用")
    private String status;

    @ApiModelProperty("创建人")
    private String createBy;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新人")
    private String updateBy;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
}

package com.bkbits.dbo.entity;

import com.bkbits.dbo.entity.proxy.DataPermissionProxy;
import com.bkbits.orm.ICreateBy;
import com.bkbits.orm.IGenId;
import com.bkbits.orm.IUpdateBy;
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

import java.time.LocalDateTime;
import java.util.List;

@ApiModel("数据权限")
@Data
@FieldNameConstants
@Table
@EntityProxy
public class DataPermission implements IGenId, ICreateBy, IUpdateBy, ProxyEntityAvailable<DataPermission, DataPermissionProxy> {

    @ApiModelProperty("主键id")
    @Column(primaryKey = true)
    private String id;

    @ApiModelProperty("权限id")
    @Column(primaryKey = true)
    private String permissionId;

    @ApiModelProperty("权限名")
    private String name;

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

    @ApiModelProperty("所属权限")
    @Navigate(
            value = RelationTypeEnum.ManyToOne,
            selfProperty = DataPermission.Fields.permissionId,
            targetProperty = Permission.Fields.id
    )
    private Permission permission;

    @ApiModelProperty("绑定角色")
    @Navigate(
            value = RelationTypeEnum.ManyToMany,
            selfProperty = DataPermission.Fields.id,
            selfMappingProperty = RoleDataPermissionRel.Fields.dataPermissionId,
            mappingClass = RoleDataPermissionRel.class,
            targetProperty = Role.Fields.id,
            targetMappingProperty = RoleDataPermissionRel.Fields.roleId
    )
    private List<Role> roleList;
}

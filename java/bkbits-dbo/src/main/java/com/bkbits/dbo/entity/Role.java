package com.bkbits.dbo.entity;

import com.bkbits.dbo.entity.proxy.RoleProxy;
import com.bkbits.orm.ICreateBy;
import com.bkbits.orm.IGenId;
import com.bkbits.orm.IUpdateBy;
import com.easy.query.core.annotation.Column;
import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Navigate;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.enums.CascadeTypeEnum;
import com.easy.query.core.enums.RelationTypeEnum;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;
import java.util.List;

@ApiModel("角色")
@Data
@FieldNameConstants
@Table
@EntityProxy
public class Role implements IGenId, ICreateBy, IUpdateBy, ProxyEntityAvailable<Role, RoleProxy> {

    @ApiModelProperty("主键id")
    @Column(primaryKey = true)
    private String id;

    @ApiModelProperty("所属租户id")
    private String tenantId;

    @ApiModelProperty("角色代码")
    private String code;

    @ApiModelProperty("角色名")
    private String name;

    @ApiModelProperty("排序")
    private Integer sort;

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

    @ApiModelProperty("所属租户")
    @Navigate(
            value = RelationTypeEnum.ManyToOne,
            selfProperty = Role.Fields.tenantId,
            targetProperty = Tenant.Fields.id
    )
    private Tenant tenant;

    @ApiModelProperty("用户列表")
    @Navigate(
            value = RelationTypeEnum.ManyToMany,
            selfProperty = Role.Fields.id,
            selfMappingProperty = UserRoleRel.Fields.roleId,
            mappingClass = UserRoleRel.class,
            targetProperty = User.Fields.userId,
            targetMappingProperty = UserRoleRel.Fields.userId,
            cascade = CascadeTypeEnum.DELETE
    )
    private List<User> userList;

    @ApiModelProperty("角色权限列表")
    @Navigate(
            value = RelationTypeEnum.ManyToMany,
            selfProperty = Role.Fields.id,
            selfMappingProperty = RolePermissionRel.Fields.roleId,
            mappingClass = RolePermissionRel.class,
            targetProperty = Permission.Fields.id,
            targetMappingProperty = RolePermissionRel.Fields.permissionId,
            cascade = CascadeTypeEnum.DELETE
    )
    private List<Permission> permissionList;

    @ApiModelProperty("数据权限列表")
    @Navigate(
            value = RelationTypeEnum.ManyToMany,
            selfProperty = Role.Fields.id,
            selfMappingProperty = RoleDataPermissionRel.Fields.roleId,
            mappingClass = RoleDataPermissionRel.class,
            targetProperty = DataPermission.Fields.id,
            targetMappingProperty = RoleDataPermissionRel.Fields.dataPermissionId,
            cascade = CascadeTypeEnum.DELETE
    )
    private List<DataPermission> dataPermissionList;

    @ApiModelProperty("数据域列表")
    @Navigate(
            value = RelationTypeEnum.OneToMany,
            selfProperty = Role.Fields.id,
            targetProperty = RoleDataScope.Fields.roleId,
            cascade = CascadeTypeEnum.DELETE
    )
    private List<RoleDataScope> dataScopeList;
}

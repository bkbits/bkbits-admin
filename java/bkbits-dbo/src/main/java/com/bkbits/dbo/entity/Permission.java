package com.bkbits.dbo.entity;

import com.bkbits.dbo.entity.proxy.PermissionProxy;
import com.bkbits.orm.ICreateBy;
import com.bkbits.orm.IGenId;
import com.bkbits.orm.IUpdateBy;
import com.bkbits.util.CollectionUtil;
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

@ApiModel("权限")
@Data
@FieldNameConstants
@Table
@EntityProxy
public class Permission implements IGenId, ICreateBy, IUpdateBy, CollectionUtil.ITree<Permission, String>, ProxyEntityAvailable<Permission, PermissionProxy> {

    @ApiModelProperty("主键id")
    @Column(primaryKey = true)
    private String id;

    @ApiModelProperty(value = "父级权限", notes = "为空表示顶级权限")
    private String parentId;

    @ApiModelProperty(value = "权限类型", notes = "D=目录,M=菜单,B=按钮")
    private String type;

    @ApiModelProperty(value = "权限", notes = "权限应该用 '.' 作为分隔符")
    private String permission;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("组件")
    private String component;

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

    @ApiModelProperty("子权限列表")
    @Navigate(
            value = RelationTypeEnum.OneToMany,
            selfProperty = Permission.Fields.id,
            targetProperty = Permission.Fields.parentId
    )
    private List<Permission> children;

    @ApiModelProperty("父级权限")
    @Navigate(
            value = RelationTypeEnum.ManyToOne,
            selfProperty = Permission.Fields.parentId,
            targetProperty = Permission.Fields.id
    )
    private Permission parent;

    @ApiModelProperty("数据权限列表")
    @Navigate(
            value = RelationTypeEnum.OneToMany,
            selfProperty = Permission.Fields.id,
            targetProperty = DataPermission.Fields.permissionId,
            cascade = CascadeTypeEnum.DELETE
    )
    private List<DataPermission> dataPermissionList;

    @Navigate(
            value = RelationTypeEnum.ManyToMany,
            selfProperty = Permission.Fields.id,
            selfMappingProperty = RolePermissionRel.Fields.permissionId,
            mappingClass = RolePermissionRel.class,
            targetProperty = Role.Fields.id,
            targetMappingProperty = RolePermissionRel.Fields.roleId,
            cascade = CascadeTypeEnum.DELETE
    )
    private List<Role> roleList;
}

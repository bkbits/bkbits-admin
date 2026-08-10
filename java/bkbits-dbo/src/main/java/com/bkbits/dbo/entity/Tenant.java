package com.bkbits.dbo.entity;

import com.bkbits.dbo.entity.proxy.TenantProxy;
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

@ApiModel("租户")
@Data
@FieldNameConstants
@Table
@EntityProxy
public class Tenant implements IGenId, ICreateBy, IUpdateBy, ProxyEntityAvailable<Tenant, TenantProxy> {
    @ApiModelProperty("租户编号")
    @Column(primaryKey = true)
    private String id;

    @ApiModelProperty(value = "租户类型", notes = "S=系统租户,U=用户租户,T=租户模板")
    private String type;

    @ApiModelProperty("租户名称")
    private String name;

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

    @ApiModelProperty("租户用户列表")
    @Navigate(
            value = RelationTypeEnum.OneToMany,
            selfProperty = Tenant.Fields.id,
            targetProperty = User.Fields.tenantId
    )
    private List<User> userList;

    @ApiModelProperty("租户角色列表")
    @Navigate(
            value = RelationTypeEnum.OneToMany,
            selfProperty = Tenant.Fields.id,
            targetProperty = Role.Fields.tenantId
    )
    private List<Role> roleList;

    @ApiModelProperty("租户部门列表")
    @Navigate(
            value = RelationTypeEnum.OneToMany,
            selfProperty = Tenant.Fields.id,
            targetProperty = Dept.Fields.tenantId
    )
    private List<Dept> deptList;

    @ApiModelProperty("租户通知列表")
    @Navigate(
            value = RelationTypeEnum.OneToMany,
            selfProperty = Tenant.Fields.id,
            targetProperty = Notification.Fields.targetId,
            extraFilter = NotificationTargetFilterStrategy.class
    )
    private List<Notification> notificationList;
}

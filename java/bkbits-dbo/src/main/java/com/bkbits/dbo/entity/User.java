package com.bkbits.dbo.entity;

import com.bkbits.dbo.entity.proxy.UserProxy;
import com.bkbits.dbo.filter.NotificationTargetFilterStrategy;
import com.bkbits.orm.ICreateBy;
import com.bkbits.orm.IGenId;
import com.bkbits.orm.IUpdateBy;
import com.easy.query.core.annotation.*;
import com.easy.query.core.basic.extension.logicdel.LogicDeleteStrategyEnum;
import com.easy.query.core.enums.CascadeTypeEnum;
import com.easy.query.core.enums.RelationTypeEnum;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;
import java.util.List;

@ApiModel("用户")
@Data
@FieldNameConstants
@Table
@EntityProxy
public class User implements IGenId, ICreateBy, IUpdateBy, ProxyEntityAvailable<User, UserProxy> {

    @ApiModelProperty("主键id")
    @Column(primaryKey = true)
    private String userId;

    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty(value = "密码", hidden = true)
    private String password;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("真实姓名")
    private String realName;

    @ApiModelProperty(value = "性别", notes = "M=男,F=女,U=未知")
    private String sex;

    @ApiModelProperty(value = "状态", notes = "E=启用,D=禁用")
    private String status;

    @ApiModelProperty("所属租户id")
    private String tenantId;

    @ApiModelProperty("所属部门id")
    private String deptId;

    @ApiModelProperty("创建人")
    private String createBy;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新人")
    private String updateBy;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("删除时间")
    @LogicDelete(strategy = LogicDeleteStrategyEnum.LOCAL_DATE_TIME)
    private LocalDateTime deleteTime;

    @ApiModelProperty("用户角色列表")
    @Navigate(
            value = RelationTypeEnum.ManyToMany,
            selfProperty = User.Fields.userId,
            selfMappingProperty = UserRoleRel.Fields.userId,
            mappingClass = UserRoleRel.class,
            targetProperty = Role.Fields.id,
            targetMappingProperty = UserRoleRel.Fields.roleId,
            cascade = CascadeTypeEnum.DELETE
    )
    private List<Role> roleList;

    @ApiModelProperty("所属部门")
    @Navigate(
            value = RelationTypeEnum.ManyToOne,
            selfProperty = User.Fields.deptId,
            targetProperty = Dept.Fields.deptId
    )
    private Dept dept;

    @ApiModelProperty("所属租户")
    @Navigate(
            value = RelationTypeEnum.ManyToOne,
            selfProperty = User.Fields.tenantId,
            targetProperty = Tenant.Fields.id
    )
    private Tenant tenant;

    @ApiModelProperty("用户通知列表")
    @Navigate(
            value = RelationTypeEnum.OneToMany,
            selfProperty = User.Fields.userId,
            targetProperty = Notification.Fields.targetId,
            extraFilter = NotificationTargetFilterStrategy.class,
            cascade = CascadeTypeEnum.DELETE
    )
    private List<Notification> notificationList;

    @ApiModelProperty("通知阅读记录列表")
    @Navigate(
            value = RelationTypeEnum.OneToMany,
            selfProperty = User.Fields.userId,
            targetProperty = NotificationRead.Fields.userId,
            cascade = CascadeTypeEnum.DELETE
    )
    private List<NotificationRead> notificationReadList;

    @Override
    public void setId(String id) {
        userId = id;
    }
}

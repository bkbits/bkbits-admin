package com.bkbits.dbo.entity;

import com.bkbits.dbo.entity.proxy.DeptProxy;
import com.bkbits.dbo.filter.NotificationTargetFilterStrategy;
import com.bkbits.orm.ICreateBy;
import com.bkbits.orm.IGenId;
import com.bkbits.orm.IUpdateBy;
import com.bkbits.util.CollectionUtil;
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

@ApiModel("部门")
@Data
@FieldNameConstants
@Table
@EntityProxy
public class Dept implements IGenId, ICreateBy, IUpdateBy, CollectionUtil.ITree<Dept, String>,
        ProxyEntityAvailable<Dept, DeptProxy> {
    @ApiModelProperty("部门编号")
    @Column(primaryKey = true)
    private String deptId;

    @ApiModelProperty(value = "父级部门编号", notes = "为空表示顶级部门")
    private String parentId;

    @ApiModelProperty("部门名称")
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

    @ApiModelProperty("删除时间")
    @LogicDelete(strategy = LogicDeleteStrategyEnum.LOCAL_DATE_TIME)
    private LocalDateTime deleteTime;

    @ApiModelProperty("子部门列表")
    @Navigate(value = RelationTypeEnum.OneToMany,
            selfProperty = Dept.Fields.deptId,
            targetProperty = Dept.Fields.parentId)
    private List<Dept> children;

    @ApiModelProperty("父级部门")
    @Navigate(value = RelationTypeEnum.ManyToOne,
            selfProperty = Dept.Fields.parentId,
            targetProperty = Dept.Fields.deptId)
    private Dept parent;

    @ApiModelProperty("部门用户列表")
    @Navigate(value = RelationTypeEnum.ManyToMany,
            mappingClass = UserDeptRel.class,
            selfProperty = Dept.Fields.deptId,
            selfMappingProperty = UserDeptRel.Fields.deptId,
            targetProperty = User.Fields.userId,
            targetMappingProperty = UserDeptRel.Fields.userId,
            cascade = CascadeTypeEnum.DELETE
    )
    private List<User> userList;

    @ApiModelProperty("部门通知列表")
    @Navigate(value = RelationTypeEnum.OneToMany,
            selfProperty = Dept.Fields.deptId,
            targetProperty = Notification.Fields.targetId,
            extraFilter = NotificationTargetFilterStrategy.class,
            cascade = CascadeTypeEnum.DELETE)
    private List<Notification> notificationList;

    @Override
    public void setId(String id) {
        deptId = id;
    }

    @Override
    public String getId() {
        return deptId;
    }
}

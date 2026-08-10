package com.bkbits.dbo.entity;

import com.bkbits.dbo.entity.proxy.NotificationProxy;
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

@ApiModel("通知")
@Data
@FieldNameConstants
@Table
@EntityProxy
public class Notification implements IGenId, ICreateBy, IUpdateBy, ProxyEntityAvailable<Notification, NotificationProxy> {

    @ApiModelProperty("通知编号")
    @Column(primaryKey = true)
    private String id;

    @ApiModelProperty(value = "通知类型", notes = "M=站内消息,T=租户通知,D=部门通知,U=用户通知")
    private String type;

    @ApiModelProperty(value = "通知目标id", notes = "站内消息时忽略,租户通知时为租户id,部门通知为部门id,用户通知时为用户id")
    private String targetId;

    @ApiModelProperty("通知标题")
    private String title;

    @ApiModelProperty("通知内容")
    private String content;

    @ApiModelProperty("发布时间")
    private LocalDateTime publishTime;

    @ApiModelProperty("过期时间")
    private LocalDateTime expiredTime;

    @ApiModelProperty("创建人")
    private String createBy;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新人")
    private String updateBy;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @ApiModelProperty("阅读列表")
    @Navigate(value = RelationTypeEnum.OneToMany,
            selfProperty = Notification.Fields.id,
            targetProperty = NotificationRead.Fields.notificationId,
            cascade = CascadeTypeEnum.DELETE
    )
    private List<NotificationRead> readList;
}

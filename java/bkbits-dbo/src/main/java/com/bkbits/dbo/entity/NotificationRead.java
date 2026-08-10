package com.bkbits.dbo.entity;

import com.bkbits.dbo.entity.proxy.NotificationReadProxy;
import com.bkbits.orm.IGenId;
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

@ApiModel("通知已读记录")
@Data
@FieldNameConstants
@Table
@EntityProxy
public class NotificationRead implements IGenId, ProxyEntityAvailable<NotificationRead, NotificationReadProxy> {

    @ApiModelProperty("主键id")
    @Column(primaryKey = true)
    private String id;

    @ApiModelProperty("通知编号")
    private String notificationId;

    @ApiModelProperty("用户编号")
    private String userId;

    @ApiModelProperty("阅读时间")
    private LocalDateTime readTime;

    @ApiModelProperty("关联通知")
    @Navigate(
            value = RelationTypeEnum.ManyToOne,
            selfProperty = NotificationRead.Fields.notificationId,
            targetProperty = Notification.Fields.id
    )
    private Notification notification;

    @ApiModelProperty("关联用户")
    @Navigate(
            value = RelationTypeEnum.ManyToOne,
            selfProperty = NotificationRead.Fields.userId,
            targetProperty = User.Fields.userId
    )
    private User user;
}

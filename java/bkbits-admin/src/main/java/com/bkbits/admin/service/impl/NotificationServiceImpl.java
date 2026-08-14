package com.bkbits.admin.service.impl;

import com.bkbits.admin.service.NotificationService;
import com.bkbits.dbo.constants.BaseConstants;
import com.bkbits.dbo.entity.Notification;
import com.bkbits.dbo.entity.NotificationRead;
import com.bkbits.util.ValidUtil;
import com.easy.query.api.proxy.client.EasyEntityQuery;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.annotation.Transaction;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class NotificationServiceImpl implements NotificationService {

    @Inject
    EasyEntityQuery easyEntityQuery;

    @Override
    public Notification add(Notification notification) {
        ValidUtil.requireNotNull(notification, "通知不能为空");
        if (easyEntityQuery.insertable(notification).executeRows() != 1) {
            throw new IllegalStateException("创建通知失败");
        }
        return notification;
    }

    @Override
    public Notification getById(String id) {
        return easyEntityQuery.queryable(Notification.class)
                .whereById(ValidUtil.requireString(id, "通知编号不能为空"))
                .singleOrNull();
    }

    @Override
    public List<Notification> listByTarget(String type, String targetId) {
        String checkedType = ValidUtil.requireString(type, "通知类型不能为空");
        return easyEntityQuery.queryable(Notification.class)
                .where(o -> {
                    o.type().eq(checkedType);
                    if (!BaseConstants.NOTIFICATION_TYPE_MESSAGE.equals(checkedType)) {
                        o.targetId().eq(ValidUtil.requireString(targetId, "通知目标编号不能为空"));
                    }
                })
                .orderBy(o -> {
                    o.publishTime().desc();
                    o.id().desc();
                })
                .toList();
    }

    @Override
    public List<Notification> listForUser(String userId, String tenantId, String deptId) {
        String checkedUserId = ValidUtil.requireString(userId, "用户编号不能为空");
        return easyEntityQuery.queryable(Notification.class)
                .where(o -> {
                    o.type().eq(BaseConstants.NOTIFICATION_TYPE_MESSAGE);
                    o.or(tenantId != null && !tenantId.isBlank(), () -> {
                        o.type().eq(BaseConstants.NOTIFICATION_TYPE_TENANT);
                        o.targetId().eq(tenantId);
                    });
                    o.or(deptId != null && !deptId.isBlank(), () -> {
                        o.type().eq(BaseConstants.NOTIFICATION_TYPE_DEPT);
                        o.targetId().eq(deptId);
                    });
                    o.or(() -> {
                        o.type().eq(BaseConstants.NOTIFICATION_TYPE_USER);
                        o.targetId().eq(checkedUserId);
                    });
                })
                .orderBy(o -> {
                    o.publishTime().desc();
                    o.id().desc();
                })
                .toList();
    }

    @Override
    public Notification update(Notification notification) {
        ValidUtil.requireNotNull(notification, "通知不能为空");
        ValidUtil.requireString(notification.getId(), "通知编号不能为空");
        easyEntityQuery.updatable(notification)
                .executeRows(1, "更新通知失败");
        return notification;
    }

    @Override
    @Transaction
    public void removeById(String id) {
        String checkedId = ValidUtil.requireString(id, "通知编号不能为空");
        easyEntityQuery.deletable(NotificationRead.class)
                .where(o -> o.notificationId().eq(checkedId))
                .executeRows();
        easyEntityQuery.deletable(Notification.class)
                .whereById(checkedId)
                .executeRows(1, "删除通知失败");
    }

    @Override
    @Transaction
    public NotificationRead markRead(String notificationId, String userId) {
        String checkedNotificationId = ValidUtil.requireString(notificationId, "通知编号不能为空");
        String checkedUserId = ValidUtil.requireString(userId, "用户编号不能为空");
        boolean notificationExists = easyEntityQuery.queryable(Notification.class)
                .whereById(checkedNotificationId)
                .any();
        if (!notificationExists) {
            throw new IllegalArgumentException("通知不存在");
        }

        NotificationRead read = easyEntityQuery.queryable(NotificationRead.class)
                .where(o -> {
                    o.notificationId().eq(checkedNotificationId);
                    o.userId().eq(checkedUserId);
                })
                .singleOrNull();
        LocalDateTime now = LocalDateTime.now();
        if (read == null) {
            read = new NotificationRead();
            read.setNotificationId(checkedNotificationId);
            read.setUserId(checkedUserId);
            read.setReadTime(now);
            if (easyEntityQuery.insertable(read).executeRows() != 1) {
                throw new IllegalStateException("标记通知已读失败");
            }
            return read;
        }

        easyEntityQuery.updatable(NotificationRead.class)
                .setColumns(o -> o.readTime().set(now))
                .whereById(read.getId())
                .executeRows(1, "更新通知已读时间失败");
        read.setReadTime(now);
        return read;
    }

    @Override
    public boolean isRead(String notificationId, String userId) {
        String checkedNotificationId = ValidUtil.requireString(notificationId, "通知编号不能为空");
        String checkedUserId = ValidUtil.requireString(userId, "用户编号不能为空");
        return easyEntityQuery.queryable(NotificationRead.class)
                .where(o -> {
                    o.notificationId().eq(checkedNotificationId);
                    o.userId().eq(checkedUserId);
                })
                .any();
    }

}

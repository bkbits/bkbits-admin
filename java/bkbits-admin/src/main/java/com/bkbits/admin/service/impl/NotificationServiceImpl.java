package com.bkbits.admin.service.impl;

import com.bkbits.admin.service.NotificationService;
import com.bkbits.dbo.constants.BaseConstants;
import com.bkbits.dbo.entity.Notification;
import com.bkbits.dbo.entity.NotificationRead;
import com.easy.query.api.proxy.client.EasyEntityQuery;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.data.annotation.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Component
public class NotificationServiceImpl implements NotificationService {

    @Inject
    EasyEntityQuery easyEntityQuery;

    @Override
    public Notification add(Notification notification) {
        Objects.requireNonNull(notification, "通知不能为空");
        if (easyEntityQuery.insertable(notification).executeRows() != 1) {
            throw new IllegalStateException("创建通知失败");
        }
        return notification;
    }

    @Override
    public Notification getById(String id) {
        return easyEntityQuery.queryable(Notification.class)
                .whereById(requireText(id, "通知编号"))
                .singleOrNull();
    }

    @Override
    public List<Notification> listByTarget(String type, String targetId) {
        String checkedType = requireText(type, "通知类型");
        return easyEntityQuery.queryable(Notification.class)
                .where(o -> {
                    o.type().eq(checkedType);
                    if (!BaseConstants.NOTIFICATION_TYPE_MESSAGE.equals(checkedType)) {
                        o.targetId().eq(requireText(targetId, "通知目标编号"));
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
        String checkedUserId = requireText(userId, "用户编号");
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
        Objects.requireNonNull(notification, "通知不能为空");
        requireText(notification.getId(), "通知编号");
        easyEntityQuery.updatable(notification)
                .executeRows(1, "更新通知失败");
        return notification;
    }

    @Override
    @Transaction
    public void removeById(String id) {
        String checkedId = requireText(id, "通知编号");
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
        String checkedNotificationId = requireText(notificationId, "通知编号");
        String checkedUserId = requireText(userId, "用户编号");
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
        String checkedNotificationId = requireText(notificationId, "通知编号");
        String checkedUserId = requireText(userId, "用户编号");
        return easyEntityQuery.queryable(NotificationRead.class)
                .where(o -> {
                    o.notificationId().eq(checkedNotificationId);
                    o.userId().eq(checkedUserId);
                })
                .any();
    }

    private String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + "不能为空");
        }
        return value;
    }
}

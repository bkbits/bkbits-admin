package com.bkbits.admin.service;

import com.bkbits.dbo.entity.Notification;
import com.bkbits.dbo.entity.NotificationRead;

import java.util.List;

/**
 * 通知服务。
 */
public interface NotificationService {

    /**
     * 新增通知。
     *
     * @param notification 通知信息
     * @return 新增后的通知
     */
    Notification add(Notification notification);

    /**
     * 按编号查询通知。
     *
     * @param id 通知编号
     * @return 通知；不存在时返回 {@code null}
     */
    Notification getById(String id);

    /**
     * 查询指定类型和目标的通知。
     *
     * @param type     通知类型
     * @param targetId 通知目标编号；站内消息可为空
     * @return 通知列表
     */
    List<Notification> listByTarget(String type, String targetId);

    /**
     * 查询指定用户可接收的站内、租户、部门及用户通知。
     *
     * @param userId   用户编号
     * @param tenantId 用户所属租户编号；可为空
     * @param deptId   用户所属部门编号；可为空
     * @return 用户可接收的通知列表
     */
    List<Notification> listForUser(String userId, String tenantId, String deptId);

    /**
     * 更新通知。
     *
     * @param notification 待更新的通知信息
     * @return 更新后的通知
     */
    Notification update(Notification notification);

    /**
     * 按编号删除通知及其已读记录。
     *
     * @param id 通知编号
     */
    void removeById(String id);

    /**
     * 将通知标记为指定用户已读。
     *
     * @param notificationId 通知编号
     * @param userId         用户编号
     * @return 新增或更新后的已读记录
     */
    NotificationRead markRead(String notificationId, String userId);

    /**
     * 判断指定用户是否已读通知。
     *
     * @param notificationId 通知编号
     * @param userId         用户编号
     * @return 已读返回 {@code true}
     */
    boolean isRead(String notificationId, String userId);
}

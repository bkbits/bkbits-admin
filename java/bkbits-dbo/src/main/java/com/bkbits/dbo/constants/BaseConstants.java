package com.bkbits.dbo.constants;

/**
 * 基础常量定义
 */
public interface BaseConstants {

    /** 性别：男 */
    String SEX_MAN = "M";

    /** 性别：女 */
    String SEX_FEMALE = "F";

    /** 性别：未知 */
    String SEX_UNKNOWN = "U";

    /** 权限类型：目录 */
    String PERMISSION_DIRECTORY = "D";

    /** 权限类型：菜单 */
    String PERMISSION_MENU = "M";

    /** 权限类型：按钮 */
    String PERMISSION_BUTTON = "B";

    /** 状态：启用 */
    String STATUS_ENABLED = "E";

    /** 状态：禁用 */
    String STATUS_DISABLED = "D";

    /** 通知类型：站内消息 */
    String NOTIFICATION_TYPE_MESSAGE = "M";

    /** 通知类型：租户通知 */
    String NOTIFICATION_TYPE_TENANT = "T";

    /** 通知类型：部门通知 */
    String NOTIFICATION_TYPE_DEPT = "D";

    /** 通知类型：用户通知 */
    String NOTIFICATION_TYPE_USER = "U";

    /** 租户类型：系统租户 */
    String TENANT_TYPE_SYSTEM = "S";

    /** 租户类型：用户租户 */
    String TENANT_TYPE_USER = "U";

    /** 租户类型：租户模板 */
    String TENANT_TYPE_TEMPLATE = "T";
}

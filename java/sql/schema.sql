-- =====================================================================
-- bkbits 管理系统 数据库建表脚本
-- 由实体类（@Table）分析生成
--
-- 兼容性说明：
--   1. 主键为应用层生成（雪花/Pear，long 类型），统一 bigint，无自增
--   2. 时间字段使用 DATETIME，MySQL/SQL Server 通用；
--      PostgreSQL/Oracle 请使用 TIMESTAMP
--   3. LogLogin.succeed 使用 boolean，Oracle 需替换为 NUMBER(1)
--   4. 枚举常量字段（sex/status/type/upload_status）使用 char(1)
--   5. 未使用 ENGINE/CHARSET 等数据库专属子句，MySQL 默认 InnoDB/utf8mb4
-- =====================================================================

-- ---------------------------------------------------------------------
-- 租户表
-- ---------------------------------------------------------------------
CREATE TABLE tenant (
    id          bigint       NOT NULL COMMENT '主键id',
    type        char(1)      NOT NULL COMMENT '租户类型 S=系统租户,U=用户租户,T=租户模板',
    name        varchar(64)  NOT NULL COMMENT '租户名称',
    status      char(1)      NOT NULL COMMENT '状态 E=启用,D=禁用',
    create_by   varchar(32)   NOT NULL COMMENT '创建人（user_name）',
    create_time DATETIME    NOT NULL COMMENT '创建时间',
    update_by   varchar(32)   NULL COMMENT '更新人（user_name）',
    update_time DATETIME    NULL COMMENT '更新时间',
    delete_time DATETIME    NULL COMMENT '删除时间（逻辑删除）',
    PRIMARY KEY (id)
);

CREATE INDEX idx_tenant_name ON tenant (name);

-- ---------------------------------------------------------------------
-- 部门表
-- ---------------------------------------------------------------------
CREATE TABLE dept (
    dept_id     bigint       NOT NULL COMMENT '部门编号',
    parent_id   bigint       NULL COMMENT '父级部门编号，为空表示顶级部门',
    tenant_id   bigint       NULL COMMENT '所属租户id',
    name        varchar(64)  NOT NULL COMMENT '部门名称',
    sort        int          NULL COMMENT '排序',
    status      char(1)      NOT NULL COMMENT '状态 E=启用,D=禁用',
    create_by   varchar(32)   NOT NULL COMMENT '创建人（user_name）',
    create_time DATETIME    NOT NULL COMMENT '创建时间',
    update_by   varchar(32)   NULL COMMENT '更新人（user_name）',
    update_time DATETIME    NULL COMMENT '更新时间',
    PRIMARY KEY (dept_id)
);

CREATE INDEX idx_dept_parent_id ON dept (parent_id);
CREATE INDEX idx_dept_tenant_id ON dept (tenant_id);

-- ---------------------------------------------------------------------
-- 角色表
-- ---------------------------------------------------------------------
CREATE TABLE role (
    id          bigint       NOT NULL COMMENT '主键id',
    tenant_id   bigint       NULL COMMENT '所属租户id',
    code        varchar(64)  NOT NULL COMMENT '角色代码',
    name        varchar(64)  NOT NULL COMMENT '角色名',
    sort        int          NULL COMMENT '排序',
    status      char(1)      NOT NULL COMMENT '状态 E=启用,D=禁用',
    create_by   varchar(32)   NOT NULL COMMENT '创建人（user_name）',
    create_time DATETIME    NOT NULL COMMENT '创建时间',
    update_by   varchar(32)   NULL COMMENT '更新人（user_name）',
    update_time DATETIME    NULL COMMENT '更新时间',
    PRIMARY KEY (id)
);

CREATE INDEX idx_role_tenant_id ON role (tenant_id);
CREATE INDEX idx_role_code ON role (code);

-- ---------------------------------------------------------------------
-- 权限表
-- ---------------------------------------------------------------------
CREATE TABLE permission (
    id          bigint        NOT NULL COMMENT '主键id',
    parent_id   bigint        NULL COMMENT '父级权限，为空表示顶级权限',
    type        char(1)       NOT NULL COMMENT '权限类型 D=目录,M=菜单,B=按钮',
    permission  varchar(128)  NULL COMMENT '权限，用 . 作为分隔符',
    name        varchar(64)   NOT NULL COMMENT '名称',
    sort        int           NULL COMMENT '排序',
    component   varchar(255)  NULL COMMENT '组件',
    status      char(1)       NOT NULL COMMENT '状态 E=启用,D=禁用',
    create_by   varchar(32)   NOT NULL COMMENT '创建人（user_name）',
    create_time DATETIME     NOT NULL COMMENT '创建时间',
    update_by   varchar(32)   NULL COMMENT '更新人（user_name）',
    update_time DATETIME     NULL COMMENT '更新时间',
    PRIMARY KEY (id)
);

CREATE INDEX idx_permission_parent_id ON permission (parent_id);
CREATE INDEX idx_permission_permission ON permission (permission);

-- ---------------------------------------------------------------------
-- 用户表
-- ---------------------------------------------------------------------
CREATE TABLE user (
    user_id     bigint       NOT NULL COMMENT '主键id',
    user_name   varchar(32)  NOT NULL COMMENT '用户名',
    password    varchar(128) NOT NULL COMMENT '密码',
    email       varchar(64)  NULL COMMENT '邮箱',
    phone       varchar(20)  NULL COMMENT '手机号',
    real_name   varchar(64)  NULL COMMENT '真实姓名',
    sex         char(1)      NULL COMMENT '性别 M=男,F=女,U=未知',
    status      char(1)      NOT NULL COMMENT '状态 E=启用,D=禁用',
    tenant_id   bigint       NULL COMMENT '所属租户id',
    dept_id     bigint       NULL COMMENT '所属部门id',
    create_by   varchar(32)   NOT NULL COMMENT '创建人（user_name）',
    create_time DATETIME    NOT NULL COMMENT '创建时间',
    update_by   varchar(32)   NULL COMMENT '更新人（user_name）',
    update_time DATETIME    NULL COMMENT '更新时间',
    delete_time DATETIME    NULL COMMENT '删除时间（逻辑删除）',
    PRIMARY KEY (user_id)
);

CREATE INDEX idx_user_user_name ON user (user_name);
CREATE INDEX idx_user_phone ON user (phone);
CREATE INDEX idx_user_tenant_id ON user (tenant_id);
CREATE INDEX idx_user_dept_id ON user (dept_id);

-- ---------------------------------------------------------------------
-- 数据权限表
-- ---------------------------------------------------------------------
CREATE TABLE data_permission (
    id            bigint       NOT NULL COMMENT '主键id',
    permission_id bigint       NULL COMMENT '关联权限id',
    data_scope    varchar(64)  NULL COMMENT '数据域',
    status        char(1)      NOT NULL COMMENT '状态 E=启用,D=禁用',
    create_by     varchar(32)   NOT NULL COMMENT '创建人（user_name）',
    create_time   DATETIME    NOT NULL COMMENT '创建时间',
    update_by     varchar(32)   NULL COMMENT '更新人（user_name）',
    update_time   DATETIME    NULL COMMENT '更新时间',
    PRIMARY KEY (id)
);

CREATE INDEX idx_data_permission_permission_id ON data_permission (permission_id);

-- ---------------------------------------------------------------------
-- 角色权限关联表
-- ---------------------------------------------------------------------
CREATE TABLE role_permission_rel (
    id            bigint NOT NULL COMMENT '主键id',
    role_id       bigint NOT NULL COMMENT '关联角色id',
    permission_id bigint NOT NULL COMMENT '关联权限id',
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX uk_role_permission ON role_permission_rel (role_id, permission_id);
CREATE INDEX idx_role_permission_permission_id ON role_permission_rel (permission_id);

-- ---------------------------------------------------------------------
-- 角色数据权限关联表
-- ---------------------------------------------------------------------
CREATE TABLE role_data_permission_rel (
    id                 bigint NOT NULL COMMENT '主键id',
    role_id            bigint NOT NULL COMMENT '关联角色id',
    permission_id      bigint NOT NULL COMMENT '关联权限id',
    data_permission_id bigint NOT NULL COMMENT '关联数据权限id',
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX uk_role_data_permission ON role_data_permission_rel (role_id, permission_id, data_permission_id);
CREATE INDEX idx_role_data_permission_permission_id ON role_data_permission_rel (permission_id);
CREATE INDEX idx_role_data_permission_data_permission_id ON role_data_permission_rel (data_permission_id);

-- ---------------------------------------------------------------------
-- 用户角色关联表
-- ---------------------------------------------------------------------
CREATE TABLE user_role_rel (
    id      bigint NOT NULL COMMENT '主键id',
    user_id bigint NOT NULL COMMENT '关联用户id',
    role_id bigint NOT NULL COMMENT '关联角色id',
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX uk_user_role ON user_role_rel (user_id, role_id);
CREATE INDEX idx_user_role_role_id ON user_role_rel (role_id);

-- ---------------------------------------------------------------------
-- 通知表
-- ---------------------------------------------------------------------
CREATE TABLE notification (
    id           bigint       NOT NULL COMMENT '主键id',
    type         char(1)      NOT NULL COMMENT '通知类型 M=站内消息,T=租户通知,D=部门通知,U=用户通知',
    target_id    bigint       NULL COMMENT '通知目标id，站内消息时忽略，租户通知时为租户id，部门通知为部门id，用户通知时为用户id',
    title        varchar(255) NOT NULL COMMENT '通知标题',
    content      text         NULL COMMENT '通知内容',
    publish_time DATETIME    NULL COMMENT '发布时间',
    expired_time DATETIME    NULL COMMENT '过期时间',
    create_by    varchar(32)   NOT NULL COMMENT '创建人（user_name）',
    create_time  DATETIME    NOT NULL COMMENT '创建时间',
    update_by    varchar(32)   NULL COMMENT '更新人（user_name）',
    update_time  DATETIME    NULL COMMENT '更新时间',
    PRIMARY KEY (id)
);

CREATE INDEX idx_notification_target ON notification (type, target_id);
CREATE INDEX idx_notification_publish_time ON notification (publish_time);

-- ---------------------------------------------------------------------
-- 通知已读记录表
-- ---------------------------------------------------------------------
CREATE TABLE notification_read (
    id              bigint    NOT NULL COMMENT '主键id',
    notification_id bigint    NOT NULL COMMENT '通知编号',
    user_id         bigint    NOT NULL COMMENT '用户编号',
    read_time       DATETIME NULL COMMENT '阅读时间',
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX uk_notification_read ON notification_read (notification_id, user_id);
CREATE INDEX idx_notification_read_user_id ON notification_read (user_id);

-- ---------------------------------------------------------------------
-- 操作日志表
-- ---------------------------------------------------------------------
CREATE TABLE log_operation (
    id          bigint       NOT NULL COMMENT '日志编号',
    name        varchar(64)  NULL COMMENT '日志名称',
    type        varchar(32)  NULL COMMENT '日志类型（业务自定义）',
    module      varchar(64)  NULL COMMENT '模块',
    method      varchar(255) NULL COMMENT '函数路径',
    args        text         NULL COMMENT '请求参数',
    result      text         NULL COMMENT '返回结果',
    ip          varchar(64)  NULL COMMENT '操作IP',
    user_agent  varchar(512) NULL COMMENT 'UserAgent记录',
    url         varchar(255) NULL COMMENT '访问的url',
    cost_time   int          NULL COMMENT '花费时间',
    remark      varchar(512) NULL COMMENT '备注',
    create_by   varchar(32)   NOT NULL COMMENT '创建者（user_name）',
    create_time DATETIME    NOT NULL COMMENT '创建时间',
    PRIMARY KEY (id)
);

CREATE INDEX idx_log_operation_create_time ON log_operation (create_time);
CREATE INDEX idx_log_operation_module ON log_operation (module);

-- ---------------------------------------------------------------------
-- 登录日志表
-- ---------------------------------------------------------------------
CREATE TABLE log_login (
    id          bigint       NOT NULL COMMENT '日志编号',
    succeed     boolean      NOT NULL COMMENT '登录是否成功（Oracle 请用 NUMBER(1)）',
    fail_reason varchar(255) NULL COMMENT '失败原因',
    ip          varchar(64)  NULL COMMENT '登录ip',
    device      varchar(255) NULL COMMENT '登录设备',
    user_agent  varchar(512) NULL COMMENT 'UserAgent',
    cost_time   int          NULL COMMENT '花费时间',
    create_by   varchar(32)   NOT NULL COMMENT '登陆者（user_name）',
    create_time DATETIME    NOT NULL COMMENT '登录时间',
    PRIMARY KEY (id)
);

CREATE INDEX idx_log_login_create_time ON log_login (create_time);

-- ---------------------------------------------------------------------
-- 上传文件表
-- ---------------------------------------------------------------------
CREATE TABLE upload_file (
    id           bigint       NOT NULL COMMENT '文件id',
    path         varchar(255) NULL COMMENT '保存路径',
    content_type varchar(128) NULL COMMENT '文件类型',
    file_size    bigint       NULL COMMENT '文件大小',
    file_name    varchar(255) NULL COMMENT '文件名称',
    hash         varchar(64)  NULL COMMENT '文件哈希',
    create_by    varchar(32)   NOT NULL COMMENT '创建者（user_name）',
    create_time  DATETIME    NOT NULL COMMENT '创建时间',
    PRIMARY KEY (id)
);

CREATE INDEX idx_upload_file_hash ON upload_file (hash);

-- ---------------------------------------------------------------------
-- 上传任务表
-- ---------------------------------------------------------------------
CREATE TABLE upload_task (
    id            bigint       NOT NULL COMMENT '任务id',
    upload_status char(1)      NULL COMMENT '任务状态 S=成功完成,A=任务丢弃,W=等待上传',
    path          varchar(255) NULL COMMENT '保存路径',
    content_type  varchar(128) NULL COMMENT '文件类型',
    file_size     bigint       NULL COMMENT '文件大小',
    file_name     varchar(255) NULL COMMENT '文件名称',
    hash          varchar(64)  NULL COMMENT '文件哈希',
    create_by     varchar(32)   NOT NULL COMMENT '创建者（user_name）',
    create_time   DATETIME    NOT NULL COMMENT '创建时间',
    PRIMARY KEY (id)
);

-- ---------------------------------------------------------------------
-- 上传任务分片表
-- ---------------------------------------------------------------------
CREATE TABLE upload_task_piece (
    id            bigint       NOT NULL COMMENT '主键id',
    upload_id     bigint       NOT NULL COMMENT '上传任务id',
    file_index    bigint       NOT NULL COMMENT '文件序号，从0开始',
    upload_status char(1)      NULL COMMENT '任务状态 S=成功完成,W=等待上传',
    path          varchar(255) NULL COMMENT '保存路径',
    file_size     bigint       NULL COMMENT '文件大小',
    hash          varchar(64)  NULL COMMENT '文件哈希',
    create_by     varchar(32)   NOT NULL COMMENT '创建者（user_name）',
    create_time   DATETIME    NOT NULL COMMENT '创建时间',
    PRIMARY KEY (id)
);

CREATE UNIQUE INDEX uk_upload_task_piece ON upload_task_piece (upload_id, file_index);
CREATE INDEX idx_upload_task_piece_upload_id ON upload_task_piece (upload_id);

-- ---------------------------------------------------------------------
-- 系统字典表
-- ---------------------------------------------------------------------
CREATE TABLE dict (
    id          bigint       NOT NULL COMMENT '主键id',
    dict_key    varchar(64)  NOT NULL COMMENT '字典键',
    name        varchar(64)  NOT NULL COMMENT '字典名称',
    sort        int          NULL COMMENT '排序',
    type        char(1)      NOT NULL COMMENT '字典类型 S=系统字典,U=用户字典',
    remark      varchar(512) NULL COMMENT '备注',
    create_by   varchar(32)   NOT NULL COMMENT '创建人（user_name）',
    create_time DATETIME    NOT NULL COMMENT '创建时间',
    update_by   varchar(32)   NULL COMMENT '更新人（user_name）',
    update_time DATETIME    NULL COMMENT '更新时间',
    PRIMARY KEY (id)
);

CREATE INDEX idx_dict_dict_key ON dict (dict_key);

-- ---------------------------------------------------------------------
-- 系统字典值表
-- ---------------------------------------------------------------------
CREATE TABLE dict_value (
    id        bigint       NOT NULL COMMENT '主键id',
    dict_id   bigint       NOT NULL COMMENT '关联字典id',
    value_key varchar(64)  NOT NULL COMMENT '值键',
    name      varchar(64)  NOT NULL COMMENT '值名称',
    sort      int          NULL COMMENT '排序',
    value     varchar(255) NULL COMMENT '值',
    type      char(1)      NULL COMMENT '类型 S=成功,I=信息,W=警告,E=错误',
    color     varchar(32)  NULL COMMENT '颜色',
    remark    varchar(512) NULL COMMENT '备注',
    PRIMARY KEY (id)
);

CREATE INDEX idx_dict_value_dict_id ON dict_value (dict_id);

-- ---------------------------------------------------------------------
-- 系统参数表
-- ---------------------------------------------------------------------
CREATE TABLE param (
    id          bigint       NOT NULL COMMENT '主键id',
    param_key   varchar(64)  NOT NULL COMMENT '参数键',
    name        varchar(64)  NOT NULL COMMENT '参数名称',
    sort        int          NULL COMMENT '排序',
    value       varchar(255) NULL COMMENT '参数值',
    type        char(1)      NOT NULL COMMENT '参数类型 S=系统参数,U=用户参数',
    remark      varchar(512) NULL COMMENT '备注',
    create_by   varchar(32)   NOT NULL COMMENT '创建人（user_name）',
    create_time DATETIME    NOT NULL COMMENT '创建时间',
    update_by   varchar(32)   NULL COMMENT '更新人（user_name）',
    update_time DATETIME    NULL COMMENT '更新时间',
    PRIMARY KEY (id)
);

CREATE INDEX idx_param_param_key ON param (param_key);

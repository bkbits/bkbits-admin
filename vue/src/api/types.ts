// 本文件由脚本从后端 Swagger 文档 (http://localhost:8088/swagger/v2?group=adminApi) 自动生成，勿手动修改。

/** 后端统一响应包装 */
export interface Result<T = unknown> {
  /** 是否成功 */
  ok: boolean;
  /** 业务状态码 */
  code: number;
  /** 提示消息 */
  message: string;
  /** 业务数据 */
  data: T;
}

/** 分页数据体 */
export interface PageData<T> {
  /** 总条数 */
  total: number;
  /** 当前页数据 */
  rows: T[];
}

/** 分页响应包装 */
export type PageResult<T> = Result<PageData<T>>;

/** 分页查询扩展参数（后端 Swagger 未列出，mock 层支持） */
export interface PageParams {
  /** 页码，从 1 开始 */
  page?: number;
  /** 每页条数 */
  pageSize?: number;
}

/** 通用删除入参 */
export interface IdDTO {
  id: string;
}

/** BindDataPermissionsToRoleDTO */
export interface BindDataPermissionsToRoleDTO {
  /** 数据权限编号集合；为空时清空绑定 */
  dataPermissionIds?: string[];
  /** 菜单权限编号 */
  permissionId?: string;
  /** 角色编号 */
  roleId?: string;
}

/** BindPermissionsToRoleDTO */
export interface BindPermissionsToRoleDTO {
  /** 权限编号集合；为空时清空绑定 */
  permissionIds?: string[];
  /** 角色编号 */
  roleId?: string;
}

/** BindRolesToUserDTO */
export interface BindRolesToUserDTO {
  /** 角色编号集合；为空时清空绑定 */
  roleIds?: string[];
  /** 用户编号 */
  userId?: string;
}

/** DataPermission */
export interface DataPermission {
  /** 创建人 */
  createBy?: string;
  /** 创建时间 */
  createTime?: string;
  /** 数据域 */
  dataScope?: string;
  /** 主键id */
  id?: string;
  /** 所属菜单权限 */
  permission?: Permission;
  /** 关联权限id */
  permissionId?: string;
  /** 角色列表 */
  roleList?: Role[];
  /** 状态 */
  status?: string;
  /** 更新人 */
  updateBy?: string;
  /** 更新时间 */
  updateTime?: string;
}

/** DataPermissionAddDTO */
export interface DataPermissionAddDTO {
  /** 数据域 */
  dataScope?: string;
  /** 菜单权限编号 */
  permissionId?: string;
  /** 状态 */
  status?: string;
}

/** DataPermissionUpdateDTO */
export interface DataPermissionUpdateDTO {
  /** 数据域 */
  dataScope?: string;
  /** 数据权限编号；更新时必填 */
  id?: string;
  /** 状态 */
  status?: string;
}

/** Dept */
export interface Dept {
  /** 子部门列表 */
  children?: Dept[];
  /** 创建人 */
  createBy?: string;
  /** 创建时间 */
  createTime?: string;
  /** 部门编号 */
  deptId?: string;
  /** 部门名称 */
  name?: string;
  /** 部门通知列表 */
  notificationList?: Notification[];
  /** 父级部门 */
  parent?: Dept;
  /** 父级部门编号 */
  parentId?: string;
  /** 排序 */
  sort?: number;
  /** 状态 */
  status?: string;
  /** 所属租户 */
  tenant?: Tenant;
  /** 所属租户id */
  tenantId?: string;
  /** 更新人 */
  updateBy?: string;
  /** 更新时间 */
  updateTime?: string;
  /** 部门用户列表 */
  userList?: User[];
}

/** DeptAddDTO */
export interface DeptAddDTO {
  /** 部门名称 */
  name?: string;
  /** 父级部门编号；为空表示顶级部门 */
  parentId?: string;
  /** 排序 */
  sort?: number;
  /** 状态（E=启用,D=禁用） */
  status?: string;
}

/** DeptQueryDTO */
export interface DeptQueryDTO {
  /** 部门编号 */
  deptId?: string;
  /** 部门名称 */
  name?: string;
  /** 父级部门编号 */
  parentId?: string;
  /** 状态 */
  status?: string;
  /** 所属租户id */
  tenantId?: string;
}

/** DeptUpdateDTO */
export interface DeptUpdateDTO {
  /** 部门编号；更新时必填 */
  deptId?: string;
  /** 部门名称 */
  name?: string;
  /** 父级部门编号；为空表示顶级部门 */
  parentId?: string;
  /** 排序 */
  sort?: number;
  /** 状态（E=启用,D=禁用） */
  status?: string;
}

/** DeptVO */
export interface DeptVO {
  /** 子部门列表 */
  children?: DeptVO[];
  /** 创建人 */
  createBy?: string;
  /** 创建时间 */
  createTime?: string;
  /** 部门编号 */
  deptId?: string;
  /** 部门名称 */
  name?: string;
  /** 父级部门编号；为空表示顶级部门 */
  parentId?: string;
  /** 排序 */
  sort?: number;
  /** 状态（E=启用,D=禁用） */
  status?: string;
  /** 所属租户id */
  tenantId?: string;
  /** 更新人 */
  updateBy?: string;
  /** 更新时间 */
  updateTime?: string;
}

/** Dict */
export interface Dict {
  /** 创建人 */
  createBy?: string;
  /** 创建时间 */
  createTime?: string;
  /** 字典键 */
  dictKey?: string;
  /** 主键id */
  id?: string;
  /** 字典名称 */
  name?: string;
  /** 备注 */
  remark?: string;
  /** 排序 */
  sort?: number;
  /** 字典类型 */
  type?: string;
  /** 更新人 */
  updateBy?: string;
  /** 更新时间 */
  updateTime?: string;
  /** 值列表 */
  valueList?: DictValue[];
}

/** DictAddDTO */
export interface DictAddDTO {
  /** 字典键 */
  dictKey?: string;
  /** 字典名称 */
  name?: string;
  /** 备注 */
  remark?: string;
  /** 排序 */
  sort?: number;
  /** 字典类型（S=系统字典,U=用户字典） */
  type?: string;
}

/** DictQueryDTO */
export interface DictQueryDTO {
  /** 字典键 */
  dictKey?: string;
  /** 字典名称 */
  name?: string;
  /** 字典类型 */
  type?: string;
}

/** DictUpdateDTO */
export interface DictUpdateDTO {
  /** 字典键 */
  dictKey?: string;
  /** 字典id */
  id?: string;
  /** 字典名称 */
  name?: string;
  /** 备注 */
  remark?: string;
  /** 排序 */
  sort?: number;
  /** 字典类型（S=系统字典,U=用户字典） */
  type?: string;
}

/** DictVO */
export interface DictVO {
  /** 创建人 */
  createBy?: string;
  /** 创建时间 */
  createTime?: string;
  /** 字典键 */
  dictKey?: string;
  /** 字典编号 */
  id?: string;
  /** 字典名称 */
  name?: string;
  /** 备注 */
  remark?: string;
  /** 排序 */
  sort?: number;
  /** 字典类型（S=系统字典,U=用户字典） */
  type?: string;
  /** 更新人 */
  updateBy?: string;
  /** 更新时间 */
  updateTime?: string;
  /** 值列表 */
  valueList?: DictValueVO[];
}

/** DictValue */
export interface DictValue {
  /** 颜色 */
  color?: string;
  /** 所属字典 */
  dict?: Dict;
  /** 关联字典id */
  dictId?: string;
  /** 主键id */
  id?: string;
  /** 值名称 */
  name?: string;
  /** 备注 */
  remark?: string;
  /** 排序 */
  sort?: number;
  /** 类型 */
  type?: string;
  /** 值 */
  value?: string;
  /** 值键 */
  valueKey?: string;
}

/** DictValueAddDTO */
export interface DictValueAddDTO {
  /** 颜色 */
  color?: string;
  /** 关联字典id */
  dictId?: string;
  /** 值名称 */
  name?: string;
  /** 备注 */
  remark?: string;
  /** 排序 */
  sort?: number;
  /** 类型（S=成功,I=信息,W=警告,E=错误） */
  type?: string;
  /** 值 */
  value?: string;
  /** 值键 */
  valueKey?: string;
}

/** DictValueUpdateDTO */
export interface DictValueUpdateDTO {
  /** 颜色 */
  color?: string;
  /** 字典值编号；更新时必填 */
  id?: string;
  /** 值名称 */
  name?: string;
  /** 备注 */
  remark?: string;
  /** 排序 */
  sort?: number;
  /** 类型（S=成功,I=信息,W=警告,E=错误） */
  type?: string;
  /** 值 */
  value?: string;
  /** 值键 */
  valueKey?: string;
}

/** DictValueVO */
export interface DictValueVO {
  /** 颜色 */
  color?: string;
  /** 关联字典id */
  dictId?: string;
  /** 主键id */
  id?: string;
  /** 值名称 */
  name?: string;
  /** 备注 */
  remark?: string;
  /** 排序 */
  sort?: number;
  /** 类型 */
  type?: string;
  /** 值 */
  value?: string;
  /** 值键 */
  valueKey?: string;
}

/** LoginDTO */
export interface LoginDTO {
  /** 验证码 */
  captcha?: string;
  /** 验证码id */
  captchaId?: string;
  /** 邮箱地址 */
  email?: string;
  /** 密码 */
  password?: string;
  /** 手机号码 */
  phone?: string;
  /** 用户名 */
  username?: string;
}

/** LoginUser */
export interface LoginUser {
  /** 部门id */
  deptId?: string;
  /** 登录设备 */
  device?: string;
  /** 登录ip */
  ip?: string;
  /** 登录时间 */
  loginTime?: string;
  /** 租户id */
  tenantId?: string;
  /** 令牌 */
  token?: string;
  /** 用户id */
  userId?: string;
  /** 用户名 */
  userName?: string;
}

/** Notification */
export interface Notification {
  /** 通知内容 */
  content?: string;
  /** 创建人 */
  createBy?: string;
  /** 创建时间 */
  createTime?: string;
  /** 过期时间 */
  expiredTime?: string;
  /** 通知编号 */
  id?: string;
  /** 发布时间 */
  publishTime?: string;
  /** 阅读列表 */
  readList?: NotificationRead[];
  /** 通知目标id */
  targetId?: string;
  /** 通知标题 */
  title?: string;
  /** 通知类型 */
  type?: string;
  /** 更新人 */
  updateBy?: string;
  /** 更新时间 */
  updateTime?: string;
}

/** NotificationRead */
export interface NotificationRead {
  /** 主键id */
  id?: string;
  /** 关联通知 */
  notification?: Notification;
  /** 通知编号 */
  notificationId?: string;
  /** 阅读时间 */
  readTime?: string;
  /** 关联用户 */
  user?: User;
  /** 用户编号 */
  userId?: string;
}

/** ParamAddDTO */
export interface ParamAddDTO {
  /** 参数名称 */
  name?: string;
  /** 参数键 */
  paramKey?: string;
  /** 备注 */
  remark?: string;
  /** 排序 */
  sort?: number;
  /** 参数类型（S=系统参数,U=用户参数） */
  type?: string;
  /** 参数值 */
  value?: string;
}

/** ParamQueryDTO */
export interface ParamQueryDTO {
  /** 参数名称 */
  name?: string;
  /** 参数键 */
  paramKey?: string;
  /** 参数类型 */
  type?: string;
}

/** ParamUpdateDTO */
export interface ParamUpdateDTO {
  /** 参数编号；更新时必填 */
  id?: string;
  /** 参数名称 */
  name?: string;
  /** 参数键 */
  paramKey?: string;
  /** 备注 */
  remark?: string;
  /** 排序 */
  sort?: number;
  /** 参数类型（S=系统参数,U=用户参数） */
  type?: string;
  /** 参数值 */
  value?: string;
}

/** ParamVO */
export interface ParamVO {
  /** 创建人 */
  createBy?: string;
  /** 创建时间 */
  createTime?: string;
  /** 参数编号 */
  id?: string;
  /** 参数名称 */
  name?: string;
  /** 参数键 */
  paramKey?: string;
  /** 备注 */
  remark?: string;
  /** 排序 */
  sort?: number;
  /** 参数类型（S=系统参数,U=用户参数） */
  type?: string;
  /** 更新人 */
  updateBy?: string;
  /** 更新时间 */
  updateTime?: string;
  /** 参数值 */
  value?: string;
}

/** Permission */
export interface Permission {
  /** 子权限列表 */
  children?: Permission[];
  /** 组件 */
  component?: string;
  /** 创建人 */
  createBy?: string;
  /** 创建时间 */
  createTime?: string;
  /** 数据权限列表 */
  dataPermissionList?: DataPermission[];
  /** 主键id */
  id?: string;
  /** 名称 */
  name?: string;
  /** 父级权限 */
  parent?: Permission;
  /** 父级权限 */
  parentId?: string;
  /** 权限 */
  permission?: string;
  roleList?: Role[];
  /** 排序 */
  sort?: number;
  /** 状态 */
  status?: string;
  /** 权限类型 */
  type?: string;
  /** 更新人 */
  updateBy?: string;
  /** 更新时间 */
  updateTime?: string;
}

/** PermissionAddDTO */
export interface PermissionAddDTO {
  /** 组件 */
  component?: string;
  /** 名称 */
  name?: string;
  /** 父级权限编号；为空表示顶级权限 */
  parentId?: string;
  /** 权限（用 . 作为分隔符） */
  permission?: string;
  /** 排序 */
  sort?: number;
  /** 状态（E=启用,D=禁用） */
  status?: string;
  /** 权限类型（D=目录,M=菜单,B=按钮） */
  type?: string;
}

/** PermissionQueryDTO */
export interface PermissionQueryDTO {
  /** 组件 */
  component?: string;
  /** 主键id */
  id?: string;
  /** 名称 */
  name?: string;
  /** 父级权限 */
  parentId?: string;
  /** 权限 */
  permission?: string;
  /** 状态 */
  status?: string;
  /** 权限类型 */
  type?: string;
}

/** PermissionUpdateDTO */
export interface PermissionUpdateDTO {
  /** 组件 */
  component?: string;
  /** 权限编号；更新时必填 */
  id?: string;
  /** 名称 */
  name?: string;
  /** 父级权限编号；为空表示顶级权限 */
  parentId?: string;
  /** 权限（用 . 作为分隔符） */
  permission?: string;
  /** 排序 */
  sort?: number;
  /** 状态（E=启用,D=禁用） */
  status?: string;
  /** 权限类型（D=目录,M=菜单,B=按钮） */
  type?: string;
}

/** Role */
export interface Role {
  /** 角色代码 */
  code?: string;
  /** 创建人 */
  createBy?: string;
  /** 创建时间 */
  createTime?: string;
  /** 数据权限列表 */
  dataPermissionList?: DataPermission[];
  /** 主键id */
  id?: string;
  /** 角色名 */
  name?: string;
  /** 角色权限列表 */
  permissionList?: Permission[];
  /** 排序 */
  sort?: number;
  /** 状态 */
  status?: string;
  /** 所属租户 */
  tenant?: Tenant;
  /** 所属租户id */
  tenantId?: string;
  /** 更新人 */
  updateBy?: string;
  /** 更新时间 */
  updateTime?: string;
  /** 用户列表 */
  userList?: User[];
}

/** RoleAddDTO */
export interface RoleAddDTO {
  /** 角色代码 */
  code?: string;
  /** 角色名 */
  name?: string;
  /** 排序 */
  sort?: number;
  /** 状态（E=启用,D=禁用） */
  status?: string;
}

/** RoleUpdateDTO */
export interface RoleUpdateDTO {
  /** 角色代码 */
  code?: string;
  /** 角色编号；更新时必填 */
  id?: string;
  /** 角色名 */
  name?: string;
  /** 排序 */
  sort?: number;
  /** 状态（E=启用,D=禁用） */
  status?: string;
}

/** Tenant */
export interface Tenant {
  /** 创建人 */
  createBy?: string;
  /** 创建时间 */
  createTime?: string;
  /** 删除时间 */
  deleteTime?: string;
  /** 租户部门列表 */
  deptList?: Dept[];
  /** 租户编号 */
  id?: string;
  /** 租户名称 */
  name?: string;
  /** 租户通知列表 */
  notificationList?: Notification[];
  /** 租户角色列表 */
  roleList?: Role[];
  /** 状态 */
  status?: string;
  /** 租户类型 */
  type?: string;
  /** 更新人 */
  updateBy?: string;
  /** 更新时间 */
  updateTime?: string;
  /** 租户用户列表 */
  userList?: User[];
}

/** TenantAddDTO */
export interface TenantAddDTO {
  /** 租户名称 */
  name?: string;
  /** 状态（E=启用,D=禁用） */
  status?: string;
  /** 租户类型（S=系统租户,U=用户租户,T=租户模板） */
  type?: string;
}

/** TenantQueryDTO */
export interface TenantQueryDTO {
  /** 租户名称 */
  name?: string;
  /** 状态 */
  status?: string;
  /** 租户类型 */
  type?: string;
}

/** TenantUpdateDTO */
export interface TenantUpdateDTO {
  /** 租户编号；更新时必填 */
  id?: string;
  /** 租户名称 */
  name?: string;
  /** 状态（E=启用,D=禁用） */
  status?: string;
  /** 租户类型（S=系统租户,U=用户租户,T=租户模板） */
  type?: string;
}

/** TenantVO */
export interface TenantVO {
  /** 创建人 */
  createBy?: string;
  /** 创建时间 */
  createTime?: string;
  /** 租户编号 */
  id?: string;
  /** 租户名称 */
  name?: string;
  /** 状态（E=启用,D=禁用） */
  status?: string;
  /** 租户类型（S=系统租户,U=用户租户,T=租户模板） */
  type?: string;
  /** 更新人 */
  updateBy?: string;
  /** 更新时间 */
  updateTime?: string;
}

/** UploadFile */
export interface UploadFile {
  contentType?: string;
  createBy?: string;
  createTime?: string;
  fileName?: string;
  fileSize?: number;
  hash?: string;
  id?: string;
  path?: string;
}

/** UploadTaskCreateDTO */
export interface UploadTaskCreateDTO {
  /** 文件类型 */
  contentType?: string;
  /** 文件哈希（SHA-256 hex） */
  fileHash?: string;
  /** 文件名称 */
  fileName?: string;
  /** 文件大小（字节） */
  fileSize?: number;
  /** 每个分片的文件哈希（SHA-256 hex），数量需与分片数一致 */
  pieceHashes?: string[];
  /** 每个分片的文件大小（字节），最后一片可能不足 */
  pieceSize?: number;
}

/** UploadTaskCreateVO */
export interface UploadTaskCreateVO {
  /** 分片数量 */
  pieceCount?: number;
  /** 分片大小（字节） */
  pieceSize?: number;
  /** 上传任务 id */
  taskId?: string;
}

/** UploadTaskFinishDTO */
export interface UploadTaskFinishDTO {
  /** 上传任务 id */
  taskId?: string;
}

/** User */
export interface User {
  /** 创建人 */
  createBy?: string;
  /** 创建时间 */
  createTime?: string;
  /** 删除时间 */
  deleteTime?: string;
  /** 所属部门 */
  dept?: Dept;
  /** 所属部门id */
  deptId?: string;
  /** 邮箱 */
  email?: string;
  /** 用户通知列表 */
  notificationList?: Notification[];
  /** 通知阅读记录列表 */
  notificationReadList?: NotificationRead[];
  /** 手机号 */
  phone?: string;
  /** 真实姓名 */
  realName?: string;
  /** 用户角色列表 */
  roleList?: Role[];
  /** 性别 */
  sex?: string;
  /** 状态 */
  status?: string;
  /** 所属租户 */
  tenant?: Tenant;
  /** 所属租户id */
  tenantId?: string;
  /** 更新人 */
  updateBy?: string;
  /** 更新时间 */
  updateTime?: string;
  /** 主键id */
  userId?: string;
  /** 用户名 */
  userName?: string;
}

/** UserAddDTO */
export interface UserAddDTO {
  /** 所属部门id */
  deptId?: string;
  /** 邮箱 */
  email?: string;
  /** 密码 */
  password?: string;
  /** 手机号 */
  phone?: string;
  /** 真实姓名 */
  realName?: string;
  /** 性别（M=男,F=女,U=未知） */
  sex?: string;
  /** 状态（E=启用,D=禁用） */
  status?: string;
  /** 所属租户id */
  tenantId?: string;
  /** 用户名 */
  userName?: string;
}

/** UserQueryDTO */
export interface UserQueryDTO {
  /** 部门id */
  deptId?: string;
  /** email */
  email?: string;
  /** 手机号码 */
  phone?: string;
  /** 姓名 */
  realName?: string;
  /** 性别 */
  sex?: string;
  /** 状态 */
  status?: string;
  /** 租户id */
  tenantId?: string;
  /** 用户id */
  userId?: string;
  /** 用户名 */
  userName?: string;
}

/** UserResetPasswordDTO */
export interface UserResetPasswordDTO {
  /** 新密码 */
  password?: string;
  /** 用户id */
  userId?: string;
}

/** UserUpdateDTO */
export interface UserUpdateDTO {
  /** 所属部门id */
  deptId?: string;
  /** 邮箱 */
  email?: string;
  /** 密码 */
  password?: string;
  /** 手机号 */
  phone?: string;
  /** 真实姓名 */
  realName?: string;
  /** 性别（M=男,F=女,U=未知） */
  sex?: string;
  /** 状态（E=启用,D=禁用） */
  status?: string;
  /** 所属租户id */
  tenantId?: string;
  /** 用户编号 */
  userId?: string;
  /** 用户名 */
  userName?: string;
}

/** UserUpdateMyPasswordDTO */
export interface UserUpdateMyPasswordDTO {
  /** 旧密码 */
  oldPassword?: string;
  /** 新密码 */
  password?: string;
}

/** UserVO */
export interface UserVO {
  /** 创建人 */
  createBy?: string;
  /** 创建时间 */
  createTime?: string;
  /** 所属部门id */
  deptId?: string;
  /** 邮箱 */
  email?: string;
  /** 手机号 */
  phone?: string;
  /** 真实姓名 */
  realName?: string;
  /** 性别（M=男,F=女,U=未知） */
  sex?: string;
  /** 状态（E=启用,D=禁用） */
  status?: string;
  /** 所属租户id */
  tenantId?: string;
  /** 更新人 */
  updateBy?: string;
  /** 更新时间 */
  updateTime?: string;
  /** 用户编号 */
  userId?: string;
  /** 用户名 */
  userName?: string;
}

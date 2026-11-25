import { defineGet, definePagePost, definePost, defineQuery } from "../define";
import type {
  BindRolesToUserDTO,
  IdDTO,
  LoginUser,
  PageParams,
  Role,
  UserAddDTO,
  UserQueryDTO,
  UserResetPasswordDTO,
  UserUpdateDTO,
  UserUpdateMyPasswordDTO,
  UserVO,
} from "../types";

/** 分页查询用户 */
export const queryUser = defineQuery<UserQueryDTO & PageParams, UserVO>("/api/sys/user/query");

/** 按编号查询用户 */
export const getUserByUserId = defineGet<{ userId: string }, UserVO>("/api/sys/user/getByUserId");

/** 按用户名查询用户 */
export const getUserByUserName = defineGet<{ userName: string }, UserVO>(
  "/api/sys/user/getByUserName",
);

/** 按邮箱查询用户 */
export const getUserByEmail = defineGet<{ email: string }, UserVO>("/api/sys/user/getByEmail");

/** 按手机号查询用户 */
export const getUserByPhone = defineGet<{ phone: string }, UserVO>("/api/sys/user/getByPhone");

/** 查询用户所有角色 */
export const listUserRoles = defineGet<{ userId: string }, Role[]>("/api/sys/user/listRoles");

/** 新增用户 */
export const addUser = definePost<UserAddDTO, unknown>("/api/sys/user/add");

/** 更新用户 */
export const updateUser = definePost<UserUpdateDTO, unknown>("/api/sys/user/update");

/** 删除用户 */
export const removeUser = definePost<IdDTO, unknown>("/api/sys/user/remove");

/** 重置密码 */
export const resetUserPassword = definePost<UserResetPasswordDTO, unknown>(
  "/api/sys/user/resetPassword",
);

/** 绑定角色到用户 */
export const bindUserRole = definePost<BindRolesToUserDTO, unknown>("/api/sys/user/bindRole");

/** 更新当前登录用户密码 */
export const updateMyPassword = definePost<UserUpdateMyPasswordDTO, unknown>(
  "/api/sys/user/updateMyPassword",
);

/** 分页查询在线用户 */
export const queryOnlineUser = definePagePost<UserQueryDTO & PageParams, LoginUser>(
  "/api/sys/user/queryLoginUser",
);

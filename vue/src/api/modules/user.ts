import { defineGet, definePagePost, definePost, defineQuery } from "@/api/define";
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
} from "@/api/types";

/** 分页查询用户 */
export const queryUser = defineQuery<UserQueryDTO & PageParams, UserVO>("/sys/user/query");

/** 按编号查询用户 */
export const getUserByUserId = defineGet<{ userId: string }, UserVO>("/sys/user/getByUserId");

/** 按用户名查询用户 */
export const getUserByUserName = defineGet<{ userName: string }, UserVO>("/sys/user/getByUserName");

/** 按邮箱查询用户 */
export const getUserByEmail = defineGet<{ email: string }, UserVO>("/sys/user/getByEmail");

/** 按手机号查询用户 */
export const getUserByPhone = defineGet<{ phone: string }, UserVO>("/sys/user/getByPhone");

/** 查询用户所有角色 */
export const listUserRoles = defineGet<{ userId: string }, Role[]>("/sys/user/listRoles");

/** 新增用户 */
export const addUser = definePost<UserAddDTO, unknown>("/sys/user/add");

/** 更新用户 */
export const updateUser = definePost<UserUpdateDTO, unknown>("/sys/user/update");

/** 删除用户 */
export const removeUser = definePost<IdDTO, unknown>("/sys/user/remove");

/** 重置密码 */
export const resetUserPassword = definePost<UserResetPasswordDTO, unknown>(
  "/sys/user/resetPassword",
);

/** 绑定角色到用户 */
export const bindUserRole = definePost<BindRolesToUserDTO, unknown>("/sys/user/bindRole");

/** 更新当前登录用户密码 */
export const updateMyPassword = definePost<UserUpdateMyPasswordDTO, unknown>(
  "/sys/user/updateMyPassword",
);

/** 分页查询在线用户 */
export const queryOnlineUser = definePagePost<UserQueryDTO & PageParams, LoginUser>(
  "/sys/user/queryLoginUser",
);

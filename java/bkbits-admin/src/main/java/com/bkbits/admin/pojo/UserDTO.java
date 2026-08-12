package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户输入参数。
 */
@Data
@ApiModel("用户输入参数")
public class UserDTO {

    @ApiModelProperty("用户编号；更新时必填")
    private String userId;

    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("盐值")
    private String salt;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("真实姓名")
    private String realName;

    @ApiModelProperty("性别（M=男,F=女,U=未知）")
    private String sex;

    @ApiModelProperty("状态（E=启用,D=禁用）")
    private String status;

    @ApiModelProperty("所属租户id")
    private String tenantId;

    @ApiModelProperty("所属部门id")
    private String deptId;
}

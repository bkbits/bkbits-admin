package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.noear.solon.validation.annotation.NotNull;

/**
 * 用户输入参数。
 */
@Data
@ApiModel("用户输入参数")
public class UserAddDTO {

    @ApiModelProperty("用户编号；更新时必填")
    @NotNull
    private String userId;

    @ApiModelProperty("用户名")
    @NotNull
    private String userName;

    @ApiModelProperty("密码")
    @NotNull
    private String password;

    @ApiModelProperty("邮箱")
    @NotNull
    private String email;

    @ApiModelProperty("手机号")
    @NotNull
    private String phone;

    @ApiModelProperty("真实姓名")
    @NotNull
    private String realName;

    @ApiModelProperty("性别（M=男,F=女,U=未知）")
    @NotNull
    private String sex;

    @ApiModelProperty("状态（E=启用,D=禁用）")
    @NotNull
    private String status;

    @ApiModelProperty("所属租户id")
    private String tenantId;

    @ApiModelProperty("所属部门id")
    private String deptId;
}

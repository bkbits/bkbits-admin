package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户输出参数。
 */
@Data
@ApiModel("用户输出参数")
public class UserVO {

    @ApiModelProperty("用户编号")
    private String userId;

    @ApiModelProperty("用户名")
    private String userName;

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

    @ApiModelProperty("创建人")
    private String createBy;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新人")
    private String updateBy;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;
}

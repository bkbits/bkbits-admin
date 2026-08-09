package com.bkbits.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@ApiModel("登录用户")
@Getter
@AllArgsConstructor
@Builder
public class LoginUser {
    @ApiModelProperty("令牌")
    private String token;

    @ApiModelProperty("用户id")
    private Object userId;

    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("登录时间")
    private LocalDateTime loginTime;

    @ApiModelProperty("登录ip")
    private String ip;

    @ApiModelProperty("登录设备")
    private String device;

    @ApiModelProperty("部门id")
    @Setter
    private Object deptId;

    @ApiModelProperty("租户id")
    @Setter
    private Object tenantId;
}

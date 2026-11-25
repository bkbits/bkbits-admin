package com.bkbits.auth;

import java.time.LocalDateTime;

import cn.dev33.satoken.json.SaJsonType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ApiModel("登录用户")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginUser implements SaJsonType {
    @ApiModelProperty("令牌")
    private String token;

    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("登录时间")
    private LocalDateTime loginTime;

    @ApiModelProperty("登录ip")
    private String ip;

    @ApiModelProperty("登录设备")
    private String device;

    @ApiModelProperty("部门id")
    private String deptId;
}

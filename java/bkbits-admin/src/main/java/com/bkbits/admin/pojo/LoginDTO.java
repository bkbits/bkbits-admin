package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.noear.solon.validation.annotation.NotNull;

@ApiModel("登录参数")
@Data
public class LoginDTO {
    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("手机号码")
    private String phone;

    @ApiModelProperty("邮箱地址")
    private String email;

    @ApiModelProperty("密码")
    @NotNull
    private String password;

    @ApiModelProperty("验证码")
    private String catpcha;

    @ApiModelProperty("验证码id")
    private String catpchaId;
}

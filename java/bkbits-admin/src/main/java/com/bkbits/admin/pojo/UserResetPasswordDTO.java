package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.noear.solon.validation.annotation.NotNull;

/**
 * 用户输入参数。
 */
@Data
@ApiModel("重置他人密码参数")
public class UserResetPasswordDTO {
    @ApiModelProperty("用户id")
    @NotNull
    private String userId;

    @ApiModelProperty("新密码")
    @NotNull
    private String password;
}

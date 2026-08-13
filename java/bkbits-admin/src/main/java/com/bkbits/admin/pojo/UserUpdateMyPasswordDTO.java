package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.noear.solon.validation.annotation.NotNull;

/**
 * 用户输入参数。
 */
@Data
@ApiModel("修改自身密码参数")
public class UserUpdateMyPasswordDTO {
    @ApiModelProperty("旧密码")
    @NotNull
    private String oldPassword;

    @ApiModelProperty("新密码")
    @NotNull
    private String password;
}

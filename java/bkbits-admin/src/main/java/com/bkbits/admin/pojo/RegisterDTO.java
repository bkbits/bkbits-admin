package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.noear.solon.validation.annotation.NotBlank;

/**
 * 注册参数。
 *
 * @easy-query-dto schema: request
 */
@ApiModel("注册参数")
@Data
public class RegisterDTO {
    @ApiModelProperty("用户名")
    @NotBlank
    private String username;

    @ApiModelProperty("密码（RSA 加密）")
    @NotBlank
    private String password;

    @ApiModelProperty("真实姓名")
    private String realName;

    @ApiModelProperty("邮箱")
    private String email;

    @ApiModelProperty("手机号")
    private String phone;
}

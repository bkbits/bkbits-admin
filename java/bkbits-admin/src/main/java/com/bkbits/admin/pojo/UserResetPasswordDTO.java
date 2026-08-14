package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.noear.solon.validation.annotation.NotBlank;

/**
 * 重置他人密码参数。
 * {@link com.bkbits.dbo.entity.User }
 *
 * @author lkq
 * @easy-query-dto schema: request
 */
@Data
@ApiModel("重置他人密码参数")
public class UserResetPasswordDTO {
    @ApiModelProperty("用户id")
    @NotBlank
    private String userId;

    @ApiModelProperty("新密码")
    @NotBlank
    private String password;
}

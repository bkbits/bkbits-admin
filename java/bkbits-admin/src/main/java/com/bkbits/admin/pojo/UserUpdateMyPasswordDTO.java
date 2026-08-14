package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.noear.solon.validation.annotation.NotBlank;

/**
 * 修改自身密码参数。
 * {@link com.bkbits.dbo.entity.User }
 *
 * @author lkq
 * @easy-query-dto schema: request
 */
@Data
@ApiModel("修改自身密码参数")
public class UserUpdateMyPasswordDTO {
    @SuppressWarnings("EasyQueryFieldMissMatch")
    @ApiModelProperty("旧密码")
    @NotBlank
    private String oldPassword;

    @ApiModelProperty("新密码")
    @NotBlank
    private String password;
}

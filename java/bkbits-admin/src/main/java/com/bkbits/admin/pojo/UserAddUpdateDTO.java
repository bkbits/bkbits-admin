package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.noear.solon.validation.annotation.NotBlank;

/**
 * 用户新增参数。
 * {@link com.bkbits.dbo.entity.User }
 *
 * @author lkq
 * @easy-query-dto schema: request
 */
@Data
@ApiModel("用户新增/更新参数")
public class UserAddUpdateDTO {
    @ApiModelProperty("用户名")
    @NotBlank
    private String userName;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("邮箱")
    @NotBlank
    private String email;

    @ApiModelProperty("手机号")
    @NotBlank
    private String phone;

    @ApiModelProperty("真实姓名")
    @NotBlank
    private String realName;

    @ApiModelProperty("性别（M=男,F=女,U=未知）")
    @NotBlank
    private String sex;

    @ApiModelProperty("状态（E=启用,D=禁用）")
    @NotBlank
    private String status;

    @ApiModelProperty("所属部门id")
    private String deptId;
}

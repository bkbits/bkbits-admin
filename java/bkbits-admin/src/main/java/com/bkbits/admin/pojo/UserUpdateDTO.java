package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.noear.solon.validation.annotation.NotBlank;

/**
 * 用户更新参数。
 * {@link com.bkbits.dbo.entity.User }
 *
 * @author lkq
 * @easy-query-dto schema: request
 */
@Data
@ApiModel("用户输入参数")
public class UserUpdateDTO {

    @ApiModelProperty("用户编号；更新时必填")
    @NotBlank
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
}

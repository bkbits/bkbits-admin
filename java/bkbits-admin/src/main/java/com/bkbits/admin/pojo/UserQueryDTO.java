package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("用户查询参数")
@Data
public class UserQueryDTO {
    @ApiModelProperty("租户id")
    private String tenantId;
    @ApiModelProperty("部门id")
    private String deptId;
    @ApiModelProperty("用户id")
    private String userId;

    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("姓名")
    private String realName;

    @ApiModelProperty("手机号码")
    private String phone;

    @ApiModelProperty("email")
    private String email;

    @ApiModelProperty("性别")
    private String sex;

    @ApiModelProperty("状态")
    private String status;
}

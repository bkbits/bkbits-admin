package com.bkbits.admin.pojo;

import com.easy.query.core.annotation.EasyWhereCondition;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户查询参数。
 * {@link com.bkbits.dbo.entity.User }
 *
 * @author lkq
 * @easy-query-dto schema: request
 */
@ApiModel("用户查询参数")
@Data
public class UserQueryDTO {
    @ApiModelProperty("租户id")
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String tenantId;

    @ApiModelProperty("部门id")
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String deptId;

    @ApiModelProperty("用户id")
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String userId;

    @ApiModelProperty("用户名")
    @EasyWhereCondition
    private String userName;

    @ApiModelProperty("姓名")
    @EasyWhereCondition
    private String realName;

    @ApiModelProperty("手机号码")
    @EasyWhereCondition
    private String phone;

    @ApiModelProperty("email")
    @EasyWhereCondition
    private String email;

    @ApiModelProperty("性别")
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String sex;

    @ApiModelProperty("状态")
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String status;
}

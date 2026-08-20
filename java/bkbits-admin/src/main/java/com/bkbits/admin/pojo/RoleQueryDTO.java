package com.bkbits.admin.pojo;

import com.easy.query.core.annotation.EasyWhereCondition;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 角色查询参数。
 * {@link com.bkbits.dbo.entity.Role }
 *
 * @easy-query-dto schema: request
 */
@Data
@ApiModel("角色查询参数")
public class RoleQueryDTO {

    @ApiModelProperty("角色代码")
    @EasyWhereCondition
    private String code;

    @ApiModelProperty("角色名")
    @EasyWhereCondition
    private String name;

    @ApiModelProperty("所属租户id")
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String tenantId;

    @ApiModelProperty("状态（E=启用,D=禁用）")
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String status;
}
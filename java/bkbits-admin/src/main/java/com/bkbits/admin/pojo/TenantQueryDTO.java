package com.bkbits.admin.pojo;

import com.easy.query.core.annotation.EasyWhereCondition;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 租户查询参数。
 * {@link com.bkbits.dbo.entity.Tenant }
 *
 * @author lkq
 * @easy-query-dto schema: request
 */
@ApiModel("租户查询参数")
@Data
public class TenantQueryDTO {

    @ApiModelProperty("租户名称")
    @EasyWhereCondition
    private String name;

    @ApiModelProperty(value = "租户类型", notes = "S=系统租户,U=用户租户,T=租户模板")
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String type;

    @ApiModelProperty(value = "状态", notes = "E=启用,D=禁用")
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String status;
}

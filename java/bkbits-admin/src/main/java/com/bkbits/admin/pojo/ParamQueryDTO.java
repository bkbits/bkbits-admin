package com.bkbits.admin.pojo;

import com.easy.query.core.annotation.EasyWhereCondition;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 系统参数查询参数。
 * {@link com.bkbits.dbo.entity.Param }
 *
 * @author lkq
 * @easy-query-dto schema: request
 */
@ApiModel("系统参数查询参数")
@Data
public class ParamQueryDTO {

    @ApiModelProperty("参数键")
    @EasyWhereCondition(propName = "paramKey")
    private String paramKey;

    @ApiModelProperty("参数名称")
    @EasyWhereCondition
    private String name;

    @ApiModelProperty(value = "参数类型", notes = "S=系统参数,U=用户参数")
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String type;
}

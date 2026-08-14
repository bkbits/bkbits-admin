package com.bkbits.admin.pojo;

import com.easy.query.core.annotation.EasyWhereCondition;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("字典查询参数")
@Data
public class DictQueryDTO {
    @ApiModelProperty("字典键")
    @EasyWhereCondition
    private String dictKey;

    @ApiModelProperty("字典名称")
    @EasyWhereCondition
    private String name;

    @ApiModelProperty(value = "字典类型", notes = "S=系统字典,U=用户字典")
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String type;
}

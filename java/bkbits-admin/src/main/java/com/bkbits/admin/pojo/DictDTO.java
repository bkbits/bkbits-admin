package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 系统字典输入参数。
 */
@Data
@ApiModel("系统字典输入参数")
public class DictDTO {

    @ApiModelProperty("字典编号；更新时必填")
    private String id;

    @ApiModelProperty("字典键")
    private String key;

    @ApiModelProperty("字典名称")
    private String name;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("字典类型（S=系统字典,U=用户字典）")
    private String type;

    @ApiModelProperty("备注")
    private String remark;
}

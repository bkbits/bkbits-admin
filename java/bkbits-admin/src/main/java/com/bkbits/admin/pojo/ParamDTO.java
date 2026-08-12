package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 系统参数输入参数。
 */
@Data
@ApiModel("系统参数输入参数")
public class ParamDTO {

    @ApiModelProperty("参数编号；更新时必填")
    private String id;

    @ApiModelProperty("参数键")
    private String key;

    @ApiModelProperty("参数名称")
    private String name;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("参数值")
    private String value;

    @ApiModelProperty("参数类型（S=系统参数,U=用户参数）")
    private String type;

    @ApiModelProperty("备注")
    private String remark;
}

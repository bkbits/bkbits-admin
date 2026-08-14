package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 字典值输出参数。
 * {@link com.bkbits.dbo.entity.DictValue }
 *
 * @author lkq
 * @easy-query-dto schema: response
 */
@Data
@ApiModel("字典值输出参数")
public class DictValueVO {
    @ApiModelProperty("主键id")
    private String id;

    @ApiModelProperty("关联字典id")
    private String dictId;

    @ApiModelProperty("值键")
    private String valueKey;

    @ApiModelProperty("值名称")
    private String name;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("值")
    private String value;

    @ApiModelProperty(value = "类型", notes = "S=成功,I=信息,W=警告,E=错误")
    private String type;

    @ApiModelProperty("颜色")
    private String color;

    @ApiModelProperty("备注")
    private String remark;
}

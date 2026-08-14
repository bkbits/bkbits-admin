package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 字典值输入参数。
 * {@link com.bkbits.dbo.entity.DictValue }
 *
 * @author lkq
 * @easy-query-dto schema: request
 */
@Data
@ApiModel("字典值输入参数")
public class DictValueAddDTO {
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

    @ApiModelProperty("类型（S=成功,I=信息,W=警告,E=错误）")
    private String type;

    @ApiModelProperty("颜色")
    private String color;

    @ApiModelProperty("备注")
    private String remark;
}

package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;

/**
 * 字典值新增参数。
 * {@link com.bkbits.dbo.entity.DictValue }
 *
 * @author lkq
 * @easy-query-dto schema: request
 */
@Data
@ApiModel("字典值新增参数")
public class DictValueAddDTO {
    @ApiModelProperty("关联字典id")
    @NotBlank
    private String dictId;

    @ApiModelProperty("值键")
    @NotBlank
    private String valueKey;

    @ApiModelProperty("值名称")
    @NotBlank
    private String name;

    @ApiModelProperty("排序")
    @NotNull
    private Integer sort;

    @ApiModelProperty("值")
    @NotNull
    private String value;

    @ApiModelProperty("类型（S=成功,I=信息,W=警告,E=错误）")
    @NotBlank
    private String type;

    @ApiModelProperty("颜色")
    private String color;

    @ApiModelProperty("备注")
    private String remark;
}

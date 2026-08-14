package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.noear.solon.validation.annotation.NotEmpty;
import org.noear.solon.validation.annotation.NotNull;

/**
 * 系统字典输入参数。
 * {@link com.bkbits.dbo.entity.Dict }
 *
 * @author lkq
 * @easy-query-dto schema: request
 */
@Data
@ApiModel("系统字典输入参数")
public class DictAddDTO {
    @ApiModelProperty("字典键")
    @NotEmpty
    private String dictKey;

    @ApiModelProperty("字典名称")
    @NotEmpty
    private String name;

    @ApiModelProperty("排序")
    @NotNull
    private Integer sort;

    @ApiModelProperty("字典类型（S=系统字典,U=用户字典）")
    @NotEmpty
    private String type;

    @ApiModelProperty("备注")
    @NotEmpty
    private String remark;
}

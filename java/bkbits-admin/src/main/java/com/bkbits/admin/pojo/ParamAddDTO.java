package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;

/**
 * 系统参数新增参数。
 * {@link com.bkbits.dbo.entity.Param }
 *
 * @author lkq
 * @easy-query-dto schema: request
 */
@Data
@ApiModel("系统参数新增参数")
public class ParamAddDTO {

    @ApiModelProperty("参数键")
    private String paramKey;

    @ApiModelProperty("参数名称")
    @NotBlank
    private String name;

    @ApiModelProperty("排序")
    @NotNull
    private Integer sort;

    @ApiModelProperty("参数值")
    @NotBlank
    private String value;

    @ApiModelProperty("参数类型（S=系统参数,U=用户参数）")
    @NotBlank
    private String type;

    @ApiModelProperty("备注")
    private String remark;
}

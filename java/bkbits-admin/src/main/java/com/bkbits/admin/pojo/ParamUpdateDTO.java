package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.noear.solon.validation.annotation.NotBlank;

/**
 * 系统参数更新参数。
 * {@link com.bkbits.dbo.entity.Param }
 *
 * @author lkq
 * @easy-query-dto schema: request
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("系统参数更新参数")
public class ParamUpdateDTO extends ParamAddDTO {

    @ApiModelProperty("参数编号；更新时必填")
    @NotBlank
    private String id;
}

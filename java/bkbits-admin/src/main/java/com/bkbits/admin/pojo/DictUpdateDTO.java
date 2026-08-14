package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.noear.solon.validation.annotation.NotNull;

/**
 * 系统字典输入参数。
 * {@link com.bkbits.dbo.entity.Dict }
 *
 * @author lkq
 * @easy-query-dto schema: request
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel("系统字典输入参数")
public class DictUpdateDTO extends DictAddDTO {
    @ApiModelProperty("字典id")
    @NotNull
    private String id;
}

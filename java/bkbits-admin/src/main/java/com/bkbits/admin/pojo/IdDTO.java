package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.noear.solon.validation.annotation.NotNull;

/**
 * 通用编号参数。
 */
@Data
@ApiModel("通用编号参数")
public class IdDTO {

    @ApiModelProperty("编号")
    @NotNull
    private String id;
}

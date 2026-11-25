package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 数据域。
 *
 * @easy-query-dto schema: response
 */
@Data
@ApiModel("数据域")
public class DataScopeVO {

    @ApiModelProperty("数据域名称")
    private String name;

    @ApiModelProperty("数据域标识")
    private String dataScope;
}

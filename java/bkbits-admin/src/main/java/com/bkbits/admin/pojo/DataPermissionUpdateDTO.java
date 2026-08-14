package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.noear.solon.validation.annotation.NotBlank;

/**
 * 数据权限更新参数。
 * {@link com.bkbits.dbo.entity.DataPermission }
 *
 * @author lkq
 * @easy-query-dto schema: request
 */
@Data
@ApiModel("数据权限更新参数")
public class DataPermissionUpdateDTO {
    @ApiModelProperty("数据权限编号；更新时必填")
    @NotBlank
    private String id;

    @ApiModelProperty("数据域")
    @NotBlank
    private String dataScope;

    @ApiModelProperty(value = "状态", notes = "（E=启用,D=禁用）")
    @NotBlank
    private String status;
}

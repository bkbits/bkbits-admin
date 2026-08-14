package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.noear.solon.validation.annotation.NotBlank;

/**
 * 数据权限新增参数。
 * {@link com.bkbits.dbo.entity.DataPermission }
 *
 * @author lkq
 * @easy-query-dto schema: request
 */
@Data
@ApiModel("数据权限新增参数")
public class DataPermissionAddDTO {

    @ApiModelProperty(value = "菜单权限编号", notes = "新增时必填，将写入数据权限的关联权限id")
    @NotBlank
    private String permissionId;

    @ApiModelProperty("数据域")
    @NotBlank
    private String dataScope;

    @ApiModelProperty(value = "状态", notes = "（E=启用,D=禁用）")
    @NotBlank
    private String status;
}

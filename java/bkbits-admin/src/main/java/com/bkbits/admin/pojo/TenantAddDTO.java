package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.noear.solon.validation.annotation.NotBlank;

/**
 * 租户新增参数。
 * {@link com.bkbits.dbo.entity.Tenant }
 *
 * @author lkq
 * @easy-query-dto schema: request
 */
@Data
@ApiModel("租户新增参数")
public class TenantAddDTO {

    @ApiModelProperty("租户类型（S=系统租户,U=用户租户,T=租户模板）")
    @NotBlank
    private String type;

    @ApiModelProperty("租户名称")
    @NotBlank
    private String name;

    @ApiModelProperty("状态（E=启用,D=禁用）")
    @NotBlank
    private String status;
}

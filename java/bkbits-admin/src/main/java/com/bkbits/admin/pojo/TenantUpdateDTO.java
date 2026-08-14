package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.noear.solon.validation.annotation.NotBlank;

/**
 * 租户更新参数。
 * {@link com.bkbits.dbo.entity.Tenant }
 *
 * @author lkq
 * @easy-query-dto schema: request
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("租户更新参数")
public class TenantUpdateDTO extends TenantAddDTO {

    @ApiModelProperty("租户编号；更新时必填")
    @NotBlank
    private String id;
}

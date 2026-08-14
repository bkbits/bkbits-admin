package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.noear.solon.validation.annotation.NotBlank;

/**
 * 数据权限更新参数。
 * {@link com.bkbits.dbo.entity.DataPermission }
 *
 * @author lkq
 * @easy-query-dto schema: request
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("数据权限更新参数")
public class DataPermissionUpdateDTO extends DataPermissionAddDTO {
    @ApiModelProperty("数据权限编号；更新时必填")
    @NotBlank
    private String id;
}

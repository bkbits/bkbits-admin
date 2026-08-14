package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.noear.solon.validation.annotation.NotBlank;
import org.noear.solon.validation.annotation.NotNull;

/**
 * 角色新增参数。
 * {@link com.bkbits.dbo.entity.Role }
 *
 * @author lkq
 * @easy-query-dto schema: request
 */
@Data
@ApiModel("角色新增参数")
public class RoleAddDTO {
    @ApiModelProperty("角色代码")
    @NotBlank
    private String code;

    @ApiModelProperty("角色名")
    @NotBlank
    private String name;

    @ApiModelProperty("排序")
    @NotNull
    private Integer sort;

    @ApiModelProperty("状态（E=启用,D=禁用）")
    @NotBlank
    private String status;
}

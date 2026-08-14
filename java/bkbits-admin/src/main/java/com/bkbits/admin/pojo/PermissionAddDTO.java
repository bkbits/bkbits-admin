package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.noear.solon.validation.annotation.NotBlank;

/**
 * 权限新增参数。
 * {@link com.bkbits.dbo.entity.Permission }
 *
 * @author lkq
 * @easy-query-dto schema: request
 */
@Data
@ApiModel("权限新增参数")
public class PermissionAddDTO {

    @ApiModelProperty("父级权限编号；为空表示顶级权限")
    private String parentId;

    @ApiModelProperty("权限类型（D=目录,M=菜单,B=按钮）")
    @NotBlank
    private String type;

    @ApiModelProperty("权限（用 . 作为分隔符）")
    @NotBlank
    private String permission;

    @ApiModelProperty("名称")
    @NotBlank
    private String name;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("组件")
    private String component;

    @ApiModelProperty("状态（E=启用,D=禁用）")
    @NotBlank
    private String status;
}

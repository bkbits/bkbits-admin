package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.noear.solon.validation.annotation.NotEmpty;
import org.noear.solon.validation.annotation.NotNull;

/**
 * 部门输入参数。
 */
@Data
@ApiModel("部门输入参数")
public class DeptAddDTO {
    @ApiModelProperty("父级部门编号；为空表示顶级部门")
    private String parentId;

    @ApiModelProperty("部门名称")
    @NotEmpty
    private String name;

    @ApiModelProperty("排序")
    @NotNull
    private Integer sort;

    @ApiModelProperty("状态（E=启用,D=禁用）")
    @NotEmpty
    private String status;
}

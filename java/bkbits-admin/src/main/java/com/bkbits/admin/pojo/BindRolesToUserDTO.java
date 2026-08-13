package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

/**
 * 用户绑定角色参数。
 */
@Data
@ApiModel("用户绑定角色参数")
public class BindRolesToUserDTO {
    @ApiModelProperty("用户编号")
    @NotNull
    private String userId;

    @ApiModelProperty("角色编号集合；为空时清空绑定")
    private List<String> roleIds = Collections.emptyList();
}

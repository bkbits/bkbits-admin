package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.noear.solon.validation.annotation.NotBlank;

/**
 * 用户更新参数。
 * {@link com.bkbits.dbo.entity.User }
 *
 * @author lkq
 * @easy-query-dto schema: request
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("用户输入参数")
public class UserUpdateDTO extends UserAddDTO {
    @ApiModelProperty("用户编号")
    @NotBlank
    private String userId;
}

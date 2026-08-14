package com.bkbits.admin.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门更新参数。
 * {@link com.bkbits.dbo.entity.Dept }
 *
 * @author lkq
 * @easy-query-dto schema: request
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel("部门输入参数")
public class DeptUpdateDTO extends DeptAddDTO {
    @ApiModelProperty("部门编号；更新时必填")
    private String deptId;
}

package com.bkbits.admin.pojo;


import com.easy.query.core.annotation.EasyWhereCondition;
import lombok.Data;
import com.easy.query.core.annotation.Column;
import com.easy.query.core.annotation.Navigate;
import com.easy.query.core.enums.RelationTypeEnum;
import com.easy.query.core.enums.CascadeTypeEnum;
import com.bkbits.util.CollectionUtil;

import java.time.LocalDateTime;

import com.bkbits.orm.IGenId;
import com.bkbits.dbo.filter.NotificationTargetFilterStrategy;

import java.util.List;

import lombok.Data;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.bkbits.orm.ICreateBy;
import com.bkbits.orm.IUpdateBy;

/**
 * 部门查询参数。
 * {@link com.bkbits.dbo.entity.Dept }
 *
 * @author lkq
 * @easy-query-dto schema: request
 */
@ApiModel("部门查询参数")
@Data
public class DeptQueryDTO {
    @ApiModelProperty("部门编号")
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String deptId;

    @ApiModelProperty(value = "父级部门编号", notes = "为空表示顶级部门")
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String parentId;

    @ApiModelProperty("所属租户id")
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String tenantId;

    @ApiModelProperty("部门名称")
    @EasyWhereCondition
    private String name;

    @ApiModelProperty(value = "状态", notes = "E=启用,D=禁用")
    @EasyWhereCondition(type = EasyWhereCondition.Condition.EQUAL)
    private String status;


}

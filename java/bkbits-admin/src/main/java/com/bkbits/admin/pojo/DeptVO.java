package com.bkbits.admin.pojo;

import com.easy.query.core.annotation.Navigate;
import com.easy.query.core.enums.RelationTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 部门输出参数。
 * {@link com.bkbits.dbo.entity.Dept }
 *
 * @author lkq
 * @easy-query-dto schema: response
 */
@Data
@ApiModel("部门输出参数")
public class DeptVO {

    @ApiModelProperty("部门编号")
    private String deptId;

    @ApiModelProperty("父级部门编号；为空表示顶级部门")
    private String parentId;

    @ApiModelProperty("所属租户id")
    private String tenantId;

    @ApiModelProperty("部门名称")
    private String name;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("状态（E=启用,D=禁用）")
    private String status;

    @ApiModelProperty("创建人")
    private String createBy;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新人")
    private String updateBy;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @Navigate(value = RelationTypeEnum.OneToMany)
    @ApiModelProperty("子部门列表")
    private List<DeptVO> children;
}

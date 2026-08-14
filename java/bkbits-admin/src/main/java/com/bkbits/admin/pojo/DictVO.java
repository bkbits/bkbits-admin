package com.bkbits.admin.pojo;

import com.easy.query.core.annotation.Navigate;
import com.easy.query.core.enums.RelationTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统字典输出参数。
 * {@link com.bkbits.dbo.entity.Dict }
 *
 * @author lkq
 * @easy-query-dto schema: response
 */
@Data
@ApiModel("系统字典输出参数")
public class DictVO {

    @ApiModelProperty("字典编号")
    private String id;

    @ApiModelProperty("字典键")
    private String dictKey;

    @ApiModelProperty("字典名称")
    private String name;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("字典类型（S=系统字典,U=用户字典）")
    private String type;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("创建人")
    private String createBy;

    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @ApiModelProperty("更新人")
    private String updateBy;

    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @Navigate(value = RelationTypeEnum.OneToMany)
    @ApiModelProperty("值列表")
    private List<DictValueVO> valueList;
}

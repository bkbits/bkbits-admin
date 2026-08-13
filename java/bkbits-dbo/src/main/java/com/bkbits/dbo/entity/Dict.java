package com.bkbits.dbo.entity;

import com.bkbits.dbo.entity.proxy.DictProxy;
import com.bkbits.orm.ICreateBy;
import com.bkbits.orm.IGenId;
import com.bkbits.orm.IUpdateBy;
import com.easy.query.core.annotation.Column;
import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Navigate;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.enums.CascadeTypeEnum;
import com.easy.query.core.enums.RelationTypeEnum;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;
import java.util.List;

@ApiModel("系统字典")
@Data
@FieldNameConstants
@Table
@EntityProxy
public class Dict implements IGenId, ICreateBy, IUpdateBy, ProxyEntityAvailable<Dict, DictProxy> {

    @ApiModelProperty("主键id")
    @Column(primaryKey = true)
    private String id;

    @ApiModelProperty("字典键")
    private String dictKey;

    @ApiModelProperty("字典名称")
    private String name;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty(value = "字典类型", notes = "S=系统字典,U=用户字典")
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

    @ApiModelProperty("值列表")
    @Navigate(
            value = RelationTypeEnum.OneToMany,
            selfProperty = Dict.Fields.id,
            targetProperty = DictValue.Fields.dictId,
            cascade = CascadeTypeEnum.DELETE
    )
    private List<DictValue> valueList;
}

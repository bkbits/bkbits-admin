package com.bkbits.dbo.entity;

import com.bkbits.dbo.entity.proxy.DictValueProxy;
import com.bkbits.orm.IGenId;
import com.easy.query.core.annotation.Column;
import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Navigate;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.enums.RelationTypeEnum;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

@ApiModel("系统字典值")
@Data
@FieldNameConstants
@Table
@EntityProxy
public class DictValue implements IGenId, ProxyEntityAvailable<DictValue, DictValueProxy> {

    @ApiModelProperty("主键id")
    @Column(primaryKey = true)
    private String id;

    @ApiModelProperty("关联字典id")
    private String dictId;

    @ApiModelProperty("值键")
    private String valueKey;

    @ApiModelProperty("值名称")
    private String name;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("值")
    private String value;

    @ApiModelProperty(value = "类型", notes = "S=成功,I=信息,W=警告,E=错误")
    private String type;

    @ApiModelProperty("颜色")
    private String color;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("所属字典")
    @Navigate(
            value = RelationTypeEnum.ManyToOne,
            selfProperty = DictValue.Fields.dictId,
            targetProperty = Dict.Fields.id
    )
    private Dict dict;
}

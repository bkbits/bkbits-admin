package com.bkbits.dbo.entity;

import com.bkbits.dbo.entity.proxy.ParamProxy;
import com.bkbits.orm.ICreateBy;
import com.bkbits.orm.IGenId;
import com.bkbits.orm.IUpdateBy;
import com.easy.query.core.annotation.Column;
import com.easy.query.core.annotation.EntityProxy;
import com.easy.query.core.annotation.Table;
import com.easy.query.core.proxy.ProxyEntityAvailable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;

@ApiModel("系统参数")
@Data
@FieldNameConstants
@Table
@EntityProxy
public class Param implements IGenId, ICreateBy, IUpdateBy, ProxyEntityAvailable<Param, ParamProxy> {

    @ApiModelProperty("主键id")
    @Column(primaryKey = true)
    private String id;

    @ApiModelProperty("参数键")
    private String paramKey;

    @ApiModelProperty("参数名称")
    private String name;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("参数值")
    private String value;

    @ApiModelProperty(value = "参数类型", notes = "S=系统参数,U=用户参数")
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
}

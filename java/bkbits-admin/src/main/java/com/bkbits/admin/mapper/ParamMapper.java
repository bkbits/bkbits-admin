package com.bkbits.admin.mapper;

import com.bkbits.admin.pojo.ParamDTO;
import com.bkbits.admin.pojo.ParamVO;
import com.bkbits.dbo.entity.Param;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 系统参数对象转换。
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ParamMapper {

    /** 内置静态单例 */
    ParamMapper INSTANCE = Mappers.getMapper(ParamMapper.class);

    /**
     * 输入参数转实体。
     *
     * @param dto 输入参数
     * @return 系统参数实体
     */
    Param toEntity(ParamDTO dto);

    /**
     * 实体转输出参数。
     *
     * @param entity 系统参数实体
     * @return 输出参数
     */
    ParamVO toVO(Param entity);

    /**
     * 实体列表转输出参数列表。
     *
     * @param entities 系统参数实体列表
     * @return 输出参数列表
     */
    List<ParamVO> toVOList(List<Param> entities);
}

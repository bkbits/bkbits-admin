package com.bkbits.admin.mapper;

import com.bkbits.admin.pojo.DictDTO;
import com.bkbits.admin.pojo.DictValueDTO;
import com.bkbits.admin.pojo.DictValueVO;
import com.bkbits.admin.pojo.DictVO;
import com.bkbits.dbo.entity.Dict;
import com.bkbits.dbo.entity.DictValue;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 系统字典对象转换。
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DictMapper {

    /** 内置静态单例 */
    DictMapper INSTANCE = Mappers.getMapper(DictMapper.class);

    /**
     * 输入参数转实体。
     *
     * @param dto 输入参数
     * @return 字典实体
     */
    Dict toEntity(DictDTO dto);

    /**
     * 实体转输出参数（含值列表）。
     *
     * @param entity 字典实体
     * @return 输出参数
     */
    DictVO toVO(Dict entity);

    /**
     * 实体列表转输出参数列表。
     *
     * @param entities 字典实体列表
     * @return 输出参数列表
     */
    List<DictVO> toDictVOList(List<Dict> entities);

    /**
     * 字典值输入参数转实体。
     *
     * @param dto 输入参数
     * @return 字典值实体
     */
    DictValue toEntity(DictValueDTO dto);

    /**
     * 字典值实体转输出参数。
     *
     * @param entity 字典值实体
     * @return 输出参数
     */
    DictValueVO toVO(DictValue entity);

    /**
     * 字典值实体列表转输出参数列表。
     *
     * @param entities 字典值实体列表
     * @return 输出参数列表
     */
    List<DictValueVO> toDictValueVOList(List<DictValue> entities);
}

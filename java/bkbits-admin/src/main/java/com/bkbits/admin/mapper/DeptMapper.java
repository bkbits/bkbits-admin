package com.bkbits.admin.mapper;

import com.bkbits.admin.pojo.DeptDTO;
import com.bkbits.admin.pojo.DeptVO;
import com.bkbits.dbo.entity.Dept;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 部门对象转换。
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DeptMapper {

    /** 内置静态单例 */
    DeptMapper INSTANCE = Mappers.getMapper(DeptMapper.class);

    /**
     * 输入参数转实体。
     *
     * @param dto 输入参数
     * @return 部门实体
     */
    Dept toEntity(DeptDTO dto);

    /**
     * 实体转输出参数（含子部门）。
     *
     * @param entity 部门实体
     * @return 输出参数
     */
    DeptVO toVO(Dept entity);

    /**
     * 实体列表转输出参数列表。
     *
     * @param entities 部门实体列表
     * @return 输出参数列表
     */
    List<DeptVO> toVOList(List<Dept> entities);
}

package com.bkbits.admin.mapper;

import com.bkbits.admin.pojo.TenantAddDTO;
import com.bkbits.admin.pojo.TenantUpdateDTO;
import com.bkbits.admin.pojo.TenantVO;
import com.bkbits.dbo.entity.Tenant;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 租户对象转换。
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TenantMapper {

    /** 内置静态单例 */
    TenantMapper INSTANCE = Mappers.getMapper(TenantMapper.class);

    /**
     * 新增参数转实体。
     *
     * @param dto 新增参数
     * @return 租户实体
     */
    Tenant toEntity(TenantAddDTO dto);

    /**
     * 更新参数转实体。
     *
     * @param dto 更新参数
     * @return 租户实体
     */
    Tenant toEntity(TenantUpdateDTO dto);

    /**
     * 实体转输出参数。
     *
     * @param entity 租户实体
     * @return 输出参数
     */
    TenantVO toVO(Tenant entity);

    /**
     * 实体列表转输出参数列表。
     *
     * @param entities 租户实体列表
     * @return 输出参数列表
     */
    List<TenantVO> toVOList(List<Tenant> entities);
}

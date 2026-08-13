package com.bkbits.admin.mapper;

import com.bkbits.admin.pojo.DataPermissionDTO;
import com.bkbits.admin.pojo.PermissionDTO;
import com.bkbits.admin.pojo.RoleDTO;
import com.bkbits.dbo.entity.DataPermission;
import com.bkbits.dbo.entity.Permission;
import com.bkbits.dbo.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

/**
 * 角色、权限及数据权限对象转换。
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PermissionMapper {

    /** 内置静态单例 */
    PermissionMapper INSTANCE = Mappers.getMapper(PermissionMapper.class);

    /**
     * 角色输入参数转实体。
     *
     * @param dto 输入参数
     * @return 角色实体
     */
    Role toRoleEntity(RoleDTO dto);

    /**
     * 权限输入参数转实体。
     *
     * @param dto 输入参数
     * @return 权限实体
     */
    Permission toPermissionEntity(PermissionDTO dto);

    /**
     * 数据权限输入参数转实体（menuPermissionId 映射为 permissionId）。
     *
     * @param dto 输入参数
     * @return 数据权限实体
     */
    DataPermission toDataPermissionEntity(DataPermissionDTO dto);
}

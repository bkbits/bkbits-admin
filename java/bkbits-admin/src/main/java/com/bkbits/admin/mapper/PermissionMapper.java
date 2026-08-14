package com.bkbits.admin.mapper;

import com.bkbits.admin.pojo.DataPermissionAddDTO;
import com.bkbits.admin.pojo.DataPermissionUpdateDTO;
import com.bkbits.admin.pojo.PermissionAddDTO;
import com.bkbits.admin.pojo.PermissionUpdateDTO;
import com.bkbits.admin.pojo.RoleAddDTO;
import com.bkbits.admin.pojo.RoleUpdateDTO;
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
     * 角色新增参数转实体。
     *
     * @param dto 新增参数
     * @return 角色实体
     */
    Role toRoleEntity(RoleAddDTO dto);

    /**
     * 角色更新参数转实体。
     *
     * @param dto 更新参数
     * @return 角色实体
     */
    Role toRoleEntity(RoleUpdateDTO dto);

    /**
     * 权限新增参数转实体。
     *
     * @param dto 新增参数
     * @return 权限实体
     */
    Permission toPermissionEntity(PermissionAddDTO dto);

    /**
     * 权限更新参数转实体。
     *
     * @param dto 更新参数
     * @return 权限实体
     */
    Permission toPermissionEntity(PermissionUpdateDTO dto);

    /**
     * 数据权限新增参数转实体（menuPermissionId 映射为 permissionId）。
     *
     * @param dto 新增参数
     * @return 数据权限实体
     */
    DataPermission toDataPermissionEntity(DataPermissionAddDTO dto);

    /**
     * 数据权限更新参数转实体（menuPermissionId 映射为 permissionId）。
     *
     * @param dto 更新参数
     * @return 数据权限实体
     */
    DataPermission toDataPermissionEntity(DataPermissionUpdateDTO dto);
}

package com.bkbits.admin.mapper;

import com.bkbits.admin.pojo.DataPermissionDTO;
import com.bkbits.admin.pojo.DataPermissionVO;
import com.bkbits.admin.pojo.PermissionDTO;
import com.bkbits.admin.pojo.PermissionVO;
import com.bkbits.admin.pojo.RoleDTO;
import com.bkbits.admin.pojo.RoleDataPermissionRelVO;
import com.bkbits.admin.pojo.RoleVO;
import com.bkbits.dbo.entity.DataPermission;
import com.bkbits.dbo.entity.Permission;
import com.bkbits.dbo.entity.Role;
import com.bkbits.dbo.entity.RoleDataPermissionRel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

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
    Role toEntity(RoleDTO dto);

    /**
     * 角色实体转输出参数（含权限与数据权限关联）。
     *
     * @param entity 角色实体
     * @return 输出参数
     */
    RoleVO toVO(Role entity);

    /**
     * 角色实体列表转输出参数列表。
     *
     * @param entities 角色实体列表
     * @return 输出参数列表
     */
    List<RoleVO> toRoleVOList(List<Role> entities);

    /**
     * 权限输入参数转实体。
     *
     * @param dto 输入参数
     * @return 权限实体
     */
    Permission toEntity(PermissionDTO dto);

    /**
     * 权限实体转输出参数（含数据权限列表）。
     *
     * @param entity 权限实体
     * @return 输出参数
     */
    PermissionVO toVO(Permission entity);

    /**
     * 权限实体列表转输出参数列表。
     *
     * @param entities 权限实体列表
     * @return 输出参数列表
     */
    List<PermissionVO> toPermissionVOList(List<Permission> entities);

    /**
     * 数据权限输入参数转实体（menuPermissionId 映射为 permissionId）。
     *
     * @param dto 输入参数
     * @return 数据权限实体
     */
    @Mapping(target = "permissionId", source = "menuPermissionId")
    DataPermission toEntity(DataPermissionDTO dto);

    /**
     * 数据权限实体转输出参数。
     *
     * @param entity 数据权限实体
     * @return 输出参数
     */
    DataPermissionVO toVO(DataPermission entity);

    /**
     * 数据权限实体列表转输出参数列表。
     *
     * @param entities 数据权限实体列表
     * @return 输出参数列表
     */
    List<DataPermissionVO> toDataPermissionVOList(List<DataPermission> entities);

    /**
     * 角色数据权限关联实体转输出参数（含关联数据权限）。
     *
     * @param entity 关联实体
     * @return 输出参数
     */
    RoleDataPermissionRelVO toVO(RoleDataPermissionRel entity);

    /**
     * 角色数据权限关联实体列表转输出参数列表。
     *
     * @param entities 关联实体列表
     * @return 输出参数列表
     */
    List<RoleDataPermissionRelVO> toRoleDataPermissionRelVOList(List<RoleDataPermissionRel> entities);
}

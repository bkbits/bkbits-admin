package com.bkbits.admin.mapper;

import com.bkbits.admin.pojo.UserAddDTO;
import com.bkbits.admin.pojo.UserUpdateDTO;
import com.bkbits.admin.pojo.UserVO;
import com.bkbits.dbo.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 用户对象转换。
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    /**
     * 内置静态单例
     */
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    /**
     * 输入参数转实体。
     *
     * @param dto 输入参数
     * @return 用户实体
     */
    User toAddEntity(UserAddDTO dto);


    User toUpdateEntity(UserUpdateDTO dto);

    /**
     * 实体转输出参数。
     *
     * @param entity 用户实体
     * @return 输出参数
     */
    UserVO toVO(User entity);

    /**
     * 实体列表转输出参数列表。
     *
     * @param entities 用户实体列表
     * @return 输出参数列表
     */
    List<UserVO> toVOList(List<User> entities);
}

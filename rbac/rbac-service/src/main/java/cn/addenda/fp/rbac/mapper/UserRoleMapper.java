package cn.addenda.fp.rbac.mapper;

import cn.addenda.fp.rbac.pojo.entity.User;
import cn.addenda.fp.rbac.pojo.entity.UserRole;
import cn.addenda.sql.vitamin.client.common.annotation.ConfigBaseEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author addenda
 * @since 2022/2/7 15:57
 */
public interface UserRoleMapper {

  void insert(UserRole userRole);

  Integer idExists(@Param("id") Long id);

  void updateNonNullFieldsById(UserRole userRole);

  List<UserRole> queryRoleOfUser(@Param("userId") Long userId, @Param("accessType") String accessType);

  void deleteById(@Param("id") Long id);

  @ConfigBaseEntity(masterView = "user")
  List<User> queryUserOnRole(@Param("roleId") Long roleId);

  List<UserRole> queryByNonNullFields(UserRole userRole);

  void deleteByUserId(@Param("userId") Long userId);

  Integer roleIdExists(@Param("roleId") Long roleId);

  Integer ruleIdExists(@Param("ruleId") Long ruleId);

}

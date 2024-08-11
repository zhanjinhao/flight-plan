package cn.addenda.fp.rbac.mapper;

import cn.addenda.fp.rbac.pojo.entity.Role;
import cn.addenda.fp.rbac.pojo.entity.RoleModule;
import cn.addenda.sql.vitamin.client.common.annotation.ConfigBaseEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author addenda
 * @since 2022/2/7 15:56
 */
public interface RoleModuleMapper {

  List<RoleModule> queryModuleOfRole(@Param("roleId") Long roleId);

  void deleteById(@Param("id") Long id);

  void insert(RoleModule roleModule);

  @ConfigBaseEntity(masterView = "role")
  List<Role> queryRoleOnModule(@Param("moduleId") Long moduleId);

  void deleteByRoleId(@Param("roleId") Long roleId);

  Integer moduleIdExists(@Param("moduleId") Long moduleId);

}

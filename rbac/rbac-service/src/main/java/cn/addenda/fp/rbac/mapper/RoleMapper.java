package cn.addenda.fp.rbac.mapper;

import cn.addenda.fp.rbac.pojo.entity.Role;
import cn.addenda.sql.vitamin.client.common.annotation.ConfigBaseEntity;
import cn.addenda.sql.vitamin.client.common.constant.BoolConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author addenda
 * @since 2022/2/7 15:56
 */
public interface RoleMapper {

  void insert(Role role);

  @ConfigBaseEntity(disable = BoolConfig.TRUE)
  Integer roleCodeExists(@Param("roleCode") String roleCode);

  @ConfigBaseEntity(disable = BoolConfig.TRUE)
  Integer idExists(@Param("id") Long id);

  void deleteById(@Param("id") Long id);

  void updateNonNullFieldsById(Role role);

  Role queryByRoleCode(@Param("roleCode") String roleCode);

  List<Role> queryByNonNullFields(Role role);

  List<Role> queryByRoleCodeList(@Param("roleCodeList") List<String> roleCodeList);

  List<Role> queryByIdList(@Param("idList") List<Long> idList);

}

package cn.addenda.fp.rbac.mapper;

import cn.addenda.fp.rbac.pojo.entity.UserRole;
import cn.addenda.fp.rbac.pojo.entity.UserRoleRecord;
import cn.addenda.sql.vitamin.client.common.annotation.ConfigBaseEntity;
import cn.addenda.sql.vitamin.client.common.constant.BoolConfig;
import org.apache.ibatis.annotations.Param;

/**
 * @author addenda
 * @since 2022/2/7 15:56
 */
public interface UserRoleRecordMapper {

  int insert(UserRoleRecord userRoleRecord);

  UserRoleRecord queryUserRoleRecordByUserId(@Param("userId") Long userId);

  void deleteByUserId(@Param("userId") Long userId);

  @ConfigBaseEntity(masterView = "t_user_role_record", selectDisable = BoolConfig.TRUE)
  UserRole queryLoginRole(@Param("userId") Long userId);

}

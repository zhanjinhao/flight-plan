package cn.addenda.fp.rbac.service;

import cn.addenda.component.user.UserInfo;
import cn.addenda.fp.rbac.pojo.entity.UserRole;
import cn.addenda.fp.rbac.pojo.entity.UserRoleRecord;

/**
 * @author addenda
 * @since 2022/2/7 17:16
 */
public interface UserRoleRecordService {

  UserInfo login(UserRoleRecord userRoleRecord);

  Boolean exit(Long userId);

  UserRole queryLoginRole(Long userId);

}

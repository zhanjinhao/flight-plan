package cn.addenda.fp.rbac.service;

import cn.addenda.fp.rbac.pojo.entity.User;
import cn.addenda.fp.rbac.pojo.entity.UserRole;

import java.util.List;

/**
 * @author addenda
 * @since 2022/2/7 17:16
 */
public interface UserRoleService {

  Boolean save(Long userId, List<Long> roleIdList);

  Boolean setPermission(Long id, UserRole userRole);

  List<UserRole> queryRoleOfUser(Long userId);

  List<User> queryUserOnRole(Long roleId);

}

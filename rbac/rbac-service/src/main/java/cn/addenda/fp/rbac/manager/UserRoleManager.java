package cn.addenda.fp.rbac.manager;

import cn.addenda.fp.rbac.pojo.entity.User;
import cn.addenda.fp.rbac.pojo.entity.UserRole;

import java.util.List;

public interface UserRoleManager {

  void deleteByUserId(Long userId);

  void batchDeleteById(List<Long> deleteList);

  void batchInsert(List<UserRole> insertList);

  boolean idExists(Long id);

  void setPermission(Long id, UserRole userRole);

  List<UserRole> queryRoleOfUser(Long userId);

  List<UserRole> queryWRoleOfUser(Long userId);

  List<UserRole> queryRRoleOfUser(Long userId);

  List<User> queryUserOnRole(Long roleId);

  List<UserRole> queryUserRoleOnRule(Long ruleId);

  boolean roleIdExists(Long roleId);

  boolean ruleIdExists(Long ruleId);

}

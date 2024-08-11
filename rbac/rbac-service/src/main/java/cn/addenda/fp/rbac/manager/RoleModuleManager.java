package cn.addenda.fp.rbac.manager;

import cn.addenda.fp.rbac.pojo.entity.Role;
import cn.addenda.fp.rbac.pojo.entity.RoleModule;

import java.util.List;

/**
 * @author addenda
 * @since 2022/10/15 15:14
 */
public interface RoleModuleManager {

  List<RoleModule> queryModuleOfRole(Long roleId);

  void batchDeleteById(List<Long> deleteList);

  void batchInsert(List<RoleModule> insertList);

  List<Role> queryRoleOnModule(Long moduleId);

  void deleteByRoleId(Long id);

  boolean moduleIdExists(Long moduleId);

}

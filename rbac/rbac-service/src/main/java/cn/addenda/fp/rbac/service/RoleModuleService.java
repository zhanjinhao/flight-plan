package cn.addenda.fp.rbac.service;

import cn.addenda.fp.rbac.pojo.bo.BModuleTree;
import cn.addenda.fp.rbac.pojo.entity.Role;

import java.util.List;

/**
 * @author addenda
 * @since 2022/2/7 17:16
 */
public interface RoleModuleService {

  Boolean save(Long roleId, List<Long> moduleIdList);

  BModuleTree queryModuleOfRole(Long roleId, String accessType);

  List<Role> queryRoleOnModule(Long moduleId);

}

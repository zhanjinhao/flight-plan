package cn.addenda.fp.rbac.manager;

import cn.addenda.fp.rbac.pojo.entity.Role;

import java.util.List;

/**
 * @author addenda
 * @since 2022/10/9 15:41
 */
public interface RoleManager {

  boolean roleCodeExists(String roleCode);

  boolean idExists(Long id);

  void insert(Role role);

  List<Role> queryByNonNullFields(Role role);

  Role queryById(Long id);

  void update(Role role);

  void setStatus(Long id, String status);

  void deleteById(Long id);

  Role queryByRoleCode(String roleCode);

  List<Role> queryByRoleCodeList(List<String> roleCodeList);

  List<Role> queryByIdList(List<Long> roleIdList);

}

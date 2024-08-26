package cn.addenda.fp.rbac.service;

import cn.addenda.fp.rbac.pojo.entity.Role;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @author addenda
 * @since 2022/2/7 17:16
 */
public interface RoleService {

  Long insert(Role role);

  Boolean deleteById(Long id);

  Boolean setStatus(Long id, String status);

  Role queryByRoleCode(String roleCode);

  PageInfo<Role> pageQuery(Integer pageNum, Integer pageSize, Role role);

  Role queryById(Long id);

  Boolean update(Role role);

  List<Role> queryByRoleCodeList(List<String> roleCodeList);

  List<Role> queryRoleOfUser(String userCode);

}

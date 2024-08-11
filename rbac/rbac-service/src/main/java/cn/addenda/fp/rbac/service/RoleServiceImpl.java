package cn.addenda.fp.rbac.service;

import cn.addenda.component.jdk.exception.ServiceException;
import cn.addenda.component.lockhelper.Locked;
import cn.addenda.component.transaction.TransactionHelper;
import cn.addenda.fp.rbac.manager.RoleManager;
import cn.addenda.fp.rbac.manager.RoleModuleManager;
import cn.addenda.fp.rbac.manager.UserRoleManager;
import cn.addenda.fp.rbac.pojo.entity.Role;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author addenda
 * @since 2022/2/7 17:16
 */
@Component
public class RoleServiceImpl implements RoleService {

  @Autowired
  private RoleManager roleManager;

  @Autowired
  private UserRoleManager userRoleManager;

  @Autowired
  private RoleModuleManager roleModuleManager;

  @Autowired
  private TransactionHelper transactionHelper;

  @Override
  @Locked(prefix = "role:roleCode", spEL = "#role.roleCode")
  @Transactional(rollbackFor = Exception.class)
  public Long insert(Role role) {
    if (roleManager.roleCodeExists(role.getRoleCode())) {
      throw new ServiceException("roleCode已存在：" + role.getRoleCode() + "。 ");
    }

    role.setStatus(Role.ACTIVE);
    roleManager.insert(role);
    return role.getId();
  }

  @Override
  public PageInfo<Role> pageQuery(Integer pageNum, Integer pageSize, Role role) {
    try {
      PageMethod.startPage(pageNum, pageSize);
      List<Role> query = roleManager.queryByNonNullFields(role);
      return new PageInfo<>(query);
    } finally {
      PageMethod.clearPage();
    }
  }

  @Override
  public Role queryById(Long id) {
    return roleManager.queryById(id);
  }

  @Override
  public Boolean update(Role role) {
    if (!roleManager.idExists(role.getId())) {
      throw new ServiceException("id不存在：" + role.getId() + "。 ");
    }

    return transactionHelper.doTransaction(() -> {
      roleManager.update(role);
      return true;
    });
  }

  @Override
  public Boolean setStatus(Long id, String status) {
    Role.assertStatus(status);
    if (!roleManager.idExists(id)) {
      throw new ServiceException("id不存在：" + id + "。 ");
    }

    return transactionHelper.doTransaction(() -> {
      roleManager.setStatus(id, status);
      return true;
    });
  }

  @Override
  public Boolean deleteById(Long id) {
    // 如果角色被用户关联，则不可删除
    if (userRoleManager.roleIdExists(id)) {
      throw new ServiceException("此角色正被用户使用，不可删除！");
    }

    return transactionHelper.doTransaction(() -> {
      roleManager.deleteById(id);
      // 删除角色的时候同步删除：角色-module的关联
      roleModuleManager.deleteByRoleId(id);
      return true;
    });
  }

  @Override
  public Role queryByRoleCode(String roleCode) {
    return roleManager.queryByRoleCode(roleCode);
  }

}

package cn.addenda.fp.rbac.service;

import cn.addenda.component.jdk.exception.ServiceException;
import cn.addenda.component.lockhelper.LockHelper;
import cn.addenda.component.lockhelper.Locked;
import cn.addenda.component.transaction.TransactionHelper;
import cn.addenda.fp.rbac.manager.RoleManager;
import cn.addenda.fp.rbac.manager.UserManager;
import cn.addenda.fp.rbac.manager.UserRoleManager;
import cn.addenda.fp.rbac.pojo.entity.Role;
import cn.addenda.fp.rbac.pojo.entity.User;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * @author addenda
 * @since 2022/2/7 17:16
 */
@Component
public class UserServiceImpl implements UserService {

  @Autowired
  private UserManager userManager;

  @Autowired
  private RoleManager roleManager;

  @Autowired
  private UserRoleManager userRoleManager;

  @Autowired
  private LockHelper lockHelper;

  @Autowired
  private TransactionHelper transactionHelper;

  @Override
  @Locked(prefix = "user:userCode", spEL = "#user.userCode")
  @Transactional(rollbackFor = Exception.class)
  public Long insert(User user) {
    if (userManager.userCodeExists(user.getUserCode())) {
      throw new ServiceException("用户Code已存在：" + user.getUserCode() + "。");
    }
    return lockHelper.lock("user:userEmail", () -> {
      if (userManager.userEmailExists(user.getUserEmail())) {
        throw new ServiceException("邮箱已存在：" + user.getUserEmail() + "。");
      }
      user.setStatus(User.ON_JOB);
      transactionHelper.doTransaction(() -> userManager.insert(user));
      return user.getId();
    }, user.getUserEmail());
  }

  @Override
  public PageInfo<User> pageQuery(Integer pageNum, Integer pageSize, User user) {
    try {
      PageMethod.startPage(pageNum, pageSize);
      List<User> query = userManager.queryByNonNullFields(user);
      return new PageInfo<>(query);
    } finally {
      PageMethod.clearPage();
    }
  }

  @Override
  public User queryById(Long id) {
    return userManager.queryById(id);
  }

  @Override
  public Boolean update(User user) {
    if (!userManager.idExists(user.getId())) {
      throw new ServiceException("id不存在：" + user.getId() + "。");
    }
    return transactionHelper.doTransaction(() -> {
      userManager.update(user);
      return true;
    });
  }

  @Override
  public Boolean setStatus(Long id, String status) {
    if (!User.ON_JOB.equals(status) &&
            !User.RETIRE.equals(status) && !User.LEAVE.equals(status)) {
      throw new ServiceException("不合法的状态：" + status + "。");
    }
    if (!userManager.idExists(id)) {
      throw new ServiceException("id不存在：" + id + "。");
    }
    return transactionHelper.doTransaction(() -> {
      userManager.setStatus(id, status);
      return true;
    });
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Boolean deleteById(Long id) {
    userManager.deleteById(id);
    // 删除用户的时候同步删除：用户-角色的关联
    userRoleManager.deleteByUserId(id);
    return true;
  }

  @Override
  public User queryByUserCode(String userCode) {
    return userManager.queryByUserCode(userCode);
  }

  @Override
  public List<User> queryByUserCodeList(List<String> userCodeList) {
    return userManager.queryByUserCodeList(userCodeList);
  }

  @Override
  public List<User> queryByRoleCode(String roleCode) {
    Role role = roleManager.queryByRoleCode(roleCode);
    if (role == null) {
      return new ArrayList<>();
    }
    return userRoleManager.queryUserOnRole(role.getId());
  }

}

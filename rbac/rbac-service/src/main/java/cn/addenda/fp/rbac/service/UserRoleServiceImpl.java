package cn.addenda.fp.rbac.service;

import cn.addenda.component.jdk.exception.ServiceException;
import cn.addenda.component.jdk.pojo.Ternary;
import cn.addenda.component.jdk.util.collection.IterableUtils;
import cn.addenda.component.lockhelper.LockHelper;
import cn.addenda.component.transaction.TransactionAttrBuilder;
import cn.addenda.component.transaction.TransactionHelper;
import cn.addenda.fp.rbac.manager.RoleManager;
import cn.addenda.fp.rbac.manager.RuleManager;
import cn.addenda.fp.rbac.manager.UserManager;
import cn.addenda.fp.rbac.manager.UserRoleManager;
import cn.addenda.fp.rbac.pojo.entity.Module;
import cn.addenda.fp.rbac.pojo.entity.User;
import cn.addenda.fp.rbac.pojo.entity.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.interceptor.TransactionAttribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author addenda
 * @since 2022/2/7 17:16
 */
@Slf4j
@Component
public class UserRoleServiceImpl implements UserRoleService {

  @Autowired
  private UserRoleManager userRoleManager;

  @Autowired
  private UserManager userManager;

  @Autowired
  private RuleManager ruleManager;

  @Autowired
  private RoleManager roleManager;

  @Autowired
  private LockHelper lockHelper;

  @Autowired
  private TransactionHelper transactionHelper;

  @Override
  public Boolean save(Long userId, List<Long> roleIdList) {
    if (!userManager.idExists(userId)) {
      throw new ServiceException("userId不存在：" + userId + "。");
    }

    return lockHelper.lock("user:userId", () -> {
      TransactionAttribute rrAttribute = TransactionAttrBuilder.newRRBuilder().build();
      return transactionHelper.doTransaction(rrAttribute, () -> {
        // 从数据库查出来用户已经有的角色
        List<UserRole> userRoleListFromDb = userRoleManager.queryRoleOfUser(userId);

        List<Long> roleIdListFromDb = userRoleListFromDb
                .stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());
        Ternary<List<Long>, List<Long>, List<Long>> separate =
                IterableUtils.separateToList(roleIdListFromDb, roleIdList);

        // 数据库有参数没有，需要删除
        List<Long> deleteList = new ArrayList<>();
        for (Long roleId : separate.getF1()) {
          Map<Long, Long> userRoleMapFromDb = userRoleListFromDb
                  .stream()
                  .collect(Collectors.toMap(UserRole::getRoleId, UserRole::getId));
          deleteList.add(userRoleMapFromDb.get(roleId));
        }

        // 参数有数据库没有，需要增加
        List<UserRole> insertList = separate.getF3()
                .stream()
                .map(item -> new UserRole(userId, item, Module.AT_WRITE, ruleManager.defaultRuleIdList()))
                .collect(Collectors.toList());

        userRoleManager.batchDeleteById(deleteList);
        userRoleManager.batchInsert(insertList);

        return true;
      });
    }, userId);
  }

  @Override
  public Boolean setPermission(Long id, UserRole userRole) {
    String accessType = userRole.getAccessType();
    if (!Module.AT_WRITE.equals(accessType) &&
            !Module.AT_READ.equals(accessType) && !Module.AT_LISTEN.equals(accessType)) {
      throw new ServiceException("不合法的进入权限：" + accessType + "。");
    }

    if (!userRoleManager.idExists(id)) {
      throw new ServiceException("id不存在：" + id + "。");
    }

    return transactionHelper.doTransaction(() -> {
      userRoleManager.setPermission(id, userRole);
      return true;
    });

  }

  @Override
  public List<UserRole> queryRoleOfUser(Long userId) {
    if (!userManager.idExists(userId)) {
      throw new ServiceException("用户Code不存在：" + userId + "。");
    }

    return userRoleManager.queryRoleOfUser(userId);
  }

  @Override
  public List<User> queryUserOnRole(Long roleId) {
    if (!roleManager.idExists(roleId)) {
      throw new ServiceException("roleId不存在：" + roleId + "。");
    }

    return userRoleManager.queryUserOnRole(roleId);
  }

}

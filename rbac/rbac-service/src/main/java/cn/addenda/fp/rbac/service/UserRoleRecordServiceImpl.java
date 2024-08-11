package cn.addenda.fp.rbac.service;

import cn.addenda.component.jdk.exception.ServiceException;
import cn.addenda.component.jdk.util.collection.IterableUtils;
import cn.addenda.component.lockhelper.LockHelper;
import cn.addenda.component.transaction.TransactionAttrBuilder;
import cn.addenda.component.transaction.TransactionHelper;
import cn.addenda.component.user.UserContext;
import cn.addenda.component.user.UserInfo;
import cn.addenda.fp.rbac.manager.RoleManager;
import cn.addenda.fp.rbac.manager.UserManager;
import cn.addenda.fp.rbac.manager.UserRoleManager;
import cn.addenda.fp.rbac.manager.UserRoleRecordManager;
import cn.addenda.fp.rbac.pojo.entity.User;
import cn.addenda.fp.rbac.pojo.entity.UserRole;
import cn.addenda.fp.rbac.pojo.entity.UserRoleRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAttribute;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author addenda
 * @since 2022/2/7 17:16
 */
@Component
public class UserRoleRecordServiceImpl implements UserRoleRecordService {

  @Autowired
  private UserRoleRecordManager userRoleRecordManager;

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
  public UserInfo login(UserRoleRecord userRoleRecord) {
    Long userId = userRoleRecord.getUserId();
    Long roleId = userRoleRecord.getRoleId();

    User user = userManager.queryById(userId);
    if (user == null) {
      throw new ServiceException("userId不存在：" + userId + "。");
    }
    if (!roleManager.idExists(roleId)) {
      throw new ServiceException("roleId不存在：" + roleId + "。");
    }

    List<UserRole> userRoleList = IterableUtils.mergeToList(userRoleManager.queryWRoleOfUser(userId), userRoleManager.queryRRoleOfUser(userId));
    if (!userRoleList.stream().map(UserRole::getRoleId).collect(Collectors.toSet()).contains(roleId)) {
      throw new ServiceException("用户 [" + userId + "] 无角色：[" + roleId + "]或无读写权限。");
    }

    return lockHelper.lock("user:userId", () -> {

      TransactionAttribute rrAttribute = TransactionAttrBuilder.newRRBuilder().build();
      return transactionHelper.doTransaction(rrAttribute, () -> {

        // 查询出来用户现有的角色
        UserRoleRecord userRoleRecordFromDb = userRoleRecordManager.queryUserRoleRecordByUserId(userId);

        UserInfo build = UserInfo.builder()
                .userId(user.getUserCode())
                .username(user.getUserName())
                .build();

        UserContext.runWithCustomUser(() -> {
          // 如果不存在，表示登录
          if (userRoleRecordFromDb == null) {
            userRoleRecord.setType(UserRoleRecord.TYPE_ENTER);
            userRoleRecordManager.insert(userRoleRecord);
          }
          // 如果存在，表示转换角色
          else {
            userRoleRecordManager.deleteByUserId(userId);
            userRoleRecord.setType(UserRoleRecord.TYPE_CHANGE_ROLE);
            userRoleRecordManager.insert(userRoleRecord);
          }

        }, build);

        return build;
      });
    }, userId);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Boolean exit(Long userId) {

    userRoleRecordManager.deleteByUserId(userId);
    return true;
  }

  @Override
  public UserRole queryLoginRole(Long userId) {
    if (!userManager.idExists(userId)) {
      throw new ServiceException("userId不存在：" + userId + "。");
    }

    return userRoleRecordManager.queryLoginRole(userId);
  }

}

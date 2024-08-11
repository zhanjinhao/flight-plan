package cn.addenda.fp.rbac.manager;

import cn.addenda.component.basemybatis.helper.MybatisBatchDmlHelper;
import cn.addenda.fp.rbac.mapper.UserRoleMapper;
import cn.addenda.fp.rbac.pojo.entity.Module;
import cn.addenda.fp.rbac.pojo.entity.User;
import cn.addenda.fp.rbac.pojo.entity.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author addenda
 * @since 2022/10/9 10:22
 */
@Component
public class UserRoleManagerImpl implements UserRoleManager {

  @Autowired
  private UserRoleMapper userRoleMapper;

  @Autowired
  private MybatisBatchDmlHelper mybatisBatchOperationHelper;

  @Override
  public void deleteByUserId(Long userId) {
    userRoleMapper.deleteByUserId(userId);
  }

  @Override
  public void batchDeleteById(List<Long> deleteList) {
    mybatisBatchOperationHelper.batch(
            UserRoleMapper.class, deleteList, UserRoleMapper::deleteById);
  }

  @Override
  public void batchInsert(List<UserRole> insertList) {
    mybatisBatchOperationHelper.batch(
            UserRoleMapper.class, insertList, UserRoleMapper::insert);
  }

  @Override
  public boolean idExists(Long id) {
    Integer integer = userRoleMapper.idExists(id);
    return integer != null && integer > 0;
  }

  @Override
  public void setPermission(Long id, UserRole userRole) {
    userRole.setId(id);
    userRoleMapper.updateNonNullFieldsById(userRole);
  }

  @Override
  public List<UserRole> queryRoleOfUser(Long userId) {
    return userRoleMapper.queryRoleOfUser(userId, null);
  }

  @Override
  public List<UserRole> queryWRoleOfUser(Long userId) {
    return userRoleMapper.queryRoleOfUser(userId, Module.AT_WRITE);
  }

  @Override
  public List<UserRole> queryRRoleOfUser(Long userId) {
    return userRoleMapper.queryRoleOfUser(userId, Module.AT_READ);
  }

  @Override
  public List<User> queryUserOnRole(Long roleId) {
    return userRoleMapper.queryUserOnRole(roleId);
  }

  @Override
  public List<UserRole> queryUserRoleOnRule(Long ruleId) {
    UserRole userRole = new UserRole();
    userRole.setRuleIdList(String.valueOf(ruleId));
    return userRoleMapper.queryByNonNullFields(userRole);
  }

  @Override
  public boolean roleIdExists(Long roleId) {
    Integer integer = userRoleMapper.roleIdExists(roleId);
    return integer != null && integer != 0;
  }

  @Override
  public boolean ruleIdExists(Long ruleId) {
    Integer integer = userRoleMapper.ruleIdExists(ruleId);
    return integer != null && integer != 0;
  }
}

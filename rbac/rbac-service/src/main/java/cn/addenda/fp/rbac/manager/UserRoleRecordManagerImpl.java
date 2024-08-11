package cn.addenda.fp.rbac.manager;

import cn.addenda.component.cachehelper.CacheHelper;
import cn.addenda.fp.rbac.mapper.UserRoleRecordMapper;
import cn.addenda.fp.rbac.pojo.entity.UserRole;
import cn.addenda.fp.rbac.pojo.entity.UserRoleRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author addenda
 * @since 2022/10/10 15:26
 */
@Component
public class UserRoleRecordManagerImpl implements UserRoleRecordManager {

  @Autowired
  private UserRoleRecordMapper userRoleRecordMapper;

  @Autowired
  private CacheHelper redisCacheHelper;

  @Override
  public UserRoleRecord queryUserRoleRecordByUserId(Long userId) {
    return userRoleRecordMapper.queryUserRoleRecordByUserId(userId);
  }

  @Override
  public void insert(UserRoleRecord userRoleRecord) {
    userRoleRecordMapper.insert(userRoleRecord);
  }

  @Override
  public void deleteByUserId(Long userId) {
    userRoleRecordMapper.deleteByUserId(userId);
  }

  @Override
  public UserRole queryLoginRole(Long userId) {
    return userRoleRecordMapper.queryLoginRole(userId);
  }

}

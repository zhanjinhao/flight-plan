package cn.addenda.fp.rbac.manager;

import cn.addenda.component.cachehelper.CacheHelper;
import cn.addenda.fp.rbac.constant.RedisKeyConst;
import cn.addenda.fp.rbac.mapper.UserMapper;
import cn.addenda.fp.rbac.pojo.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author addenda
 * @since 2022/10/8 17:17
 */
@Component
public class UserManagerImpl implements UserManager {

  @Autowired
  private UserMapper userMapper;

  @Autowired
  private CacheHelper redisCacheHelper;

  @Override
  public boolean userCodeExists(String userCode) {
    Integer integer = userMapper.userCodeExists(userCode);
    return integer != null && integer != 0;
  }

  @Override
  public boolean userEmailExists(String userEmail) {
    Integer integer = userMapper.userEmailExists(userEmail);
    return integer != null && integer != 0;
  }

  @Override
  public void insert(User user) {
    userMapper.insert(user);
  }

  @Override
  public void deleteById(Long id) {
    redisCacheHelper.acceptWithPpf(
            RedisKeyConst.USER_ID_KEY, id, aLong -> userMapper.deleteById(id));
  }

  @Override
  public void setStatus(Long id, String status) {
    redisCacheHelper.acceptWithPpf(RedisKeyConst.USER_ID_KEY, id,
            aLong -> {
              User user = new User();
              user.setId(id);
              user.setStatus(status);
              userMapper.updateNonNullFieldsById(user);
            });
  }

  @Override
  public boolean idExists(Long id) {
    Integer integer = userMapper.idExists(id);
    return integer != null && integer != 0;
  }

  @Override
  public User queryByUserCode(String userCode) {
    return redisCacheHelper.queryWithPpf(RedisKeyConst.USER_USER_CODE_KEY,
            userCode, User.class, userMapper::queryByUserCode, RedisKeyConst.CACHE_DEFAULT_TTL);
  }

  @Override
  public List<User> queryByNonNullFields(User user) {
    return userMapper.queryByNonNullFields(user);
  }

  @Override
  public User queryById(Long id) {
    return redisCacheHelper.queryWithPpf(RedisKeyConst.USER_ID_KEY,
            id, User.class, this::doQueryById, RedisKeyConst.CACHE_DEFAULT_TTL);
  }

  private User doQueryById(Long id) {
    User user = new User();
    user.setId(id);
    List<User> users = userMapper.queryByNonNullFields(user);
    if (users.isEmpty()) {
      return null;
    }
    return users.get(0);
  }

  @Override
  public void update(User user) {
    redisCacheHelper.acceptWithPpf(
            RedisKeyConst.USER_ID_KEY, user.getId(), aLong -> userMapper.updateNonNullFieldsById(user));
  }

}

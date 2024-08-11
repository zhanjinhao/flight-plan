package cn.addenda.fp.rbac.manager;


import cn.addenda.fp.rbac.pojo.entity.User;

import java.util.List;

/**
 * @author addenda
 * @since 2022/10/8 17:17
 */
public interface UserManager {

  boolean userCodeExists(String userId);

  boolean userEmailExists(String userId);

  boolean idExists(Long id);

  void insert(User user);

  List<User> queryByNonNullFields(User user);

  User queryById(Long id);

  void update(User user);

  void setStatus(Long id, String status);

  void deleteById(Long id);

  User queryByUserCode(String userId);

}

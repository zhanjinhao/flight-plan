package cn.addenda.fp.rbac.service;

import cn.addenda.fp.rbac.pojo.entity.User;
import com.github.pagehelper.PageInfo;

/**
 * @author addenda
 * @since 2022/2/7 17:15
 */
public interface UserService {

  Long insert(User user);

  PageInfo<User> pageQuery(Integer pageNum, Integer pageSize, User user);

  User queryById(Long id);

  Boolean update(User user);

  Boolean setStatus(Long id, String status);

  Boolean deleteById(Long id);

  User queryByUserCode(String userCode);

}

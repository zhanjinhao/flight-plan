package cn.addenda.fp.rbac.rpc;

import cn.addenda.component.jdk.util.BeanUtils;
import cn.addenda.fp.rbac.dto.DUser;
import cn.addenda.fp.rbac.pojo.entity.User;
import cn.addenda.fp.rbac.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author addenda
 * @since 2022/10/15 10:21
 */
@FeignClient(value = "rbac-service")
public class UserRpcImpl implements UserRpc {

  @Autowired
  private UserService userService;

  @Override
  public DUser queryByUserCode(String userId) {
    User user = userService.queryByUserCode(userId);
    return BeanUtils.copyProperties(user, new DUser());
  }

}

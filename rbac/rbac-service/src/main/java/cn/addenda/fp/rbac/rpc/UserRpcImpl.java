package cn.addenda.fp.rbac.rpc;

import cn.addenda.component.jdk.util.BeanUtils;
import cn.addenda.fp.rbac.dto.UserDto;
import cn.addenda.fp.rbac.pojo.entity.User;
import cn.addenda.fp.rbac.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author addenda
 * @since 2022/10/15 10:21
 */
@RestController
@RequestMapping("/userRpc")
public class UserRpcImpl implements UserRpc {

  @Autowired
  private UserService userService;

  @Override
  @PostMapping("/queryByUserCode")
  public UserDto queryByUserCode(@RequestBody String userCode) {
    User user = userService.queryByUserCode(userCode);
    return BeanUtils.copyProperties(user, new UserDto());
  }

  @Override
  @PostMapping("/queryByUserCodeList")
  public List<UserDto> queryByUserCodeList(@RequestBody List<String> userCodeList) {
    List<User> userList = userService.queryByUserCodeList(userCodeList);
    return BeanUtils.copyProperties(userList, UserDto.class);
  }

  @Override
  @PostMapping("/queryByRoleCode")
  public List<UserDto> queryByRoleCode(@RequestBody String roleCode) {
    List<User> userList = userService.queryByRoleCode(roleCode);
    return BeanUtils.copyProperties(userList, UserDto.class);
  }

}

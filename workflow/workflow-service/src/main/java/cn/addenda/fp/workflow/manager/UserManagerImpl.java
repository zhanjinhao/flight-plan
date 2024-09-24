package cn.addenda.fp.workflow.manager;

import cn.addenda.component.user.UserInfo;
import cn.addenda.fp.rbac.dto.UserDto;
import cn.addenda.fp.rbac.rpc.UserRpc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UserManagerImpl implements UserManager {

  @Autowired
  private UserRpc userRpc;

  @Override
  public String getUserNameByUserId(String userId) {
    if (!StringUtils.hasText(userId)) {
      return null;
    }
    return Optional.ofNullable(userRpc.queryByUserCode(userId)).map(UserDto::getUserName).orElse(null);
  }

  @Override
  public UserInfo getUser(String userId) {
    if (!StringUtils.hasText(userId)) {
      return null;
    }
    UserDto userDto = userRpc.queryByUserCode(userId);
    if (userDto == null) {
      return null;
    }
    return new UserInfo(userDto.getUserCode(), userDto.getUserName());
  }

  @Override
  public List<UserInfo> getUserListByGroupId(String groupId) {
    List<UserDto> userDtoList = userRpc.queryByRoleCode(groupId);
    return userDtoList.stream()
            .map(a -> new UserInfo(a.getUserCode(), a.getUserName()))
            .collect(Collectors.toList());
  }

}

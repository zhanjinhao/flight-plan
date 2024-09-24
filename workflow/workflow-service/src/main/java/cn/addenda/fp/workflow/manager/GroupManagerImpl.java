package cn.addenda.fp.workflow.manager;

import cn.addenda.fp.rbac.dto.RoleDto;
import cn.addenda.fp.rbac.rpc.RoleRpc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Component
public class GroupManagerImpl implements GroupManager {

  @Autowired
  private RoleRpc roleRpc;

  @Override
  public String getGroupNameByGroupId(String groupId) {
    if (!StringUtils.hasText(groupId)) {
      return null;
    }
    return Optional.ofNullable(roleRpc.queryByRoleCode(groupId)).map(RoleDto::getRoleName).orElse(null);
  }

}

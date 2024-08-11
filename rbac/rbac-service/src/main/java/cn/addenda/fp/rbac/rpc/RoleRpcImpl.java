package cn.addenda.fp.rbac.rpc;

import cn.addenda.component.jdk.util.BeanUtils;
import cn.addenda.fp.rbac.dto.DRole;
import cn.addenda.fp.rbac.pojo.entity.Role;
import cn.addenda.fp.rbac.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author addenda
 * @since 2022/10/15 18:01
 */
@FeignClient(value = "rbac-service")
public class RoleRpcImpl implements RoleRpc {

  @Autowired
  private RoleService roleService;

  @Override
  public DRole queryByRoleCode(String roleCode) {
    Role role = roleService.queryByRoleCode(roleCode);
    return BeanUtils.copyProperties(role, new DRole());
  }

}

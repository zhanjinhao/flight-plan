package cn.addenda.fp.rbac.rpc;

import cn.addenda.component.jdk.util.BeanUtils;
import cn.addenda.fp.rbac.dto.RoleDto;
import cn.addenda.fp.rbac.pojo.entity.Role;
import cn.addenda.fp.rbac.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author addenda
 * @since 2022/10/15 18:01
 */
@RestController
@RequestMapping("/roleRpc")
public class RoleRpcImpl implements RoleRpc {

  @Autowired
  private RoleService roleService;

  @Override
  @PostMapping("/queryByRoleCode")
  public RoleDto queryByRoleCode(@RequestBody String roleCode) {
    Role role = roleService.queryByRoleCode(roleCode);
    return BeanUtils.copyProperties(role, new RoleDto());
  }

  @Override
  @PostMapping("/queryByRoleCodeList")
  public List<RoleDto> queryByRoleCodeList(@RequestBody List<String> roleCodeList) {
    List<Role> roleList = roleService.queryByRoleCodeList(roleCodeList);
    return BeanUtils.copyProperties(roleList, RoleDto.class);
  }

  @Override
  @PostMapping("/queryRoleOfUser")
  public List<RoleDto> queryRoleOfUser(@RequestBody String userCode) {
    List<Role> roleList = roleService.queryRoleOfUser(userCode);
    return BeanUtils.copyProperties(roleList, RoleDto.class);
  }

}

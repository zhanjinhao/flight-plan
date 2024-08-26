package cn.addenda.fp.rbac.rpc;

import cn.addenda.fp.rbac.config.FeignConfiguration;
import cn.addenda.fp.rbac.dto.RoleDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author addenda
 * @since 2022/11/26 20:45
 */
@FeignClient(
        value = "rbac-service",
        contextId = "role",
        path = "/rbac/roleRpc",
        configuration = FeignConfiguration.class)
public interface RoleRpc {

  @PostMapping("/queryByRoleCode")
  RoleDto queryByRoleCode(@RequestBody String roleCode);

  @PostMapping("/queryByRoleCodeList")
  List<RoleDto> queryByRoleCodeList(@RequestBody List<String> roleCodeList);

  @PostMapping("/queryRoleOfUser")
  List<RoleDto> queryRoleOfUser(@RequestBody String userCode);

}

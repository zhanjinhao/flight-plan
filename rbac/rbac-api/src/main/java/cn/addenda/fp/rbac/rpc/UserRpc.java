package cn.addenda.fp.rbac.rpc;

import cn.addenda.fp.rbac.config.FeignConfiguration;
import cn.addenda.fp.rbac.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author addenda
 * @since 2022/10/15 10:20
 */
@FeignClient(
        value = "rbac-service",
        contextId = "user",
        path = "/rbac/userRpc",
        configuration = FeignConfiguration.class)
public interface UserRpc {

  @PostMapping("/queryByUserCode")
  UserDto queryByUserCode(@RequestBody String userCode);

  @PostMapping("/queryByUserCodeList")
  List<UserDto> queryByUserCodeList(@RequestBody List<String> userCodeList);

  @PostMapping("/queryByRoleCode")
  List<UserDto> queryByRoleCode(@RequestBody String roleCode);

}

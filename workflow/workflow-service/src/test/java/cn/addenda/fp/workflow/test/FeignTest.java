package cn.addenda.fp.workflow.test;

import cn.addenda.component.jdk.util.collection.ArrayUtils;
import cn.addenda.fp.rbac.dto.RoleDto;
import cn.addenda.fp.rbac.dto.UserDto;
import cn.addenda.fp.rbac.rpc.RoleRpc;
import cn.addenda.fp.rbac.rpc.UserRpc;
import cn.addenda.fp.workflow.FpWorkFlowApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(classes = FpWorkFlowApplication.class)
class FeignTest {

  @Autowired
  private UserRpc userRpc;

  @Autowired
  private RoleRpc roleRpc;

  @Test
  void test1() {
    UserDto userDto = userRpc.queryByUserCode("125");
    System.out.println(userDto);
  }

  @Test
  void test2() {
    List<UserDto> userDto = userRpc.queryByUserCodeList(ArrayUtils.asArrayList("125"));
    System.out.println(userDto);
  }

  @Test
  void test3() {
    List<UserDto> userDto = userRpc.queryByRoleCode("qingbao");
    System.out.println(userDto);
  }

  @Test
  void test4() {
    RoleDto userDto = roleRpc.queryByRoleCode("qingbao");
    System.out.println(userDto);
  }

  @Test
  void test5() {
    List<RoleDto> userDto = roleRpc.queryByRoleCodeList(ArrayUtils.asArrayList("qingbao"));
    System.out.println(userDto);
  }

  @Test
  void test6() {
    List<RoleDto> userDto = roleRpc.queryRoleOfUser("125");
    System.out.println(userDto);
  }

}

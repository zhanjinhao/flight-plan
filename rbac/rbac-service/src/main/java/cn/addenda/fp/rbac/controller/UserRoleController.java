package cn.addenda.fp.rbac.controller;

import cn.addenda.component.basaspring.util.AssertUtils;
import cn.addenda.component.jdk.result.Result;
import cn.addenda.component.jdk.util.BeanUtils;
import cn.addenda.fp.rbac.pojo.entity.UserRole;
import cn.addenda.fp.rbac.pojo.vo.VUserRolePermission;
import cn.addenda.fp.rbac.pojo.vo.VUserWithAllFields;
import cn.addenda.fp.rbac.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author addenda
 * @since 2022/2/8 12:56
 */
@RestController
@RequestMapping("/userRole")
public class UserRoleController {

  @Autowired
  private UserRoleService userRoleService;

  @PutMapping("/save")
  public Result<Boolean> save(@RequestParam("userId") Long userId, @RequestBody List<Long> roleIdList) {
    AssertUtils.notNull(userId, "userId");
    AssertUtils.notNull(roleIdList);

    return Result.success(userRoleService.save(userId, roleIdList));
  }

  @PutMapping("/setPermission")
  public Result<Boolean> setPermission(@RequestParam("id") Long id, @RequestBody VUserRolePermission rolePermission) {
    AssertUtils.notNull(id, "id");
    AssertUtils.notNull(rolePermission);
    AssertUtils.notNull(rolePermission.getAccessType(), "accessType");

    return Result.success(userRoleService.setPermission(id, BeanUtils.copyProperties(rolePermission, new UserRole())));
  }

  @GetMapping("/queryRoleOfUser")
  public Result<List<UserRole>> queryRoleOfUser(@RequestParam("userId") Long userId) {
    AssertUtils.notNull(userId, "userId");

    return Result.success(userRoleService.queryRoleOfUser(userId));
  }

  @GetMapping("/queryUserOnRole")
  public Result<List<VUserWithAllFields>> queryUserOnRole(@RequestParam("roleId") Long roleId) {
    AssertUtils.notNull(roleId, "roleId");

    return Result.success(userRoleService.queryUserOnRole(roleId),
            userList -> userList.stream()
                    .map(user -> BeanUtils.copyProperties(user, new VUserWithAllFields()))
                    .collect(Collectors.toList()));
  }

}

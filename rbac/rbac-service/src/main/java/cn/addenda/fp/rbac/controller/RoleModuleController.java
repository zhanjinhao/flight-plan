package cn.addenda.fp.rbac.controller;

import cn.addenda.component.basaspring.util.AssertUtils;
import cn.addenda.component.jdk.result.Result;
import cn.addenda.component.jdk.util.BeanUtils;
import cn.addenda.fp.rbac.pojo.bo.BModuleTree;
import cn.addenda.fp.rbac.pojo.vo.VRoleWithAllFields;
import cn.addenda.fp.rbac.service.RoleModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author addenda
 * @since 2022/2/7 16:43
 */
@RestController
@RequestMapping("/roleModule")
public class RoleModuleController {

  @Autowired
  private RoleModuleService roleModuleService;

  @PutMapping("/save")
  public Result<Boolean> save(@RequestParam("roleId") Long roleId, @RequestBody List<Long> moduleIdList) {
    AssertUtils.notNull(roleId, "roleId");
    AssertUtils.notNull(moduleIdList);

    return Result.success(roleModuleService.save(roleId, moduleIdList));
  }

  @GetMapping("/queryModuleOfRole")
  public Result<BModuleTree> queryModuleOfRole(@RequestParam("roleId") Long roleId, @RequestParam("accessType") String accessType) {
    AssertUtils.notNull(roleId, "roleId");
    AssertUtils.notNull(accessType, "accessType");

    return Result.success(roleModuleService.queryModuleOfRole(roleId, accessType));
  }

  @GetMapping("/queryRoleOnModule")
  public Result<List<VRoleWithAllFields>> queryRoleOnModule(@RequestParam("moduleId") Long moduleId) {
    AssertUtils.notNull(moduleId, "moduleId");

    return Result.success(roleModuleService.queryRoleOnModule(moduleId),
            roleList -> roleList.stream()
                    .map(role -> BeanUtils.copyProperties(role, new VRoleWithAllFields()))
                    .collect(Collectors.toList()));
  }

}

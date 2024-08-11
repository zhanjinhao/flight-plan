package cn.addenda.fp.rbac.controller;

import cn.addenda.component.basaspring.util.AssertUtils;
import cn.addenda.component.jdk.result.Result;
import cn.addenda.component.jdk.util.BeanUtils;
import cn.addenda.fp.rbac.pojo.entity.Role;
import cn.addenda.fp.rbac.pojo.vo.VRole;
import cn.addenda.fp.rbac.service.RoleService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author addenda
 * @since 2022/2/7 16:43
 */
@RestController
@RequestMapping("/role")
public class RoleController {

  @Autowired
  private RoleService roleService;

  @PostMapping("/insert")
  public Result<Long> insert(@RequestBody VRole role) {
    AssertUtils.notNull(role);
    AssertUtils.notNull(role.getRoleCode(), "roleCode");
    AssertUtils.notNull(role.getRoleName(), "roleName");

    return Result.success(roleService.insert(BeanUtils.copyProperties(role, new Role())));
  }

  @PostMapping("/pageQuery")
  public Result<PageInfo<Role>> pageQuery(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize, @RequestBody VRole role) {
    AssertUtils.notNull(pageNum, "pageNum");
    AssertUtils.notNull(pageSize, "pageSize");
    AssertUtils.notNull(role);

    return Result.success(roleService.pageQuery(pageNum, pageSize, BeanUtils.copyProperties(role, new Role())));
  }

  @GetMapping("/queryById")
  public Result<Role> queryById(@RequestParam("id") Long id) {
    AssertUtils.notNull(id, "id");

    return Result.success(roleService.queryById(id));
  }

  @PutMapping("/update")
  public Result<Boolean> update(@RequestParam("id") Long id, @RequestBody VRole role) {
    AssertUtils.notNull(id, "id");
    AssertUtils.notNull(role);
    AssertUtils.notNull(role.getRoleName(), "roleName");

    AssertUtils.notModified(role.getRoleCode(), "roleCode");
    return Result.success(roleService.update(BeanUtils.copyProperties(role, new Role(id))));
  }

  @PutMapping("/setStatus")
  public Result<Boolean> setStatus(@RequestParam("id") Long id, @RequestBody String status) {
    AssertUtils.notNull(id, "id");
    AssertUtils.notNull(status);

    return Result.success(roleService.setStatus(id, status));
  }

  @DeleteMapping("/deleteById")
  public Result<Boolean> deleteById(@RequestBody Long id) {
    AssertUtils.notNull(id);

    return Result.success(roleService.deleteById(id));
  }

}

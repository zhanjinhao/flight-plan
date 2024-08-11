package cn.addenda.fp.rbac.controller;

import cn.addenda.component.basaspring.util.AssertUtils;
import cn.addenda.component.jdk.result.Result;
import cn.addenda.component.jdk.util.BeanUtils;
import cn.addenda.fp.rbac.pojo.entity.User;
import cn.addenda.fp.rbac.pojo.vo.VUser;
import cn.addenda.fp.rbac.service.UserService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author addenda
 * @since 2022/2/7 16:43
 */
@RestController
@RequestMapping("/user")
public class UserController {

  @Autowired
  private UserService userService;

  @PostMapping("/insert")
  public Result<Long> insert(@RequestBody VUser user) {
    AssertUtils.notNull(user);
    AssertUtils.notNull(user.getUserCode(), "userId");
    AssertUtils.notNull(user.getUserEmail(), "userEmail");
    AssertUtils.notNull(user.getUserName(), "userName");

    return Result.success(userService.insert(BeanUtils.copyProperties(user, new User())));
  }

  @PostMapping("/pageQuery")
  public Result<PageInfo<User>> pageQuery(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize, @RequestBody VUser user) {
    AssertUtils.notNull(pageNum, "pageNum");
    AssertUtils.notNull(pageSize, "pageSize");
    AssertUtils.notNull(user);

    return Result.success(userService.pageQuery(pageNum, pageSize, BeanUtils.copyProperties(user, new User())));
  }

  @GetMapping("/queryById")
  public Result<User> queryById(@RequestParam("id") Long id) {
    AssertUtils.notNull(id, "id");

    return Result.success(userService.queryById(id));
  }

  @PutMapping("/update")
  public Result<Boolean> update(@RequestParam("id") Long id, @RequestBody VUser user) {
    AssertUtils.notNull(id, "id");
    AssertUtils.notNull(user);
    AssertUtils.notNull(user.getUserName(), "userName");
    AssertUtils.notModified(user.getUserEmail(), "userEmail");
    AssertUtils.notModified(user.getUserCode(), "userId");

    return Result.success(userService.update(BeanUtils.copyProperties(user, new User(id))));
  }

  @PutMapping("/setStatus")
  public Result<Boolean> setStatus(@RequestParam("id") Long id, @RequestBody String status) {
    AssertUtils.notNull(id, "id");
    AssertUtils.notNull(status);

    return Result.success(userService.setStatus(id, status));
  }

  @DeleteMapping("/deleteById")
  public Result<Boolean> deleteById(@RequestBody Long id) {
    AssertUtils.notNull(id);

    return Result.success(userService.deleteById(id));
  }

}

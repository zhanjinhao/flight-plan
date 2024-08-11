package cn.addenda.fp.rbac.controller;

import cn.addenda.component.basaspring.util.AssertUtils;
import cn.addenda.component.jdk.result.Result;
import cn.addenda.component.jdk.util.BeanUtils;
import cn.addenda.component.user.UserInfo;
import cn.addenda.fp.rbac.pojo.entity.UserRole;
import cn.addenda.fp.rbac.pojo.entity.UserRoleRecord;
import cn.addenda.fp.rbac.pojo.vo.VUserRoleRecord;
import cn.addenda.fp.rbac.service.UserRoleRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author addenda
 * @since 2022/10/7 17:17
 */
@RestController
@RequestMapping("/userRoleRecord")
public class UserRoleRecordController {

  @Autowired
  private UserRoleRecordService userRoleRecordService;

  @PostMapping("/login")
  public Result<UserInfo> login(@RequestBody VUserRoleRecord userRoleRecord) {
    AssertUtils.notNull(userRoleRecord);
    AssertUtils.notNull(userRoleRecord.getUserId(), "userId");
    AssertUtils.notNull(userRoleRecord.getRoleId(), "roleId");

    return Result.success(userRoleRecordService.login(BeanUtils.copyProperties(userRoleRecord, new UserRoleRecord())));
  }

  @DeleteMapping("/exit")
  public Result<Boolean> exit(@RequestParam("userId") Long userId) {
    AssertUtils.notNull(userId, "userId");

    return Result.success(userRoleRecordService.exit(userId));
  }

  @GetMapping("/queryLoginRole")
  public Result<UserRole> queryLoginRole(@RequestParam("userId") Long userId) {
    AssertUtils.notNull(userId, "userId");

    return Result.success(userRoleRecordService.queryLoginRole(userId));
  }

}

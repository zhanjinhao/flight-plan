package cn.addenda.fp.rbac.controller;

import cn.addenda.component.basaspring.util.AssertUtils;
import cn.addenda.component.jdk.result.Result;
import cn.addenda.component.jdk.util.BeanUtils;
import cn.addenda.fp.rbac.pojo.entity.Rule;
import cn.addenda.fp.rbac.pojo.vo.VRule;
import cn.addenda.fp.rbac.pojo.vo.VUserRoleWithBizFields;
import cn.addenda.fp.rbac.service.RuleService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author addenda
 * @since 2022/10/19 19:16
 */
@RestController
@RequestMapping("/rule")
public class RuleController {

  @Autowired
  private RuleService ruleService;

  @PostMapping("/insert")
  public Result<Long> insert(@RequestBody VRule rule) {
    AssertUtils.notNull(rule);
    AssertUtils.notNull(rule.getRuleCode(), "ruleCode");
    AssertUtils.notNull(rule.getRuleName(), "ruleName");
    AssertUtils.notNull(rule.getTableName(), "tableName");
    AssertUtils.notNull(rule.getCondition(), "condition");

    return Result.success(ruleService.insert(BeanUtils.copyProperties(rule, new Rule())));
  }

  @PostMapping("/pageQuery")
  public Result<PageInfo<Rule>> pageQuery(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize, @RequestBody VRule rule) {
    AssertUtils.notNull(pageNum, "pageNum");
    AssertUtils.notNull(pageSize, "pageSize");
    AssertUtils.notNull(rule);

    return Result.success(ruleService.pageQuery(pageNum, pageSize, BeanUtils.copyProperties(rule, new Rule())));
  }

  @GetMapping("/queryById")
  public Result<Rule> queryById(@RequestParam("id") Long id) {
    AssertUtils.notNull(id, "id");

    return Result.success(ruleService.queryById(id));
  }

  @PutMapping("/update")
  public Result<Boolean> update(@RequestParam("id") Long id, @RequestBody VRule rule) {
    AssertUtils.notNull(id, "id");
    AssertUtils.notNull(rule);
    AssertUtils.notNull(rule.getRuleName(), "ruleName");
    AssertUtils.notNull(rule.getTableName(), "tableName");
    AssertUtils.notNull(rule.getCondition(), "condition");

    AssertUtils.notModified(rule.getRuleCode(), "ruleCode");
    return Result.success(ruleService.update(BeanUtils.copyProperties(rule, new Rule(id))));
  }

  @PutMapping("/setStatus")
  public Result<Boolean> setStatus(@RequestParam("id") Long id, @RequestBody String status) {
    AssertUtils.notNull(id, "id");
    AssertUtils.notNull(status);

    return Result.success(ruleService.setStatus(id, status));
  }

  @DeleteMapping("/deleteById")
  public Result<Boolean> deleteById(@RequestBody Long id) {
    AssertUtils.notNull(id);

    return Result.success(ruleService.deleteById(id));
  }

  @GetMapping("/queryUserRoleOnRule")
  public Result<List<VUserRoleWithBizFields>> queryUserRoleOnRule(@RequestParam("ruleId") Long ruleId) {
    AssertUtils.notNull(ruleId, "ruleId");

    return Result.success(ruleService.queryUserRoleOnRule(ruleId),
            urList -> urList.stream()
                    .map(ur -> BeanUtils.copyProperties(ur, new VUserRoleWithBizFields()))
                    .collect(Collectors.toList()));
  }

}

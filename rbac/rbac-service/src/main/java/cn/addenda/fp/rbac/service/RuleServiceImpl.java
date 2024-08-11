package cn.addenda.fp.rbac.service;

import cn.addenda.component.jdk.exception.ServiceException;
import cn.addenda.component.jdk.util.BeanUtils;
import cn.addenda.component.lockhelper.Locked;
import cn.addenda.component.transaction.TransactionHelper;
import cn.addenda.fp.rbac.manager.*;
import cn.addenda.fp.rbac.pojo.bo.BUserRoleWithBizFields;
import cn.addenda.fp.rbac.pojo.entity.Role;
import cn.addenda.fp.rbac.pojo.entity.Rule;
import cn.addenda.fp.rbac.pojo.entity.User;
import cn.addenda.fp.rbac.pojo.entity.UserRole;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author addenda
 * @since 2022/10/19 19:18
 */
@Component
public class RuleServiceImpl implements RuleService {

  @Autowired
  private RuleManager ruleManager;

  @Autowired
  private UserRoleManager userRoleManager;

  @Autowired
  private UserManager userManager;

  @Autowired
  private RoleManager roleManager;

  @Autowired
  private UserRoleRecordManager userRoleRecordManager;

  @Autowired
  private TransactionHelper transactionHelper;

  @Override
  @Locked(prefix = "rule:ruleCode", spEL = "#rule.ruleCode")
  public Long insert(Rule rule) {
    if (ruleManager.ruleCodeExists(rule.getRuleCode())) {
      throw new ServiceException("ruleCode已存在：" + rule.getRuleCode() + "。");
    }

    rule.setStatus(Rule.ACTIVE);
    transactionHelper.doTransaction(() -> ruleManager.insert(rule));
    return rule.getId();
  }

  @Override
  public PageInfo<Rule> pageQuery(Integer pageNum, Integer pageSize, Rule rule) {
    try {
      PageMethod.startPage(pageNum, pageSize);
      List<Rule> query = ruleManager.queryByNonNullFields(rule);
      return new PageInfo<>(query);
    } finally {
      PageMethod.clearPage();
    }
  }

  @Override
  public Rule queryById(Long id) {
    return ruleManager.queryById(id);
  }

  @Override
  public Boolean update(Rule rule) {
    if (!ruleManager.idExists(rule.getId())) {
      throw new ServiceException("id不存在：" + rule.getId() + "。 ");
    }

    return transactionHelper.doTransaction(() -> {
      ruleManager.update(rule);
      return true;
    });
  }

  @Override
  public Boolean setStatus(Long id, String status) {
    Rule.assertStatus(status);
    if (!ruleManager.idExists(id)) {
      throw new ServiceException("id不存在：" + id + "。 ");
    }

    return transactionHelper.doTransaction(() -> {
      ruleManager.setStatus(id, status);
      return true;
    });
  }

  @Override
  public Boolean deleteById(Long id) {
    // 如果rule被用户关联，则不可删除
    if (userRoleManager.ruleIdExists(id)) {
      throw new ServiceException("此Rule正被用户使用，不可删除！");
    }

    return transactionHelper.doTransaction(() -> {
      ruleManager.deleteById(id);
      return true;
    });
  }

  @Override
  public List<BUserRoleWithBizFields> queryUserRoleOnRule(Long ruleId) {
    if (!ruleManager.idExists(ruleId)) {
      throw new ServiceException("ruleId不存在！");
    }
    List<UserRole> userRoleList = userRoleManager.queryUserRoleOnRule(ruleId);
    List<BUserRoleWithBizFields> resultList = new ArrayList<>();
    for (UserRole userRole : userRoleList) {
      BUserRoleWithBizFields result = BeanUtils.copyProperties(userRole, new BUserRoleWithBizFields());
      User user = userManager.queryById(result.getUserId());
      BeanUtils.copyProperties(user, result);
      Role role = roleManager.queryById(result.getRoleId());
      BeanUtils.copyProperties(role, result);
      resultList.add(result);
    }
    return resultList;
  }

  @Override
  public List<Rule> queryRuleList(String userCode) {
    User user = userManager.queryByUserCode(userCode);
    if (user == null) {
      throw new ServiceException("用户不存在：" + userCode + "。");
    }
    UserRole userRole = userRoleRecordManager.queryLoginRole(user.getId());
    if (userRole == null) {
      throw new ServiceException("用户未登录：" + userCode + "。");
    }
    String ruleIdListStr = userRole.getRuleIdList();
    if (!StringUtils.hasText(ruleIdListStr)) {
      return new ArrayList<>();
    }
    List<Long> ruleIdList = Arrays.stream(ruleIdListStr.split(","))
            .map(Long::valueOf).collect(Collectors.toList());
    return ruleManager.queryByRuleIdList(ruleIdList);
  }

}

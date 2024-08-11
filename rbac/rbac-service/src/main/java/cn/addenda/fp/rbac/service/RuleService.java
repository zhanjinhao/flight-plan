package cn.addenda.fp.rbac.service;

import cn.addenda.fp.rbac.pojo.bo.BUserRoleWithBizFields;
import cn.addenda.fp.rbac.pojo.entity.Rule;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @author addenda
 * @since 2022/10/19 19:18
 */
public interface RuleService {

  PageInfo<Rule> pageQuery(Integer pageNum, Integer pageSize, Rule rule);

  Rule queryById(Long id);

  Long insert(Rule rule);

  Boolean deleteById(Long id);

  Boolean update(Rule rule);

  Boolean setStatus(Long id, String status);

  List<BUserRoleWithBizFields> queryUserRoleOnRule(Long ruleId);

  List<Rule> queryRuleList(String userId);

}

package cn.addenda.fp.rbac.manager;

import cn.addenda.fp.rbac.pojo.entity.Rule;

import java.util.List;

public interface RuleManager {

  /**
   * 默认权限是全权限，即不进行任何过滤
   */
  String defaultRuleIdList();

  List<Rule> queryByNonNullFields(Rule rule);

  Rule queryById(Long id);

  boolean ruleCodeExists(String ruleCode);

  void insert(Rule rule);

  void deleteById(Long id);

  boolean idExists(Long id);

  void update(Rule rule);

  void setStatus(Long id, String status);

  Rule queryByRuleCode(String ruleCode);

  List<Rule> queryByRuleIdList(List<Long> ruleIdList);

}

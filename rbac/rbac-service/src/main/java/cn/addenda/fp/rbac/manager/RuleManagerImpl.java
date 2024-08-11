package cn.addenda.fp.rbac.manager;

import cn.addenda.component.cachehelper.CacheHelper;
import cn.addenda.fp.rbac.constant.RedisKeyConst;
import cn.addenda.fp.rbac.mapper.RuleMapper;
import cn.addenda.fp.rbac.pojo.entity.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author addenda
 * @since 2022/10/13 19:04
 */
@Component
public class RuleManagerImpl implements RuleManager {

  @Autowired
  private RuleMapper ruleMapper;

  @Autowired
  private CacheHelper redisCacheHelper;

  @Override
  public String defaultRuleIdList() {
    return "0";
  }

  @Override
  public List<Rule> queryByNonNullFields(Rule rule) {
    return ruleMapper.queryByNonNullFields(rule);
  }

  @Override
  public Rule queryById(Long id) {
    return redisCacheHelper.queryWithPpf(RedisKeyConst.RULE_ID_KEY,
            id, Rule.class, this::doQueryById, RedisKeyConst.CACHE_DEFAULT_TTL);
  }

  private Rule doQueryById(Long id) {
    Rule rule = new Rule();
    rule.setId(id);
    List<Rule> rules = ruleMapper.queryByNonNullFields(rule);
    if (rules.isEmpty()) {
      return null;
    }
    return rules.get(0);
  }

  @Override
  public boolean ruleCodeExists(String ruleCode) {
    Integer integer = ruleMapper.ruleCodeExists(ruleCode);
    return integer != null && integer != 0;
  }

  @Override
  public void insert(Rule rule) {
    ruleMapper.insert(rule);
  }

  @Override
  public void deleteById(Long id) {
    ruleMapper.deleteById(id);
  }

  @Override
  public boolean idExists(Long id) {
    Integer integer = ruleMapper.idExists(id);
    return integer != null && integer != 0;
  }

  @Override
  public void update(Rule rule) {
    ruleMapper.updateNonNullFieldsById(rule);
  }

  @Override
  public void setStatus(Long id, String status) {
    Rule rule = new Rule();
    rule.setId(id);
    rule.setStatus(status);
    ruleMapper.updateNonNullFieldsById(rule);
  }

  @Override
  public Rule queryByRuleCode(String ruleCode) {
    return redisCacheHelper.queryWithPpf(RedisKeyConst.RULE_RULE_CODE_KEY,
            ruleCode, Rule.class, ruleMapper::queryByRuleCode, RedisKeyConst.CACHE_DEFAULT_TTL);
  }

  @Override
  public List<Rule> queryByRuleIdList(List<Long> ruleIdList) {
    return ruleIdList.stream().map(this::queryById).collect(Collectors.toList());
  }

}

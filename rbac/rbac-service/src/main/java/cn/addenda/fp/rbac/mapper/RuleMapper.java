package cn.addenda.fp.rbac.mapper;

import cn.addenda.fp.rbac.pojo.entity.Rule;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RuleMapper {

  void insert(Rule rule);

  Integer ruleCodeExists(String ruleCode);

  Integer idExists(Long id);

  void deleteById(@Param("id") Long id);

  void updateNonNullFieldsById(Rule rule);

  List<Rule> queryByNonNullFields(Rule rule);

  Rule queryByRuleCode(@Param("ruleCode") String ruleCode);

}

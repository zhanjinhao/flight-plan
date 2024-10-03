package cn.addenda.fp.rbac.rpc;

import cn.addenda.component.basaspring.util.AssertUtils;
import cn.addenda.component.jdk.util.BeanUtils;
import cn.addenda.fp.rbac.dto.RuleDto;
import cn.addenda.fp.rbac.pojo.entity.Rule;
import cn.addenda.fp.rbac.service.RuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author addenda
 * @since 2022/12/4 14:11
 */
@RestController
@RequestMapping("/ruleRpc")
public class RuleRpcImpl implements RuleRpc {

  @Autowired
  private RuleService ruleService;

  @Override
  public List<RuleDto> queryRuleList(String userId) {
    AssertUtils.notNull(userId);
    List<Rule> rules = ruleService.queryRuleList(userId);
    return rules.stream().map(r -> BeanUtils.copyProperties(r, new RuleDto())).collect(Collectors.toList());
  }

}

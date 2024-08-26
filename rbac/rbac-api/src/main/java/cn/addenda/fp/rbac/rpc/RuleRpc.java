package cn.addenda.fp.rbac.rpc;

import cn.addenda.fp.rbac.dto.RuleDto;

import java.util.List;

/**
 * @author addenda
 * @since 2022/11/26 20:45
 */
public interface RuleRpc {

  List<RuleDto> queryRuleList(String userId);

}

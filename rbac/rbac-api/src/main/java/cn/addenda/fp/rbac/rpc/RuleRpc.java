package cn.addenda.fp.rbac.rpc;

import cn.addenda.fp.rbac.config.FeignConfiguration;
import cn.addenda.fp.rbac.dto.RuleDto;
import org.springframework.cloud.openfeign.FeignClient;

import java.util.List;

/**
 * @author addenda
 * @since 2022/11/26 20:45
 */
@FeignClient(
        value = "rbac-service",
        contextId = "rule",
        path = "/rbac/ruleRpc",
        configuration = FeignConfiguration.class)
public interface RuleRpc {

  List<RuleDto> queryRuleList(String userId);

}

package cn.addenda.fp.rbac.constant;

public class RedisKeyConst {

  private RedisKeyConst() {
  }

  public static final Long CACHE_DEFAULT_TTL = 30 * 60 * 1000L;

  public static final String USER_ID_KEY = "rbac:user:id:";
  public static final String USER_USER_CODE_KEY = "rbac:user:userCode:";

  public static final String MODULE_ID_KEY = "rbac:module:id:";
  public static final String MODULE_MODULE_CODE_KEY = "rbac:module:moduleCode:";

  public static final String ROLE_ID_KEY = "rbac:role:id:";
  public static final String ROLE_ROLE_CODE_KEY = "rbac:role:roleCode:";

  public static final String RULE_ID_KEY = "rbac:rule:id:";
  public static final String RULE_RULE_CODE_KEY = "rbac:rule:ruleCode:";

}

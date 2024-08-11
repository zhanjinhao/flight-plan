package cn.addenda.fp.rbac.pojo.entity;

import cn.addenda.component.idgenerator.annotation.IdScope;
import cn.addenda.component.jdk.exception.ServiceException;
import cn.addenda.sql.vitamin.rewriter.baseentity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author addenda
 * @since 2022/10/19 19:23
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
@IdScope(scopeName = "Rule")
public class Rule extends BaseEntity {
  public static final String DISABLE = "D";

  public static final String ACTIVE = "A";

  private Long id;

  private String ruleCode;

  private String ruleName;

  private String tableName;

  private String condition;

  private String status;

  public static void assertStatus(String status) {
    if (!ACTIVE.equals(status) && !DISABLE.equals(status)) {
      throw new ServiceException("不合法的状态：" + status + "。");
    }
  }

  public Rule(Long id) {
    this.id = id;
  }
}

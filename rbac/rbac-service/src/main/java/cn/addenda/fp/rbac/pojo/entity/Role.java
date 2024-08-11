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
 * @since 2022/1/17 20:31
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@IdScope(scopeName = "Role")
public class Role extends BaseEntity {

  public static final String DISABLE = "D";

  public static final String ACTIVE = "A";

  private Long id;

  /**
   * 唯一索引
   */
  private String roleCode;

  private String roleName;

  private String status;

  public static void assertStatus(String status) {
    if (!ACTIVE.equals(status) && !DISABLE.equals(status)) {
      throw new ServiceException("不合法的状态：" + status + "。");
    }
  }

  public Role(Long id) {
    this.id = id;
  }
}

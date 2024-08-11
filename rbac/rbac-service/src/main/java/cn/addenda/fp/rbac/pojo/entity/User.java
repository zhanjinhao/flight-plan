package cn.addenda.fp.rbac.pojo.entity;

import cn.addenda.component.idgenerator.annotation.IdScope;
import cn.addenda.sql.vitamin.rewriter.baseentity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author addenda
 * @since 2022/1/17 20:28
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@IdScope(scopeName = "User")
public class User extends BaseEntity {

  public static final String ON_JOB = "J";

  public static final String RETIRE = "R";

  public static final String LEAVE = "L";

  private Long id;

  /**
   * 唯一索引
   */
  private String userCode;

  private String userName;

  private String userEmail;

  private String status;

  public User(Long id) {
    this.id = id;
  }
}

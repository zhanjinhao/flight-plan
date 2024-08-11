package cn.addenda.fp.rbac.pojo.entity;

import cn.addenda.component.idgenerator.annotation.IdScope;
import cn.addenda.sql.vitamin.rewriter.baseentity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author addenda
 * @since 2022/1/17 20:46
 */
@Getter
@Setter
@ToString
@IdScope(scopeName = "UserRoleRecord")
public class UserRoleRecord extends BaseEntity {

  public static final String TYPE_ENTER = "ER";

  public static final String TYPE_CHANGE_ROLE = "CR";

  private Long id;

  private Long userId;

  private Long roleId;

  private String type;

}

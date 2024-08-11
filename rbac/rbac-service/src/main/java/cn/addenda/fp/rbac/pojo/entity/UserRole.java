package cn.addenda.fp.rbac.pojo.entity;

import cn.addenda.component.idgenerator.annotation.IdScope;
import cn.addenda.sql.vitamin.rewriter.baseentity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author addenda
 * @since 2022/1/17 20:44
 */
@Getter
@Setter
@ToString(callSuper = true)
@IdScope(scopeName = "UserRole")
public class UserRole extends BaseEntity {

  public UserRole() {
  }

  public UserRole(Long userId, Long roleId, String accessType, String ruleIdList) {
    this.userId = userId;
    this.roleId = roleId;
    this.accessType = accessType;
    this.ruleIdList = ruleIdList;
  }

  private Long id;

  private Long userId;

  private Long roleId;

  private String accessType;

  private String ruleIdList;

}

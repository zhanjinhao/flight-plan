package cn.addenda.fp.rbac.pojo.entity;

import cn.addenda.component.idgenerator.annotation.IdScope;
import cn.addenda.sql.vitamin.rewriter.baseentity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author addenda
 * @since 2022/1/17 20:45
 */
@Getter
@Setter
@ToString
@IdScope(scopeName = "RoleModule")
public class RoleModule extends BaseEntity {

  public RoleModule() {
  }

  public RoleModule(long roleId, long moduleId) {
    this.roleId = roleId;
    this.moduleId = moduleId;
  }

  private Long id;

  private Long roleId;

  private Long moduleId;

}

package cn.addenda.fp.rbac.pojo.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author addenda
 * @since 2022/10/10 15:23
 */
@Setter
@Getter
@ToString
public class VUserRoleRecord {

  private Long userId;

  private Long roleId;

}

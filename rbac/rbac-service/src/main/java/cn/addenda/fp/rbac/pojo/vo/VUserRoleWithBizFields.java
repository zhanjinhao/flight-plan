package cn.addenda.fp.rbac.pojo.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author addenda
 * @since 2022/10/19 20:24
 */
@Setter
@Getter
@ToString
public class VUserRoleWithBizFields {

  private Long id;

  private Long userId;

  private Long roleId;

  private String userCode;

  private String userName;

  private String userEmail;

  private String roleCode;

  private String roleName;

}

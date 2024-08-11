package cn.addenda.fp.rbac.pojo.bo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author addenda
 * @since 2022/10/20 18:17
 */
@Setter
@Getter
@ToString
public class BUserRoleWithBizFields {

  private Long id;

  private Long userId;

  private Long roleId;

  private String userCode;

  private String userName;

  private String userEmail;

  private String roleCode;

  private String roleName;

}

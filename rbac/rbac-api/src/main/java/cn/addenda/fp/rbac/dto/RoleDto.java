package cn.addenda.fp.rbac.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author addenda
 * @since 2022/10/19 19:27
 */
@Setter
@Getter
@ToString
public class RoleDto implements Serializable {

  private static final long serialVersionUID = 1L;

  private String roleCode;

  private String roleName;

  private String status;

}

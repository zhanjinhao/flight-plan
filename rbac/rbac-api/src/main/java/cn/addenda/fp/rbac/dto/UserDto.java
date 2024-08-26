package cn.addenda.fp.rbac.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author addenda
 * @since 2022/11/26 20:45
 */
@Setter
@Getter
@ToString
public class UserDto implements Serializable {

  private static final long serialVersionUID = 1L;

  private String userCode;

  private String userName;

  private String userEmail;

  private String status;

}
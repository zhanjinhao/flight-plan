package cn.addenda.fp.rbac.pojo.vo;

import cn.addenda.fp.rbac.pojo.entity.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author addenda
 * @since 2022/10/19 19:06
 */
@Setter
@Getter
@ToString(callSuper = true)
@JsonIgnoreProperties
public class VRoleWithAllFields extends Role {
}

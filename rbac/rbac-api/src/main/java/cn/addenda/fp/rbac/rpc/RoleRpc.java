package cn.addenda.fp.rbac.rpc;

import cn.addenda.fp.rbac.dto.DRole;

/**
 * @author addenda
 * @since 2022/11/26 20:45
 */
public interface RoleRpc {

  DRole queryByRoleCode(String roleCode);

}

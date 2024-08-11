package cn.addenda.fp.rbac.rpc;

import cn.addenda.fp.rbac.dto.DUser;

/**
 * @author addenda
 * @since 2022/10/15 10:20
 */
public interface UserRpc {

  DUser queryByUserCode(String userCode);

}

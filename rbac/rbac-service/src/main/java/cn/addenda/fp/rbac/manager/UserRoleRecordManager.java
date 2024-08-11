package cn.addenda.fp.rbac.manager;

import cn.addenda.fp.rbac.pojo.entity.UserRole;
import cn.addenda.fp.rbac.pojo.entity.UserRoleRecord;

/**
 * @author addenda
 * @since 2022/10/10 15:25
 */
public interface UserRoleRecordManager {

    UserRoleRecord queryUserRoleRecordByUserId(Long userId);

    void insert(UserRoleRecord userRoleRecord);

    void deleteByUserId(Long userId);

    UserRole queryLoginRole(Long userId);

}

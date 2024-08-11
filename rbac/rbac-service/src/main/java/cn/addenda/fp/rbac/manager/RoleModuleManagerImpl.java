package cn.addenda.fp.rbac.manager;

import cn.addenda.component.basemybatis.helper.MybatisBatchDmlHelper;
import cn.addenda.fp.rbac.mapper.RoleModuleMapper;
import cn.addenda.fp.rbac.pojo.entity.Role;
import cn.addenda.fp.rbac.pojo.entity.RoleModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author addenda
 * @since 2022/10/15 15:14
 */
@Component
public class RoleModuleManagerImpl implements RoleModuleManager {

  @Autowired
  private RoleModuleMapper roleModuleMapper;

  @Autowired
  private MybatisBatchDmlHelper mybatisBatchOperationHelper;

  @Override
  public List<RoleModule> queryModuleOfRole(Long roleId) {
    return roleModuleMapper.queryModuleOfRole(roleId);
  }

  @Override
  public void batchDeleteById(List<Long> deleteList) {
    mybatisBatchOperationHelper.batch(
            RoleModuleMapper.class, deleteList, RoleModuleMapper::deleteById);
  }

  @Override
  public void batchInsert(List<RoleModule> insertList) {
    mybatisBatchOperationHelper.batch(
            RoleModuleMapper.class, insertList, RoleModuleMapper::insert);
  }

  @Override
  public List<Role> queryRoleOnModule(Long moduleId) {
    return roleModuleMapper.queryRoleOnModule(moduleId);
  }

  @Override
  public void deleteByRoleId(Long roleId) {
    roleModuleMapper.deleteByRoleId(roleId);
  }

  @Override
  public boolean moduleIdExists(Long moduleId) {
    Integer integer = roleModuleMapper.moduleIdExists(moduleId);
    return integer != null && integer != 0;
  }

}

package cn.addenda.fp.rbac.manager;

import cn.addenda.component.cachehelper.CacheHelper;
import cn.addenda.fp.rbac.constant.RedisKeyConst;
import cn.addenda.fp.rbac.mapper.RoleMapper;
import cn.addenda.fp.rbac.pojo.entity.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author addenda
 * @since 2022/10/9 15:41
 */
@Component
public class RoleManagerImpl implements RoleManager {

  @Autowired
  private RoleMapper roleMapper;

  @Autowired
  private CacheHelper redisCacheHelper;

  @Override
  public boolean roleCodeExists(String roleCode) {
    Integer integer = roleMapper.roleCodeExists(roleCode);
    return integer != null && integer > 0;
  }

  @Override
  public boolean idExists(Long id) {
    Integer integer = roleMapper.idExists(id);
    return integer != null && integer > 0;
  }

  @Override
  public void insert(Role role) {
    roleMapper.insert(role);
  }

  @Override
  public void deleteById(Long id) {
    roleMapper.deleteById(id);
  }

  @Override
  public void setStatus(Long id, String status) {
    Role role = new Role();
    role.setId(id);
    role.setStatus(status);
    roleMapper.updateNonNullFieldsById(role);
  }

  @Override
  public Role queryByRoleCode(String roleCode) {
    return redisCacheHelper.queryWithPpf(RedisKeyConst.ROLE_ROLE_CODE_KEY,
            roleCode, Role.class, roleMapper::queryByRoleCode, RedisKeyConst.CACHE_DEFAULT_TTL);
  }

  @Override
  public List<Role> queryByRoleCodeList(List<String> roleCodeList) {
    if (CollectionUtils.isEmpty(roleCodeList)) {
      return new ArrayList<>();
    }
    return roleMapper.queryByRoleCodeList(roleCodeList);
  }

  @Override
  public List<Role> queryByIdList(List<Long> idList) {
    if (CollectionUtils.isEmpty(idList)) {
      return new ArrayList<>();
    }
    return roleMapper.queryByIdList(idList);
  }

  @Override
  public List<Role> queryByNonNullFields(Role role) {
    return roleMapper.queryByNonNullFields(role);
  }

  @Override
  public Role queryById(Long id) {
    return redisCacheHelper.queryWithPpf(RedisKeyConst.ROLE_ID_KEY,
            id, Role.class, this::doQueryById, RedisKeyConst.CACHE_DEFAULT_TTL);
  }

  private Role doQueryById(Long id) {
    Role role = new Role();
    role.setId(id);
    List<Role> roles = roleMapper.queryByNonNullFields(role);
    if (roles.isEmpty()) {
      return null;
    }
    return roles.get(0);
  }

  @Override
  public void update(Role role) {
    roleMapper.updateNonNullFieldsById(role);
  }

}

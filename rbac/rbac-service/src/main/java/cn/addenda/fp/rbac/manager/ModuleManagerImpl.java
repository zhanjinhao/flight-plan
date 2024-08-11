package cn.addenda.fp.rbac.manager;

import cn.addenda.component.cachehelper.CacheHelper;
import cn.addenda.fp.rbac.constant.RedisKeyConst;
import cn.addenda.fp.rbac.mapper.ModuleMapper;
import cn.addenda.fp.rbac.pojo.entity.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author addenda
 * @since 2022/10/10 11:27
 */
@Component
public class ModuleManagerImpl implements ModuleManager {

  @Autowired
  private ModuleMapper moduleMapper;

  @Autowired
  private CacheHelper redisCacheHelper;

  @Override
  public boolean moduleCodeExists(String moduleCode) {
    Integer integer = moduleMapper.moduleCodeExists(moduleCode);
    return integer != null && integer > 0;
  }

  @Override
  public void insert(Module module) {
    moduleMapper.insert(module);
  }

  @Override
  public boolean idExists(Long id) {
    Integer integer = moduleMapper.idExists(id);
    return integer != null && integer > 0;
  }

  @Override
  public void deleteById(Long id) {
    moduleMapper.deleteById(id);
  }

  @Override
  public void setStatus(Long id, String status) {
    Module module = new Module();
    module.setId(id);
    module.setStatus(status);
    moduleMapper.updateNonNullFieldsById(module);
  }

  @Override
  public Module queryByModuleCode(String moduleCode) {
    return redisCacheHelper.queryWithPpf(RedisKeyConst.MODULE_MODULE_CODE_KEY,
            moduleCode, Module.class, moduleMapper::queryByModuleCode, RedisKeyConst.CACHE_DEFAULT_TTL);
  }

  @Override
  public List<Module> queryByNonNullFields(Module module) {
    return moduleMapper.queryByNonNullFields(module);
  }

  @Override
  public Module queryById(Long id) {
    return redisCacheHelper.queryWithPpf(RedisKeyConst.MODULE_ID_KEY,
            id, Module.class, this::doQueryById, RedisKeyConst.CACHE_DEFAULT_TTL);
  }

  private Module doQueryById(Long id) {
    Module module = new Module();
    module.setId(id);
    List<Module> moduleList = moduleMapper.queryByNonNullFields(module);
    if (moduleList.isEmpty()) {
      return null;
    }
    return moduleList.get(0);
  }

  @Override
  public void update(Module module) {
    redisCacheHelper.acceptWithPpf(
            RedisKeyConst.MODULE_ID_KEY, module.getId(),
            aLong -> moduleMapper.updateNonNullFieldsById(module));
  }
}

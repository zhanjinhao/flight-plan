package cn.addenda.fp.rbac.manager;

import cn.addenda.fp.rbac.pojo.entity.Module;

import java.util.List;

/**
 * @author addenda
 * @since 2022/10/10 11:27
 */
public interface ModuleManager {

  boolean moduleCodeExists(String moduleCode);

  void insert(Module module);

  boolean idExists(Long id);

  void deleteById(Long id);

  void setStatus(Long id, String status);

  Module queryByModuleCode(String moduleCode);

  List<Module> queryByNonNullFields(Module module);

  Module queryById(Long id);

  void update(Module module);

}

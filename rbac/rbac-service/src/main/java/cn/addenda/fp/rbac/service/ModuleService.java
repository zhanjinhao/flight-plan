package cn.addenda.fp.rbac.service;

import cn.addenda.fp.rbac.pojo.entity.Module;
import com.github.pagehelper.PageInfo;

/**
 * @author addenda
 * @since 2022/2/7 17:16
 */
public interface ModuleService {

  Long rootId();

  Long insert(Module module);

  PageInfo<Module> pageQuery(Integer pageNum, Integer pageSize, Module module);

  Module queryById(Long id);

  Boolean update(Module module);

  Boolean setStatus(Long id, String status);

  Boolean deleteById(Long id);

  Module queryByModuleCode(String moduleCode);

}

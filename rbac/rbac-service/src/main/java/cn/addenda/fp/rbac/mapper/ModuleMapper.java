package cn.addenda.fp.rbac.mapper;

import cn.addenda.fp.rbac.pojo.entity.Module;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author addenda
 * @since 2022/2/7 15:56
 */
public interface ModuleMapper {

  void insert(Module module);

  Integer moduleCodeExists(@Param("moduleCode") String moduleCode);

  Integer idExists(@Param("id") Long id);

  void deleteById(@Param("id") Long id);

  void updateNonNullFieldsById(Module module);

  Module queryByModuleCode(@Param("moduleCode") String moduleCode);

  List<Module> queryByNonNullFields(Module module);

}

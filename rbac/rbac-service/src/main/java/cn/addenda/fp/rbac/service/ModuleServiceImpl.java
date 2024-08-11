package cn.addenda.fp.rbac.service;

import cn.addenda.component.jdk.exception.ServiceException;
import cn.addenda.component.lockhelper.LockHelper;
import cn.addenda.component.lockhelper.Locked;
import cn.addenda.component.transaction.TransactionHelper;
import cn.addenda.fp.rbac.manager.ModuleManager;
import cn.addenda.fp.rbac.manager.RoleModuleManager;
import cn.addenda.fp.rbac.pojo.entity.Module;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.page.PageMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author addenda
 * @since 2022/2/7 17:16
 */
@Component
public class ModuleServiceImpl implements ModuleService {

  @Autowired
  private ModuleManager moduleManager;

  @Autowired
  private RoleModuleManager roleModuleManager;

  @Autowired
  private LockHelper lockHelper;

  @Autowired
  private TransactionHelper transactionHelper;

  @Override
  public Long rootId() {
    return Module.ROOT_ID;
  }

  @Override
  @Locked(prefix = "module:moduleCode", spEL = "#module.moduleCode")
  public Long insert(Module module) {
    assertModule(module);
    if (moduleManager.moduleCodeExists(module.getModuleCode())) {
      throw new ServiceException("moduleCode已存在：" + module.getModuleCode() + "。");
    }
    return lockHelper.lock("module:root", () -> {
      // 特殊处理根目录
      if (Module.PARENT_ID_OF_ROOT.equals(module.getParentId())) {
        if (moduleManager.idExists(0L)) {
          throw new ServiceException("根目录已存在！");
        }
        module.setId(0L);
      }
      module.setStatus(Module.ACTIVE);
      transactionHelper.doTransaction(() -> moduleManager.insert(module));
      return module.getId();
    }, 0);
  }

  @Override
  public PageInfo<Module> pageQuery(Integer pageNum, Integer pageSize, Module module) {
    try {
      PageMethod.startPage(pageNum, pageSize);
      List<Module> query = moduleManager.queryByNonNullFields(module);
      return new PageInfo<>(query);
    } finally {
      PageMethod.clearPage();
    }
  }

  @Override
  public Module queryById(Long id) {
    return moduleManager.queryById(id);
  }

  @Override
  public Boolean update(Module module) {
    assertModule(module);
    if (!moduleManager.idExists(module.getId())) {
      throw new ServiceException("id不存在：" + module.getId() + "。");
    }

    return transactionHelper.doTransaction(() -> {
      moduleManager.update(module);
      return true;
    });
  }

  @Override
  public Boolean setStatus(Long id, String status) {
    Module.assertStatus(status);
    if (!moduleManager.idExists(id)) {
      throw new ServiceException("id不存在：" + id + "。 ");
    }

    return transactionHelper.doTransaction(() -> {
      moduleManager.setStatus(id, status);
      return true;
    });
  }

  @Override
  public Boolean deleteById(Long id) {
    // 如果module被角色关联，则不可删除
    if (roleModuleManager.moduleIdExists(id)) {
      throw new ServiceException("此Module正被角色使用，不可删除！");
    }

    return transactionHelper.doTransaction(() -> {
      moduleManager.deleteById(id);
      return true;
    });
  }

  @Override
  public Module queryByModuleCode(String moduleCode) {
    return moduleManager.queryByModuleCode(moduleCode);
  }

  private void assertModule(Module module) {
    if (!Module.AT_READ.equals(module.getAccessType())
            && !Module.AT_WRITE.equals(module.getAccessType())) {
      throw new ServiceException("不合法的accessType：" + module.getAccessType() + "。");
    }
    if (!Module.ST_NAVIGATION.equals(module.getShowType())
            && !Module.ST_PAGE.equals(module.getShowType()) && !Module.ST_FUNCTION.equals(module.getShowType())) {
      throw new ServiceException("不合法的showType：" + module.getShowType() + "。");
    }
    if (!Module.RTT_CURRENT.equals(module.getResponseToType())
            && !Module.RTT_NEW.equals(module.getResponseToType()) && !Module.RTT_JUMP.equals(module.getResponseToType())) {
      throw new ServiceException("不合法的responseToType：" + module.getResponseToType() + "。");
    }
  }

}

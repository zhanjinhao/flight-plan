package cn.addenda.fp.rbac.controller;

import cn.addenda.component.basaspring.util.AssertUtils;
import cn.addenda.component.jdk.result.Result;
import cn.addenda.component.jdk.util.BeanUtils;
import cn.addenda.fp.rbac.pojo.entity.Module;
import cn.addenda.fp.rbac.pojo.vo.VModule;
import cn.addenda.fp.rbac.service.ModuleService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author addenda
 * @since 2022/2/7 16:43
 */
@RestController
@RequestMapping("/module")
public class ModuleController {

  @Autowired
  private ModuleService moduleService;

  @GetMapping("/rootId")
  public Result<Long> rootId() {
    return Result.success(moduleService.rootId());
  }

  @PostMapping("/insert")
  public Result<Long> insert(@RequestBody VModule module) {
    AssertUtils.notNull(module);
    AssertUtils.notNull(module.getModuleCode(), "moduleCode");
    AssertUtils.notNull(module.getModuleName(), "moduleName");
    AssertUtils.notNull(module.getParentId(), "parentId");
    AssertUtils.notNull(module.getAction(), "action");
    AssertUtils.notNull(module.getShowType(), "showType");
    AssertUtils.notNull(module.getResponseToType(), "responseToType");

    return Result.success(moduleService.insert(BeanUtils.copyProperties(module, new Module())));
  }

  @PostMapping("/pageQuery")
  public Result<PageInfo<Module>> pageQuery(@RequestParam("pageNum") Integer pageNum, @RequestParam("pageSize") Integer pageSize, @RequestBody VModule module) {
    AssertUtils.notNull(pageNum, "pageNum");
    AssertUtils.notNull(pageSize, "pageSize");
    AssertUtils.notNull(module);

    return Result.success(moduleService.pageQuery(pageNum, pageSize, BeanUtils.copyProperties(module, new Module())));
  }

  @GetMapping("/queryById")
  public Result<Module> queryById(@RequestParam("id") Long id) {
    AssertUtils.notNull(id, "id");

    return Result.success(moduleService.queryById(id));
  }

  @PutMapping("/setStatus")
  public Result<Boolean> setStatus(@RequestParam("id") Long id, @RequestBody String status) {
    AssertUtils.notNull(id, "id");
    AssertUtils.notNull(status);

    return Result.success(moduleService.setStatus(id, status));
  }

  @PutMapping("/update")
  public Result<Boolean> update(@RequestParam("id") Long id, @RequestBody VModule module) {
    AssertUtils.notNull(id, "id");
    AssertUtils.notNull(module);
    AssertUtils.notNull(module.getModuleName(), "moduleName");
    AssertUtils.notNull(module.getAction(), "action");
    AssertUtils.notNull(module.getAccessType(), "accessType");
    AssertUtils.notNull(module.getShowType(), "showType");
    AssertUtils.notNull(module.getResponseToType(), "responseToType");

    AssertUtils.notModified(module.getModuleCode(), "moduleCode");
    AssertUtils.notModified(module.getParentId(), "parentId");

    return Result.success(moduleService.update(BeanUtils.copyProperties(module, new Module(id))));
  }

  @DeleteMapping("/deleteById")
  public Result<Boolean> deleteById(@RequestBody Long id) {
    AssertUtils.notNull(id);

    return Result.success(moduleService.deleteById(id));
  }

}

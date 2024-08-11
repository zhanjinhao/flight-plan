package cn.addenda.fp.rbac.service;

import cn.addenda.component.jdk.exception.ServiceException;
import cn.addenda.component.jdk.pojo.Ternary;
import cn.addenda.component.jdk.util.BeanUtils;
import cn.addenda.component.jdk.util.collection.IterableUtils;
import cn.addenda.component.lockhelper.Locked;
import cn.addenda.component.transaction.TransactionAttrBuilder;
import cn.addenda.component.transaction.TransactionHelper;
import cn.addenda.fp.rbac.manager.ModuleManager;
import cn.addenda.fp.rbac.manager.RoleManager;
import cn.addenda.fp.rbac.manager.RoleModuleManager;
import cn.addenda.fp.rbac.pojo.bo.BModuleTree;
import cn.addenda.fp.rbac.pojo.entity.Module;
import cn.addenda.fp.rbac.pojo.entity.Role;
import cn.addenda.fp.rbac.pojo.entity.RoleModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.interceptor.TransactionAttribute;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author addenda
 * @since 2022/2/7 17:16
 */
@Component
public class RoleModuleServiceImpl implements RoleModuleService {

  @Autowired
  private RoleModuleManager roleModuleManager;

  @Autowired
  private RoleManager roleManager;

  @Autowired
  private ModuleManager moduleManager;

  @Autowired
  private TransactionHelper transactionHelper;

  @Locked(prefix = "role:roleId")
  @Override
  public Boolean save(Long roleId, List<Long> moduleIdList) {
    if (!roleManager.idExists(roleId)) {
      throw new ServiceException("roleId不存在：" + roleId + "。");
    }

    TransactionAttribute rrAttribute = TransactionAttrBuilder.newRRBuilder().build();
    return transactionHelper.doTransaction(rrAttribute, () -> {
      // 从数据库查出来角色已经有的模块
      List<RoleModule> roleModuleListFromDb = roleModuleManager.queryModuleOfRole(roleId);

      List<Long> moduleIdListFromDb = roleModuleListFromDb
              .stream()
              .map(RoleModule::getModuleId)
              .collect(Collectors.toList());
      Ternary<List<Long>, List<Long>, List<Long>> separate =
              IterableUtils.separateToList(moduleIdListFromDb, moduleIdList);

      // 数据库有&参数没有，需要删除
      List<Long> deleteList = new ArrayList<>();
      for (Long moduleId : separate.getF1()) {
        Map<Long, Long> roleModuleMapFromDb = roleModuleListFromDb
                .stream()
                .collect(Collectors.toMap(RoleModule::getModuleId, RoleModule::getId));
        deleteList.add(roleModuleMapFromDb.get(moduleId));
      }

      // 参数有&数据库没有，需要增加
      List<RoleModule> insertList = separate.getF3()
              .stream()
              .map(item -> new RoleModule(roleId, item))
              .collect(Collectors.toList());

      roleModuleManager.batchDeleteById(deleteList);
      roleModuleManager.batchInsert(insertList);

      return true;
    });
  }

  @Override
  public BModuleTree queryModuleOfRole(Long roleId, String accessType) {
    if (!roleManager.idExists(roleId)) {
      throw new ServiceException("roleId不存在：" + roleId + "。");
    }
    List<RoleModule> roleModuleList = roleModuleManager.queryModuleOfRole(roleId);

    // 查询出来这个角色下的目录
    List<Module> moduleList = roleModuleList.stream().map(
            item -> moduleManager.queryById(item.getModuleId())).filter(Objects::nonNull).collect(Collectors.toList());

    if (Module.AT_LISTEN.equals(accessType)) {
      return getRoot();
    } else if (Module.AT_WRITE.equals(accessType)) {
      moduleList.removeIf(item -> Module.AT_READ.equals(item.getAccessType()));
    } else if (Module.AT_READ.equals(accessType)) {
      // no-op
    } else {
      throw new ServiceException("不合法的accessType！");
    }

    // 按照 <parent, List<child>> 分组
    Map<Long, List<Long>> parentIdModuleMap = new HashMap<>();
    for (Module module : moduleList) {
      List<Long> tree = parentIdModuleMap.computeIfAbsent(module.getParentId(), s -> new ArrayList<>());
      tree.add(module.getId());
    }

    Map<Long, Module> moduleMap = moduleList.stream().collect(Collectors.toMap(Module::getId, a -> a));
    BModuleTree root = getRoot();
    Queue<BModuleTree> queue = new LinkedList<>();
    queue.offer(root);
    while (!queue.isEmpty()) {
      BModuleTree parent = queue.poll();
      Long id = parent.getId();
      List<Long> childrenId = parentIdModuleMap.get(id);

      if (childrenId != null && !childrenId.isEmpty()) {
        List<BModuleTree> bModuleTreeList = parent.getBModuleTreeList();
        for (Long childId : childrenId) {
          BModuleTree child = BeanUtils.copyProperties(moduleMap.get(childId), new BModuleTree());
          bModuleTreeList.add(child);
          queue.offer(child);
        }
      }
    }

    return root;
  }

  private BModuleTree getRoot() {
    Module module = moduleManager.queryById(Module.ROOT_ID);
    return BeanUtils.copyProperties(module, new BModuleTree());
  }

  @Override
  public List<Role> queryRoleOnModule(Long moduleId) {
    if (!moduleManager.idExists(moduleId)) {
      throw new ServiceException("moduleId不存在：" + moduleId + "。");
    }

    return roleModuleManager.queryRoleOnModule(moduleId);
  }

}

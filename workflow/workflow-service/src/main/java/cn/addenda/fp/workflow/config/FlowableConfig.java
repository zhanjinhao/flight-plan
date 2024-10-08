package cn.addenda.fp.workflow.config;

import cn.addenda.fp.rbac.dto.RoleDto;
import cn.addenda.fp.rbac.dto.UserDto;
import cn.addenda.fp.rbac.rpc.RoleRpc;
import cn.addenda.fp.rbac.rpc.UserRpc;
import cn.addenda.fp.workflow.flowable.listener.ActivityTimeoutCallbackListener;
import cn.addenda.fp.workflow.flowable.listener.MultiInstanceCollectionAssignmentListener;
import cn.addenda.fp.workflow.flowable.listener.ProcessStatusCompleteListener;
import cn.addenda.fp.workflow.flowable.listener.SingleInstanceGroupCodeAssignmentListener;
import cn.addenda.fp.workflow.manager.UserManager;
import cn.addenda.fp.workflow.service.InstanceService;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RuntimeService;
import org.flowable.idm.api.*;
import org.flowable.idm.engine.impl.GroupQueryImpl;
import org.flowable.idm.engine.impl.UserQueryImpl;
import org.flowable.idm.engine.impl.persistence.entity.GroupEntityImpl;
import org.flowable.idm.engine.impl.persistence.entity.UserEntity;
import org.flowable.idm.engine.impl.persistence.entity.UserEntityImpl;
import org.flowable.idm.spring.SpringIdmEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class FlowableConfig implements ApplicationListener<ContextStartedEvent>, ApplicationContextAware {

  private ApplicationContext applicationContext;

  @Bean
  public EngineConfigurationConfigurer<SpringIdmEngineConfiguration> idmEngineConfigurationConfigurer(UserRpc userRpc, RoleRpc roleRpc) {
    return idmEngineConfiguration -> idmEngineConfiguration.setIdmIdentityService(new MyIdmIdentityServiceImpl(userRpc, roleRpc));
  }

  @Bean
  @Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public MultiInstanceCollectionAssignmentListener multiInstanceCollectionAssignmentListener(UserManager userManager) {
    MultiInstanceCollectionAssignmentListener multiInstanceCollectionAssignmentListener = new MultiInstanceCollectionAssignmentListener();
    multiInstanceCollectionAssignmentListener.setUserManager(userManager);
    return multiInstanceCollectionAssignmentListener;
  }

  @Bean
  public ProcessStatusCompleteListener processStatusCompleteListener(RuntimeService runtimeService) {
    return new ProcessStatusCompleteListener(runtimeService);
  }

  @Bean
  @Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public ActivityTimeoutCallbackListener activityTimeoutCallbackListener(InstanceService instanceService) {
    // todo feign和restTemplate用同一个底层
    RestTemplate restTemplate = new RestTemplate();
    ActivityTimeoutCallbackListener activityTimeoutCallbackListener = new ActivityTimeoutCallbackListener();
    activityTimeoutCallbackListener.setInstanceService(instanceService);
    activityTimeoutCallbackListener.setRestTemplate(restTemplate);
    return activityTimeoutCallbackListener;
  }

  @Bean
  @Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
  public SingleInstanceGroupCodeAssignmentListener singleInstanceGroupCodeAssignmentListener() {
    return new SingleInstanceGroupCodeAssignmentListener();
  }

//  @Bean
//  public DefaultAsyncJobExecutor asyncExecutor(StandaloneProcessEngineConfiguration processEngineConfiguration) {
//    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//    executor.setCorePoolSize(5); // 核心线程数
//    executor.setMaxPoolSize(10); // 最大线程数
//    executor.setQueueCapacity(100); // 队列大小
//    executor.initialize();
//    DefaultAsyncJobExecutor asyncExecutor = new DefaultAsyncJobExecutor();
//    asyncExecutor.setProcessEngineConfiguration(processEngineConfiguration);
//    asyncExecutor.setThreadPoolSize(5); // 这里的值应该与taskExecutor的核心线程数一致
//    asyncExecutor.setQueueCapacity(100); // 这里的值应该与taskExecutor的队列大小一致
//    asyncExecutor.setDelegate(taskExecutor);
//    asyncExecutor.initialize();
//    return asyncExecutor;
//  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.applicationContext = applicationContext;
  }

  @Override
  public void onApplicationEvent(ContextStartedEvent event) {
    ProcessEngine processEngine = applicationContext.getBean(ProcessEngine.class);
    RuntimeService runtimeService = processEngine.getRuntimeService();
    ProcessStatusCompleteListener processStatusCompleteListener = applicationContext.getBean(ProcessStatusCompleteListener.class);
    // 流程正常结束
    runtimeService.addEventListener(processStatusCompleteListener, FlowableEngineEventType.PROCESS_COMPLETED);
  }

  static class MyGroupQueryImpl extends GroupQueryImpl {

    private final RoleRpc roleRpc;

    public MyGroupQueryImpl(RoleRpc roleRpc) {
      this.roleRpc = roleRpc;
    }

    @Override
    public long executeCount(CommandContext commandContext) {
      return executeList(commandContext).size();
    }

    @Override
    public List<Group> executeList(CommandContext commandContext) {
      if (getUserId() != null) {
        return findGroupsOfUser(getUserId());
      } else if (getId() != null) {
        List<Group> groups = new ArrayList<>();
        groups.add(findByGroupId(getId()));
        return groups;
      } else if (getIds() != null) {
        return findGroupsByIds(getIds());
      }
      throw new UnsupportedOperationException();
    }

    private List<Group> findGroupsOfUser(String userId) {
      List<RoleDto> roleDtoList = roleRpc.queryRoleOfUser(userId);
      if (CollectionUtils.isEmpty(roleDtoList)) {
        return new ArrayList<>();
      }
      return roleDtoList.stream()
              .map(roleDto -> {
                Group group = new GroupEntityImpl();
                group.setId(roleDto.getRoleCode());
                group.setName(roleDto.getRoleName());
                return group;
              })
              .collect(Collectors.toList());
    }

    private Group findByGroupId(String id) {
      RoleDto roleDto = roleRpc.queryByRoleCode(id);
      if (roleDto == null) {
        return null;
      }
      Group group = new GroupEntityImpl();
      group.setId(id);
      group.setName(roleDto.getRoleName());
      return group;
    }

    private List<Group> findGroupsByIds(List<String> ids) {
      List<RoleDto> roleDtoList = roleRpc.queryByRoleCodeList(ids);
      if (CollectionUtils.isEmpty(roleDtoList)) {
        return new ArrayList<>();
      }
      return roleDtoList.stream()
              .map(roleDto -> {
                Group group = new GroupEntityImpl();
                group.setId(roleDto.getRoleCode());
                group.setName(roleDto.getRoleName());
                return group;
              })
              .collect(Collectors.toList());
    }
  }

  static class MyIdmIdentityServiceImpl implements IdmIdentityService {

    private UserRpc userRpc;
    private RoleRpc roleRpc;

    public MyIdmIdentityServiceImpl(UserRpc userRpc, RoleRpc roleRpc) {
      this.userRpc = userRpc;
      this.roleRpc = roleRpc;
    }

    @Override
    public Group newGroup(String groupId) {
      throw new UnsupportedOperationException();
    }

    @Override
    public User newUser(String userId) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void saveGroup(Group group) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void saveUser(User user) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void updateUserPassword(User user) {
      throw new UnsupportedOperationException();
    }

    @Override
    public UserQuery createUserQuery() {
      return new MyUserQueryImpl(userRpc);
    }

    @Override
    public NativeUserQuery createNativeUserQuery() {
      throw new UnsupportedOperationException();
    }

    @Override
    public GroupQuery createGroupQuery() {
      return new MyGroupQueryImpl(roleRpc);
    }

    @Override
    public NativeGroupQuery createNativeGroupQuery() {
      throw new UnsupportedOperationException();
    }

    @Override
    public void createMembership(String userId, String groupId) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void deleteGroup(String groupId) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void deleteMembership(String userId, String groupId) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean checkPassword(String userId, String password) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setAuthenticatedUserId(String authenticatedUserId) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void deleteUser(String userId) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Token newToken(String tokenId) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void saveToken(Token token) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void deleteToken(String tokenId) {
      throw new UnsupportedOperationException();
    }

    @Override
    public TokenQuery createTokenQuery() {
      throw new UnsupportedOperationException();
    }

    @Override
    public NativeTokenQuery createNativeTokenQuery() {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setUserPicture(String userId, Picture picture) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Picture getUserPicture(String userId) {
      throw new UnsupportedOperationException();
    }

    @Override
    public String getUserInfo(String userId, String key) {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<String> getUserInfoKeys(String userId) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setUserInfo(String userId, String key, String value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void deleteUserInfo(String userId, String key) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Privilege createPrivilege(String name) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void addUserPrivilegeMapping(String privilegeId, String userId) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void deleteUserPrivilegeMapping(String privilegeId, String userId) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void addGroupPrivilegeMapping(String privilegeId, String groupId) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void deleteGroupPrivilegeMapping(String privilegeId, String groupId) {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<PrivilegeMapping> getPrivilegeMappingsByPrivilegeId(String privilegeId) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void deletePrivilege(String id) {
      throw new UnsupportedOperationException();
    }

    @Override
    public PrivilegeQuery createPrivilegeQuery() {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<Group> getGroupsWithPrivilege(String name) {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<User> getUsersWithPrivilege(String name) {
      throw new UnsupportedOperationException();
    }
  }

  static class MyUserQueryImpl extends UserQueryImpl {

    private final UserRpc userRpc;

    public MyUserQueryImpl(UserRpc userRpc) {
      this.userRpc = userRpc;
    }

    @Override
    public long executeCount(CommandContext commandContext) {
      return executeList(commandContext).size();
    }

    @Override
    public List<User> executeList(CommandContext commandContext) {
      if (getId() != null) {
        List<User> users = new ArrayList<>();
        users.add(findByUserId(getId()));
        return users;
      } else if (getIds() != null) {
        return findByUserIdList(getIds());
      } else if (getGroupId() != null) {
        return findByGroupId(getGroupId());
      } else if (getGroupIds() != null) {
        return findByGroups(getGroupIds());
      }
      throw new UnsupportedOperationException();
    }

    private User findByUserId(final String userId) {
      UserDto userDto = userRpc.queryByUserCode(userId);
      if (userDto == null) {
        return null;
      }
      UserEntity userEntity = new UserEntityImpl();
      userEntity.setId(userId);
      userEntity.setDisplayName(userDto.getUserName());
      return userEntity;
    }

    private List<User> findByUserIdList(List<String> userIds) {
      List<UserDto> userDtoList = userRpc.queryByUserCodeList(userIds);
      if (CollectionUtils.isEmpty(userDtoList)) {
        return new ArrayList<>();
      }
      return userDtoList.stream()
              .map(userDto -> {
                User userEntity = new UserEntityImpl();
                userEntity.setId(userDto.getUserCode());
                userEntity.setDisplayName(userDto.getUserName());
                return userEntity;
              })
              .collect(Collectors.toList());
    }

    private List<User> findByGroupId(final String groupId) {
      List<UserDto> userDtoList = userRpc.queryByRoleCode(groupId);
      if (CollectionUtils.isEmpty(userDtoList)) {
        return new ArrayList<>();
      }
      return userDtoList.stream()
              .map(userDto -> {
                User userEntity = new UserEntityImpl();
                userEntity.setId(userDto.getUserCode());
                userEntity.setDisplayName(userDto.getUserName());
                return userEntity;
              })
              .collect(Collectors.toList());
    }

    private List<User> findByGroups(List<String> groupIds) {
      List<User> users = new ArrayList<>();
      groupIds.forEach(g -> users.addAll(findByGroupId(g)));
      return users;
    }
  }

}

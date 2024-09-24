package cn.addenda.fp.workflow.service;

import cn.addenda.fp.workflow.constant.TaskConstants;
import cn.addenda.fp.workflow.constant.TaskReceiveType;
import cn.addenda.fp.workflow.dto.*;
import cn.addenda.fp.workflow.manager.GroupManager;
import cn.addenda.fp.workflow.manager.UserManager;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.identitylink.api.IdentityLinkInfo;
import org.flowable.identitylink.api.IdentityLinkType;
import org.flowable.identitylink.api.history.HistoricIdentityLink;
import org.flowable.task.api.DelegationState;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class TaskListServiceImpl implements TaskListService {

  @Autowired
  private org.flowable.engine.TaskService taskService;

  @Autowired
  private RepositoryService repositoryService;

  @Autowired
  private HistoryService historyService;

  @Autowired
  private GroupManager groupManager;

  @Autowired
  private UserManager userManager;

  @Override
  public List<TaskToClaimDto> queryTaskToClaim(String userId) {

//    select  RES.*,
//            VAR.ID_                    as VAR_ID_,
//            VAR.NAME_                  as VAR_NAME_,
//            VAR.TYPE_                  as VAR_TYPE_,
//            VAR.REV_                   as VAR_REV_,
//            VAR.PROC_INST_ID_          as VAR_PROC_INST_ID_,
//            VAR.EXECUTION_ID_          as VAR_EXECUTION_ID_,
//            VAR.TASK_ID_               as VAR_TASK_ID_,
//            VAR.BYTEARRAY_ID_          as VAR_BYTEARRAY_ID_,
//            VAR.DOUBLE_                as VAR_DOUBLE_,
//            VAR.TEXT_                  as VAR_TEXT_,
//            VAR.TEXT2_                 as VAR_TEXT2_,
//            VAR.LONG_                  as VAR_LONG_,
//            VAR.SCOPE_ID_              as VAR_SCOPE_ID_,
//            VAR.SUB_SCOPE_ID_          as VAR_SUB_SCOPE_ID_,
//            VAR.SCOPE_TYPE_            as VAR_SCOPE_TYPE_,
//            ILINK.ID_                  as ILINK_ID_,
//            ILINK.TYPE_                as ILINK_TYPE_,
//            ILINK.USER_ID_             as ILINK_USER_ID_,
//            ILINK.GROUP_ID_            as ILINK_GROUP_ID_,
//            ILINK.TASK_ID_             as ILINK_TASK_ID_,
//            ILINK.PROC_INST_ID_        as ILINK_PROC_INST_ID_,
//            ILINK.PROC_DEF_ID_         as ILINK_PROC_DEF_ID_,
//            ILINK.SCOPE_ID_            as ILINK_SCOPE_ID_,
//            ILINK.SCOPE_DEFINITION_ID_ as ILINK_SCOPE_DEFINITION_ID_
//    from (select RES.*
//          from ACT_RU_TASK RES
//          where RES.ASSIGNEE_ is null
//            and exists (select LINK.ID_
//                          from ACT_RU_IDENTITYLINK LINK
//                          where LINK.TYPE_ = 'candidate'
//                            and LINK.TASK_ID_ = RES.ID_
//                            and (LINK.USER_ID_ = ? or LINK.GROUP_ID_ in (?)))
//                  and RES.SUSPENSION_STATE_ = 1
//                  order by RES.CREATE_TIME_ desc) RES
//                  left join ACT_RU_VARIABLE VAR on RES.PROC_INST_ID_ = VAR.EXECUTION_ID_
//                  left join ACT_RU_IDENTITYLINK ILINK on RES.ID_ = ILINK.TASK_ID_
//    order by RES.CREATE_TIME_ desc

    TaskQuery taskQuery = taskService.createTaskQuery()
            .active()
            .includeProcessVariables()
            // flowable会通过用户寻找用户的组
            // 具体实现SQL为：LINK.USER_ID_ = ? or LINK.GROUP_ID_ in (?)
            .taskCandidateUser(userId)
            .orderByTaskCreateTime()
            .includeIdentityLinks()
            .desc();
    // 构建搜索条件
//    ProcessUtils.buildProcessSearch(taskQuery, processQuery);
    List<Task> taskList = taskQuery.list();
    List<TaskToClaimDto> taskToClaimDtoList = new ArrayList<>();
    for (Task task : taskList) {
      TaskToClaimDto taskToClaimDto = new TaskToClaimDto();
      taskToClaimDto.accept(task, groupManager::getGroupNameByGroupId);
      acceptForAbstract(taskToClaimDto, task);
      taskToClaimDtoList.add(taskToClaimDto);
    }

    return taskToClaimDtoList;
  }


  @Override
  public List<TaskToDoDto> queryTaskToDo(String userId) {

// select RES.*,
//        VAR.ID_           as VAR_ID_,
//        VAR.NAME_         as VAR_NAME_,
//        VAR.TYPE_         as VAR_TYPE_,
//        VAR.REV_          as VAR_REV_,
//        VAR.PROC_INST_ID_ as VAR_PROC_INST_ID_,
//        VAR.EXECUTION_ID_ as VAR_EXECUTION_ID_,
//        VAR.TASK_ID_      as VAR_TASK_ID_,
//        VAR.BYTEARRAY_ID_ as VAR_BYTEARRAY_ID_,
//        VAR.DOUBLE_       as VAR_DOUBLE_,
//        VAR.TEXT_         as VAR_TEXT_,
//        VAR.TEXT2_        as VAR_TEXT2_,
//        VAR.LONG_         as VAR_LONG_,
//        VAR.SCOPE_ID_     as VAR_SCOPE_ID_,
//        VAR.SUB_SCOPE_ID_ as VAR_SUB_SCOPE_ID_,
//        VAR.SCOPE_TYPE_   as VAR_SCOPE_TYPE_
// from (select RES.*
//       from ACT_RU_TASK RES
//       where RES.ASSIGNEE_ = ? and RES.SUSPENSION_STATE_ = 1
//       order by RES.CREATE_TIME_ desc) RES
//          left join ACT_RU_VARIABLE VAR on RES.ID_ = VAR.TASK_ID_ or RES.PROC_INST_ID_ = VAR.EXECUTION_ID_
// order by RES.CREATE_TIME_ desc

    TaskQuery taskQuery = taskService.createTaskQuery()
            .active()
            .includeProcessVariables()
            .includeTaskLocalVariables()
            .includeIdentityLinks()
            .taskAssignee(userId)
            .orderByTaskCreateTime()
            .desc();
    // 构建搜索条件
//    ProcessUtils.buildProcessSearch(taskQuery, processQuery);
    List<Task> taskList = taskQuery.list();
    List<TaskToDoDto> toDoDtoList = new ArrayList<>();
    for (Task task : taskList) {
      TaskToDoDto taskToDoDto = new TaskToDoDto();
      taskToDoDto.accept(task, groupManager::getGroupNameByGroupId);
      acceptForAbstract(taskToDoDto, task);
      acceptForTaskToDo(taskToDoDto, task);
      toDoDtoList.add(taskToDoDto);
    }
    return toDoDtoList;
  }

  @Override
  public List<TaskDelegatedDto> queryTaskDelegated(String userId) {

    // select RES.*,
    //        VAR.ID_                    as VAR_ID_,
    //        VAR.NAME_                  as VAR_NAME_,
    //        VAR.TYPE_                  as VAR_TYPE_,
    //        VAR.REV_                   as VAR_REV_,
    //        VAR.PROC_INST_ID_          as VAR_PROC_INST_ID_,
    //        VAR.EXECUTION_ID_          as VAR_EXECUTION_ID_,
    //        VAR.TASK_ID_               as VAR_TASK_ID_,
    //        VAR.BYTEARRAY_ID_          as VAR_BYTEARRAY_ID_,
    //        VAR.DOUBLE_                as VAR_DOUBLE_,
    //        VAR.TEXT_                  as VAR_TEXT_,
    //        VAR.TEXT2_                 as VAR_TEXT2_,
    //        VAR.LONG_                  as VAR_LONG_,
    //        VAR.SCOPE_ID_              as VAR_SCOPE_ID_,
    //        VAR.SUB_SCOPE_ID_          as VAR_SUB_SCOPE_ID_,
    //        VAR.SCOPE_TYPE_            as VAR_SCOPE_TYPE_,
    //        ILINK.ID_                  as ILINK_ID_,
    //        ILINK.TYPE_                as ILINK_TYPE_,
    //        ILINK.USER_ID_             as ILINK_USER_ID_,
    //        ILINK.GROUP_ID_            as ILINK_GROUP_ID_,
    //        ILINK.TASK_ID_             as ILINK_TASK_ID_,
    //        ILINK.PROC_INST_ID_        as ILINK_PROC_INST_ID_,
    //        ILINK.PROC_DEF_ID_         as ILINK_PROC_DEF_ID_,
    //        ILINK.SCOPE_ID_            as ILINK_SCOPE_ID_,
    //        ILINK.SCOPE_DEFINITION_ID_ as ILINK_SCOPE_DEFINITION_ID_
    // from (select RES.*
    //       from ACT_RU_TASK RES
    //       where RES.OWNER_ = ?
    //         and RES.DELEGATION_ = ?
    //       order by RES.CREATE_TIME_ desc) RES
    //          left join ACT_RU_VARIABLE VAR on RES.ID_ = VAR.TASK_ID_ or RES.PROC_INST_ID_ = VAR.EXECUTION_ID_
    //          left join ACT_RU_IDENTITYLINK ILINK on RES.ID_ = ILINK.TASK_ID_
    // order by RES.CREATE_TIME_ desc


    TaskQuery taskQuery = taskService.createTaskQuery()
            .includeProcessVariables()
            .includeTaskLocalVariables()
            .includeIdentityLinks()
            .taskDelegationState(DelegationState.PENDING)
            .taskOwner(userId)
            .orderByTaskCreateTime()
            .desc();
    // 构建搜索条件
//    ProcessUtils.buildProcessSearch(taskQuery, processQuery);
    List<Task> taskList = taskQuery.list();
    List<TaskDelegatedDto> taskDelegatedDtoList = new ArrayList<>();
    for (Task task : taskList) {
      TaskDelegatedDto taskDelegatedDto = new TaskDelegatedDto();
      taskDelegatedDto.accept(task, groupManager::getGroupNameByGroupId);
      acceptForAbstract(taskDelegatedDto, task);
      taskDelegatedDto.setAssignee(task.getAssignee());
      taskDelegatedDto.setAssigneeName(userManager.getUserNameByUserId(task.getAssignee()));

      List<HistoricIdentityLink> historicIdentityLinksForTask = historyService.getHistoricIdentityLinksForTask(task.getId());
      historicIdentityLinksForTask.sort(Comparator.comparing(HistoricIdentityLink::getCreateTime).reversed());
      for (HistoricIdentityLink historicIdentityLink : historicIdentityLinksForTask) {
        if (task.getAssignee().equals(historicIdentityLink.getUserId())
                && IdentityLinkType.ASSIGNEE.equals(historicIdentityLink.getType())) {
          taskDelegatedDto.setDelegationDateTime(historicIdentityLink.getCreateTime());
          break;
        }
      }

      taskDelegatedDtoList.add(taskDelegatedDto);
    }
    return taskDelegatedDtoList;
  }

  @Override
  public List<TaskInvolvedDto> queryTaskInvolved(String userId) {

// select RES.*,
//        VAR.ID_                    as VAR_ID_,
//        VAR.NAME_                  as VAR_NAME_,
//        VAR.VAR_TYPE_              as VAR_TYPE_,
//        VAR.REV_                   as VAR_REV_,
//        VAR.PROC_INST_ID_          as VAR_PROC_INST_ID_,
//        VAR.EXECUTION_ID_          as VAR_EXECUTION_ID_,
//        VAR.TASK_ID_               as VAR_TASK_ID_,
//        VAR.BYTEARRAY_ID_          as VAR_BYTEARRAY_ID_,
//        VAR.DOUBLE_                as VAR_DOUBLE_,
//        VAR.TEXT_                  as VAR_TEXT_,
//        VAR.TEXT2_                 as VAR_TEXT2_,
//        VAR.LAST_UPDATED_TIME_     as VAR_LAST_UPDATED_TIME_,
//        VAR.LONG_                  as VAR_LONG_,
//        VAR.SCOPE_ID_              as VAR_SCOPE_ID_,
//        VAR.SUB_SCOPE_ID_          as VAR_SUB_SCOPE_ID_,
//        VAR.SCOPE_TYPE_            as VAR_SCOPE_TYPE_,
//        ILINK.ID_                  as ILINK_ID_,
//        ILINK.TYPE_                as ILINK_TYPE_,
//        ILINK.USER_ID_             as ILINK_USER_ID_,
//        ILINK.GROUP_ID_            as ILINK_GROUP_ID_,
//        ILINK.TASK_ID_             as ILINK_TASK_ID_,
//        ILINK.PROC_INST_ID_        as ILINK_PROC_INST_ID_,
//        ILINK.CREATE_TIME_         as ILINK_CREATE_TIME_,
//        ILINK.SCOPE_ID_            as ILINK_SCOPE_ID_,
//        ILINK.SCOPE_DEFINITION_ID_ as ILINK_SCOPE_DEFINITION_ID_
//   from (select RES.*
//         from ACT_HI_TASKINST RES
//         where exists (select LINK.ID_ from ACT_HI_IDENTITYLINK LINK where LINK.USER_ID_ = ? and LINK.TASK_ID_ = RES.ID_)
//            or RES.ASSIGNEE_ = ?
//            or RES.OWNER_ = ?
//         order by RES.START_TIME_ desc) RES
//            left join ACT_HI_VARINST VAR on RES.ID_ = VAR.TASK_ID_ or RES.PROC_INST_ID_ = VAR.EXECUTION_ID_
//            left join ACT_HI_IDENTITYLINK ILINK on RES.ID_ = ILINK.TASK_ID_
//   order by RES.START_TIME_ desc

    HistoricTaskInstanceQuery taskQuery = historyService.createHistoricTaskInstanceQuery()
            // owner或assignee或ACT_HI_IDENTITYLINK里能与任务关联上的userId
            .taskInvolvedUser(userId)
            .includeTaskLocalVariables()
            .includeProcessVariables()
            .includeIdentityLinks()
            .orderByTaskCreateTime()
            .desc();
    // 构建搜索条件
//    ProcessUtils.buildProcessSearch(taskQuery, processQuery);
    List<HistoricTaskInstance> historicTaskInstanceList = taskQuery.list();
    List<TaskInvolvedDto> taskInvolvedDtoList = new ArrayList<>();
    for (HistoricTaskInstance historicTaskInstance : historicTaskInstanceList) {
      TaskInvolvedDto taskInvolvedDto = new TaskInvolvedDto();
      taskInvolvedDto.accept(historicTaskInstance, groupManager::getGroupNameByGroupId);
      acceptForAbstract(taskInvolvedDto, historicTaskInstance);
      acceptForTaskToDo(taskInvolvedDto, historicTaskInstance);

      taskInvolvedDto.setTaskEndDateTime(historicTaskInstance.getEndTime());

      List<? extends IdentityLinkInfo> identityLinkList = historicTaskInstance.getIdentityLinks();
      String involveType = identityLinkList.stream()
              .filter(a -> userId.equals(a.getUserId()))
              .map(IdentityLinkInfo::getType)
              .distinct()
              .collect(Collectors.joining(","));
      taskInvolvedDto.setInvolveType(involveType);

      taskInvolvedDtoList.add(taskInvolvedDto);
    }
    return taskInvolvedDtoList;
  }

  private void acceptForTaskToDo(TaskToDoDto taskToDoDto, TaskInfo task) {
    taskToDoDto.setOwner(task.getOwner() != null ? task.getOwner() : task.getAssignee());
    taskToDoDto.setOwnerName(userManager.getUserNameByUserId(taskToDoDto.getOwner()));

    // 设置taskReceiveType
    Map<String, Object> taskLocalVariables = task.getTaskLocalVariables();
    String taskReceiveType = (String) taskLocalVariables.get(TaskConstants.TASK_RECEIVE_TYPE_KEY);
    if (taskReceiveType == null) {
      taskToDoDto.setTaskReceiveType(TaskReceiveType.ASSIGN);
    } else {
      taskToDoDto.setTaskReceiveType(TaskReceiveType.valueOf(taskReceiveType));
    }

    if (TaskReceiveType.CLAIM == taskToDoDto.getTaskReceiveType()) {
      taskToDoDto.setReceiveDateTime(task.getClaimTime());
    } else {
      List<HistoricIdentityLink> historicIdentityLinksForTask = historyService.getHistoricIdentityLinksForTask(task.getId());
      historicIdentityLinksForTask.removeIf(a -> !IdentityLinkType.ASSIGNEE.equals(a.getType()));
      historicIdentityLinksForTask.sort(Comparator.comparing(a -> ((HistoricIdentityLink) a).getCreateTime()).reversed());
      if (!CollectionUtils.isEmpty(historicIdentityLinksForTask)) {
        taskToDoDto.setReceiveDateTime((historicIdentityLinksForTask.get(0)).getCreateTime());
      }

      if (taskToDoDto.getTaskReceiveType() == TaskReceiveType.DELEGATION
              // owner与assignee相同，表示任务委托后，委托人完成任务。
              // Task的DelegationState为RESOLVE也表示此意思。但HistoricTask里没有此字段。
              && task.getAssignee().equals(taskToDoDto.getOwner())) {
        taskToDoDto.setIfSelfDo(false);
      }
    }

    taskToDoDto.setAssignee(task.getAssignee());
    taskToDoDto.setAssigneeName(userManager.getUserNameByUserId(task.getAssignee()));
  }

  private void acceptForAbstract(AbstractTaskDto abstractTaskDto, TaskInfo task) {
    // 流程定义信息
    ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
            .processDefinitionId(task.getProcessDefinitionId())
            .singleResult();
    abstractTaskDto.accept(processDefinition);

    // 流程部署实例信息
    Deployment deployment = repositoryService.createDeploymentQuery()
            .deploymentId(processDefinition.getDeploymentId())
            .singleResult();
    abstractTaskDto.accept(deployment);

    // 流程发起人信息
    HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
            .processInstanceId(task.getProcessInstanceId())
            .includeProcessVariables()
            .singleResult();
    abstractTaskDto.accept(historicProcessInstance, userManager::getUserNameByUserId);
  }

}

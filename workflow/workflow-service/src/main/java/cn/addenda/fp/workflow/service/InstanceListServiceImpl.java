package cn.addenda.fp.workflow.service;

import cn.addenda.fp.workflow.dto.ProcessInstanceDto;
import cn.addenda.fp.workflow.dto.ProcessInstanceInvolvedDto;
import cn.addenda.fp.workflow.manager.UserManager;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.identitylink.api.history.HistoricIdentityLink;
import org.flowable.task.api.Task;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InstanceListServiceImpl implements InstanceListService {

  @Autowired
  private HistoryService historyService;

  @Autowired
  private RepositoryService repositoryService;

  @Autowired
  private TaskService taskService;

  @Autowired
  private UserManager userManager;

  @Override
  public List<ProcessInstanceDto> queryInstanceStartedByUserId(String userId, boolean includeFinished) {

//    select  RES.*,
//            VAR.ID_                as VAR_ID_,
//            VAR.NAME_              as VAR_NAME_,
//            VAR.VAR_TYPE_          as VAR_TYPE_,
//            VAR.REV_               as VAR_REV_,
//            VAR.PROC_INST_ID_      as VAR_PROC_INST_ID_,
//            VAR.EXECUTION_ID_      as VAR_EXECUTION_ID_,
//            VAR.TASK_ID_           as VAR_TASK_ID_,
//            VAR.BYTEARRAY_ID_      as VAR_BYTEARRAY_ID_,
//            VAR.DOUBLE_            as VAR_DOUBLE_,
//            VAR.TEXT_              as VAR_TEXT_,
//            VAR.TEXT2_             as VAR_TEXT2_,
//            VAR.LAST_UPDATED_TIME_ as VAR_LAST_UPDATED_TIME_,
//            VAR.LONG_              as VAR_LONG_
//    from (select RES.*,
//            DEF.KEY_           as PROC_DEF_KEY_,
//            DEF.NAME_          as PROC_DEF_NAME_,
//            DEF.VERSION_       as PROC_DEF_VERSION_,
//            DEF.DEPLOYMENT_ID_ as DEPLOYMENT_ID_
//            from ACT_HI_PROCINST RES
//            left join ACT_RE_PROCDEF DEF on RES.PROC_DEF_ID_ = DEF.ID_
//            where RES.START_USER_ID_ = ?
//            order by RES.START_TIME_ desc) RES
//    left join ACT_HI_VARINST VAR on RES.PROC_INST_ID_ = VAR.EXECUTION_ID_
//    order by RES.START_TIME_ desc

    HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery()
            .startedBy(userId)
            .orderByProcessInstanceStartTime()
            // 只包含实例级别的变量
            .includeProcessVariables()
            .desc();
    historicProcessInstanceQuery = includeFinished ? historicProcessInstanceQuery : historicProcessInstanceQuery.unfinished();
    // 构建搜索条件
//    ProcessUtils.buildProcessSearch(historicProcessInstanceQuery, processQuery);
    List<HistoricProcessInstance> historicProcessInstances = historicProcessInstanceQuery.list();
    return assemble(historicProcessInstances);
  }

  @Override
  public List<ProcessInstanceInvolvedDto> queryInstanceInvolvedByUserId(String userId, boolean includeFinished) {

//    select  RES.*,
//            VAR.ID_                as VAR_ID_,
//            VAR.NAME_              as VAR_NAME_,
//            VAR.VAR_TYPE_          as VAR_TYPE_,
//            VAR.REV_               as VAR_REV_,
//            VAR.PROC_INST_ID_      as VAR_PROC_INST_ID_,
//            VAR.EXECUTION_ID_      as VAR_EXECUTION_ID_,
//            VAR.TASK_ID_           as VAR_TASK_ID_,
//            VAR.BYTEARRAY_ID_      as VAR_BYTEARRAY_ID_,
//            VAR.DOUBLE_            as VAR_DOUBLE_,
//            VAR.TEXT_              as VAR_TEXT_,
//            VAR.TEXT2_             as VAR_TEXT2_,
//            VAR.LAST_UPDATED_TIME_ as VAR_LAST_UPDATED_TIME_,
//            VAR.LONG_              as VAR_LONG_
//    from (select RES.*,
//            DEF.KEY_           as PROC_DEF_KEY_,
//            DEF.NAME_          as PROC_DEF_NAME_,
//            DEF.VERSION_       as PROC_DEF_VERSION_,
//            DEF.DEPLOYMENT_ID_ as DEPLOYMENT_ID_
//            from ACT_HI_PROCINST RES
//            left join ACT_RE_PROCDEF DEF on RES.PROC_DEF_ID_ = DEF.ID_
//            where exists (select ID_
//                    from ACT_HI_IDENTITYLINK I
//                    where I.PROC_INST_ID_ = RES.ID_ and I.USER_ID_ = ? and I.TYPE_ = ?)
//            order by RES.START_TIME_ desc) RES
//    left join ACT_HI_VARINST VAR on RES.PROC_INST_ID_ = VAR.EXECUTION_ID_
//    order by RES.START_TIME_ desc

    HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery()
            // IdentityLinkType.PARTICIPANT：流程的参与者
            // IdentityLinkType.STARTER：流程的发起者
            // IdentityLinkType.OWNER：任务的拥有者
            // IdentityLinkType.ASSIGNEE：任务的执行者
            // IdentityLinkType.CANDIDATE：任务的候选者
            .involvedUser(userId)
            .orderByProcessInstanceStartTime()
            // 只包含实例级别的变量
            .includeProcessVariables()
            .desc();
    historicProcessInstanceQuery = includeFinished ? historicProcessInstanceQuery : historicProcessInstanceQuery.unfinished();
    // 构建搜索条件
//    ProcessUtils.buildProcessSearch(historicProcessInstanceQuery, processQuery);
    List<HistoricProcessInstance> historicProcessInstanceList = historicProcessInstanceQuery.list();
    List<ProcessInstanceDto> processInstanceDtoList = assemble(historicProcessInstanceList);

    List<ProcessInstanceInvolvedDto> processInstanceInvolvedDtoList = new ArrayList<>();
    for (ProcessInstanceDto processInstanceDto : processInstanceDtoList) {
      List<HistoricIdentityLink> historicIdentityLinkList = historyService.getHistoricIdentityLinksForProcessInstance(processInstanceDto.getProcessInstanceId());
      ProcessInstanceInvolvedDto processInstanceInvolvedDto = new ProcessInstanceInvolvedDto();
      BeanUtils.copyProperties(processInstanceDto, processInstanceInvolvedDto);
      String involveType = historicIdentityLinkList.stream()
              .filter(a -> userId.equals(a.getUserId()))
              .map(HistoricIdentityLink::getType)
              .distinct()
              .collect(Collectors.joining(","));
      processInstanceInvolvedDto.setInvolveType(involveType);
      processInstanceInvolvedDtoList.add(processInstanceInvolvedDto);
    }

    return processInstanceInvolvedDtoList;
  }

  private List<ProcessInstanceDto> assemble(List<HistoricProcessInstance> historicProcessInstanceList) {

    List<ProcessInstanceDto> processInstanceDtoList = new ArrayList<>();

    for (HistoricProcessInstance historicProcessInstance : historicProcessInstanceList) {
      ProcessInstanceDto processInstanceDto = new ProcessInstanceDto();
      processInstanceDto.accept(historicProcessInstance, userManager::getUserNameByUserId);

      // 流程部署实例信息
      Deployment deployment = repositoryService.createDeploymentQuery()
              .deploymentId(historicProcessInstance.getDeploymentId())
              .singleResult();
      processInstanceDto.accept(deployment);

      // 流程定义信息
      ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
              .processDefinitionId(historicProcessInstance.getProcessDefinitionId())
              .singleResult();
      processInstanceDto.accept(processDefinition);

      // 当前所处流程
      List<Task> taskList = taskService.createTaskQuery()
              .processInstanceId(historicProcessInstance.getId())
              .list();
      if (!CollectionUtils.isEmpty(taskList)) {
        processInstanceDto.setTaskName(taskList.stream()
                .map(Task::getName)
                .filter(StringUtils::hasText)
                .collect(Collectors.joining(",")));
      } else {
        processInstanceDto.setTaskName("已完成！");
      }
      processInstanceDtoList.add(processInstanceDto);
    }
    return processInstanceDtoList;
  }

}

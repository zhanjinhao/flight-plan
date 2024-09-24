package cn.addenda.fp.workflow.service;

import cn.addenda.component.jdk.exception.ServiceException;
import cn.addenda.component.user.UserContext;
import cn.addenda.fp.workflow.constant.*;
import cn.addenda.fp.workflow.flowable.diagram.MyProcessDiagramGenerator;
import cn.addenda.fp.workflow.dto.*;
import cn.addenda.fp.workflow.manager.GroupManager;
import cn.addenda.fp.workflow.manager.InstanceManager;
import cn.addenda.fp.workflow.manager.UserManager;
import cn.addenda.fp.workflow.util.ModelUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.MultiInstanceLoopCharacteristics;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.task.Comment;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.flowable.variable.service.impl.persistence.entity.HistoricVariableInstanceEntityImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.addenda.fp.workflow.constant.ProcessConstants.PROCESS_STATUS_KEY;

@Component
public class InstanceServiceImpl implements InstanceService {

  @Autowired
  private RepositoryService repositoryService;

  @Autowired
  private RuntimeService runtimeService;

  @Autowired
  private HistoryService historyService;

  @Autowired
  private ProcessEngine processEngine;

  @Autowired
  private org.flowable.engine.TaskService taskService;

  @Autowired
  private GroupManager groupManager;

  @Autowired
  private UserManager userManager;

  @Autowired
  private InstanceManager instanceManager;

  @Override
  public String createInstance(String definitionKey, String processInstanceName, Map<String, String> variables) {
    ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
            .processDefinitionKey(definitionKey)
            .latestVersion()
            .singleResult();
    // 设置流程发起人Id到流程中
    String userIdStr = UserContext.getUserId();
    variables.put(ProcessConstants.INITIATOR_KEY, userIdStr);
    // 设置流程状态为进行中
    variables.put(PROCESS_STATUS_KEY, ProcessStatusEnum.RUNNING.getStatus());
    // 发起流程实例
    Map<String, Object> variables2 = new HashMap<>(variables);
    ProcessInstance processInstance = runtimeService
            .startProcessInstanceById(processDefinition.getId(), variables2);
    runtimeService.setProcessInstanceName(processInstance.getId(), processInstanceName);
    return processInstance.getId();
  }


  @Override
  public void downloadImage(String processInstanceId, HttpServletResponse response) throws IOException {
    // png
    response.setContentType(MediaType.IMAGE_PNG_VALUE);
    response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + processInstanceId + "\"");
    downloadImage(processInstanceId, response.getOutputStream());
  }

  @Override
  public void downloadImage(String processInstanceId, OutputStream out) throws IOException {
    HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
            .processInstanceId(processInstanceId)
            .singleResult();
    if (historicProcessInstance == null) {
      throw new ServiceException("实例不存在！");
    }
    String processDefinitionId = historicProcessInstance.getProcessDefinitionId();

    // 获得活动的节点
    List<HistoricActivityInstance> historicActivityInstanceList = historyService.createHistoricActivityInstanceQuery()
            .processInstanceId(processInstanceId)
            .orderByHistoricActivityInstanceStartTime()
            .asc()
            .list();

    Set<String> highLightedFlowList = new HashSet<>();
    Set<String> highLightedNodeList = new HashSet<>();
    Set<String> highLightedRunningNodeList = new HashSet<>();
    historicActivityInstanceList.forEach(historicActivityInstance -> {
      // 表示撤回了，不能点亮
      if (historicActivityInstance.getDeleteReason() != null
              && historicActivityInstance.getDeleteReason().contains("Change activity to ")) {
        return;
      }
      if ("sequenceFlow".equals(historicActivityInstance.getActivityType())) {
        // 添加高亮连线
        highLightedFlowList.add(historicActivityInstance.getActivityId());
      } else {
        // 添加已完成高亮节点-默认绿色
        highLightedNodeList.add(historicActivityInstance.getActivityId());
        if (historicActivityInstance.getEndTime() == null) {
          // 添加运行中高亮节点-红色
          highLightedRunningNodeList.add(historicActivityInstance.getActivityId());
        }
      }
    });
    
    String imageType = "png";
    double scaleFactor = 1.0d; // 比例因子，默认即可
    boolean drawSequenceFlowNameWithNoLabelDI = true; // 不设置连线标签不会画

    BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
    ProcessEngineConfiguration configuration = processEngine.getProcessEngineConfiguration();
    MyProcessDiagramGenerator diagramGenerator = new MyProcessDiagramGenerator();
    try (InputStream png = diagramGenerator.generateDiagram(bpmnModel, imageType,
            new ArrayList<>(highLightedNodeList), new ArrayList<>(highLightedFlowList), new ArrayList<>(highLightedRunningNodeList),
            configuration.getActivityFontName(), configuration.getLabelFontName(), configuration.getAnnotationFontName(),
            configuration.getClassLoader(), scaleFactor, drawSequenceFlowNameWithNoLabelDI)) {
      byte[] buffer = new byte[4096];
      int bytesRead;
      while ((bytesRead = png.read(buffer)) != -1) {
        out.write(buffer, 0, bytesRead);
      }
      out.flush();
    }
  }

  @Override
  public void cancelInstance(String processInstanceId, String comment) {
    ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
            .startedBy(UserContext.getUserId())
            .processInstanceId(processInstanceId)
            .singleResult();
    if (processInstance == null) {
      throw new ServiceException("只有任务发起人可以取消任务！");
    }

    List<Task> taskList = taskService.createTaskQuery()
            .processInstanceId(processInstanceId)
            .list();
    if (CollectionUtils.isEmpty(taskList)) {
      throw new ServiceException("流程未启动或已执行完成，取消申请失败");
    }

    // 取消任务
    runtimeService.setVariable(processInstance.getId(), ProcessConstants.PROCESS_STATUS_KEY, ProcessStatusEnum.CANCELED.getStatus());
    for (Task task : taskList) {
      String userComment = UserContext.getUserId() + ": " + comment;
      taskService.addComment(task.getId(), processInstance.getProcessInstanceId(), CommentType.CANCEL.getType(), userComment);
    }

    // 将活动节点移动至结束节点
    instanceManager.moveExecutionToEndEvent(processInstance.getProcessDefinitionId(), processInstanceId);
  }


  @Override
  public InstanceVariablesDto queryInstanceVariables(String processInstanceId) {
    // 实现类是 HistoricVariableInstanceEntityImpl
    List<HistoricVariableInstance> historicVariableInstanceList = historyService.createHistoricVariableInstanceQuery()
            .processInstanceId(processInstanceId)
            .list();

    // 实例级变量
    List<HistoricVariableInstance> instanceVariableList = historicVariableInstanceList.stream()
            .filter(a -> a.getTaskId() == null)
            .filter(a -> processInstanceId.equals(((HistoricVariableInstanceEntityImpl) a).getExecutionId()))
            .collect(Collectors.toList());
    List<VariableDto> instanceVariableDtoList = new ArrayList<>();
    for (HistoricVariableInstance historicVariableInstance : instanceVariableList) {
      instanceVariableDtoList.add(toVariableDto(historicVariableInstance, VariableType.INSTANCE));
    }

    // 任务级变量
    List<HistoricVariableInstance> taskVariableList = historicVariableInstanceList.stream()
            .filter(a -> a.getTaskId() != null)
            .collect(Collectors.toList());
    Map<String, List<VariableDto>> taskVariableMap = new HashMap<>();
    for (HistoricVariableInstance historicVariableInstance : taskVariableList) {
      List<VariableDto> variableDtoList = taskVariableMap.computeIfAbsent(historicVariableInstance.getTaskId(), s -> new ArrayList<>());
      variableDtoList.add(toVariableDto(historicVariableInstance, VariableType.TASK));
    }

    // 执行级变量
    List<HistoricVariableInstance> executionVariableList = historicVariableInstanceList.stream()
            .filter(a -> a.getTaskId() == null)
            .filter(a -> !processInstanceId.equals(((HistoricVariableInstanceEntityImpl) a).getExecutionId()))
            .collect(Collectors.toList());
    Map<String, List<VariableDto>> executionVariableMap = new HashMap<>();
    for (HistoricVariableInstance historicVariableInstance : executionVariableList) {
      List<VariableDto> variableDtoList = executionVariableMap.computeIfAbsent(((HistoricVariableInstanceEntityImpl) historicVariableInstance).getExecutionId(), s -> new ArrayList<>());
      variableDtoList.add(toVariableDto(historicVariableInstance, VariableType.EXECUTION));
    }

    InstanceVariablesDto instanceVariablesDto = new InstanceVariablesDto();
    instanceVariablesDto.setInstanceVariableDtoList(instanceVariableDtoList);
    instanceVariablesDto.setTaskVariableDtoListMap(taskVariableMap);
    instanceVariablesDto.setExecutionVariableDtoListMap(executionVariableMap);
    return instanceVariablesDto;
  }

  private VariableDto toVariableDto(HistoricVariableInstance historicVariableInstance, VariableType variableType) {
    VariableDto variableDto = new VariableDto();
    variableDto.setProcessInstanceId(historicVariableInstance.getProcessInstanceId());
    variableDto.setTaskId(historicVariableInstance.getTaskId());
    variableDto.setExecutionId(((HistoricVariableInstanceEntityImpl) historicVariableInstance).getExecutionId());
    variableDto.setVariableName(historicVariableInstance.getVariableName());
    variableDto.setVariableValue(String.valueOf(historicVariableInstance.getValue()));
    variableDto.setVariableType(variableType);
    return variableDto;
  }

  @Override
  public ProcessInstanceDetailDto queryDetailProcess(String processInstanceId) {
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
//            where RES.PROC_INST_ID_ = ?
//            order by RES.ID_ asc) RES
//    left join ACT_HI_VARINST VAR on RES.PROC_INST_ID_ = VAR.EXECUTION_ID_
//    order by RES.ID_ asc
    HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
            .processInstanceId(processInstanceId)
            .includeProcessVariables()
            .singleResult();

    ProcessInstanceDetailDto processInstanceDetailDto = new ProcessInstanceDetailDto();
    processInstanceDetailDto.accept(historicProcessInstance, userManager::getUserNameByUserId);

    ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
            .processDefinitionId(historicProcessInstance.getProcessDefinitionId())
            .singleResult();
    processInstanceDetailDto.accept(processDefinition);

    Deployment deployment = repositoryService.createDeploymentQuery()
            .deploymentId(processDefinition.getDeploymentId())
            .singleResult();
    processInstanceDetailDto.accept(deployment);

    BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());

    List<Comment> commentList = taskService.getProcessInstanceComments(processInstanceId);

//    select  RES.*,
//            VAR.ID_                    as VAR_ID_,
//            VAR.NAME_                  as VAR_NAME_,
//            VAR.VAR_TYPE_              as VAR_TYPE_,
//            VAR.REV_                   as VAR_REV_,
//            VAR.PROC_INST_ID_          as VAR_PROC_INST_ID_,
//            VAR.EXECUTION_ID_          as VAR_EXECUTION_ID_,
//            VAR.TASK_ID_               as VAR_TASK_ID_,
//            VAR.BYTEARRAY_ID_          as VAR_BYTEARRAY_ID_,
//            VAR.DOUBLE_                as VAR_DOUBLE_,
//            VAR.TEXT_                  as VAR_TEXT_,
//            VAR.TEXT2_                 as VAR_TEXT2_,
//            VAR.LAST_UPDATED_TIME_     as VAR_LAST_UPDATED_TIME_,
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
//            ILINK.CREATE_TIME_         as ILINK_CREATE_TIME_,
//            ILINK.SCOPE_ID_            as ILINK_SCOPE_ID_,
//            ILINK.SCOPE_DEFINITION_ID_ as ILINK_SCOPE_DEFINITION_ID_
//    from (select RES.* from ACT_HI_TASKINST RES where RES.PROC_INST_ID_ = ? order by RES.START_TIME_ desc) RES
//    left join ACT_HI_VARINST VAR on RES.ID_ = VAR.TASK_ID_
//    left join ACT_HI_IDENTITYLINK ILINK on RES.ID_ = ILINK.TASK_ID_
//    order by RES.START_TIME_ desc
    List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery()
            .processInstanceId(processInstanceId)
            .includeTaskLocalVariables()
            .includeIdentityLinks()
            .orderByHistoricTaskInstanceStartTime()
            .desc()
            .list();

    List<HistoricTaskInstance> taskList = historicTaskInstanceList.stream()
            .filter(a -> a.getEndTime() == null)
            .collect(Collectors.toList());
    if (!CollectionUtils.isEmpty(taskList)) {
      processInstanceDetailDto.setTaskName(taskList.stream()
              .map(HistoricTaskInstance::getName)
              .filter(StringUtils::hasText)
              .collect(Collectors.joining(",")));
    } else {
      processInstanceDetailDto.setTaskName("已完成！");
    }

    List<TaskDetailInfoDto> taskDetailInfoDtoList = new ArrayList<>();
    processInstanceDetailDto.setTaskDetailInfoDtoList(taskDetailInfoDtoList);
    historicTaskInstanceList.forEach(historicTaskInstance -> {
      TaskDetailInfoDto taskDetailInfoDto = new TaskDetailInfoDto();
      taskDetailInfoDto.accept(historicTaskInstance, bpmnModel,
              groupManager::getGroupNameByGroupId, userManager::getUserNameByUserId);

      // 获取评论
      if (!CollectionUtils.isEmpty(commentList)) {
        List<CommentDto> commentDtoList = commentList.stream()
                .filter(a -> historicTaskInstance.getId().equals(a.getTaskId()))
                .map(this::toCommentDto)
                .sorted(Comparator.comparing(CommentDto::getCommentDateTime))
                .collect(Collectors.toList());
        taskDetailInfoDto.setCommentDtoList(commentDtoList);
      }
      taskDetailInfoDtoList.add(taskDetailInfoDto);
    });
    return processInstanceDetailDto;
  }

  private CommentDto toCommentDto(Comment comment) {
    CommentDto commentDto = new CommentDto();
    commentDto.setId(comment.getId());
    commentDto.setProcessInstanceId(comment.getProcessInstanceId());
    commentDto.setTaskId(comment.getTaskId());
    commentDto.setCommentType(CommentType.valueOf(comment.getType()));
    commentDto.setCommentUserId(comment.getUserId());
    commentDto.setCommentUserName(userManager.getUserNameByUserId(comment.getUserId()));
    commentDto.setMessage(comment.getFullMessage());
    commentDto.setCommentDateTime(comment.getTime());
    return commentDto;
  }


  @Override
  public void deleteMultiInstanceExecution(String processInstanceId, String taskDefinitionKey, String deleteUserCode) {
    ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
            .processInstanceId(processInstanceId)
            .singleResult();
    if (processInstance == null) {
      throw new ServiceException("流程实例不存在！");
    }

    List<Task> taskList = taskService.createTaskQuery()
            .processInstanceId(processInstanceId)
            .taskDefinitionKey(taskDefinitionKey)
            .list();
    if (taskList.size() == 1) {
      throw new ServiceException("当前节点只有一个人在审批，无法减签！");
    }
    Task task = taskService.createTaskQuery()
            .processInstanceId(processInstanceId)
            .taskAssignee(deleteUserCode)
            .taskDefinitionKey(taskDefinitionKey)
            .singleResult();
    if (CollectionUtils.isEmpty(taskList)) {
      throw new ServiceException(String.format("用户[%s]没有节点的审批任务，无法减签！", deleteUserCode));
    }
    BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
    UserTask userTask = ModelUtils.getUserTask(bpmnModel, taskDefinitionKey);
    MultiInstanceLoopCharacteristics loopCharacteristics = userTask.getLoopCharacteristics();
    if (loopCharacteristics == null) {
      throw new ServiceException("任务节点不是多实例！");
    }
    // 更新变量名
    String collectionName = loopCharacteristics.getInputDataItem();
    collectionName = collectionName.replace("${", "").replace("}", "");
    List<String> curUserCodeList = (List<String>) runtimeService.getVariable(processInstanceId, collectionName);
    curUserCodeList.remove(deleteUserCode);
    runtimeService.setVariable(processInstanceId, collectionName, curUserCodeList);

    runtimeService.deleteMultiInstanceExecution(task.getExecutionId(), true);
  }

  @Override
  public void addMultiInstanceExecution(String processInstanceId, String taskDefinitionKey, String addUserCode) {
    ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
            .processInstanceId(processInstanceId)
            .singleResult();
    if (processInstance == null) {
      throw new ServiceException("流程实例不存在！");
    }
    List<Task> taskList = taskService.createTaskQuery().
            processInstanceId(processInstanceId).
            taskDefinitionKey(taskDefinitionKey).
            list();
    if (CollectionUtils.isEmpty(taskList)) {
      throw new ServiceException("当前节点未在运行中，无法加签！");
    }
    BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
    UserTask userTask = ModelUtils.getUserTask(bpmnModel, taskDefinitionKey);
    MultiInstanceLoopCharacteristics loopCharacteristics = userTask.getLoopCharacteristics();
    if (loopCharacteristics == null) {
      throw new ServiceException("任务节点不是多实例！");
    }
    // 更新多实例的集合
    String collectionVariableName = loopCharacteristics.getInputDataItem();
    collectionVariableName = collectionVariableName.replace("${", "").replace("}", "");
    List<String> curUserCodeList = (List<String>) runtimeService.getVariable(processInstanceId, collectionVariableName);
    curUserCodeList.add(addUserCode);
    runtimeService.setVariable(processInstanceId, collectionVariableName, curUserCodeList);
    String elementVariableName = loopCharacteristics.getElementVariable();
    // 会更新nrOfInstances
    runtimeService.addMultiInstanceExecution(
            taskDefinitionKey, processInstanceId, Collections.singletonMap(elementVariableName, addUserCode));
  }

  @Override
  public void messageEventReceived(String processInstanceId, String messageEventAttachedActivityKey, String messageName, Map<String, String> instanceVariableMap) {
    List<Execution> executionList = runtimeService.createExecutionQuery()
            .processInstanceId(processInstanceId)
            .activityId(messageEventAttachedActivityKey)
            .list();
    if (CollectionUtils.isEmpty(executionList)) {
      throw new ServiceException("未找到正在活跃的Activity！");
    }
    for (Execution execution : executionList) {
      runtimeService.messageEventReceived(messageName, execution.getId(), new HashMap<>(instanceVariableMap));
    }
  }

  /**
   * 用户在我处理的任务界面，查看某任务是否可以被撤回：任务的ID在返回结果之中，可以撤回否则不可以撤回。
   */
  @Override
  public List<TaskDetailInfoDto> queryTaskCanBeWithdrawn(String processInstanceId) {
    ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
            .processInstanceId(processInstanceId)
            .singleResult();

    if (processInstance == null) {
      throw new ServiceException("流程实例不存在或已结束！");
    }

    BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());

    // 可以撤回的用户任务
    List<String> userTaskCanBeWithdrawn = ModelUtils.findUserTaskCanBeWithdrawn(bpmnModel);

    // 所有的任务实例
    List<HistoricTaskInstance> allHistoricTaskInstanceList = historyService.createHistoricTaskInstanceQuery()
            .processInstanceId(processInstanceId)
            .list();

    // 所有的任务实例
    List<HistoricTaskInstance> historicTaskInstanceList = allHistoricTaskInstanceList.stream()
            .filter(a -> userTaskCanBeWithdrawn.contains(a.getTaskDefinitionKey()))
            .collect(Collectors.toList());

    // 未完成的任务实例的key
    List<String> unFinishedTaskKeyList = historicTaskInstanceList.stream()
            .filter(a -> a.getEndTime() == null)
            .map(TaskInfo::getTaskDefinitionKey)
            .distinct()
            .collect(Collectors.toList());

    // 已完成的任务实例
    List<HistoricTaskInstance> finishedHistoricTaskInstanceList = historicTaskInstanceList.stream()
            .filter(a -> a.getEndTime() != null)
            .collect(Collectors.toList());

    // 如果节点在运行中，这个节点的所有任务都是不能撤回的
    finishedHistoricTaskInstanceList.removeIf(a -> unFinishedTaskKeyList.contains(a.getTaskDefinitionKey()));

    // 取每个节点最晚结束的那个任务
    Collection<HistoricTaskInstance> taskCanBeWithdrawn = finishedHistoricTaskInstanceList.stream()
            .collect(Collectors.toMap(HistoricTaskInstance::getTaskDefinitionKey, Function.identity(), (o1, o2) -> {
              if (o1.getEndTime().after(o2.getEndTime())) {
                return o1;
              }
              return o2;
            })).values();

    // 任务的下一级节点所有任务都不能完成，否则不能撤回
    Iterator<HistoricTaskInstance> iterator = taskCanBeWithdrawn.iterator();
    while (iterator.hasNext()) {
      HistoricTaskInstance historicTaskInstance = iterator.next();
      UserTask userTask = ModelUtils.getUserTask(bpmnModel, historicTaskInstance.getTaskDefinitionKey());
      // 查找下一级用户任务节点
      List<UserTask> nextUserTaskList = ModelUtils.findUserTaskToBeWithdrawn(userTask);
      List<String> nextUserTaskKeyList = nextUserTaskList.stream()
              .map(UserTask::getId)
              .collect(Collectors.toList());

      // 检查已完成流程历史节点是否存在下一级中
      for (HistoricTaskInstance taskInstance : allHistoricTaskInstanceList) {
        if (taskInstance.getEndTime() != null
                && taskInstance.getCreateTime().after(historicTaskInstance.getEndTime())
                && nextUserTaskKeyList.contains(taskInstance.getTaskDefinitionKey())) {
          iterator.remove();
          break;
        }
      }
    }

    // 可以撤回的任务ID
    List<String> taskIdCanBeWithdrawn = taskCanBeWithdrawn.stream()
            .map(HistoricTaskInstance::getId)
            .collect(Collectors.toList());

    // 查询流程实例详情并获取任务Dto
    ProcessInstanceDetailDto processInstanceDetailDto = queryDetailProcess(processInstanceId);
    List<TaskDetailInfoDto> taskDetailInfoDtoList = processInstanceDetailDto.getTaskDetailInfoDtoList();

    // 过滤出来可以撤回的任务Dto
    taskDetailInfoDtoList.removeIf(a -> !taskIdCanBeWithdrawn.contains(a.getTaskId()));

    return taskDetailInfoDtoList;
  }

}

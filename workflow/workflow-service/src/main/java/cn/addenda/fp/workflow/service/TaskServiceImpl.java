package cn.addenda.fp.workflow.service;

import cn.addenda.component.jdk.exception.ServiceException;
import cn.addenda.component.jdk.util.collection.ArrayUtils;
import cn.addenda.component.user.UserContext;
import cn.addenda.fp.workflow.constant.*;
import cn.addenda.fp.workflow.dto.CompleteVariableDto;
import cn.addenda.fp.workflow.flowable.command.UpdateHistoricActivityInstanceDeleteReasonCommand;
import cn.addenda.fp.workflow.manager.InstanceManager;
import cn.addenda.fp.workflow.util.ModelUtils;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.common.engine.impl.interceptor.CommandExecutor;
import org.flowable.engine.HistoryService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricDetail;
import org.flowable.engine.history.HistoricVariableUpdate;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.DelegationState;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class TaskServiceImpl implements TaskService {

  @Autowired
  private org.flowable.engine.TaskService taskService;

  @Autowired
  private RepositoryService repositoryService;

  @Autowired
  private HistoryService historyService;

  @Autowired
  private RuntimeService runtimeService;

  @Autowired
  private InstanceManager instanceManager;

  @Autowired
  private ProcessEngine processEngine;

  @Override
  public void claim(String taskId) {
    Task task = taskService.createTaskQuery()
            .taskId(taskId)
            // 未拾取的任务才能查出来
            .taskUnassigned()
            // 只有组里的候选人才能查出来
            .taskCandidateUser(UserContext.getUserId())
            .singleResult();
    if (task != null) {
      // 如果任务已经被别人拾取了，抛FlowableTaskAlreadyClaimedException
      // 已拾取的用户可以再次拾取
      taskService.claim(taskId, UserContext.getUserId());
      // 设置任务接收方式为CLAIM
      taskService.setVariableLocal(taskId, TaskConstants.TASK_RECEIVE_TYPE_KEY, TaskReceiveType.CLAIM.toString());
    } else {
      throw new ServiceException("任务不存在或用户无权限！");
    }
  }


  /**
   * 用户归还任务
   */
  @Override
  public void unClaim(String taskId) {
    Task task = taskService.createTaskQuery()
            .taskId(taskId)
            // 拾取的任务才能查出来
            .taskAssigned()
            // 只有组里的候选人才能查出来
            .taskAssignee(UserContext.getUserId())
            .singleResult();
    if (task != null) {
      taskService.unclaim(taskId);
    } else {
      throw new ServiceException("任务不存在或用户无权限！");
    }
  }


  @Transactional
  @Override
  public void withdraw(String taskId, String comment) {
    // 获取待撤回任务实例
    HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery()
            .taskId(taskId)
            .finished()
            .taskAssignee(UserContext.getUserId())
            .singleResult();
    if (null == historicTaskInstance) {
      throw new ServiceException("当前任务不存在，无法执行撤回！");
    }
    if (historicTaskInstance.getEndTime() == null) {
      throw new ServiceException("当前任务未完成，无法执行撤回！");
    }

    // 校验流程是否结束
    String processInstanceId = historicTaskInstance.getProcessInstanceId();
    ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
            .processInstanceId(processInstanceId)
            .active()
            .singleResult();
    if (null == processInstance) {
      throw new ServiceException("流程已结束或已挂起，无法执行撤回！");
    }

    // 当前流程实例里，当前节点的最后一个任务才能撤回
    List<HistoricTaskInstance> sameKeyHistoricTaskInstanceList = historyService.createHistoricTaskInstanceQuery()
            .taskDefinitionKey(historicTaskInstance.getTaskDefinitionKey())
            .processInstanceId(processInstanceId)
            .list();
    sameKeyHistoricTaskInstanceList.sort(Comparator.comparing(TaskInfo::getCreateTime));
    if (!taskId.equals(sameKeyHistoricTaskInstanceList.get(sameKeyHistoricTaskInstanceList.size() - 1).getId())) {
      throw new ServiceException("当前节点的最后一个任务才能撤回！");
    }

    // 获取 bpmn 模型
    BpmnModel bpmnModel = repositoryService.getBpmnModel(historicTaskInstance.getProcessDefinitionId());
    UserTask withdrawUserTaskModel = ModelUtils.getUserTask(bpmnModel, historicTaskInstance.getTaskDefinitionKey());
    if (!ModelUtils.checkIfElementCanBeWithdrawn(withdrawUserTaskModel)) {
      throw new ServiceException("当前节点无法撤销！");
    }

    // 查找下一级用户任务节点
    List<UserTask> nextUserTaskList = ModelUtils.findUserTaskToBeWithdrawn(withdrawUserTaskModel);
    List<String> nextUserTaskKeyList = nextUserTaskList.stream()
            .map(UserTask::getId)
            .collect(Collectors.toList());

    // 当前流程实例下全部的任务
    List<HistoricTaskInstance> taskInstanceList = historyService.createHistoricTaskInstanceQuery()
            .processInstanceId(processInstanceId)
            .list();

    // 检查已完成流程历史节点是否存在下一级中
    for (HistoricTaskInstance taskInstance : taskInstanceList) {
      if (taskInstance.getEndTime() != null
              && taskInstance.getCreateTime().after(historicTaskInstance.getEndTime())
              && nextUserTaskKeyList.contains(taskInstance.getTaskDefinitionKey())) {
        throw new ServiceException("下一流程已处理，无法执行撤回操作");
      }
    }

    // 获取所有激活的任务节点，找到需要撤回的任务
    List<HistoricTaskInstance> withdrawTaskInstanceList = new ArrayList<>();
    for (HistoricTaskInstance taskInstance : taskInstanceList) {
      // 检查激活的任务节点是否存在下一级中，如果存在，则加入到需要撤回的节点
      if (taskInstance.getCreateTime().after(historicTaskInstance.getEndTime())
              && nextUserTaskKeyList.contains(taskInstance.getTaskDefinitionKey())) {
        // 下一层激活的节点的assignee设为自己
        taskService.setAssignee(taskInstance.getId(), UserContext.getUserId());
        String userComment = UserContext.getUser() + ": " + comment;
        taskService.addComment(taskInstance.getId(), taskInstance.getProcessInstanceId(), CommentType.WITHDRAW.getType(), userComment);

        withdrawTaskInstanceList.add(taskInstance);
      }
    }

    try {
      LinkedHashSet<String> withdrawExecutionIdList = withdrawTaskInstanceList.stream()
              .map(HistoricTaskInstance::getExecutionId)
              .collect(Collectors.toCollection(LinkedHashSet::new));
      runtimeService.createChangeActivityStateBuilder()
              .processInstanceId(processInstanceId)
              .moveExecutionsToSingleActivityId(new ArrayList<>(withdrawExecutionIdList), historicTaskInstance.getTaskDefinitionKey())
              .changeState();
    } catch (FlowableObjectNotFoundException e) {
      throw new ServiceException("未找到流程实例，流程可能已发生变化", e);
    } catch (FlowableException e) {
      throw new ServiceException("执行撤回操作失败", e);
    }

    // 查询从撤回节点到下一级节点之间的连线和网关
    List<FlowElement> traversedElementToUserTaskCanBeWithdrawn = ModelUtils.findTraversedElementToUserTaskCanBeWithdrawn(withdrawUserTaskModel);
    List<String> traversedElementIdToUserTaskCanBeWithdrawn = traversedElementToUserTaskCanBeWithdrawn.stream()
            .map(FlowElement::getId).distinct().collect(Collectors.toList());

    // 查询已经完成的act
    List<HistoricActivityInstance> historicActivityInstanceList = historyService.createHistoricActivityInstanceQuery()
            .processInstanceId(processInstanceId)
            .finished()
            .orderByHistoricActivityInstanceEndTime()
            .asc()
            .list();
    List<String> actIdList = new ArrayList<>();
    for (HistoricActivityInstance historicActivityInstance : historicActivityInstanceList) {
      // 如果结束时间在当前时间之后
      if (traversedElementIdToUserTaskCanBeWithdrawn.contains(historicActivityInstance.getActivityId())
              && historicActivityInstance.getEndTime().after(historicTaskInstance.getEndTime())) {
        actIdList.add(historicActivityInstance.getId());
      }
    }

    // 查询用户任务的删除原因
    String deleteTaskId = withdrawTaskInstanceList.stream().map(HistoricTaskInstance::getId).collect(Collectors.toList()).get(0);
    String deleteReason = historyService.createHistoricTaskInstanceQuery().taskId(deleteTaskId).singleResult().getDeleteReason();

    // 将act标记为删除
    CommandExecutor commandExecutor = processEngine.getProcessEngineConfiguration().getCommandExecutor();
    for (String actId : actIdList) {
      commandExecutor.execute(new UpdateHistoricActivityInstanceDeleteReasonCommand(actId, deleteReason));
    }


    // 当前用户拾取任务
    Task task = taskService.createTaskQuery()
            .processInstanceId(historicTaskInstance.getProcessInstanceId())
            .taskDefinitionKey(historicTaskInstance.getTaskDefinitionKey())
            .singleResult();
    if (task == null) {
      throw new ServiceException("执行撤回操作失败，无法将撤回后的任务分配给当前用户！");
    }
    claim(task.getId());
  }

  @Override
  public void complete(String taskId, String comment, CompleteVariableDto completeVariableDto) {
    Task task = taskService.createTaskQuery()
            .taskId(taskId)
            .taskAssignee(UserContext.getUserId())
            .singleResult();
    if (Objects.isNull(task)) {
      throw new ServiceException("任务不存在或不归您处理！");
    }

    if (DelegationState.PENDING.equals(task.getDelegationState())) {
      throw new ServiceException("任务已委托，委托人需先处理任务！");
    } else {
      String userComment = UserContext.getUser() + ": " + comment;
      taskService.addComment(taskId, task.getProcessInstanceId(), CommentType.COMPLETE.getType(), userComment);
      Map<String, String> instanceVariableMap = completeVariableDto.getInstanceVariableMap();
      if (!CollectionUtils.isEmpty(instanceVariableMap)) {
        taskService.setVariables(taskId, new HashMap<>(instanceVariableMap));
      }
      Map<String, String> taskVariableMap = completeVariableDto.getTaskVariableMap();
      if (!CollectionUtils.isEmpty(taskVariableMap)) {
        taskService.setVariablesLocal(taskId, new HashMap<>(taskVariableMap));
      }
      taskService.complete(taskId);
    }
  }

  /**
   * 转办任务
   */
  @Override
  public void transfer(String taskId, String transfer, String comment) {
    // 当前任务 task
    Task task = taskService.createTaskQuery()
            .taskId(taskId)
            .taskAssignee(UserContext.getUserId())
            .singleResult();
    if (null == task) {
      throw new ServiceException("获取任务失败！");
    }
    if (task.getDelegationState() != null) {
      throw new ServiceException("任务已被委托，不可转交！");
    }
    String userComment = UserContext.getUser() + "->" + transfer + ": " + comment;
    // 添加审批意见
    taskService.addComment(taskId, task.getProcessInstanceId(), CommentType.TRANSFER.getType(), userComment);
    // 转办任务
    taskService.setAssignee(taskId, transfer);
    // 设置任务接收方式为TRANSFER
    taskService.setVariableLocal(taskId, TaskConstants.TASK_RECEIVE_TYPE_KEY, TaskReceiveType.TRANSFER.toString());
  }


  @Override
  public void reject(String taskId, String comment) {
    // 当前任务 task
    Task task = taskService.createTaskQuery()
            .taskId(taskId)
            .taskAssignee(UserContext.getUserId())
            .singleResult();
    if (null == task) {
      throw new ServiceException("获取任务信息异常！");
    }
    if (task.isSuspended()) {
      throw new ServiceException("任务处于挂起状态");
    }
    // 获取流程实例
    String processInstanceId = task.getProcessInstanceId();
    ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
            .processInstanceId(processInstanceId)
            .singleResult();
    if (processInstance == null) {
      throw new ServiceException("流程实例不存在，请确认！");
    }
    String userComment = UserContext.getUser() + ": " + comment;
    // 添加审批意见
    taskService.addComment(taskId, processInstance.getId(), CommentType.REJECT.getType(), userComment);
    // 设置流程状态为已终结
    runtimeService.setVariable(processInstance.getId(), ProcessConstants.PROCESS_STATUS_KEY, ProcessStatusEnum.TERMINATED.getStatus());
    // 将活动节点移动至结束节点
    instanceManager.moveExecutionToEndEvent(processInstance.getProcessDefinitionId(), processInstanceId);
  }


  /**
   * 委派任务。委托人：principal；被委托人：delegate
   */
  @Override
  public void delegate(String taskId, String delegate, String comment) {
    // 当前任务 task
    Task task = taskService.createTaskQuery()
            .taskId(taskId)
            .taskAssignee(UserContext.getUserId())
            .singleResult();
    if (task == null) {
      throw new ServiceException("获取任务失败！");
    }
    if (task.getOwner() != null) {
      throw new ServiceException("任务已经委托过，不可再次委托！");
    }
    String userComment = UserContext.getUser() + "->" + delegate + ": " + comment;
    // 添加审批意见
    taskService.addComment(taskId, task.getProcessInstanceId(), CommentType.DELEGATE.getType(), userComment);
    // 设置办理人为当前登录人
    taskService.setOwner(taskId, UserContext.getUserId());
    // 执行委派
    taskService.delegateTask(taskId, delegate);
    // 设置任务接收方式为DELEGATION
    taskService.setVariableLocal(taskId, TaskConstants.TASK_RECEIVE_TYPE_KEY, TaskReceiveType.DELEGATION.toString());
  }


  @Override
  public void resolve(String taskId, String comment) {
    Task task = taskService.createTaskQuery()
            .taskId(taskId)
            .taskAssignee(UserContext.getUserId())
            .singleResult();
    if (Objects.isNull(task)) {
      throw new ServiceException("任务不存在或不归您处理！");
    }
    if (DelegationState.PENDING.equals(task.getDelegationState())) {
      String userComment = UserContext.getUser() + ": " + comment;
      taskService.addComment(taskId, task.getProcessInstanceId(), CommentType.RESOLVE.getType(), userComment);
      // resolveTask之后不会触发后续流程。resolveTask()执行完成之后assignee会赋owner的值，
      // 下次从/complete接口进来的请求会走complete()逻辑
      taskService.resolveTask(taskId);
    } else {
      throw new ServiceException("当前任务未被委托！");
    }
  }


  /**
   * 委派任务。委托人：principal；被委托人：delegate
   */
  @Override
  public void cancelDelegate(String taskId, String comment) {
    // 当前任务 task
    Task task = taskService.createTaskQuery()
            .taskId(taskId)
            .taskDelegationState(DelegationState.PENDING)
            .taskOwner(UserContext.getUserId())
            .singleResult();
    if (task == null) {
      throw new ServiceException("获取任务失败！");
    }
    String userComment = UserContext.getUser() + ": " + comment;
    taskService.addComment(taskId, task.getProcessInstanceId(), CommentType.CANCEL_DELEGATE.getType(), userComment);
    taskService.resolveTask(taskId);
    // 取消owner
    taskService.setOwner(taskId, null);

    // 任务接收方式回退到delegate之前
    List<HistoricDetail> historicDetailList = historyService.createHistoricDetailQuery()
            .taskId(taskId)
            .variableUpdates()
            .orderByTime()
            .desc()
            .list();
    historicDetailList.removeIf(historicDetail -> {
      HistoricVariableUpdate historicVariableUpdate = (HistoricVariableUpdate) historicDetail;
      return !TaskConstants.TASK_RECEIVE_TYPE_KEY.equals(historicVariableUpdate.getVariableName());
    });
    taskService.setVariableLocal(taskId, TaskConstants.TASK_RECEIVE_TYPE_KEY,
            ((HistoricVariableUpdate) historicDetailList.get(1)).getValue());
  }

  /**
   * 用户在我的待办任务列表，查看任务可以退回的节点列表。
   */
  @Override
  public List<String> findTaskKeyCanReturn(String taskId) {
    // 当前任务 task
    Task task = taskService.createTaskQuery()
            .taskAssignee(UserContext.getUserId())
            .taskId(taskId)
            .singleResult();
    if (task == null) {
      throw new ServiceException("任务不存在！");
    }

    // 查询已完成的任务实例
    List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery()
            .processInstanceId(task.getProcessInstanceId())
            .orderByHistoricTaskInstanceEndTime()
            .asc()
            .list();

    // 每个任务节点最新的任务
    List<HistoricTaskInstance> latestHaistoricTaskInstanceList = historicTaskInstanceList.stream()
            .collect(Collectors.toMap(TaskInfo::getTaskDefinitionKey, a -> a, (o1, o2) -> {
              if (o1.getCreateTime().after(o2.getCreateTime())) {
                return o1;
              }
              return o2;
            })).values().stream()
            .collect(Collectors.toList());

    // 未完成的任务
    List<String> unfinishedTaskKeyList = latestHaistoricTaskInstanceList.stream()
            .filter(a -> a.getEndTime() == null)
            .map(HistoricTaskInstance::getTaskDefinitionKey)
            .distinct()
            .collect(Collectors.toList());

    List<String> finishedTaskKeyList = latestHaistoricTaskInstanceList.stream()
            .filter(a -> a.getEndTime() != null)
            .map(HistoricTaskInstance::getTaskDefinitionKey)
            .filter(taskKey -> !taskKey.equals(task.getTaskDefinitionKey()))
            .distinct()
            .collect(Collectors.toList());

    // 获取流程模型信息
    BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
    if (ModelUtils.ifMultiInstance(bpmnModel, task.getTaskDefinitionKey())) {
      return new ArrayList<>();
    }


    // 获取当前任务节点元素
    FlowElement source = ModelUtils.getFlowElement(bpmnModel, task.getTaskDefinitionKey());
    List<FlowElement> elementList = new ArrayList<>();
    for (String taskKey : finishedTaskKeyList) {
      FlowElement target = ModelUtils.getFlowElement(bpmnModel, taskKey);
      boolean isSequential = ModelUtils.ifSequentialReachable(target, source, unfinishedTaskKeyList);
      if (isSequential) {
        elementList.add(target);
      }
    }
    return elementList.stream().map(FlowElement::getId).collect(Collectors.toList());
  }


  @Override
  public void taskReturn(String taskId, String targetActivityId, String comment) {
    // 当前任务 task
    Task task = taskService.createTaskQuery()
            .taskId(taskId)
            .singleResult();
    if (null == task) {
      throw new ServiceException("获取任务信息异常！");
    }
    if (task.isSuspended()) {
      throw new ServiceException("任务处于挂起状态");
    }
    // 获取流程定义信息
    ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
            .processDefinitionId(task.getProcessDefinitionId())
            .singleResult();
    // 获取流程模型信息
    BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinition.getId());
    if (ModelUtils.ifMultiInstance(bpmnModel, task.getTaskDefinitionKey())) {
      throw new ServiceException("多实例任务不支持退回！");
    }

    // 通过流程变量退回不会清理ACT，这里也不清理

    // 查询已完成的任务实例
    List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery()
            .processInstanceId(task.getProcessInstanceId())
            .orderByHistoricTaskInstanceEndTime()
            .asc()
            .list();

    // 每个任务节点最新的任务
    List<HistoricTaskInstance> latestHaistoricTaskInstanceList = historicTaskInstanceList.stream()
            .collect(Collectors.toMap(TaskInfo::getTaskDefinitionKey, a -> a, (o1, o2) -> {
              if (o1.getCreateTime().after(o2.getCreateTime())) {
                return o1;
              }
              return o2;
            })).values().stream()
            .collect(Collectors.toList());

    // 未完成的任务
    List<String> unfinishedTaskKeyList = latestHaistoricTaskInstanceList.stream()
            .filter(a -> a.getEndTime() == null)
            .map(HistoricTaskInstance::getTaskDefinitionKey)
            .distinct()
            .collect(Collectors.toList());

    // 获取当前任务节点元素
    FlowElement source = ModelUtils.getFlowElement(bpmnModel, task.getTaskDefinitionKey());
    // 获取跳转的节点元素
    FlowElement target = ModelUtils.getFlowElement(bpmnModel, targetActivityId);
    // 从当前节点向前扫描，判断当前节点与目标节点是否属于串行，若目标节点是在并行网关上或非同一路线上，不可跳转
    boolean isSequential = ModelUtils.ifSequentialReachable(target, source, unfinishedTaskKeyList);
    if (!isSequential) {
      throw new ServiceException("当前节点相对于目标节点，不属于串行关系，无法回退");
    }

    try {
      // 1 对 1 或 多 对 1 情况，currentIds 当前要跳转的节点列表(1或多)，targetKey 跳转到的节点(1)
      runtimeService.createChangeActivityStateBuilder()
              .processInstanceId(task.getProcessInstanceId())
              .moveExecutionsToSingleActivityId(ArrayUtils.asArrayList(task.getExecutionId()), targetActivityId)
              .changeState();
    } catch (FlowableObjectNotFoundException e) {
      throw new ServiceException("未找到流程实例，流程可能已发生变化");
    } catch (FlowableException e) {
      throw new ServiceException("无法取消或开始活动");
    }
  }

}

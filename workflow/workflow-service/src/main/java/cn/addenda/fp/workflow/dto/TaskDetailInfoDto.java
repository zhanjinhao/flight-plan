package cn.addenda.fp.workflow.dto;

import cn.addenda.fp.workflow.constant.TaskConstants;
import cn.addenda.fp.workflow.constant.TaskReceiveType;
import cn.addenda.fp.workflow.util.ModelUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.UserTask;
import org.flowable.identitylink.api.IdentityLinkInfo;
import org.flowable.identitylink.api.IdentityLinkType;
import org.flowable.identitylink.api.history.HistoricIdentityLink;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Setter
@Getter
@ToString
public class TaskDetailInfoDto {
  /**
   * 任务编号
   */
  private String taskId;
  /**
   * 任务名称
   */
  private String taskName;
  /**
   * 任务Key
   */
  private String taskDefinitionKey;
  /**
   * 任务执行人所属组
   */
  private String groupId;
  /**
   * 任务执行人所属组名称
   */
  private String groupName;
  /**
   * 任务的开始时间
   */
  private Date taskStartDateTime;
  /**
   * 任务的结束时间
   */
  private Date taskEndDateTime;
  /**
   * 任务的耗时
   */
  private Long taskDuration;
  /**
   * 任务的实际完成人
   */
  private String assignee;
  /**
   * 任务的实际完成人名称
   */
  private String assigneeName;
  /**
   * 任务的完成方式：CLAIM，DELEGATION，TRANSFER
   */
  private TaskReceiveType taskReceiveType;
  /**
   * 如果任务的owner是null，赋值为assignee
   */
  private String owner;

  private String ownerName;
  /**
   * 如果taskReceiveType是claim，receiveDateTime是claim_time；
   * 如果taskReceiveType是delegation，receiveDateTime是ACT_HI_IDENTITYLINK里最后assignee对应的create_time。
   * 如果taskReceiveType是transfer，receiveDateTime是ACT_HI_IDENTITYLINK里最后assignee对应的create_time。
   */
  private Date receiveDateTime;

  private boolean ifMultiInstance;

  private List<CommentDto> commentDtoList;

  public void accept(Task task, BpmnModel bpmnModel,
                     Function<String, String> groupNameFunction, Function<String, String> userNameFunction,
                     List<HistoricIdentityLink> historicIdentityLinkList) {
    this.setTaskDuration(new Date().getTime() - task.getCreateTime().getTime());
    doAccept(task, bpmnModel, groupNameFunction, userNameFunction, historicIdentityLinkList);
  }

  public void accept(HistoricTaskInstance historicTaskInstance, BpmnModel bpmnModel,
                     Function<String, String> groupNameFunction, Function<String, String> userNameFunction) {
    if (historicTaskInstance.getEndTime() == null) {
      this.setTaskDuration(new Date().getTime() - historicTaskInstance.getCreateTime().getTime());
    } else {
      this.setTaskDuration(historicTaskInstance.getEndTime().getTime() - historicTaskInstance.getCreateTime().getTime());
    }
    doAccept(historicTaskInstance, bpmnModel, groupNameFunction, userNameFunction, null);
  }

  private void doAccept(TaskInfo taskInfo, BpmnModel bpmnModel,
                        Function<String, String> groupNameFunction, Function<String, String> userNameFunction,
                        List<HistoricIdentityLink> historicIdentityLinkList) {
    this.setTaskId(taskInfo.getId());
    this.setTaskName(taskInfo.getName());
    this.setTaskDefinitionKey(taskInfo.getTaskDefinitionKey());
    this.setTaskStartDateTime(taskInfo.getCreateTime());

    List<? extends IdentityLinkInfo> identityLinkList = Optional.ofNullable(taskInfo.getIdentityLinks()).orElse(new ArrayList<>());
    Map<String, String> identityLinkMap = identityLinkList.stream()
            .filter(a -> IdentityLinkType.CANDIDATE.equals(a.getType()))
            .filter(a -> a.getTaskId() != null)
            .collect(Collectors.toMap(IdentityLinkInfo::getTaskId, IdentityLinkInfo::getGroupId));
    this.setGroupId(identityLinkMap.get(taskInfo.getId()));
    this.setGroupName(groupNameFunction.apply(identityLinkMap.get(taskInfo.getId())));

    // 设置taskReceiveType
    Map<String, Object> taskLocalVariables = taskInfo.getTaskLocalVariables();
    String taskReceiveType_ = (String) taskLocalVariables.get(TaskConstants.TASK_RECEIVE_TYPE_KEY);
    if (taskReceiveType_ == null) {
      this.setTaskReceiveType(TaskReceiveType.ASSIGN);
    } else {
      this.setTaskReceiveType(TaskReceiveType.valueOf(taskReceiveType_));
    }
    if (TaskReceiveType.CLAIM == getTaskReceiveType()) {
      this.setReceiveDateTime(taskInfo.getClaimTime());
    } else {
      // 此时 taskInfo instanceof HistoricTaskInstance == true
      if (historicIdentityLinkList == null) {
        historicIdentityLinkList = taskInfo.getIdentityLinks().stream().map(a -> (HistoricIdentityLink) a).collect(Collectors.toList());
      }
      historicIdentityLinkList.removeIf(a -> !IdentityLinkType.ASSIGNEE.equals(a.getType()));
      historicIdentityLinkList.sort(Comparator.comparing(a -> ((HistoricIdentityLink) a).getCreateTime()).reversed());
      if (!CollectionUtils.isEmpty(historicIdentityLinkList)) {
        this.setReceiveDateTime(historicIdentityLinkList.get(0).getCreateTime());
      }
    }

    this.setOwner(taskInfo.getOwner() != null ? taskInfo.getOwner() : taskInfo.getAssignee());
    this.setOwnerName(userNameFunction.apply(this.getOwner()));
    if (StringUtils.hasText(taskInfo.getAssignee())) {
      this.setAssignee(taskInfo.getAssignee());
      this.setAssigneeName(userNameFunction.apply(taskInfo.getAssignee()));
    }

    UserTask userTaskByKey = ModelUtils.getUserTask(bpmnModel, taskInfo.getTaskDefinitionKey());
    this.setIfMultiInstance(userTaskByKey.hasMultiInstanceLoopCharacteristics());
  }

}


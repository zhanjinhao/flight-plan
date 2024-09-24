package cn.addenda.fp.workflow.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.flowable.identitylink.api.IdentityLinkInfo;
import org.flowable.identitylink.api.IdentityLinkType;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.history.HistoricTaskInstance;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 任务基础数据
 */
@Setter
@Getter
@ToString
public abstract class AbstractTaskDto extends AbstractProcessInstanceDto {
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
   * 任务的耗时
   */
  private Long taskDuration;

  public void accept(Task task, Function<String, String> groupNameFunction) {
    this.setTaskDuration(new Date().getTime() - task.getCreateTime().getTime());
    doAccept(task, groupNameFunction);
  }

  public void accept(HistoricTaskInstance historicTaskInstance, Function<String, String> groupNameFunction) {
    if (historicTaskInstance.getEndTime() == null) {
      this.setTaskDuration(new Date().getTime() - historicTaskInstance.getCreateTime().getTime());
    } else {
      this.setTaskDuration(historicTaskInstance.getEndTime().getTime() - historicTaskInstance.getCreateTime().getTime());
    }
    doAccept(historicTaskInstance, groupNameFunction);
  }

  private void doAccept(TaskInfo taskInfo, Function<String, String> groupNameFunction) {
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
  }

}

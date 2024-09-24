package cn.addenda.fp.workflow.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.flowable.engine.impl.persistence.entity.HistoricProcessInstanceEntityImpl;
import org.flowable.engine.runtime.ProcessInstance;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.addenda.fp.workflow.constant.ProcessConstants.PROCESS_STATUS_KEY;

/**
 * 流程实例基础数据
 */
@Setter
@Getter
@ToString
public abstract class AbstractProcessInstanceDto extends AbstractProcessDefinitionDto {
  /**
   * 流程实例ID
   */
  private String processInstanceId;
  /**
   * 流程实例名称
   */
  private String processInstanceName;
  /**
   * 流程实例开始时间
   */
  private Date processInstanceStartDateTime;
  /**
   * 流程实例解释时间
   */
  private Date processInstanceEndDateTime;
  /**
   * 流程实例耗时：单位ms
   */
  private Long processInstanceDuration;
  /**
   * 流程状态
   */
  private String processInstanceStatus;
  /**
   * 流程实例发起人
   */
  private String processInstanceStartUserId;
  /**
   * 流程实例发起人名称
   */
  private String processInstanceStartUserName;
  /**
   * 业务 key
   */
  private String businessKey;

  public void accept(HistoricProcessInstance historicProcessInstance, Function<String, String> startUserNameFunction) {
    this.setProcessInstanceId(historicProcessInstance.getId());
    this.setProcessInstanceName(historicProcessInstance.getName());
    this.setProcessInstanceStartDateTime(historicProcessInstance.getStartTime());
    this.setProcessInstanceEndDateTime(historicProcessInstance.getEndTime());
    // 计算耗时
    if (null != historicProcessInstance.getEndTime()) {
      this.setProcessInstanceDuration(historicProcessInstance.getEndTime().getTime() - historicProcessInstance.getStartTime().getTime());
    } else {
      this.setProcessInstanceDuration(new Date().getTime() - historicProcessInstance.getStartTime().getTime());
    }
    if (!(historicProcessInstance instanceof HistoricProcessInstanceEntityImpl)) {
      throw new UnsupportedOperationException(
              String.format("type of historicProcessInstance should be: [%s], current is: [%s].",
                      HistoricProcessInstanceEntityImpl.class.getName(),
                      historicProcessInstance.getClass().getName()));
    }
    Map<String, String> processVariableMap = historicProcessInstance.getProcessVariables().entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, a -> a.getValue().toString()));
    this.setProcessInstanceStatus(processVariableMap.get(PROCESS_STATUS_KEY));
    this.setProcessInstanceStartUserId(historicProcessInstance.getStartUserId());
    this.setProcessInstanceStartUserName(Optional.ofNullable(historicProcessInstance.getStartUserId()).map(startUserNameFunction).orElse(null));
  }

  public void accept(ProcessInstance processInstance, Function<String, String> startUserNameFunction) {
    this.setProcessInstanceId(processInstance.getId());
    this.setProcessInstanceName(processInstance.getName());
    this.setProcessInstanceStartDateTime(processInstance.getStartTime());
    this.setProcessInstanceDuration(new Date().getTime() - processInstance.getStartTime().getTime());
    if (!(processInstance instanceof ExecutionEntityImpl)) {
      throw new UnsupportedOperationException(
              String.format("type of historicProcessInstance should be: [%s], current is: [%s].",
                      HistoricProcessInstanceEntityImpl.class.getName(),
                      processInstance.getClass().getName()));
    }
    Map<String, String> processVariableMap = processInstance.getProcessVariables().entrySet().stream()
            .collect(Collectors.toMap(Map.Entry::getKey, a -> a.getValue().toString()));
    this.setProcessInstanceStatus(processVariableMap.get(PROCESS_STATUS_KEY));
    this.setProcessInstanceStartUserId(processInstance.getStartUserId());
    this.setProcessInstanceStartUserName(Optional.ofNullable(processInstance.getStartUserId()).map(startUserNameFunction).orElse(null));
  }

}

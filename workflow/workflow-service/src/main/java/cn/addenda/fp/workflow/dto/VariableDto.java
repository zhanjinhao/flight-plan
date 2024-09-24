package cn.addenda.fp.workflow.dto;

import cn.addenda.fp.workflow.constant.VariableType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class VariableDto {

  // 根executionId == 实例的Id

  // EXECUTION_ID：根executionId；TASK_ID：null   -->  全局变量
  // org.flowable.engine.RuntimeService.startProcessInstanceByKey(processDefinitionKey, variableMap)

  // ifLocal=true:  EXECUTION_ID：taskId对应的executionId；TASK_ID：taskId   -->  任务级变量
  // ifLocal=false: EXECUTION_ID：根executionId；TASK_ID：null   -->  全局变量
  // org.flowable.engine.TaskService.complete(taskId, variableMap, ifLocal)

  // EXECUTION_ID：taskId对应的executionId的根executionId；TASK_ID：null   -->  全局变量
  // org.flowable.engine.TaskService.setVariable(taskId, variableName, variableValue)

  // EXECUTION_ID：taskId对应的executionId；TASK_ID：taskId    -->  任务级变量
  // org.flowable.engine.TaskService.setVariableLocal(taskId, variableName, variableValue)

  // EXECUTION_ID：executionId的根executionId；TASK_ID：null   -->  全局变量
  // org.flowable.engine.RuntimeService.setVariable(executionId, variableName, variableValue)

  // EXECUTION_ID：executionId；TASK_ID：null     -->    执行级变量
  // org.flowable.engine.RuntimeService.setVariableLocal(executionId, variableName, variableValue)

  // org.flowable.task.service.impl.persistence.entity.TaskEntityImpl.getTaskLocalVariables里判断local变量的逻辑：
  //   variableInstance.getId() != null && variableInstance.getTaskId() != null
  // org.flowable.task.service.impl.persistence.entity.TaskEntityImpl.getProcessVariables里判断global变量的逻辑：
  //   this.getProcessInstanceId() != null && this.getProcessInstanceId()
  //                        .equals(variableInstance.getProcessInstanceId()) && variableInstance.getTaskId() == null

  private String variableName;

  private String variableValue;

  private String processInstanceId;

  private String executionId;

  private String taskId;

  private VariableType variableType;

}

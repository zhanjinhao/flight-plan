package cn.addenda.fp.workflow.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.flowable.engine.repository.ProcessDefinition;

/**
 * 流程定义基础数据
 */
@Setter
@Getter
@ToString
public abstract class AbstractProcessDefinitionDto extends AbstractDeploymentDto {

  /**
   * 流程ID
   */
  private String processDefinitionId;
  /**
   * 流程key
   */
  private String processDefinitionKey;
  /**
   * 流程定义名称
   */
  private String processDefinitionName;
  /**
   * 流程定义内置使用版本
   */
  private int processDefinitionVersion;
  /**
   * 流程资源图名称
   */
  private String bpmnResourceName;
  /**
   * 流程示意图名称
   */
  private String diagramResourceName;

  public void accept(ProcessDefinition processDefinition) {
    this.setProcessDefinitionId(processDefinition.getId());
    this.setProcessDefinitionKey(processDefinition.getKey());
    this.setProcessDefinitionName(processDefinition.getName());
    this.setProcessDefinitionVersion(processDefinition.getVersion());
    this.setBpmnResourceName(processDefinition.getResourceName());
    this.setDiagramResourceName(processDefinition.getDiagramResourceName());
  }

}

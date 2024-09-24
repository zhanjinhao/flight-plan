package cn.addenda.fp.workflow.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 流程实例
 */
@Setter
@Getter
@ToString
public class ProcessInstanceDto extends AbstractProcessInstanceDto implements Serializable {

  /**
   * 正在运行的任务集合
   */
  private String taskName;

}

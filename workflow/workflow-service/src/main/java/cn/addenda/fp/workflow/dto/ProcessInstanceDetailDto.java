package cn.addenda.fp.workflow.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 流程实例详情：包含任务详情
 */
@Setter
@Getter
@ToString
public class ProcessInstanceDetailDto extends ProcessInstanceDto implements Serializable {

  private List<TaskDetailInfoDto> taskDetailInfoDtoList;

}

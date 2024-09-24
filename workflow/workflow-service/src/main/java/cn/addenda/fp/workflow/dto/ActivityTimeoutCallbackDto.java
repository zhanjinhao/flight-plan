package cn.addenda.fp.workflow.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
public class ActivityTimeoutCallbackDto extends ProcessInstanceDto {

  private List<ActivityTimeoutCallbackTaskDto> activityTimeoutCallbackTaskDtoList;

}

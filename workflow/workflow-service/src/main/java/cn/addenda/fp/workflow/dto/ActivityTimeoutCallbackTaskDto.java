package cn.addenda.fp.workflow.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ActivityTimeoutCallbackTaskDto extends TaskDetailInfoDto {

  private boolean ifInActivity;

}

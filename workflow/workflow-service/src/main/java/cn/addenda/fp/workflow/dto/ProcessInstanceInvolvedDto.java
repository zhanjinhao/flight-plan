package cn.addenda.fp.workflow.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ProcessInstanceInvolvedDto extends ProcessInstanceDto {

  private String involveType;

}

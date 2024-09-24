package cn.addenda.fp.workflow.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Setter
@Getter
@ToString
public class CompleteVariableDto {

  private Map<String, String> instanceVariableMap;
  private Map<String, String> taskVariableMap;

}

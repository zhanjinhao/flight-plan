package cn.addenda.fp.workflow.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Setter
@Getter
@ToString
public class InstanceVariablesDto {

  private List<VariableDto> instanceVariableDtoList;

  /**
   * taskId -> variableDtoList
   */
  private Map<String, List<VariableDto>> taskVariableDtoListMap;

  /**
   * executionId -> variableDtoList
   */
  private Map<String, List<VariableDto>> executionVariableDtoListMap;
}

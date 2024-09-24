package cn.addenda.fp.workflow.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 我参与的任务
 */
@Setter
@Getter
@ToString
public class TaskInvolvedDto extends TaskDoneDto implements Serializable {

  private String involveType;

}

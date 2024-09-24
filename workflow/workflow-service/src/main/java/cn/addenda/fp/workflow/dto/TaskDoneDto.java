package cn.addenda.fp.workflow.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 已完成的任务
 */
@Setter
@Getter
@ToString
public class TaskDoneDto extends TaskToDoDto implements Serializable {

  /**
   * 任务的结束时间
   */
  private Date taskEndDateTime;

}

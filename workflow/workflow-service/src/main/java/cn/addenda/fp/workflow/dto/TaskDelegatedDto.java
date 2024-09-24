package cn.addenda.fp.workflow.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 委托出去的任务
 */
@Setter
@Getter
@ToString
public class TaskDelegatedDto extends AbstractTaskDto implements Serializable {

  /**
   * 委托时间
   */
  private Date delegationDateTime;
  /**
   * 被委托人
   */
  private String assignee;

  private String assigneeName;

}

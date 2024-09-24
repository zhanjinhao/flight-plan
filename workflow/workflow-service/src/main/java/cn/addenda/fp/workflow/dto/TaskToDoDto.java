package cn.addenda.fp.workflow.dto;

import cn.addenda.fp.workflow.constant.TaskReceiveType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * 待执行的任务
 */
@Setter
@Getter
@ToString
public class TaskToDoDto extends TaskToClaimDto implements Serializable {

  /**
   * 任务的来源：CLAIM，DELEGATION，TRANSFER
   */
  private TaskReceiveType taskReceiveType;

  /**
   * false：委托给别人了，此任务自己不能做
   * true：自己做
   */
  private boolean ifSelfDo = true;

  /**
   * 如果任务的owner是null，赋值为assignee
   */
  private String owner;

  private String ownerName;

  /**
   * 如果taskReceiveType是claim，receiveDateTime是claim_time；
   * 如果taskReceiveType是delegation，receiveDateTime是ACT_HI_IDENTITYLINK里最后assignee对应的create_time；
   * 如果taskReceiveType是transfer，receiveDateTime是ACT_HI_IDENTITYLINK里最后assignee对应的create_time。
   */
  private Date receiveDateTime;

  /**
   * 任务的实际完成人
   */
  private String assignee;

  private String assigneeName;

}

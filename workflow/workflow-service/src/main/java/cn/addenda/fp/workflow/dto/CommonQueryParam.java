package cn.addenda.fp.workflow.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * 流程查询实体对象
 */
@Setter
@Getter
@ToString
public class CommonQueryParam {

  /**
   * 流程标识
   */
  private String processKey;

  /**
   * 流程名称
   */
  private String processName;

  /**
   * 流程分类
   */
  private String category;

  /**
   * 状态
   */
  private String state;

  /**
   * 对于任务来说，是任务创建时间；对于流程实例来说，是流程实例创建时间
   */
  private Date objectStartDateTime;

  /**
   * 对于任务来说，是任务结束时间；对于流程实例来说，是流程实例结束时间
   */
  private Date objectEndDateTime;
}

package cn.addenda.fp.workflow.dto;

import cn.addenda.fp.workflow.constant.CommentType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Setter
@Getter
@ToString
public class CommentDto {

  private String id;

  private CommentType commentType;

  private String commentUserId;

  private String commentUserName;

  private String taskId;

  private String processInstanceId;

  private String message;

  private Date commentDateTime;

}

package cn.addenda.fp.workflow.constant;

import lombok.Getter;

@Getter
public enum CommentType {

  COMPLETE("COMPLETE", "正常"),

  REJECT("REJECT", "驳回"),

  DELEGATE("DELEGATE", "委派"),

  CANCEL_DELEGATE("CANCEL_DELEGATE", "委派"),

  RESOLVE("RESOLVE", "解决"),

  TRANSFER("TRANSFER", "转办"),

  CANCEL("CANCEL", "终止"),

  REBACK("REBACK", "退回"),

  WITHDRAW("WITHDRAW", "撤回");

  /**
   * 类型
   */
  private final String type;

  /**
   * 说明
   */
  private final String remark;

  CommentType(String type, String remark) {
    this.type = type;
    this.remark = remark;
  }

}

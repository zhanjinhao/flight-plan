package cn.addenda.fp.workflow.constant;

public enum TaskReceiveType {

  /**
   * 拾取
   */
  CLAIM,
  /**
   * 转交
   */
  TRANSFER,
  /**
   * 委托
   */
  DELEGATION,
  /**
   * 赋值（用于多实例场景）
   */
  ASSIGN

}

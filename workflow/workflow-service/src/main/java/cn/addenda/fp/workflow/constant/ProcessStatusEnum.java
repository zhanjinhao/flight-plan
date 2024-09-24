package cn.addenda.fp.workflow.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.util.StringUtils;

@Getter
@AllArgsConstructor
public enum ProcessStatusEnum {

  /**
   * 进行中（审批中）
   */
  RUNNING("running"),
  /**
   * 已终止
   */
  TERMINATED("terminated"),
  /**
   * 已完成
   */
  COMPLETED("completed"),
  /**
   * 已取消
   */
  CANCELED("canceled");

  private final String status;

  public static ProcessStatusEnum getProcessStatus(String str) {
    if (StringUtils.hasText(str)) {
      for (ProcessStatusEnum value : values()) {
        if (equalsIgnoreCase(str, value.getStatus())) {
          return value;
        }
      }
    }
    return null;
  }

  public static boolean equalsIgnoreCase(final String cs1, final String cs2) {
    if (cs1 == cs2) {
      return true;
    }
    if (cs1 == null || cs2 == null) {
      return false;
    }
    if (cs1.length() != cs2.length()) {
      return false;
    }
    return cs1.equalsIgnoreCase(cs2);
  }

}

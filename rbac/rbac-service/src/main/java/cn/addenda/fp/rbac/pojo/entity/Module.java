package cn.addenda.fp.rbac.pojo.entity;

import cn.addenda.component.idgenerator.annotation.IdScope;
import cn.addenda.component.jdk.exception.ServiceException;
import cn.addenda.sql.vitamin.rewriter.baseentity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author addenda
 * @since 2022/1/17 20:39
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@IdScope(scopeName = "Module")
public class Module extends BaseEntity {

  public static final String DISABLE = "D";

  public static final String ACTIVE = "A";

  /**
   * 只能接收短信、邮件...，不能进入系统
   */
  public static final String AT_LISTEN = "L";

  /**
   * 只能读系统数据，不能写系统数据
   */
  public static final String AT_READ = "R";

  /**
   * 可以读系统数据，也能写系统数据
   */
  public static final String AT_WRITE = "W";

  /**
   * 目录数树
   */
  public static final String ST_NAVIGATION = "N";

  /**
   * 页面
   */
  public static final String ST_PAGE = "P";

  /**
   * 函数
   */
  public static final String ST_FUNCTION = "F";

  /**
   * 当前页面
   */
  public static final String RTT_CURRENT = "C";

  /**
   * 新页面
   */
  public static final String RTT_NEW = "N";

  /**
   * 跳到其他业务的界面
   */
  public static final String RTT_JUMP = "J";

  public static final Long PARENT_ID_OF_ROOT = -1L;
  public static final Long ROOT_ID = 0L;

  private Long id;

  /**
   * 唯一索引
   */
  private String moduleCode;

  private String moduleName;

  private String accessType;

  private String showType;

  private String responseToType;

  /**
   * 最顶层的目录是ROOT，它的id是0。<br/>
   * ROOT的parentId是-1。
   */
  private Long parentId;

  private String action;

  private String status;

  public static void assertStatus(String status) {
    if (!ACTIVE.equals(status) && !DISABLE.equals(status)) {
      throw new ServiceException("不合法的状态：" + status + "。");
    }
  }

  public Module(Long id) {
    this.id = id;
  }
}

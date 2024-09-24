package cn.addenda.fp.workflow.util;

import cn.addenda.fp.workflow.dto.CommonQueryParam;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.flowable.common.engine.api.query.Query;
import org.flowable.common.engine.impl.db.SuspensionState;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InstanceListQueryUtils {

  public static void buildProcessSearch(Query<?, ?> query, CommonQueryParam commonQueryParam) {
    if (query instanceof ProcessDefinitionQuery) {
      buildProcessDefinitionSearch((ProcessDefinitionQuery) query, commonQueryParam);
    } else if (query instanceof TaskQuery) {
      buildTaskSearch((TaskQuery) query, commonQueryParam);
    } else if (query instanceof HistoricTaskInstanceQuery) {
      buildHistoricTaskInstanceSearch((HistoricTaskInstanceQuery) query, commonQueryParam);
    } else if (query instanceof HistoricProcessInstanceQuery) {
      buildHistoricProcessInstanceSearch((HistoricProcessInstanceQuery) query, commonQueryParam);
    }
  }

  /**
   * 构建流程定义搜索
   */
  public static void buildProcessDefinitionSearch(ProcessDefinitionQuery query, CommonQueryParam commonQueryParam) {
    // 流程标识
    if (StringUtils.isNotBlank(commonQueryParam.getProcessKey())) {
      query.processDefinitionKeyLike("%" + commonQueryParam.getProcessKey() + "%");
    }
    // 流程名称
    if (StringUtils.isNotBlank(commonQueryParam.getProcessName())) {
      query.processDefinitionNameLike("%" + commonQueryParam.getProcessName() + "%");
    }
    // 流程分类
    if (StringUtils.isNotBlank(commonQueryParam.getCategory())) {
      query.processDefinitionCategory(commonQueryParam.getCategory());
    }
    // 流程状态
    if (StringUtils.isNotBlank(commonQueryParam.getState())) {
      if (SuspensionState.ACTIVE.toString().equals(commonQueryParam.getState())) {
        query.active();
      } else if (SuspensionState.SUSPENDED.toString().equals(commonQueryParam.getState())) {
        query.suspended();
      }
    }
  }

  /**
   * 构建任务搜索
   */
  public static void buildTaskSearch(TaskQuery query, CommonQueryParam commonQueryParam) {
    if (StringUtils.isNotBlank(commonQueryParam.getProcessKey())) {
      query.processDefinitionKeyLike("%" + commonQueryParam.getProcessKey() + "%");
    }
    if (StringUtils.isNotBlank(commonQueryParam.getProcessName())) {
      query.processDefinitionNameLike("%" + commonQueryParam.getProcessName() + "%");
    }
    if (commonQueryParam.getObjectStartDateTime() != null) {
      query.taskCreatedAfter(commonQueryParam.getObjectStartDateTime());
    }
    if (commonQueryParam.getObjectEndDateTime() != null) {
      query.taskCreatedBefore(commonQueryParam.getObjectEndDateTime());
    }
  }

  private static void buildHistoricTaskInstanceSearch(HistoricTaskInstanceQuery query, CommonQueryParam commonQueryParam) {
    if (StringUtils.isNotBlank(commonQueryParam.getProcessKey())) {
      query.processDefinitionKeyLike("%" + commonQueryParam.getProcessKey() + "%");
    }
    if (StringUtils.isNotBlank(commonQueryParam.getProcessName())) {
      query.processDefinitionNameLike("%" + commonQueryParam.getProcessName() + "%");
    }
    if (commonQueryParam.getObjectStartDateTime() != null) {
      query.taskCreatedAfter(commonQueryParam.getObjectStartDateTime());
    }
    if (commonQueryParam.getObjectEndDateTime() != null) {
      query.taskCreatedBefore(commonQueryParam.getObjectEndDateTime());
    }
  }

  /**
   * 构建历史流程实例搜索
   */
  public static void buildHistoricProcessInstanceSearch(HistoricProcessInstanceQuery query, CommonQueryParam commonQueryParam) {
    // 流程标识
    if (StringUtils.isNotBlank(commonQueryParam.getProcessKey())) {
      query.processDefinitionKey(commonQueryParam.getProcessKey());
    }
    // 流程名称
    if (StringUtils.isNotBlank(commonQueryParam.getProcessName())) {
      query.processDefinitionName(commonQueryParam.getProcessName());
    }
    // 流程名称
    if (StringUtils.isNotBlank(commonQueryParam.getCategory())) {
      query.processDefinitionCategory(commonQueryParam.getCategory());
    }
    if (commonQueryParam.getObjectStartDateTime() != null) {
      query.startedAfter(commonQueryParam.getObjectStartDateTime());
    }
    if (commonQueryParam.getObjectEndDateTime() != null) {
      query.startedBefore(commonQueryParam.getObjectEndDateTime());
    }
  }

}

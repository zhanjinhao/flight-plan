package cn.addenda.fp.workflow.flowable.command;

import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.impl.persistence.entity.HistoricActivityInstanceEntity;
import org.flowable.engine.impl.util.CommandContextUtil;

public class UpdateHistoricActivityInstanceDeleteReasonCommand implements Command<HistoricActivityInstance> {
  protected String activityInstanceId;
  protected String deleteReason;

  public UpdateHistoricActivityInstanceDeleteReasonCommand(String activityInstanceId, String deleteReason) {
    this.activityInstanceId = activityInstanceId;
    this.deleteReason = deleteReason;
  }

  @Override
  public HistoricActivityInstance execute(CommandContext commandContext) {
    HistoricActivityInstanceEntity activityInstanceEntity = CommandContextUtil.getHistoricActivityInstanceEntityManager().findById(activityInstanceId);
    if (activityInstanceEntity != null) {
      activityInstanceEntity.setDeleteReason(deleteReason);
      activityInstanceEntity.setEndTime(CommandContextUtil.getProcessEngineConfiguration().getClock().getCurrentTime());
      if (activityInstanceEntity.getEndTime() != null && activityInstanceEntity.getStartTime() != null) {
        activityInstanceEntity.setDurationInMillis(activityInstanceEntity.getEndTime().getTime() - activityInstanceEntity.getStartTime().getTime());
      }
    }
    return activityInstanceEntity;
  }
}
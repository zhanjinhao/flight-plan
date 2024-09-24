package cn.addenda.fp.workflow.flowable.listener;

import cn.addenda.fp.workflow.constant.ProcessConstants;
import cn.addenda.fp.workflow.constant.ProcessStatusEnum;
import lombok.AllArgsConstructor;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEntityEvent;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.event.AbstractFlowableEngineEventListener;

@AllArgsConstructor
public class ProcessStatusCompleteListener extends AbstractFlowableEngineEventListener {

  private RuntimeService runtimeService;

  /**
   * 流程结束监听器
   */
  @Override
  protected void processCompleted(FlowableEngineEntityEvent event) {
    String processInstanceId = event.getProcessInstanceId();
    Object variable = runtimeService.getVariable(processInstanceId, ProcessConstants.PROCESS_STATUS_KEY);
    if (variable != null) {
      ProcessStatusEnum status = ProcessStatusEnum.getProcessStatus(variable.toString());
      if (ProcessStatusEnum.RUNNING == status) {
        runtimeService.setVariable(processInstanceId, ProcessConstants.PROCESS_STATUS_KEY, ProcessStatusEnum.COMPLETED.getStatus());
      }
    }
    super.processCompleted(event);
  }

}

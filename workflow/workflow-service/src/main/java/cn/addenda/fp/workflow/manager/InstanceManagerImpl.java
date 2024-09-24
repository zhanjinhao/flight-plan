package cn.addenda.fp.workflow.manager;

import cn.addenda.component.jdk.exception.ServiceException;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.bpmn.model.Process;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.Execution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class InstanceManagerImpl implements InstanceManager {

  @Autowired
  private RepositoryService repositoryService;

  @Autowired
  private RuntimeService runtimeService;

  @Override
  public void moveExecutionToEndEvent(String processDefinitionId, String processInstanceId) {
    // 获取所有结束信息
    BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
    Process process = bpmnModel.getMainProcess();
    List<EndEvent> endEventList = process.findFlowElementsOfType(EndEvent.class, false);
    if (!CollectionUtils.isEmpty(endEventList)) {
      // 获取当前流程最后一个节点。如果有多个，随机挑一个。
      String endEventId = endEventList.get(0).getId();
      List<Execution> executionList = runtimeService.createExecutionQuery()
              .parentId(processInstanceId)
              .list();
      List<String> executionIdList = executionList.stream()
              .map(Execution::getId)
              .collect(Collectors.toList());
      // 变更流程为已结束状态
      runtimeService.createChangeActivityStateBuilder()
              .processInstanceId(processInstanceId)
              .moveExecutionsToSingleActivityId(executionIdList, endEventId)
              .changeState();
    } else {
      throw new ServiceException("无法找到结束事件，无法取消！");
    }
  }

}

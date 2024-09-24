package cn.addenda.fp.workflow.flowable.listener;

import cn.addenda.component.user.UserInfo;
import cn.addenda.fp.workflow.manager.UserManager;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.MultiInstanceLoopCharacteristics;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Setter
public class MultiInstanceCollectionAssignmentListener implements ExecutionListener {

  private UserManager userManager;

  Expression groupCode;

  @Override
  public void notify(DelegateExecution execution) {
    FlowElement element = execution.getCurrentFlowElement();
    if (!(element instanceof SequenceFlow)) {
      return;
    }
    SequenceFlow sequenceFlow = (SequenceFlow) element;
    FlowElement targetFlowElement = sequenceFlow.getTargetFlowElement();
    if (!(targetFlowElement instanceof UserTask)) {
      return;
    }
    UserTask userTask = (UserTask) targetFlowElement;
    MultiInstanceLoopCharacteristics loopCharacteristics = userTask.getLoopCharacteristics();
    if (loopCharacteristics == null) {
      return;
    }
    String collectionVariableName = loopCharacteristics.getInputDataItem();
    collectionVariableName = collectionVariableName.replace("${", "").replace("}", "");

    Object collectionVariableValue = execution.getVariableLocal(collectionVariableName);
    if (collectionVariableValue != null) {
      return;
    }

    String multiInstanceGroupCode = (String) groupCode.getValue(execution);
    List<UserInfo> userList = userManager.getUserListByGroupId(multiInstanceGroupCode);
    List<String> userCodeList = userList.stream().map(UserInfo::getUserId).collect(Collectors.toList());
    execution.setVariable(collectionVariableName, userCodeList);
  }

}

package cn.addenda.fp.workflow.flowable.listener;

import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.MultiInstanceLoopCharacteristics;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;

import java.util.List;

@Slf4j
public class SingleInstanceGroupCodeAssignmentListener implements ExecutionListener {

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
    if (loopCharacteristics != null) {
      return;
    }
    List<String> candidateGroupList = userTask.getCandidateGroups();
    if (candidateGroupList == null || candidateGroupList.size() != 1) {
      return;
    }
    String candidateGroup = candidateGroupList.get(0);
    candidateGroup = candidateGroup.replace("${", "").replace("}", "");
    Object value = groupCode.getValue(execution);
    execution.setVariable(candidateGroup, value);
  }

}

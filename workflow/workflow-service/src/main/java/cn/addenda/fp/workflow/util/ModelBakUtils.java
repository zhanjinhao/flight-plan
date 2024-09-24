package cn.addenda.fp.workflow.util;

import org.flowable.bpmn.model.*;
import org.flowable.bpmn.model.Process;

import java.util.*;

public class ModelBakUtils {

  /**
   * 获取元素表单Key（限开始节点和用户节点可用）
   *
   * @param flowElement 元素
   * @return 表单Key
   */
  public static String getFormKey(FlowElement flowElement) {
    if (flowElement != null) {
      if (flowElement instanceof StartEvent) {
        return ((StartEvent) flowElement).getFormKey();
      } else if (flowElement instanceof UserTask) {
        return ((UserTask) flowElement).getFormKey();
      }
    }
    return null;
  }

  /**
   * 获取开始节点属性值
   *
   * @param model bpmnModel对象
   * @param name  属性名
   * @return 属性值
   */
  public static String getStartEventAttributeValue(BpmnModel model, String name) {
    StartEvent startEvent = getStartEvent(model);
    return getElementAttributeValue(startEvent, name);
  }

  /**
   * 获取结束节点属性值
   *
   * @param model bpmnModel对象
   * @param name  属性名
   * @return 属性值
   */
  public static String getEndEventAttributeValue(BpmnModel model, String name) {
    EndEvent endEvent = getEndEvent(model);
    return getElementAttributeValue(endEvent, name);
  }

  /**
   * 获取用户任务节点属性值
   *
   * @param model   bpmnModel对象
   * @param taskKey 任务Key
   * @param name    属性名
   * @return 属性值
   */
  public static String getUserTaskAttributeValue(BpmnModel model, String taskKey, String name) {
    UserTask userTask = ModelUtils.getUserTask(model, taskKey);
    return getElementAttributeValue(userTask, name);
  }

  /**
   * 获取元素属性值
   *
   * @param baseElement 流程元素
   * @param name        属性名
   * @return 属性值
   */
  public static String getElementAttributeValue(BaseElement baseElement, String name) {
    if (baseElement != null) {
      List<ExtensionAttribute> attributes = baseElement.getAttributes().get(name);
      if (attributes != null && !attributes.isEmpty()) {
        attributes.iterator().next().getValue();
        Iterator<ExtensionAttribute> attrIterator = attributes.iterator();
        if (attrIterator.hasNext()) {
          ExtensionAttribute attribute = attrIterator.next();
          return attribute.getValue();
        }
      }
    }
    return null;
  }


  /**
   * 获取开始节点
   *
   * @param model bpmnModel对象
   * @return 开始节点（未找到开始节点，返回null）
   */
  public static StartEvent getStartEvent(BpmnModel model) {
    Process process = model.getMainProcess();
    FlowElement startElement = process.getInitialFlowElement();
    if (startElement instanceof StartEvent) {
      return (StartEvent) startElement;
    }
    return getStartEvent(process.getFlowElements());
  }

  /**
   * 获取开始节点
   *
   * @param flowElements 流程元素集合
   * @return 开始节点（未找到开始节点，返回null）
   */
  public static StartEvent getStartEvent(Collection<FlowElement> flowElements) {
    for (FlowElement flowElement : flowElements) {
      if (flowElement instanceof StartEvent) {
        return (StartEvent) flowElement;
      }
    }
    return null;
  }

  /**
   * 获取结束节点
   *
   * @param model bpmnModel对象
   * @return 结束节点（未找到开始节点，返回null）
   */
  public static EndEvent getEndEvent(BpmnModel model) {
    Process process = model.getMainProcess();
    return getEndEvent(process.getFlowElements());
  }


  /**
   * 获取结束节点
   *
   * @param flowElements 流程元素集合
   * @return 结束节点（未找到开始节点，返回null）
   */
  public static EndEvent getEndEvent(Collection<FlowElement> flowElements) {
    for (FlowElement flowElement : flowElements) {
      if (flowElement instanceof EndEvent) {
        return (EndEvent) flowElement;
      }
    }
    return null;
  }


  protected static boolean isInEventSubprocess(FlowElement flowElement) {
    FlowElementsContainer flowElementsContainer = flowElement.getParentContainer();
    while (flowElementsContainer != null) {
      if (flowElementsContainer instanceof EventSubProcess) {
        return true;
      }

      if (flowElementsContainer instanceof FlowElement) {
        flowElementsContainer = ((FlowElement) flowElementsContainer).getParentContainer();
      } else {
        flowElementsContainer = null;
      }
    }
    return false;
  }


  public static List<UserTask> iteratorFindChildUserTasks(FlowElement target, List<String> runTaskKeyList) {
    return iteratorFindChildUserTasks(target, runTaskKeyList, new HashSet<>(), new ArrayList<>());
  }

  /**
   * 根据正在运行的任务节点，迭代获取子级任务节点列表，向后找
   *
   * @param source                 需要回退的节点
   * @param runTaskKeyList         正在运行的任务 Key，用于校验任务节点是否是正在运行的节点
   * @param visitedSequenceFlowSet 已经经过的连线的 ID，用于判断线路是否重复
   * @param visitedUserTaskList    需要撤回的用户任务列表
   * @return
   */
  public static List<UserTask> iteratorFindChildUserTasks(FlowElement source, List<String> runTaskKeyList,
                                                          Set<String> visitedSequenceFlowSet, List<UserTask> visitedUserTaskList) {
    visitedSequenceFlowSet = visitedSequenceFlowSet == null ? new HashSet<>() : visitedSequenceFlowSet;
    visitedUserTaskList = visitedUserTaskList == null ? new ArrayList<>() : visitedUserTaskList;

    // 如果该节点为开始节点，且存在上级子节点，则顺着上级子节点继续迭代
    if (source instanceof StartEvent && source.getSubProcess() != null) {
      visitedUserTaskList = iteratorFindChildUserTasks(source.getSubProcess(), runTaskKeyList, visitedSequenceFlowSet, visitedUserTaskList);
    }

    // 根据类型，获取出口连线
    List<SequenceFlow> elementOutgoingFlowList = ModelUtils.getElementOutgoingFlowList(source);

    if (elementOutgoingFlowList != null) {
      // 循环找到目标元素
      for (SequenceFlow sequenceFlow : elementOutgoingFlowList) {
        // 如果发现连线重复，说明循环了，跳过这个循环
        if (visitedSequenceFlowSet.contains(sequenceFlow.getId())) {
          continue;
        }
        // 添加已经走过的连线
        visitedSequenceFlowSet.add(sequenceFlow.getId());
        // 如果为用户任务类型，且任务节点的 Key 正在运行的任务中存在，添加
        if (sequenceFlow.getTargetFlowElement() instanceof UserTask
                && runTaskKeyList.contains((sequenceFlow.getTargetFlowElement()).getId())) {
          visitedUserTaskList.add((UserTask) sequenceFlow.getTargetFlowElement());
          continue;
        }
        // 如果节点为子流程节点情况，则从节点中的第一个节点开始获取
        if (sequenceFlow.getTargetFlowElement() instanceof SubProcess) {
          SubProcess subProcess = (SubProcess) sequenceFlow.getTargetFlowElement();
          Collection<FlowElement> subProcessFlowElementCollection = subProcess.getFlowElements();
          FlowElement start = new ArrayList<>(subProcessFlowElementCollection).get(0);
          List<UserTask> childUserTaskList = iteratorFindChildUserTasks(start, runTaskKeyList, visitedSequenceFlowSet, null);
          // 如果找到节点，则说明该线路找到节点，不继续向下找，反之继续
          if (childUserTaskList != null && !childUserTaskList.isEmpty()) {
            visitedUserTaskList.addAll(childUserTaskList);
            continue;
          }
        }
        // 继续迭代
        visitedUserTaskList = iteratorFindChildUserTasks(sequenceFlow.getTargetFlowElement(), runTaskKeyList, visitedSequenceFlowSet, visitedUserTaskList);
      }
    }
    return visitedUserTaskList;
  }

}

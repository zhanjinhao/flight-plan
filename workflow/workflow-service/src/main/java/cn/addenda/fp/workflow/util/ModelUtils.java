package cn.addenda.fp.workflow.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;

import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ModelUtils {

  /**
   * 获得某节点之后的节点。只能用于有向无环图。
   *
   * @param bpmnModel   流程模型
   * @param startNodeId 开始节点的ID
   * @return 开始节点之后的节点
   */
  public static Set<String> getAfterNodeIdSet(BpmnModel bpmnModel, String startNodeId) {
    Process mainProcess = bpmnModel.getMainProcess();
    Collection<FlowElement> flowElementCollection = mainProcess.getFlowElements();
    Map<String, FlowElement> flowElementMap = flowElementCollection.stream().collect(Collectors.toMap(FlowElement::getId, a -> a));
    FlowElement flowElement = flowElementMap.get(startNodeId);
    Set<String> visitedActivityIdSet = new HashSet<>();
    Deque<FlowElement> stack = new ArrayDeque<>();
    stack.push(flowElement);
    while (!stack.isEmpty()) {
      FlowElement pop = stack.pop();
      visitedActivityIdSet.add(pop.getId());
      List<SequenceFlow> outgoingSequenceFlowList = getElementOutgoingFlowList(pop);
      if (!outgoingSequenceFlowList.isEmpty()) {
        for (SequenceFlow sequenceFlow : outgoingSequenceFlowList) {
          if (visitedActivityIdSet.contains(sequenceFlow.getId())) {
            continue;
          }
          visitedActivityIdSet.add(sequenceFlow.getId());
          FlowElement targetFlowElement = sequenceFlow.getTargetFlowElement();
          stack.push(targetFlowElement);
        }
      }
    }

    visitedActivityIdSet.remove(flowElement.getId());
    return visitedActivityIdSet;
  }

  /**
   * 根据节点，获取入口连线
   *
   * @param source 起始节点
   * @return 入口连线列表
   */
  public static List<SequenceFlow> getElementIncomingFlowList(FlowElement source) {
    List<SequenceFlow> sequenceFlowList = new ArrayList<>();
    if (source instanceof FlowNode) {
      sequenceFlowList = ((FlowNode) source).getIncomingFlows();
    }
    return sequenceFlowList;
  }


  /**
   * 根据节点，获取出口连线
   *
   * @param source 起始节点
   * @return 出口连线列表
   */
  public static List<SequenceFlow> getElementOutgoingFlowList(FlowElement source) {
    List<SequenceFlow> sequenceFlowList = new ArrayList<>();
    if (source instanceof FlowNode) {
      sequenceFlowList = ((FlowNode) source).getOutgoingFlows();
    }
    return sequenceFlowList;
  }

  /**
   * 获取用户任务模型信息
   *
   * @param model            bpmnModel对象
   * @param taskDefinitionId 用户任务定义ID
   * @return 元素信息
   */
  public static UserTask getUserTask(BpmnModel model, String taskDefinitionId) {
    FlowElement flowElement = getFlowElement(model, taskDefinitionId);
    if (flowElement instanceof UserTask) {
      return ((UserTask) flowElement);
    }
    throw new IllegalArgumentException(String.format("model[%s] does not contain userTask[%s]", model, taskDefinitionId));
  }

  /**
   * 获取流程元素模型信息
   *
   * @param model         bpmnModel对象
   * @param flowElementId 元素ID
   * @return 元素信息
   */
  public static FlowElement getFlowElement(BpmnModel model, String flowElementId) {
    Process process = model.getMainProcess();
    Deque<FlowElementsContainer> flowElementsContainerDeque = new ArrayDeque<>();
    flowElementsContainerDeque.push(process);
    while (!flowElementsContainerDeque.isEmpty()) {
      FlowElementsContainer pop = flowElementsContainerDeque.pop();
      Collection<FlowElement> flowElements = pop.getFlowElements();
      for (FlowElement flowElement : flowElements) {
        if (flowElement.getId().equals(flowElementId)) {
          return flowElement;
        } else if (flowElement instanceof FlowElementsContainer) {
          flowElementsContainerDeque.push((FlowElementsContainer) flowElement);
        }
      }
    }
    throw new IllegalArgumentException(String.format("BpmnModel[%s] does not contain FlowElement[%s]", model, flowElementId));
  }

  /**
   * 获取所有用户任务节点
   *
   * @param model bpmnModel对象
   * @return 用户任务节点列表
   */
  public static Collection<UserTask> getAllUserTask(BpmnModel model) {
    Process process = model.getMainProcess();
    Collection<FlowElement> flowElements = process.getFlowElements();
    return getAllUserTask(flowElements, null);
  }

  /**
   * 获取所有用户任务节点
   *
   * @param flowElements 流程元素集合
   * @param allElements  所有流程元素集合
   * @return 用户任务节点列表
   */
  private static Collection<UserTask> getAllUserTask(Collection<FlowElement> flowElements, Collection<UserTask> allElements) {
    allElements = allElements == null ? new ArrayList<>() : allElements;
    for (FlowElement flowElement : flowElements) {
      if (flowElement instanceof UserTask) {
        allElements.add((UserTask) flowElement);
      }
      if (flowElement instanceof SubProcess) {
        // 继续深入子流程，进一步获取子流程
        allElements = getAllUserTask(((SubProcess) flowElement).getFlowElements(), allElements);
      }
    }
    return allElements;
  }


  /**
   * 查询可以执行撤回操作的用户任务
   *
   * @param model 流程定义
   * @return 可以执行撤回的用户任务列表
   */
  public static List<String> findUserTaskCanBeWithdrawn(BpmnModel model) {
    Collection<UserTask> allUserTaskEvent = getAllUserTask(model);
    List<String> result = new ArrayList<>();
    for (UserTask userTask : allUserTaskEvent) {
      if (checkIfElementCanBeWithdrawn(userTask)) {
        result.add(userTask.getId());
      }
    }
    return result;
  }

  /**
   * 校验节点是否可撤回
   *
   * @param source 节点
   * @return 是否可撤回
   */
  public static boolean checkIfElementCanBeWithdrawn(FlowElement source) {
    // 非用户任务，不支持；多实例用户任务，不支持。
    if (!(source instanceof UserTask) || ((UserTask) source).getLoopCharacteristics() != null) {
      return false;
    }
    return checkIfElementCanBeWithdrawn(source, null);
  }

  private static boolean checkIfElementCanBeWithdrawn(FlowElement source, Set<String> visitedSequenceFlowSet) {
    visitedSequenceFlowSet = Optional.ofNullable(visitedSequenceFlowSet).orElse(new HashSet<>());
    // 获取出口连线
    List<SequenceFlow> outgoingSequenceFlowList = getElementOutgoingFlowList(source);
    boolean result = true;
    for (SequenceFlow sequenceFlow : outgoingSequenceFlowList) {
      // 如果发现连线重复，说明循环了，跳过这个循环
      if (visitedSequenceFlowSet.contains(sequenceFlow.getId())) {
        // 两种场景，第一种是自己指向自己，第二种是透过网关指向自己。
        return false;
      }
      // 添加已经走过的连线
      visitedSequenceFlowSet.add(sequenceFlow.getId());
      FlowElement targetFlowElement = sequenceFlow.getTargetFlowElement();
      if (targetFlowElement instanceof UserTask) {
        result = result && true;
      } else if (targetFlowElement instanceof Gateway) {
        // 若节点为网关且只有一个入口，继续递归查找下一个节点
        List<SequenceFlow> incomingFlowList = getElementIncomingFlowList(targetFlowElement);
        if (incomingFlowList.size() != 1) {
          return false;
        }
        result = result && checkIfElementCanBeWithdrawn(targetFlowElement, visitedSequenceFlowSet);
      } else {
        result = result && false;
      }
    }
    return result;
  }

  /**
   * @param source 可以撤回的节点
   * @return 需要撤回的节点
   */
  public static List<UserTask> findUserTaskToBeWithdrawn(FlowElement source) {
    // 非用户任务，不支持；多实例用户任务，不支持。
    if (!(source instanceof UserTask) || ((UserTask) source).getLoopCharacteristics() != null) {
      throw new UnsupportedOperationException(String.format("节点[%s]不支持撤回！", source.getId()));
    }
    return findUserTaskToBeWithdrawn(source, null, null);
  }

  private static List<UserTask> findUserTaskToBeWithdrawn(FlowElement source, Set<String> visitedSequenceFlowSet, List<UserTask> visitedUserTaskList) {
    visitedSequenceFlowSet = Optional.ofNullable(visitedSequenceFlowSet).orElse(new HashSet<>());
    visitedUserTaskList = Optional.ofNullable(visitedUserTaskList).orElse(new ArrayList<>());
    // 获取出口连线
    List<SequenceFlow> outgoingSequenceFlowList = getElementOutgoingFlowList(source);
    for (SequenceFlow sequenceFlow : outgoingSequenceFlowList) {
      // 如果发现连线重复，说明循环了，跳过这个循环
      if (visitedSequenceFlowSet.contains(sequenceFlow.getId())) {
        // 两种场景，第一种是自己指向自己，第二种是透过网关指向自己。
        throw new UnsupportedOperationException(String.format("节点[%s]不支持撤回！", source.getId()));
      }
      // 添加已经走过的连线
      visitedSequenceFlowSet.add(sequenceFlow.getId());
      FlowElement targetFlowElement = sequenceFlow.getTargetFlowElement();
      if (targetFlowElement instanceof UserTask) {
        visitedUserTaskList.add((UserTask) targetFlowElement);
      } else if (targetFlowElement instanceof Gateway) {
        // 若节点非用户任务，继续递归查找下一个节点
        List<SequenceFlow> incomingFlowList = getElementIncomingFlowList(targetFlowElement);
        if (incomingFlowList.size() != 1) {
          throw new UnsupportedOperationException(String.format("节点[%s]不支持撤回！", source.getId()));
        }
        findUserTaskToBeWithdrawn(targetFlowElement, visitedSequenceFlowSet, visitedUserTaskList);
      } else {
        throw new UnsupportedOperationException(String.format("节点[%s]不支持撤回！", source.getId()));
      }
    }
    return visitedUserTaskList;
  }


  /**
   * 查询可撤回节点到待撤回节点之间的元素
   *
   * @param source 可撤回节点
   */
  public static List<FlowElement> findTraversedElementToUserTaskCanBeWithdrawn(FlowElement source) {
    // 非用户任务，不支持；多实例用户任务，不支持。
    if (!(source instanceof UserTask) || ((UserTask) source).getLoopCharacteristics() != null) {
      throw new UnsupportedOperationException(String.format("节点[%s]不支持撤回！", source.getId()));
    }
    return findTraversedElementToUserTaskCanBeWithdrawn(source, null, null, null);
  }

  private static List<FlowElement> findTraversedElementToUserTaskCanBeWithdrawn(
          FlowElement source, Set<String> visitedSequenceFlowSet, Deque<FlowElement> sequenceFlowDeque, List<FlowElement> result) {
    visitedSequenceFlowSet = Optional.ofNullable(visitedSequenceFlowSet).orElse(new HashSet<>());
    sequenceFlowDeque = Optional.ofNullable(sequenceFlowDeque).orElse(new ArrayDeque<>());
    result = Optional.ofNullable(result).orElse(new ArrayList<>());
    // 获取出口连线
    List<SequenceFlow> outgoingSequenceFlowList = getElementOutgoingFlowList(source);
    for (SequenceFlow sequenceFlow : outgoingSequenceFlowList) {
      // 如果发现连线重复，说明循环了，跳过这个循环
      if (visitedSequenceFlowSet.contains(sequenceFlow.getId())) {
        // 两种场景，第一种是自己指向自己，第二种是透过网关指向自己。
        throw new UnsupportedOperationException(String.format("节点[%s]不支持撤回！", source.getId()));
      }
      sequenceFlowDeque.push(sequenceFlow);
      // 添加已经走过的连线
      visitedSequenceFlowSet.add(sequenceFlow.getId());
      FlowElement targetFlowElement = sequenceFlow.getTargetFlowElement();
      if (targetFlowElement instanceof UserTask) {
        result.addAll(sequenceFlowDeque);
      } else if (targetFlowElement instanceof Gateway) {
        sequenceFlowDeque.push(targetFlowElement);
        // 若节点非用户任务，继续递归查找下一个节点
        List<SequenceFlow> incomingFlowList = getElementIncomingFlowList(targetFlowElement);
        if (incomingFlowList.size() != 1) {
          throw new UnsupportedOperationException(String.format("节点[%s]不支持撤回！", source.getId()));
        }
        findTraversedElementToUserTaskCanBeWithdrawn(targetFlowElement, visitedSequenceFlowSet, sequenceFlowDeque, result);
        sequenceFlowDeque.pop();
      } else {
        throw new UnsupportedOperationException(String.format("节点[%s]不支持撤回！", source.getId()));
      }
      sequenceFlowDeque.pop();
    }
    return result;
  }

  /**
   * 节点之间是不是顺序可达的
   */
  public static boolean ifSequentialReachable(FlowElement source, FlowElement target, List<String> unfinishedTaskKeyList) {
    // 非用户任务，不支持；多实例用户任务，不支持。
    if (!(source instanceof UserTask) || ((UserTask) source).getLoopCharacteristics() != null) {
      return false;
    }

    return ifSequentialReachable(source, target, null, unfinishedTaskKeyList);
  }

  /**
   * 迭代从后向前扫描，判断目标节点相对于当前节点是否是串行
   *
   * @param source                   起始节点
   * @param target                   目标节点
   * @param visitedSequenceFlowIdSet 已经经过的连线的 ID，用于判断线路是否重复
   * @return 结果
   */
  private static boolean ifSequentialReachable(FlowElement source, FlowElement target,
                                               Set<String> visitedSequenceFlowIdSet, List<String> unfinishedTaskKeyList) {
    visitedSequenceFlowIdSet = visitedSequenceFlowIdSet == null ? new HashSet<>() : visitedSequenceFlowIdSet;

    // 如果不是同一个容器，返回false
    if (!source.getParentContainer().equals(target.getParentContainer())) {
      return false;
    }

    // 根据类型，获取入口连线
    List<SequenceFlow> sequenceFlows = getElementOutgoingFlowList(source);
    if (sequenceFlows != null && !sequenceFlows.isEmpty()) {
      // 循环找到目标元素
      for (SequenceFlow sequenceFlow : sequenceFlows) {
        // 如果发现连线重复，说明循环了，跳过这个循环
        if (visitedSequenceFlowIdSet.contains(sequenceFlow.getId())) {
          continue;
        }
        // 添加已经走过的连线
        visitedSequenceFlowIdSet.add(sequenceFlow.getId());
        FlowElement targetFlowElement = sequenceFlow.getTargetFlowElement();
        // 这条线路存在目标节点，这条线路完成，进入下个线路
        if (target.getId().equals(targetFlowElement.getId())) {
          continue;
        }
        if (targetFlowElement instanceof UserTask && unfinishedTaskKeyList.contains(targetFlowElement.getId())) {
          return false;
        }
        // 如果目标节点为并行网关，则不继续
        if (targetFlowElement instanceof Gateway && !(targetFlowElement instanceof ExclusiveGateway)) {
          return false;
        }
        // 否则就继续迭代
        boolean isSequential = ifSequentialReachable(targetFlowElement, target, visitedSequenceFlowIdSet, unfinishedTaskKeyList);
        if (!isSequential) {
          return false;
        }
      }
      return true;
    } else {
      return false;
    }
  }

  public static boolean ifMultiInstance(BpmnModel model, String taskKey) {
    FlowElement flowElement = ModelUtils.getFlowElement(model, taskKey);
    if (!(flowElement instanceof UserTask)) {
      return false;
    }

    return ((UserTask) flowElement).hasMultiInstanceLoopCharacteristics();
  }

}

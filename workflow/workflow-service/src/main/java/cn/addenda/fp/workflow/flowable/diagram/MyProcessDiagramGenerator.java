package cn.addenda.fp.workflow.flowable.diagram;

import org.flowable.bpmn.model.*;
import org.flowable.bpmn.model.Process;
import org.flowable.image.impl.DefaultProcessDiagramCanvas;
import org.flowable.image.impl.DefaultProcessDiagramGenerator;

import java.awt.*;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MyProcessDiagramGenerator extends DefaultProcessDiagramGenerator {

  public InputStream generateDiagram(BpmnModel bpmnModel, String imageType,
                                     List<String> highLightActivityList, List<String> highLightFlowList, List<String> runningActivityList,
                                     String activityFontName, String labelFontName, String annotationFontName,
                                     ClassLoader customClassLoader, double scaleFactor, boolean drawSequenceFlowNameWithNoLabelDI) {

    MyProcessDiagramGenerator.RUNNING_ACTIVITIES_TL.set(runningActivityList);
    try {
      return generateProcessDiagram(bpmnModel, imageType, highLightActivityList, highLightFlowList,
              activityFontName, labelFontName, annotationFontName, customClassLoader, scaleFactor, drawSequenceFlowNameWithNoLabelDI).generateImage(imageType);
    } finally {
      MyProcessDiagramGenerator.RUNNING_ACTIVITIES_TL.remove();
    }
  }


  protected DefaultProcessDiagramCanvas generateProcessDiagram(BpmnModel bpmnModel, String imageType,
                                                               List<String> highLightedActivities, List<String> highLightedFlows,
                                                               String activityFontName, String labelFontName, String annotationFontName, ClassLoader customClassLoader, double scaleFactor, boolean drawSequenceFlowNameWithNoLabelDI) {

    prepareBpmnModel(bpmnModel);

    DefaultProcessDiagramCanvas processDiagramCanvas = initProcessDiagramCanvas(bpmnModel, imageType, activityFontName, labelFontName, annotationFontName, customClassLoader);

    // Draw pool shape, if process is participant in collaboration
    for (Pool pool : bpmnModel.getPools()) {
      GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(pool.getId());
      processDiagramCanvas.drawPoolOrLane(pool.getName(), graphicInfo, scaleFactor);
    }

    // Draw lanes
    for (Process process : bpmnModel.getProcesses()) {
      for (Lane lane : process.getLanes()) {
        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(lane.getId());
        processDiagramCanvas.drawPoolOrLane(lane.getName(), graphicInfo, scaleFactor);
      }
    }

    // Draw activities and their sequence-flows
    for (Process process : bpmnModel.getProcesses()) {
      for (FlowNode flowNode : process.findFlowElementsOfType(FlowNode.class)) {
        if (!isPartOfCollapsedSubProcess(flowNode, bpmnModel)) {
          drawActivity(processDiagramCanvas, bpmnModel, flowNode, highLightedActivities, highLightedFlows, scaleFactor, drawSequenceFlowNameWithNoLabelDI);
        }
      }
    }

    // Draw artifacts
    for (Process process : bpmnModel.getProcesses()) {

      for (Artifact artifact : process.getArtifacts()) {
        drawArtifact(processDiagramCanvas, bpmnModel, artifact);
      }

      List<SubProcess> subProcesses = process.findFlowElementsOfType(SubProcess.class, true);
      if (subProcesses != null) {
        for (SubProcess subProcess : subProcesses) {

          GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(subProcess.getId());
          if (graphicInfo != null && graphicInfo.getExpanded() != null && !graphicInfo.getExpanded()) {
            continue;
          }

          if (!isPartOfCollapsedSubProcess(subProcess, bpmnModel)) {
            for (Artifact subProcessArtifact : subProcess.getArtifacts()) {
              drawArtifact(processDiagramCanvas, bpmnModel, subProcessArtifact);
            }
          }
        }
      }
    }

    return processDiagramCanvas;
  }

  protected static DefaultProcessDiagramCanvas initProcessDiagramCanvas(BpmnModel bpmnModel, String imageType,
                                                                        String activityFontName, String labelFontName, String annotationFontName, ClassLoader customClassLoader) {

    // We need to calculate maximum values to know how big the image will be in its entirety
    double minX = Double.MAX_VALUE;
    double maxX = 0;
    double minY = Double.MAX_VALUE;
    double maxY = 0;

    for (Pool pool : bpmnModel.getPools()) {
      GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(pool.getId());
      minX = graphicInfo.getX();
      maxX = graphicInfo.getX() + graphicInfo.getWidth();
      minY = graphicInfo.getY();
      maxY = graphicInfo.getY() + graphicInfo.getHeight();
    }

    List<FlowNode> flowNodes = gatherAllFlowNodes(bpmnModel);
    for (FlowNode flowNode : flowNodes) {

      GraphicInfo flowNodeGraphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());

      // width
      if (flowNodeGraphicInfo.getX() + flowNodeGraphicInfo.getWidth() > maxX) {
        maxX = flowNodeGraphicInfo.getX() + flowNodeGraphicInfo.getWidth();
      }
      if (flowNodeGraphicInfo.getX() < minX) {
        minX = flowNodeGraphicInfo.getX();
      }
      // height
      if (flowNodeGraphicInfo.getY() + flowNodeGraphicInfo.getHeight() > maxY) {
        maxY = flowNodeGraphicInfo.getY() + flowNodeGraphicInfo.getHeight();
      }
      if (flowNodeGraphicInfo.getY() < minY) {
        minY = flowNodeGraphicInfo.getY();
      }

      for (SequenceFlow sequenceFlow : flowNode.getOutgoingFlows()) {
        List<GraphicInfo> graphicInfoList = bpmnModel.getFlowLocationGraphicInfo(sequenceFlow.getId());
        if (graphicInfoList != null) {
          for (GraphicInfo graphicInfo : graphicInfoList) {
            // width
            if (graphicInfo.getX() > maxX) {
              maxX = graphicInfo.getX();
            }
            if (graphicInfo.getX() < minX) {
              minX = graphicInfo.getX();
            }
            // height
            if (graphicInfo.getY() > maxY) {
              maxY = graphicInfo.getY();
            }
            if (graphicInfo.getY() < minY) {
              minY = graphicInfo.getY();
            }
          }
        }
      }
    }

    List<Artifact> artifacts = gatherAllArtifacts(bpmnModel);
    for (Artifact artifact : artifacts) {

      GraphicInfo artifactGraphicInfo = bpmnModel.getGraphicInfo(artifact.getId());

      if (artifactGraphicInfo != null) {
        // width
        if (artifactGraphicInfo.getX() + artifactGraphicInfo.getWidth() > maxX) {
          maxX = artifactGraphicInfo.getX() + artifactGraphicInfo.getWidth();
        }
        if (artifactGraphicInfo.getX() < minX) {
          minX = artifactGraphicInfo.getX();
        }
        // height
        if (artifactGraphicInfo.getY() + artifactGraphicInfo.getHeight() > maxY) {
          maxY = artifactGraphicInfo.getY() + artifactGraphicInfo.getHeight();
        }
        if (artifactGraphicInfo.getY() < minY) {
          minY = artifactGraphicInfo.getY();
        }
      }

      List<GraphicInfo> graphicInfoList = bpmnModel.getFlowLocationGraphicInfo(artifact.getId());
      if (graphicInfoList != null) {
        for (GraphicInfo graphicInfo : graphicInfoList) {
          // width
          if (graphicInfo.getX() > maxX) {
            maxX = graphicInfo.getX();
          }
          if (graphicInfo.getX() < minX) {
            minX = graphicInfo.getX();
          }
          // height
          if (graphicInfo.getY() > maxY) {
            maxY = graphicInfo.getY();
          }
          if (graphicInfo.getY() < minY) {
            minY = graphicInfo.getY();
          }
        }
      }
    }

    int nrOfLanes = 0;
    for (Process process : bpmnModel.getProcesses()) {
      for (Lane l : process.getLanes()) {

        nrOfLanes++;

        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(l.getId());
        // // width
        if (graphicInfo.getX() + graphicInfo.getWidth() > maxX) {
          maxX = graphicInfo.getX() + graphicInfo.getWidth();
        }
        if (graphicInfo.getX() < minX) {
          minX = graphicInfo.getX();
        }
        // height
        if (graphicInfo.getY() + graphicInfo.getHeight() > maxY) {
          maxY = graphicInfo.getY() + graphicInfo.getHeight();
        }
        if (graphicInfo.getY() < minY) {
          minY = graphicInfo.getY();
        }
      }
    }

    // Special case, see https://activiti.atlassian.net/browse/ACT-1431
    if (flowNodes.isEmpty() && bpmnModel.getPools().isEmpty() && nrOfLanes == 0) {
      // Nothing to show
      minX = 0;
      minY = 0;
    }

    return new MyProcessDiagramCanvas((int) maxX + 10, (int) maxY + 10, (int) minX, (int) minY,
            imageType, activityFontName, labelFontName, annotationFontName, customClassLoader);
  }

  static ThreadLocal<List<String>> RUNNING_ACTIVITIES_TL = ThreadLocal.withInitial(ArrayList::new);

  protected void drawActivity(DefaultProcessDiagramCanvas processDiagramCanvas, BpmnModel bpmnModel,
                              FlowNode flowNode, List<String> highLightedActivities, List<String> highLightedFlows, double scaleFactor, Boolean drawSequenceFlowNameWithNoLabelDI) {

    ActivityDrawInstruction drawInstruction = activityDrawInstructions.get(flowNode.getClass());
    if (drawInstruction != null) {

      drawInstruction.draw(processDiagramCanvas, bpmnModel, flowNode);

      // Gather info on the multi instance marker
      boolean multiInstanceSequential = false;
      boolean multiInstanceParallel = false;
      boolean collapsed = false;
      if (flowNode instanceof Activity) {
        Activity activity = (Activity) flowNode;
        MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics = activity.getLoopCharacteristics();
        if (multiInstanceLoopCharacteristics != null) {
          multiInstanceSequential = multiInstanceLoopCharacteristics.isSequential();
          multiInstanceParallel = !multiInstanceSequential;
        }
      }

      // Gather info on the collapsed marker
      GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
      if (flowNode instanceof SubProcess) {
        collapsed = graphicInfo.getExpanded() != null && !graphicInfo.getExpanded();
      } else if (flowNode instanceof CallActivity) {
        collapsed = true;
      }

      if (scaleFactor == 1.0) {
        // Actually draw the markers
        processDiagramCanvas.drawActivityMarkers((int) graphicInfo.getX(), (int) graphicInfo.getY(), (int) graphicInfo.getWidth(), (int) graphicInfo.getHeight(),
                multiInstanceSequential, multiInstanceParallel, collapsed);
      }
      // Draw highlighted activities
      if (highLightedActivities.contains(flowNode.getId())) {
        if (RUNNING_ACTIVITIES_TL.get().contains(flowNode.getId())) {
          MyProcessDiagramCanvas.COLOR_TL.set(Color.RED);
          try {
            processDiagramCanvas.drawHighLight((int) graphicInfo.getX(), (int) graphicInfo.getY(), (int) graphicInfo.getWidth(), (int) graphicInfo.getHeight());
          } finally {
            MyProcessDiagramCanvas.COLOR_TL.remove();
          }
        } else {
          processDiagramCanvas.drawHighLight((int) graphicInfo.getX(), (int) graphicInfo.getY(), (int) graphicInfo.getWidth(), (int) graphicInfo.getHeight());
        }
      }

    } else if (flowNode instanceof Task) {
      activityDrawInstructions.get(Task.class).draw(processDiagramCanvas, bpmnModel, flowNode);

      if (highLightedActivities.contains(flowNode.getId())) {
        GraphicInfo graphicInfo = bpmnModel.getGraphicInfo(flowNode.getId());
        if (RUNNING_ACTIVITIES_TL.get().contains(flowNode.getId())) {
          MyProcessDiagramCanvas.COLOR_TL.set(Color.RED);
          try {
            processDiagramCanvas.drawHighLight((int) graphicInfo.getX(), (int) graphicInfo.getY(), (int) graphicInfo.getWidth(), (int) graphicInfo.getHeight());
          } finally {
            MyProcessDiagramCanvas.COLOR_TL.remove();
          }
        } else {
          processDiagramCanvas.drawHighLight((int) graphicInfo.getX(), (int) graphicInfo.getY(), (int) graphicInfo.getWidth(), (int) graphicInfo.getHeight());
        }

      }
    }

    // Outgoing transitions of activity
    for (SequenceFlow sequenceFlow : flowNode.getOutgoingFlows()) {
      boolean highLighted = (highLightedFlows.contains(sequenceFlow.getId()));
      String defaultFlow = null;
      if (flowNode instanceof Activity) {
        defaultFlow = ((Activity) flowNode).getDefaultFlow();
      } else if (flowNode instanceof Gateway) {
        defaultFlow = ((Gateway) flowNode).getDefaultFlow();
      }

      boolean isDefault = false;
      if (defaultFlow != null && defaultFlow.equalsIgnoreCase(sequenceFlow.getId())) {
        isDefault = true;
      }
      boolean drawConditionalIndicator = sequenceFlow.getConditionExpression() != null && sequenceFlow.getConditionExpression().trim().length() > 0 && !(flowNode instanceof Gateway);

      String sourceRef = sequenceFlow.getSourceRef();
      String targetRef = sequenceFlow.getTargetRef();
      FlowElement sourceElement = bpmnModel.getFlowElement(sourceRef);
      FlowElement targetElement = bpmnModel.getFlowElement(targetRef);
      List<GraphicInfo> graphicInfoList = bpmnModel.getFlowLocationGraphicInfo(sequenceFlow.getId());
      if (graphicInfoList != null && graphicInfoList.size() > 0) {
        graphicInfoList = connectionPerfectionizer(processDiagramCanvas, bpmnModel, sourceElement, targetElement, graphicInfoList);
        int[] xPoints = new int[graphicInfoList.size()];
        int[] yPoints = new int[graphicInfoList.size()];

        for (int i = 1; i < graphicInfoList.size(); i++) {
          GraphicInfo graphicInfo = graphicInfoList.get(i);
          GraphicInfo previousGraphicInfo = graphicInfoList.get(i - 1);

          if (i == 1) {
            xPoints[0] = (int) previousGraphicInfo.getX();
            yPoints[0] = (int) previousGraphicInfo.getY();
          }
          xPoints[i] = (int) graphicInfo.getX();
          yPoints[i] = (int) graphicInfo.getY();

        }
        processDiagramCanvas.drawSequenceflow(xPoints, yPoints, drawConditionalIndicator, isDefault, highLighted, scaleFactor);

        // Draw sequenceflow label
        GraphicInfo labelGraphicInfo = bpmnModel.getLabelGraphicInfo(sequenceFlow.getId());
        if (labelGraphicInfo != null) {
          processDiagramCanvas.drawLabel(sequenceFlow.getName(), labelGraphicInfo, false);
        } else {
          if (drawSequenceFlowNameWithNoLabelDI) {
            GraphicInfo lineCenter = getLineCenter(graphicInfoList);
            processDiagramCanvas.drawLabel(sequenceFlow.getName(), lineCenter, false);
          }

        }
      }
    }

    // Nested elements
    if (flowNode instanceof FlowElementsContainer) {
      for (FlowElement nestedFlowElement : ((FlowElementsContainer) flowNode).getFlowElements()) {
        if (nestedFlowElement instanceof FlowNode && !isPartOfCollapsedSubProcess(nestedFlowElement, bpmnModel)) {
          drawActivity(processDiagramCanvas, bpmnModel, (FlowNode) nestedFlowElement,
                  highLightedActivities, highLightedFlows, scaleFactor, drawSequenceFlowNameWithNoLabelDI);
        }
      }
    }
  }
}

package cn.addenda.fp.workflow.flowable.listener;

import cn.addenda.component.jackson.util.JacksonUtils;
import cn.addenda.component.jdk.util.BeanUtils;
import cn.addenda.component.jdk.util.collection.ArrayUtils;
import cn.addenda.fp.workflow.dto.ActivityTimeoutCallbackDto;
import cn.addenda.fp.workflow.dto.ActivityTimeoutCallbackTaskDto;
import cn.addenda.fp.workflow.dto.ProcessInstanceDetailDto;
import cn.addenda.fp.workflow.dto.TaskDetailInfoDto;
import cn.addenda.fp.workflow.service.InstanceService;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.springframework.http.*;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Setter
public class ActivityTimeoutCallbackListener implements ExecutionListener {

  private InstanceService instanceService;

  private RestTemplate restTemplate;

  Expression timeoutCallbackUrl;

  @Override
  public void notify(DelegateExecution delegateExecution) {
    String activityTimeoutCallbackUrl = (String) timeoutCallbackUrl.getValue(delegateExecution);

    List<String> taskDefinitionKeyList = taskDefinitionKeyList(delegateExecution);

    ProcessInstanceDetailDto processInstanceDetailDto = instanceService.queryDetailProcess(delegateExecution.getProcessInstanceId());

    ActivityTimeoutCallbackDto activityTimeoutCallbackDto = new ActivityTimeoutCallbackDto();
    BeanUtils.copyProperties(processInstanceDetailDto, activityTimeoutCallbackDto);

    List<TaskDetailInfoDto> taskDetailInfoDtoList = processInstanceDetailDto.getTaskDetailInfoDtoList();
    List<ActivityTimeoutCallbackTaskDto> activityTimeoutCallbackTaskDtoList = taskDetailInfoDtoList.stream()
            .map(taskDetailInfoDto -> {
              ActivityTimeoutCallbackTaskDto activityTimeoutCallbackTaskDto = new ActivityTimeoutCallbackTaskDto();
              BeanUtils.copyProperties(taskDetailInfoDto, activityTimeoutCallbackTaskDto);
              activityTimeoutCallbackTaskDto.setIfInActivity(taskDefinitionKeyList.contains(activityTimeoutCallbackTaskDto.getTaskDefinitionKey()));
              return activityTimeoutCallbackTaskDto;
            })
            .collect(Collectors.toList());
    activityTimeoutCallbackDto.setActivityTimeoutCallbackTaskDtoList(activityTimeoutCallbackTaskDtoList);

    HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<ActivityTimeoutCallbackDto> httpEntity = new HttpEntity<>(activityTimeoutCallbackDto, httpHeaders);

    ResponseEntity<Void> responseEntity = restTemplate.postForEntity(activityTimeoutCallbackUrl, httpEntity, Void.class);
    HttpStatus statusCode = responseEntity.getStatusCode();
    if (statusCode == HttpStatus.OK) {
      log.info("请求接口[{}]成功！", activityTimeoutCallbackUrl);
    } else {
      log.error("请求接口[{}]失败！参数为：{}。", activityTimeoutCallbackUrl, JacksonUtils.toStr(activityTimeoutCallbackTaskDtoList));
    }
  }

  private List<String> taskDefinitionKeyList(DelegateExecution delegateExecution) {
    // 承载执行监听器的Task
    FlowElement timeoutCallbackTask = delegateExecution.getCurrentFlowElement();
    if (!(timeoutCallbackTask instanceof FlowNode)) {
      return new ArrayList<>();
    }
    // 连接Activity和Event的线
    FlowNode flowNode = (FlowNode) timeoutCallbackTask;
    List<SequenceFlow> incomingFlowList = flowNode.getIncomingFlows();
    if (CollectionUtils.isEmpty(incomingFlowList) || incomingFlowList.size() != 1) {
      return new ArrayList<>();
    }
    SequenceFlow sequenceFlow = incomingFlowList.get(0);
    // 连接源头的Event
    FlowElement sourceFlowElement = sequenceFlow.getSourceFlowElement();
    if (!(sourceFlowElement instanceof BoundaryEvent)) {
      return new ArrayList<>();
    }
    BoundaryEvent boundaryEvent = (BoundaryEvent) sourceFlowElement;
    // 承载Event的Activity
    Activity attachedToRef = boundaryEvent.getAttachedToRef();
    if (attachedToRef instanceof SubProcess) {
      SubProcess subProcess = (SubProcess) attachedToRef;
      Collection<FlowElement> flowElementList = subProcess.getFlowElements();
      return flowElementList.stream().map(FlowElement::getId).collect(Collectors.toList());
    } else if (attachedToRef instanceof Task) {
      return ArrayUtils.asArrayList(attachedToRef.getId());
    }
    return new ArrayList<>();
  }

}

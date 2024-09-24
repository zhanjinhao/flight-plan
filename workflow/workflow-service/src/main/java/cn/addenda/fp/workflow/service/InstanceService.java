package cn.addenda.fp.workflow.service;

import cn.addenda.fp.workflow.dto.InstanceVariablesDto;
import cn.addenda.fp.workflow.dto.ProcessInstanceDetailDto;
import cn.addenda.fp.workflow.dto.TaskDetailInfoDto;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public interface InstanceService {

  String createInstance(String definitionKey, String processInstanceName, Map<String, String> variables);

  void downloadImage(String processInstanceId, HttpServletResponse response) throws IOException;

  void downloadImage(String processInstanceId, OutputStream out) throws IOException;

  void cancelInstance(String processInstanceId, String comment);

  InstanceVariablesDto queryInstanceVariables(String processInstanceId);

  ProcessInstanceDetailDto queryDetailProcess(String processInstanceId);

  void deleteMultiInstanceExecution(String processInstanceId, String taskDefinitionKey, String deleteUserCode);

  void addMultiInstanceExecution(String processInstanceId, String taskDefinitionKey, String addUserCode);

  void messageEventReceived(String processInstanceId, String messageEventAttachedActivityKey, String messageName, Map<String, String> instanceVariableMap);

  List<TaskDetailInfoDto> queryTaskCanBeWithdrawn(String processInstanceId);
}

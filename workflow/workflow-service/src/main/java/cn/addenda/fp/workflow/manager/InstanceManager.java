package cn.addenda.fp.workflow.manager;

public interface InstanceManager {

  void moveExecutionToEndEvent(String processDefinitionId, String processInstanceId);

}

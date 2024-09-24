package cn.addenda.fp.workflow.test;

import cn.addenda.component.jackson.util.JacksonUtils;
import cn.addenda.fp.workflow.FpWorkFlowApplication;
import cn.addenda.fp.workflow.util.ModelUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.engine.*;
import org.flowable.engine.repository.Deployment;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

@SpringBootTest(classes = FpWorkFlowApplication.class)
class DeployTest {

  @Autowired
  private RepositoryService repositoryService;

  private static final String WEEK_FLIGHT_PUBLISH_PROCESS_DEF_KEY = "WeekFlightPublish";
  private static final String TASK_RETURN_TEST_PROCESS_DEF_KEY = "TaskReturnTest";

  /**
   * 部署流程
   */
  @Test
  void testDeployWeekFlightPublishDefinition() {
    Deployment deploy = repositoryService
            .createDeployment()
            .addClasspathResource("flowable/" + WEEK_FLIGHT_PUBLISH_PROCESS_DEF_KEY + ".bpmn20.xml")
            .name("周计划发布")
            .key(WEEK_FLIGHT_PUBLISH_PROCESS_DEF_KEY)
            .deploy();
    System.out.println(deploy.getId());
  }

  /**
   * 部署流程
   */
  @Test
  void testDeployTaskReturnTestDefinition() {
    Deployment deploy = repositoryService
            .createDeployment()
            .addClasspathResource("flowable/" + TASK_RETURN_TEST_PROCESS_DEF_KEY + ".bpmn20.xml")
            .name("任务回退测试")
            .key(TASK_RETURN_TEST_PROCESS_DEF_KEY)
            .deploy();
    System.out.println(deploy.getId());
  }

  @Test
  void testGetAfterActivityIdSet() {
    BpmnModel bpmnModel = repositoryService.getBpmnModel("WeekFlightPublish:2:6264d7b0-6a92-11ef-93f9-166f47116fc3");
    Set<String> afterActivityIdSet = ModelUtils.getAfterNodeIdSet(bpmnModel, "WeekFlightPublish-Publish");
    System.out.println(JacksonUtils.toStr(afterActivityIdSet));
  }

  @Test
  void testApplicationStart() {

  }

}

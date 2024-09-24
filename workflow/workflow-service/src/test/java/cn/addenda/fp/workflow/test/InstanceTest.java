package cn.addenda.fp.workflow.test;

import cn.addenda.component.jackson.util.JacksonUtils;
import cn.addenda.component.user.UserContext;
import cn.addenda.component.user.UserInfo;
import cn.addenda.fp.workflow.dto.InstanceVariablesDto;
import cn.addenda.fp.workflow.dto.ProcessInstanceDetailDto;
import cn.addenda.fp.workflow.service.InstanceService;
import org.flowable.engine.IdentityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class InstanceTest {

  @Autowired
  private InstanceService instanceService;

  @Autowired
  private IdentityService identityService;

  @Test
  void testQueryInstanceVariables() {
    String processInstanceId = "e1b85c17-66a4-11ef-9d25-3af6104d9862";
    InstanceVariablesDto instanceVariablesDto = instanceService.queryInstanceVariables(processInstanceId);
    System.out.println(JacksonUtils.toStr(instanceVariablesDto));
  }

  @Test
  void testQueryDetailProcess() {
    String processInstanceId = "e1b85c17-66a4-11ef-9d25-3af6104d9862";
    ProcessInstanceDetailDto processInstanceDetailDto = instanceService.queryDetailProcess(processInstanceId);
    System.out.println(JacksonUtils.toStr(processInstanceDetailDto));
  }

  @Test
  void testDownloadImage() throws IOException {
    String processInstanceId = "e1b85c17-66a4-11ef-9d25-3af6104d9862";
    FileOutputStream fileOutputStream = new FileOutputStream("src/test/resources/" + processInstanceId + ".png");
    instanceService.downloadImage(processInstanceId, fileOutputStream);
  }

  @Test
  void testCancelInstance() {
    UserContext.runWithCustomUser(() -> {
      String processInstanceId = "e1b85c17-66a4-11ef-9d25-3af6104d9862";
      identityService.setAuthenticatedUserId("abc1");
      instanceService.cancelInstance(processInstanceId, "测试取消");
    }, new UserInfo("abc1", "abc1"));
  }

  @Test
  void testCreateInstance() {
    Map<String, String> variables = new HashMap<>();
    variables.put("SchedulingGroupCode", "Scheduling");
    variables.put("InterFlightGroupCode", "InterFlight");
    variables.put("DomFlightGroupCode", "DomFlight");
    variables.put("SchedulingLeaderGroupCode", "SchedulingLeader");
    instanceService.createInstance("WeekFlightPublish", "2024年第二周周计划", variables);
  }

}

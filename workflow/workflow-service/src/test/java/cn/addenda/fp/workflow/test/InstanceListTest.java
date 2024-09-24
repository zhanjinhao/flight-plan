package cn.addenda.fp.workflow.test;

import cn.addenda.component.jackson.util.JacksonUtils;
import cn.addenda.fp.workflow.dto.ProcessInstanceDto;
import cn.addenda.fp.workflow.dto.ProcessInstanceInvolvedDto;
import cn.addenda.fp.workflow.service.InstanceListService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class InstanceListTest {

  @Autowired
  private InstanceListService instanceListService;

  @Test
  void test1() {
    List<ProcessInstanceDto> processInstanceDtoList = instanceListService.queryInstanceStartedByUserId("abc1", true);
    System.out.println(JacksonUtils.toStr(processInstanceDtoList));
  }


  @Test
  void test2() {
    List<ProcessInstanceInvolvedDto> processInstanceDtoList = instanceListService.queryInstanceInvolvedByUserId("SchedulingUser1", true);
    System.out.println(JacksonUtils.toStr(processInstanceDtoList));
  }

}

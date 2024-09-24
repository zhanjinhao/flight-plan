package cn.addenda.fp.workflow.test;

import cn.addenda.component.jackson.util.JacksonUtils;
import cn.addenda.fp.workflow.dto.TaskInvolvedDto;
import cn.addenda.fp.workflow.dto.TaskToClaimDto;
import cn.addenda.fp.workflow.dto.TaskToDoDto;
import cn.addenda.fp.workflow.service.TaskListService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class TaskListTest {

  @Autowired
  private TaskListService taskListService;

  @Test
  void testQueryTaskToClaim() {
    List<TaskToClaimDto> taskToClaimDtoList = taskListService.queryTaskToClaim("SchedulingUser1");
    System.out.println(JacksonUtils.toStr(taskToClaimDtoList));
  }

  @Test
  void testQueryTaskToDo() {
    List<TaskToDoDto> taskToDoDtoList = taskListService.queryTaskToDo("SchedulingUser1");
    System.out.println(JacksonUtils.toStr(taskToDoDtoList));
  }

  @Test
  void testQueryTaskInvolved() {
    List<TaskInvolvedDto> taskInvolvedDtoList = taskListService.queryTaskInvolved("abc1");
    System.out.println(JacksonUtils.toStr(taskInvolvedDtoList));
  }

}

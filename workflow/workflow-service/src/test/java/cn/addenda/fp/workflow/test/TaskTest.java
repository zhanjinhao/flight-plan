package cn.addenda.fp.workflow.test;

import cn.addenda.fp.workflow.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TaskTest {

  @Autowired
  private TaskService taskService;

  @Test
  void test1() {
    String taskId = "";
    taskService.claim("");
  }

}

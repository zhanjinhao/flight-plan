package cn.addenda.fp.workflow.test;

import liquibase.pro.packaged.A;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.service.HistoricTaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class VariableTest {

  @Autowired
  private RuntimeService runtimeService;

  @Autowired
  private TaskService taskService;

  @Autowired
  private HistoryService historyService;

  @Test
  void test1() {
    // 4a4fccc2-7301-11ef-936e-62a87771143b 流程实例
    // 3892faaa-7302-11ef-936e-62a87771143b 审批子流程
    // 389321be-7302-11ef-936e-62a87771143b 边界定时器事件

    // ACT_RU_VARIABLE VAR on RES.ID_ = VAR.TASK_ID_ or RES.PROC_INST_ID_ = VAR.EXECUTION_ID_

    // 在审批子流程上设置一个全局变量，会设置到流程实例上去
    runtimeService.setVariable("3892faaa-7302-11ef-936e-62a87771143b", "runtimeService.setVariable", "1");
    // 在审批子流程上设置一个Local变量，会设置到审批子流程上去
    runtimeService.setVariableLocal("3892faaa-7302-11ef-936e-62a87771143b", "runtimeService.setVariableLocal", "1");

    // getVariable方法会沿着PARENT_ID_向上找变量
    // getVariableLocal只会找Execution本身的变量

    Object variable1 = runtimeService.getVariable("4a4fccc2-7301-11ef-936e-62a87771143b", "runtimeService.setVariableLocal");
    System.out.println(variable1);  // null

    Object variable2 = runtimeService.getVariable("3892faaa-7302-11ef-936e-62a87771143b", "runtimeService.setVariableLocal");
    System.out.println(variable2);  // 1

    Object variable3 = runtimeService.getVariable("389321be-7302-11ef-936e-62a87771143b", "runtimeService.setVariableLocal");
    System.out.println(variable3);  // 1

    Object variable4 = runtimeService.getVariableLocal("4a4fccc2-7301-11ef-936e-62a87771143b", "runtimeService.setVariableLocal");
    System.out.println(variable4);  // null

    Object variable5 = runtimeService.getVariableLocal("3892faaa-7302-11ef-936e-62a87771143b", "runtimeService.setVariableLocal");
    System.out.println(variable5);  // 1

    Object variable6 = runtimeService.getVariableLocal("389321be-7302-11ef-936e-62a87771143b", "runtimeService.setVariableLocal");
    System.out.println(variable6);  // null

    taskService.setVariable("38a5e67b-7302-11ef-936e-62a87771143b", "taskService.setVariable", "1");
    taskService.setVariableLocal("38a5e67b-7302-11ef-936e-62a87771143b", "taskService.setVariableLocal", "1");

  }

}

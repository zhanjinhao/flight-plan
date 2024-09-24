package cn.addenda.fp.workflow.controller;

import cn.addenda.component.jdk.result.Result;
import cn.addenda.fp.workflow.dto.TaskDelegatedDto;
import cn.addenda.fp.workflow.dto.TaskInvolvedDto;
import cn.addenda.fp.workflow.dto.TaskToClaimDto;
import cn.addenda.fp.workflow.dto.TaskToDoDto;
import cn.addenda.fp.workflow.service.TaskListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author addenda
 * @since 2022/2/7 16:43
 */
@RestController
@RequestMapping("/taskList")
public class TaskListController {

  @Autowired
  private TaskListService taskListService;

  @GetMapping("/queryTaskCanBeClaimed")
  public Result<List<TaskToClaimDto>> queryTaskCanBeClaimed(@RequestParam("userId") String userId) {
    return Result.success(taskListService.queryTaskToClaim(userId));
  }

  @GetMapping("/queryTaskToDo")
  public Result<List<TaskToDoDto>> queryTaskToDo(@RequestParam("userId") String userId) {
    return Result.success(taskListService.queryTaskToDo(userId));
  }

  @GetMapping("/queryTaskDelegated")
  public Result<List<TaskDelegatedDto>> queryTaskDelegated(@RequestParam("userId") String userId) {
    return Result.success(taskListService.queryTaskDelegated(userId));
  }

  @GetMapping("/queryTaskInvolved")
  public Result<List<TaskInvolvedDto>> queryTaskInvolved(@RequestParam("userId") String userId) {
    return Result.success(taskListService.queryTaskInvolved(userId));
  }

}
package cn.addenda.fp.workflow.controller;

import cn.addenda.component.jdk.result.Result;
import cn.addenda.fp.workflow.dto.CompleteVariableDto;
import cn.addenda.fp.workflow.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author addenda
 * @since 2022/2/7 16:43
 */
@RestController
@RequestMapping("/task")
public class TaskController {

  @Autowired
  private TaskService taskService;

  @PostMapping("/claim")
  public Result<Void> claim(@RequestParam("taskId") String taskId) {
    taskService.claim(taskId);
    return Result.success();
  }

  @PostMapping("/unClaim")
  public Result<Void> unClaim(@RequestParam("taskId") String taskId) {
    taskService.unClaim(taskId);
    return Result.success();
  }

  @PostMapping("/withdraw")
  public Result<Void> withdraw(@RequestParam("taskId") String taskId, @RequestParam("comment") String comment) {
    taskService.withdraw(taskId, comment);
    return Result.success();
  }

  @PostMapping("/complete")
  public Result<Void> complete(
          @RequestParam("taskId") String taskId, @RequestParam("comment") String comment,
          @RequestBody CompleteVariableDto completeVariableDto) {
    taskService.complete(taskId, comment, completeVariableDto);
    return Result.success();
  }

  @PostMapping("/transfer")
  public Result<Void> transfer(
          @RequestParam("taskId") String taskId, @RequestParam("transfer") String transfer,
          @RequestParam("comment") String comment) {
    taskService.transfer(taskId, transfer, comment);
    return Result.success();
  }

  @PostMapping("/reject")
  public Result<Void> reject(
          @RequestParam("taskId") String taskId, @RequestParam("delegate") String delegate,
          @RequestParam("comment") String comment) {
    taskService.reject(taskId, comment);
    return Result.success();
  }

  @PostMapping("/delegate")
  public Result<Void> delegate(
          @RequestParam("taskId") String taskId, @RequestParam("delegate") String delegate,
          @RequestParam("comment") String comment) {
    taskService.delegate(taskId, delegate, comment);
    return Result.success();
  }

  @PostMapping("/resolve")
  public Result<Void> resolve(@RequestParam("taskId") String taskId, @RequestParam("comment") String comment) {
    taskService.resolve(taskId, comment);
    return Result.success();
  }

  @PostMapping("/cancelDelegate")
  public Result<Void> cancelDelegate(@RequestParam("taskId") String taskId,
                                     @RequestParam("comment") String comment) {
    taskService.cancelDelegate(taskId, comment);
    return Result.success();
  }

  @GetMapping("/findTaskKeyCanReturn")
  public Result<List<String>> findTaskKeyCanReturn(@RequestParam("taskId") String taskId) {
    return Result.success(taskService.findTaskKeyCanReturn(taskId));
  }

  @PostMapping("/taskReturn")
  public Result<List<String>> taskReturn(
          @RequestParam("taskId") String taskId,
          @RequestParam("targetTaskKey") String targetTaskKey,
          @RequestParam("comment") String comment) {
    taskService.taskReturn(taskId, targetTaskKey, comment);
    return Result.success();
  }

}
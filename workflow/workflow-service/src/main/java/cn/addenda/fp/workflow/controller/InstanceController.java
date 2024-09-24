package cn.addenda.fp.workflow.controller;

import cn.addenda.component.jdk.exception.ServiceException;
import cn.addenda.component.jdk.result.Result;
import cn.addenda.fp.workflow.dto.InstanceVariablesDto;
import cn.addenda.fp.workflow.dto.ProcessInstanceDetailDto;
import cn.addenda.fp.workflow.dto.TaskDetailInfoDto;
import cn.addenda.fp.workflow.service.InstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author addenda
 * @since 2022/2/7 16:43
 */
@RestController
@RequestMapping("/instance")
public class InstanceController {

  @Autowired
  private InstanceService instanceService;

  @PostMapping("/createInstance")
  public Result<String> createInstance(
          @RequestParam("definitionKey") String definitionKey, @RequestParam("processInstanceName") String processInstanceName,
          @RequestBody Map<String, String> variables) {
    return Result.success(instanceService.createInstance(definitionKey, processInstanceName, variables));
  }

  @GetMapping("/download")
  public void downloadImage(@RequestParam("processInstanceId") String processInstanceId, HttpServletResponse response) {
    try {
      instanceService.downloadImage(processInstanceId, response);
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new ServiceException("下载图片失败！", e);
    }
  }

  @PostMapping("/cancelInstance")
  public Result<Void> cancelInstance(@RequestParam("processInstanceId") String processInstanceId,
                                     @RequestParam("comment") String comment) {
    instanceService.cancelInstance(processInstanceId, comment);
    return Result.success();
  }

  @GetMapping("/queryInstanceVariables")
  public Result<InstanceVariablesDto> queryInstanceVariables(@RequestParam("processInstanceId") String processInstanceId) {
    return Result.success(instanceService.queryInstanceVariables(processInstanceId));
  }

  @GetMapping("/queryDetailProcess")
  public Result<ProcessInstanceDetailDto> queryDetailProcess(@RequestParam("processInstanceId") String processInstanceId) {
    return Result.success(instanceService.queryDetailProcess(processInstanceId));
  }

  @PostMapping("/deleteMultiInstanceExecution")
  public Result<Void> deleteMultiInstanceExecution(
          @RequestParam("processInstanceId") String processInstanceId,
          @RequestParam("taskDefinitionKey") String taskDefinitionKey,
          @RequestParam("deleteUserCode") String deleteUserCode) {
    instanceService.deleteMultiInstanceExecution(processInstanceId, taskDefinitionKey, deleteUserCode);
    return Result.success();
  }

  @PostMapping("/addMultiInstanceExecution")
  public Result<Void> addMultiInstanceExecution(
          @RequestParam("processInstanceId") String processInstanceId,
          @RequestParam("taskDefinitionKey") String taskDefinitionKey,
          @RequestParam("addUserCode") String addUserCode) {
    instanceService.addMultiInstanceExecution(processInstanceId, taskDefinitionKey, addUserCode);
    return Result.success();
  }

  @PostMapping("/messageEventReceived")
  public Result<Void> messageEventReceived(
          @RequestParam("processInstanceId") String processInstanceId,
          @RequestParam("messageEventAttachedActivityKey") String messageEventAttachedActivityKey,
          @RequestParam("messageName") String messageName,
          @RequestBody Map<String, String> instanceVariableMap) {
    instanceService.messageEventReceived(processInstanceId, messageEventAttachedActivityKey, messageName, instanceVariableMap);
    return Result.success();
  }

  @GetMapping("/queryTaskCanBeWithdrawn")
  public Result<List<TaskDetailInfoDto>> queryTaskCanBeWithdrawn(@RequestParam("processInstanceId") String processInstanceId) {
    return Result.success(instanceService.queryTaskCanBeWithdrawn(processInstanceId));
  }

}
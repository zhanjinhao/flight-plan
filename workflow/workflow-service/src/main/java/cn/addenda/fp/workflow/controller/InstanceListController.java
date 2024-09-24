package cn.addenda.fp.workflow.controller;

import cn.addenda.component.jdk.result.Result;
import cn.addenda.fp.workflow.dto.ProcessInstanceDto;
import cn.addenda.fp.workflow.dto.ProcessInstanceInvolvedDto;
import cn.addenda.fp.workflow.service.InstanceListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author addenda
 * @since 2022/2/7 16:43
 */
@RestController
@RequestMapping("/instanceList")
public class InstanceListController {

  @Autowired
  private InstanceListService instanceListService;

  @GetMapping("/queryInstanceStartedByUserId")
  public Result<List<ProcessInstanceDto>> queryInstanceStartedByUserId(
          @RequestParam("userId") String userId, @RequestParam("includeFinished") boolean includeFinished) {
    return Result.success(instanceListService.queryInstanceStartedByUserId(userId, includeFinished));
  }

  @GetMapping("/queryInstanceInvolvedByUserId")
  public Result<List<ProcessInstanceInvolvedDto>> queryInstanceInvolvedByUserId(
          @RequestParam("userId") String userId, @RequestParam("includeFinished") boolean includeFinished) {
    return Result.success(instanceListService.queryInstanceInvolvedByUserId(userId, includeFinished));
  }

}

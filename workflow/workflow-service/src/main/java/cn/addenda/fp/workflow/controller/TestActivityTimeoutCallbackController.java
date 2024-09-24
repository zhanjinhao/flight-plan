package cn.addenda.fp.workflow.controller;

import cn.addenda.component.jackson.util.JacksonUtils;
import cn.addenda.component.jdk.result.Result;
import cn.addenda.fp.workflow.dto.ActivityTimeoutCallbackDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/testActivityTimeoutCallbackController")
public class TestActivityTimeoutCallbackController {

  @PostMapping("/test")
  public Result<Void> test(@RequestBody ActivityTimeoutCallbackDto activityTimeoutCallbackDto) {
    log.info("{}", JacksonUtils.toStr(activityTimeoutCallbackDto));
    return Result.success();
  }

}

package cn.addenda.fp.workflow.service;

import cn.addenda.fp.workflow.dto.*;

import java.util.List;

public interface TaskListService {

  List<TaskToClaimDto> queryTaskToClaim(String userId);

  List<TaskToDoDto> queryTaskToDo(String userId);

  List<TaskDelegatedDto> queryTaskDelegated(String userId);

  List<TaskInvolvedDto> queryTaskInvolved(String userId);

}

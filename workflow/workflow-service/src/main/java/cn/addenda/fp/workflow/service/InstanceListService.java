package cn.addenda.fp.workflow.service;

import cn.addenda.fp.workflow.dto.ProcessInstanceDto;
import cn.addenda.fp.workflow.dto.ProcessInstanceInvolvedDto;

import java.util.List;

public interface InstanceListService {

  List<ProcessInstanceDto> queryInstanceStartedByUserId(String userId, boolean includeFinished);

  List<ProcessInstanceInvolvedDto> queryInstanceInvolvedByUserId(String userId, boolean includeFinished);

}

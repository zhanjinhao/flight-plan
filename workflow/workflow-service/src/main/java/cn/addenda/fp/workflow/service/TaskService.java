package cn.addenda.fp.workflow.service;

import cn.addenda.fp.workflow.dto.CompleteVariableDto;

import java.util.List;

public interface TaskService {

  void claim(String taskId);

  void unClaim(String taskId);

  void withdraw(String taskId, String comment);

  void complete(String taskId, String comment, CompleteVariableDto completeVariableDto);

  void transfer(String taskId, String transfer, String comment);

  void reject(String taskId, String comment);

  void delegate(String taskId, String delegate, String comment);

  void resolve(String taskId, String comment);

  void cancelDelegate(String taskId, String comment);

  List<String> findTaskKeyCanReturn(String taskId);

  void taskReturn(String taskId, String targetActivityId, String comment);
}

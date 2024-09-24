package cn.addenda.fp.workflow.manager;

import cn.addenda.component.user.UserInfo;

import java.util.List;

public interface UserManager {

  String getUserNameByUserId(String userId);

  UserInfo getUser(String userId);

  List<UserInfo> getUserListByGroupId(String groupId);
}

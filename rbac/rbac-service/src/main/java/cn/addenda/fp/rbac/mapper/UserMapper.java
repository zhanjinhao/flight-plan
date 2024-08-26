package cn.addenda.fp.rbac.mapper;

import cn.addenda.fp.rbac.pojo.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author addenda
 * @since 2022/2/7 15:55
 */
public interface UserMapper {

  Integer insert(User user);

  Integer userCodeExists(@Param("userCode") String userCode);

  Integer userEmailExists(@Param("userEmail") String userEmail);

  Integer idExists(@Param("id") Long id);

  void deleteById(@Param("id") Long id);

  User queryByUserCode(@Param("userCode") String userCode);

  List<User> queryByUserCodeList(@Param("userCodeList") List<String> userCodeList);

  List<User> queryByNonNullFields(User user);

  void updateNonNullFieldsById(User user);

}

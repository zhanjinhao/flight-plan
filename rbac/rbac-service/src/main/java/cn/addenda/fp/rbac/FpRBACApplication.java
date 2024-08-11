package cn.addenda.fp.rbac;

import cn.addenda.component.argreslog.EnableArgResLog;
import cn.addenda.component.lockhelper.EnableLockManagement;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.Ordered;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author addenda
 * @since 2022/1/14 15:44
 */
@EnableLockManagement(namespace = "rbac-service", order = Ordered.LOWEST_PRECEDENCE - 60)
@EnableTransactionManagement(order = Ordered.LOWEST_PRECEDENCE - 70)
@EnableArgResLog(order = Ordered.LOWEST_PRECEDENCE - 90)
@MapperScan("cn.addenda.fp.rbac.mapper")
@SpringBootApplication
public class FpRBACApplication {

  public static void main(String[] args) {
    SpringApplication.run(FpRBACApplication.class, args);
  }

}

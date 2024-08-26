package cn.addenda.fp.workflow;

import cn.addenda.component.argreslog.EnableArgResLog;
import cn.addenda.component.lockhelper.EnableLockManagement;
import org.flowable.spring.boot.FlowableSecurityAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.core.Ordered;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author addenda
 * @since 2022/1/14 15:44
 */
@EnableLockManagement(namespace = "workflow-service", order = Ordered.LOWEST_PRECEDENCE - 60)
@EnableTransactionManagement(order = Ordered.LOWEST_PRECEDENCE - 70)
@EnableArgResLog(order = Ordered.LOWEST_PRECEDENCE - 90)
@EnableFeignClients(basePackages = "cn.addenda.fp.rbac.rpc")
@MapperScan("cn.addenda.fp.workflow.mapper")
@SpringBootApplication(exclude = {FlowableSecurityAutoConfiguration.class})
public class FpWorkFlowApplication {

  public static void main(String[] args) {
    SpringApplication.run(FpWorkFlowApplication.class, args);
  }

}

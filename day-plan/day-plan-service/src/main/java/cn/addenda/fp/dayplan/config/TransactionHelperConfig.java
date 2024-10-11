package cn.addenda.fp.dayplan.config;

import cn.addenda.component.transaction.TransactionHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author addenda
 * @since 2023/8/18 15:22
 */
@Configuration
public class TransactionHelperConfig {

  @Bean
  public TransactionHelper transactionHelper() {
    return new TransactionHelper();
  }

}

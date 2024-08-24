package cn.addenda.fp.workflow.config;

import cn.addenda.component.tomcat.NonFoundTomcatWebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author addenda
 * @since 2023/8/19 13:01
 */
@Configuration
public class TomcatCommonAutoConfiguration {

  @Bean
  public NonFoundTomcatWebServerFactoryCustomizer nonFoundTomcatWebServerFactoryCustomizer() {
    return new NonFoundTomcatWebServerFactoryCustomizer();
  }

}

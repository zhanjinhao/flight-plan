package cn.addenda.fp.dayplan.config;

import cn.addenda.component.user.UserTransmitFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 用户配置自动装配
 */
@Configuration
public class UserContextConfig {

  /**
   * 用户信息传递过滤器
   */
  @Bean
  public FilterRegistrationBean<UserTransmitFilter> globalUserTransmitFilter() {
    FilterRegistrationBean<UserTransmitFilter> registration = new FilterRegistrationBean<>();
    registration.setFilter(new UserTransmitFilter());
    registration.addUrlPatterns("/*");
    registration.setOrder(100);
    return registration;
  }

}

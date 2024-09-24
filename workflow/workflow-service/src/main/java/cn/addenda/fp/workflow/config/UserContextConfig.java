package cn.addenda.fp.workflow.config;

import cn.addenda.component.user.UserTransmitFilter;
import cn.addenda.fp.workflow.intercepter.AuthenticatedUserIdFilter;
import org.flowable.engine.IdentityService;
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
  public FilterRegistrationBean<UserTransmitFilter> userTransmitFilter() {
    FilterRegistrationBean<UserTransmitFilter> registration = new FilterRegistrationBean<>();
    registration.setFilter(new UserTransmitFilter());
    registration.addUrlPatterns("/*");
    registration.setOrder(100);
    return registration;
  }

  /**
   * 用户信息传递过滤器
   */
  @Bean
  public FilterRegistrationBean<AuthenticatedUserIdFilter> authenticatedUserIdFilter(IdentityService identityService) {
    FilterRegistrationBean<AuthenticatedUserIdFilter> registration = new FilterRegistrationBean<>();
    registration.setFilter(new AuthenticatedUserIdFilter(identityService));
    registration.addUrlPatterns("/*");
    registration.setOrder(1000);
    return registration;
  }

}

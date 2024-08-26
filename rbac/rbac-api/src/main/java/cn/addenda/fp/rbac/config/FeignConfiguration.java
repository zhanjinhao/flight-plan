package cn.addenda.fp.rbac.config;

import cn.addenda.component.user.UserConstant;
import cn.addenda.component.user.UserContext;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfiguration {

  @Bean
  public RequestInterceptor requestInterceptor() {
    return template -> {
      template.header(UserConstant.USER_NAME_KEY, UserContext.getUsername());
      template.header(UserConstant.USER_ID_KEY, UserContext.getUserId());
    };
  }

}

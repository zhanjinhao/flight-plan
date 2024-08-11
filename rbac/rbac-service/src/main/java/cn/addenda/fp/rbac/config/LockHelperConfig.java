package cn.addenda.fp.rbac.config;

import cn.addenda.component.lockhelper.LockConfigurer;
import cn.addenda.component.lockhelper.LockHelper;
import cn.addenda.component.redis.allocator.RedissonLockAllocator;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author addenda
 * @since 2022/11/30 19:43
 */
@Configuration
public class LockHelperConfig implements EnvironmentAware {

  private Environment environment;

  @Bean
  public LockConfigurer lockConfigurer(RedissonLockAllocator redissonLockAllocator) {
    return new LockConfigurer(redissonLockAllocator);
  }

  @Bean
  public LockHelper lockHelper(RedissonLockAllocator redissonLockHelper) {
    return new LockHelper(environment.resolvePlaceholders("${spring.application.name}"), redissonLockHelper);
  }

  @Override
  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }

}

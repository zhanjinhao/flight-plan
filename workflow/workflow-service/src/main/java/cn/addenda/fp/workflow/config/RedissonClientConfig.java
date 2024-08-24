package cn.addenda.fp.workflow.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class RedissonClientConfig implements EnvironmentAware {

  private Environment environment;

  @Bean
  public RedissonClient redissonClient() {
    // 配置
    Config config = new Config();
    config.useSingleServer()
            .setAddress("redis://"
                    + environment.resolvePlaceholders("${redis.host}")
                    + ":"
                    + environment.resolvePlaceholders("${redis.port}"))
            .setPassword(environment.resolvePlaceholders("${redis.password}"))
            .setConnectionMinimumIdleSize(2)
            .setConnectionPoolSize(4)
            .setSubscriptionConnectionMinimumIdleSize(1)
            .setSubscriptionConnectionPoolSize(2);
    // 创建RedissonClient对象
    return Redisson.create(config);
  }

  @Override
  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }

}

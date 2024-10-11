package cn.addenda.fp.dayplan.config;

import cn.addenda.component.jdk.allocator.lock.ReentrantLockAllocator;
import cn.addenda.component.redis.allocator.RedissonLockAllocator;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LockAllocatorConfig {

  @Bean
  public ReentrantLockAllocator reentrantLockAllocator() {
    return new ReentrantLockAllocator();
  }

  @Bean
  public RedissonLockAllocator redissonLockAllocator(RedissonClient redissonClient) {
    return new RedissonLockAllocator(redissonClient);
  }

}

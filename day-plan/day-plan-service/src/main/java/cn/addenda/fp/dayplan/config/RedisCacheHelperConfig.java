package cn.addenda.fp.dayplan.config;

import cn.addenda.component.cachehelper.RedisCacheHelper;
import cn.addenda.component.jdk.allocator.lock.LockAllocator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author addenda
 * @since 2022/10/27 19:32
 */
@Configuration
public class RedisCacheHelperConfig {

  @Bean
  public RedisCacheHelper redisCacheHelper(StringRedisTemplate stringRedisTemplate,
                                           @Qualifier("reentrantLockAllocator") LockAllocator<ReentrantLock> lockAllocator) {
    RedisCacheHelper redisCacheHelper = new RedisCacheHelper(stringRedisTemplate, 1000, lockAllocator);
    redisCacheHelper.setRdfBusyLoop(5);
    redisCacheHelper.setLockWaitTime(100);
    return redisCacheHelper;
  }

}

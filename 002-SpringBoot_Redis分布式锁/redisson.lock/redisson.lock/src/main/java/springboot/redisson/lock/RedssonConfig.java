package springboot.redisson.lock;

import java.io.IOException;

import org.redisson.Redisson;
import org.redisson.api.RAtomicLongRx;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonRxClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import io.reactivex.Single;
import lombok.extern.slf4j.Slf4j;

/**
 * Redission配置类
 */
@Slf4j
@Configuration
public class RedssonConfig {
    @Bean(destroyMethod="shutdown")
    public RedissonClient redisson() throws IOException {
    	RedissonClient redisson = Redisson.create(Config.fromYAML(new ClassPathResource("redisson-single.yml").getInputStream()));
        return redisson;
    }
    
    public void test()  throws IOException  {
    	RedissonRxClient client = Redisson.createRx(Config.fromYAML(new ClassPathResource("redisson-single.yml").getInputStream()));
    	RAtomicLongRx atomicLong = client.getAtomicLong("myLong");
    	Single<Boolean> cs = atomicLong.compareAndSet(10, 91);
    	Single<Long> get = atomicLong.get();
    }
    
    
    public void test1()  throws IOException  {
    	RedissonClient client = Redisson.create(Config.fromYAML(new ClassPathResource("redisson-single.yml").getInputStream()));
    	RLock lock = client.getLock("anyLock");
    	// 最常见的使用方法
    	lock.lock();
    }
}
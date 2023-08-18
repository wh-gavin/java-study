package springboot.redis.lock.redis;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisCacheService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    
    public void cache() {
    	String a = "0	0.002.cate.%.102010000	102010000,1020100,10201";
    	int step = 100000;
    	for(int i = 0; i < step; i++) {
    		String uuid = "XZW:CACHE:" + UUID.randomUUID().toString();
    		stringRedisTemplate.opsForValue().set(uuid, a);
    	}
    }
}

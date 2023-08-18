package springboot.redis.lock.web;

import java.util.Date;
import java.util.Random;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import springboot.redis.lock.redis.RedisCacheService;
import springboot.redis.lock.redis.RedisLockUtils;

@RestController
public class IndexController {
    @Resource
    private RedisTemplate redisTemplate;
    
    @Resource
    private RedisCacheService cacheService;
    
    @RequestMapping("/cache")
    public String cache() {
    	long start = System.currentTimeMillis();
    	Date starts = new Date();
    	System.out.println(starts);
    	cacheService.cache();
    	long end = System.currentTimeMillis();
    	Date ends = new Date();
    	
    	System.out.println(starts);
    	System.out.println(ends);
    	System.out.println((end - start) / 1000);
    	
    	return "succ";
    }
    @Autowired
    private RedisLockUtils redisLock;

    @RequestMapping("/deduct-stock")
    public String deductStock() {
        String productId = "product001";
        System.out.println("---------------->>>开始扣减库存");
        String key = productId;
        String requestId = productId + Thread.currentThread().getId();
        try {
            boolean locked = redisLock.lock(key, requestId, 10);
            if (!locked) {
                return "error";
            }

            //执行业务逻辑
            //System.out.println("---------------->>>执行业务逻辑:"+appTitle);
            int stock = Integer.parseInt(redisTemplate.opsForValue().get("product001-stock").toString());
            int currentStock = stock-1;
            redisTemplate.opsForValue().set("product001-stock",currentStock);
            try {
                Random random = new Random();
                Thread.sleep(random.nextInt(3) *1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("---------------->>>扣减库存结束:current stock:" + currentStock);
            return "success,current stock:" + currentStock;
        } finally {
            redisLock.unlock(key, requestId);
        }

    }
}

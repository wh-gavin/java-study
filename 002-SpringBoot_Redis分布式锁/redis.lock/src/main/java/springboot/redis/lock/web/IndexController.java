package springboot.redis.lock.web;

import java.util.Random;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import springboot.redis.lock.redis.RedisLockUtils;

@RestController
public class IndexController {
    @Resource
    private RedisTemplate redisTemplate;
    
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

package springboot.redis.lock;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = App.class)
public class RedisTest {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void test() throws Exception {
        // 设置值，默认不过期
        stringRedisTemplate.opsForValue().set("userName", "张三");

        // 获取值
        String value = stringRedisTemplate.opsForValue().get("userName");
        System.out.println("获取userName对应的值：" +  value);

        // 设置值并且设置2秒过期时间，过期之后自动删除
        stringRedisTemplate.opsForValue().set("email", "123@123.com", 2, TimeUnit.SECONDS);
        Thread.sleep(1000);
        System.out.println("获取email过期时间（单位秒）：" + stringRedisTemplate.getExpire("email"));
        System.out.println("获取email对应的值：" +  stringRedisTemplate.opsForValue().get("email"));
        Thread.sleep(1000);
        System.out.println("获取email对应的值：" +  stringRedisTemplate.opsForValue().get("email"));

        // 删除key
        Boolean result = stringRedisTemplate.delete("userName");
        System.out.println("删除userName结果：" +  result);
    }
}

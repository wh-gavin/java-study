package springboot.redis.lock.redis;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RedisServiceImpl implements RedisService {

    //设置默认过期时间
    private final static int DEFAULT_LOCK_EXPIRY_TIME = 20;
    //自定义lock key前缀
    private final static String LOCK_PREFIX = "LOCK:REDIS";

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public <T> T callWithLock(String lockKey, Callable<T> callable) throws Exception{
        //自定义lock key
        //String lockKey = getLockKey(customerBalance.getCustomerNumber(), customerBalance.getSubAccountNumber(), customerBalance.getCurrencyCode());
        //将UUID当做value，确保唯一性
        String lockReference = UUID.randomUUID().toString();

        try {
            if (!lock(lockKey, lockReference, DEFAULT_LOCK_EXPIRY_TIME, TimeUnit.SECONDS)) {
                throw new Exception("lock加锁失败");
            }
            return callable.call();
        } finally {
            unlock(lockKey, lockReference);
        }
    }

    //定义lock key
    String getLockKey(String customerNumber, String subAccountNumber, String currencyCode) {
        return String.format("%s:%s:%s:%s", LOCK_PREFIX, customerNumber, subAccountNumber, currencyCode);
    }

    //redis加锁
    private boolean lock(String key, String value, long timeout, TimeUnit timeUnit) {
        Boolean locked;
        try {
            //SET_IF_ABSENT --> NX: Only set the key if it does not already exist.
            //SET_IF_PRESENT --> XX: Only set the key if it already exist.
            locked = (Boolean) redisTemplate.execute((RedisCallback<Boolean>) connection ->
                    connection.set(key.getBytes(StandardCharsets.UTF_8), value.getBytes(StandardCharsets.UTF_8),
                            Expiration.from(timeout, timeUnit), RedisStringCommands.SetOption.SET_IF_ABSENT));
        } catch (Exception e) {
            log.error("Lock failed for redis key: {}, value: {}", key, value);
            locked = false;
        }
        return locked != null && locked;
    }

    //redis解锁
    private boolean unlock(String key, String value) {
        try {
            //使用lua脚本保证删除的原子性，确保解锁
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] " +
                            "then return redis.call('del', KEYS[1]) " +
                            "else return 0 end";
            Boolean unlockState = (Boolean) redisTemplate.execute((RedisCallback<Boolean>) connection ->
                    connection.eval(script.getBytes(), ReturnType.BOOLEAN, 1,
                            key.getBytes(StandardCharsets.UTF_8), value.getBytes(StandardCharsets.UTF_8)));
            return unlockState == null || !unlockState;
        } catch (Exception e) {
            log.error("unLock failed for redis key: {}, value: {}", key, value);
            return false;
        }
    }
}
package springboot.redis.lock.redis;

import java.util.concurrent.Callable;

public interface RedisService {
	   public <T> T callWithLock(String key, Callable<T> callable) throws Exception;
}

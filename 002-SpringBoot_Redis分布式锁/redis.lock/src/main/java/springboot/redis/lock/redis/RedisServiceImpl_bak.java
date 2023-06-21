package springboot.redis.lock.redis;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DigestUtils;
import org.springframework.data.redis.core.script.RedisScript;

public class RedisServiceImpl_bak {
	@Autowired
	private RedisTemplate<String, ?> redisTemplate;

	private static final String LOCK_PREFIX = "lock:";

	public boolean lock(String lockKey, String lockValue, long expireTime) {
	    // 使用Lua脚本执行加锁操作
	    String script = "if redis.call('setnx', KEYS[1], ARGV[1]) == 1 then " +
	                    "redis.call('pexpire', KEYS[1], ARGV[2]); return true; " +
	                    "else return false; end;";
	    RedisScript<Boolean> redisScript = RedisScript.of(script, Boolean.class);
	    Boolean result = (Boolean)redisTemplate.execute(redisScript, Collections.singletonList(lockKey), lockValue, expireTime);
	    return result != null && result;
	}

	public boolean unlock(String lockKey, String lockValue) {
		// 使用Lua脚本执行释放锁操作
		String script = "if redis.call('get', KEYS[1]) == ARGV[1] then "
				+ "return redis.call('del', KEYS[1]); else return 0; end;";
		RedisScript<Long> redisScript = RedisScript.of(script, Long.class);
		Long result = (Long)redisTemplate.execute(redisScript, Collections.singletonList(lockKey), lockValue);
		return result != null && result > 0;
	}

	private static final String LUA_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then"
			+ "   redis.call('set', KEYS[1], ARGV[2])" + "   redis.call('expire', KEYS[1], ARGV[3])"
			+ "   return 'true'" + "else" + "   return 'false'" + "end";

	public boolean tryLock(String key, String value, long expireTime) {
		String lockKey = LOCK_PREFIX + key;
		long currentTime = System.currentTimeMillis();
		String lockExpireTime = String.valueOf(currentTime + expireTime);
		String result = (String)redisTemplate.execute(new RedisScript<String>() {
			@Override
			public String getSha1() {
				return DigestUtils.sha1DigestAsHex(LUA_SCRIPT);
			}

			@Override
			public Class<String> getResultType() {
				return String.class;
			}

			@Override
			public String getScriptAsString() {
				return LUA_SCRIPT;
			}
		}, Collections.singletonList(lockKey), value, lockExpireTime, String.valueOf(expireTime));
		return "true".equals(result);
	}
}

package springboot.redisson.lock;


import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import org.redisson.RedissonLockEntry;
import org.redisson.api.RFuture;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * 分布式Redis锁
 */
@Slf4j
@Component
public class DistributedRedisLock {

    @Autowired
    private RedissonClient redissonClient;

   
    // 释放锁
    public Boolean unlock(String lockName) {
        if (redissonClient == null) {
            log.info("DistributedRedisLock redissonClient is null");
            return false;
        }

        try {
            RLock lock = redissonClient.getLock(lockName);
            lock.unlock();
            log.info("Thread [{}] DistributedRedisLock unlock [{}] success", Thread.currentThread().getName(), lockName);
            // 释放锁成功
            return true;
        } catch (Exception e) {
            log.error("DistributedRedisLock unlock [{}] Exception:", lockName, e);
            return false;
        }
    }
    
    	/**************************可重入锁**************************/
    	
    	/**
    	 * 拿不到lock就不罢休，不然线程就一直block
    	 * 没有超时时间,默认30s
    	 * 
    	 * @param lockKey
    	 * @return
    	 */
    	public RLock lock(String lockKey) {
    		RLock lock = redissonClient.getLock(lockKey);
    		lock.lock();
    		return lock;
    	}
    	
    	public RLock getLock(String lockKey) {
    		RLock lock = redissonClient.getLock(lockKey);
    		return lock;
    	}
     
    	/**
    	 * 自己设置超时时间
    	 * 
    	 * @param lockKey 锁的key
    	 * @param timeout 秒 如果是-1，直到自己解锁，否则不会自动解锁
    	 * @return
    	 */
    	public RLock lock(String lockKey, int timeout) {
    		RLock lock = redissonClient.getLock(lockKey);
    		lock.lock(timeout, TimeUnit.SECONDS);
    		return lock;
    	}
    	
    	/**
    	 * 自己设置超时时间
    	 * 
    	 * @param lockKey 锁的key
    	 * @param unit 锁时间单位
    	 * @param timeout 超时时间
    	 * 
    	 */
    	public RLock lock(String lockKey, TimeUnit unit, int timeout) {
    		RLock lock = redissonClient.getLock(lockKey);
    		lock.lock(timeout, unit);
    		return lock;
    	}
     
    	/**
    	 *  尝试加锁，最多等待waitTime，上锁以后leaseTime自动解锁
    	 * @param lockKey   锁key
    	 * @param unit      锁时间单位
    	 * @param waitTime  等到最大时间，强制获取锁
    	 * @param leaseTime 锁失效时间
    	 * @return 如果获取成功，则返回true，如果获取失败（即锁已被其他线程获取），则返回false
    	 */
    	public boolean tryLock(String lockKey, TimeUnit unit, int waitTime, int leaseTime) {
    		RLock lock = redissonClient.getLock(lockKey);
    		try {
    			return lock.tryLock(waitTime, leaseTime, unit);
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}
    		return false;
    	}
    	
    	
    	
    	/**************************公平锁**************************/
    	/**
    	 *   尝试加锁，最多等待waitTime，上锁以后leaseTime自动解锁
    	 * @param lockKey   锁key
    	 * @param unit      锁时间单位
    	 * @param waitTime  等到最大时间，强制获取锁
    	 * @param leaseTime 锁失效时间
    	 * @return 如果获取成功，则返回true，如果获取失败（即锁已被其他线程获取），则返回false
    	 */
    	public boolean fairLock(String lockKey, TimeUnit unit, int waitTime, int leaseTime) {
    		RLock fairLock = redissonClient.getFairLock(lockKey);
    		try {
    			return fairLock.tryLock(waitTime, leaseTime, unit);
    		} catch (InterruptedException e) {
    			e.printStackTrace();
    		}
    		return false;
    	}
     
}

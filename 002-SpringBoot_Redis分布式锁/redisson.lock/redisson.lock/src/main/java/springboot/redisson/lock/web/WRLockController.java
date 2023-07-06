package springboot.redisson.lock.web;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RCountDownLatch;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class WRLockController {
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedissonClient redisson;

	/**
	 * 保证一定能读到最新数据，修改期间，写锁是一个排它锁（互斥锁、独享锁），读锁是一个共享锁 写锁没释放读锁必须等待 读 + 读
	 * ：相当于无锁，并发读，只会在Redis中记录好，所有当前的读锁。他们都会同时加锁成功 写 + 读 ：必须等待写锁释放 写 + 写 ：阻塞方式 读 + 写
	 * ：有读锁。写也需要等待 只要有读或者写的存都必须等待
	 * 
	 * @return
	 */
	@GetMapping(value = "/write")
	@ResponseBody
	public String writeValue() {
		String s = "";
		RReadWriteLock readWriteLock = redisson.getReadWriteLock("rw-lock");
		RLock rLock = readWriteLock.writeLock();
		try {
			// 1、改数据加写锁，读数据加读锁
			rLock.lock();
			s = UUID.randomUUID().toString();
			ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
			ops.set("writeValue", s);
			TimeUnit.SECONDS.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			rLock.unlock();
		}

		return s;
	}

	@GetMapping(value = "/read")
	@ResponseBody
	public String readValue() {
		String s = "";
		RReadWriteLock readWriteLock = redisson.getReadWriteLock("rw-lock");
		// 加读锁
		RLock rLock = readWriteLock.readLock();
		try {
			rLock.lock();
			ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
			s = ops.get("writeValue");
			try {
				TimeUnit.SECONDS.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			rLock.unlock();
		}

		return s;
	}
	
	
	
	/**
	* 车库停车
	* 3车位
	* 信号量也可以做分布式限流
	*/
	@GetMapping(value = "/park")
	@ResponseBody
	public String park() throws InterruptedException {

	    RSemaphore park = redisson.getSemaphore("park");
	   // park.addPermits(2);
	    //park.acquire();     //获取一个信号、获取一个值,占一个车位
	    System.out.println("permits:" + park.availablePermits());
	    //park.acquire();
	    boolean flag = park.tryAcquire();
	    if (flag) {
	        System.out.println("1=" + park.availablePermits());
	    } else {
	        return "error1";
	    }
	    flag = park.tryAcquire();
	    if (flag) {
	    	System.out.println("2=" + park.availablePermits());
	    } else {
	        return "error1";
	    }
	    flag = park.tryAcquire();
	    if (flag) {
	    	System.out.println("3=" + park.availablePermits());
	    } else {
	        return "error1";
	    }

	    return "ok=>" + flag;
	}

	@GetMapping(value = "/go")
	@ResponseBody
	public String go() {
	    RSemaphore park = redisson.getSemaphore("park");
	    park.release();     //释放一个车位
	    return "ok" + park.availablePermits();
	}
	
	@GetMapping(value = "/lockDoor")
	@ResponseBody
	public String lockDoor() throws InterruptedException {


	    RCountDownLatch door = redisson.getCountDownLatch("door");
	    door.trySetCount(5);
	    door.await();       //等待闭锁完成
	    return "放假了...";
	}

	@GetMapping(value = "/gogogo/{id}")
	@ResponseBody
	public String gogogo(@PathVariable("id") Long id) {
	    RCountDownLatch door = redisson.getCountDownLatch("door");
	    door.countDown();       //计数-1
	    return id + "班的人都走了...";
	}
}

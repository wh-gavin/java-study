package springboot.redisson.lock;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = App.class)
@Slf4j
public class RedssonTest {

	@Autowired
	private DistributedRedisLock redisson;

	@Test
	public void test() throws Exception {
		try {
			redisson.lock("test");
			System.out.println("Locked");
		} finally {
			redisson.unlock("test");
		}
	}

	@Test
	public void test1() {

		// 1、获取一把锁，只要锁的名字一样，就是同一把锁
		RLock myLock = redisson.getLock("my-lock");

		// 2、加锁
		myLock.lock(); // 阻塞式等待。默认加的锁都是30s

		// 1）、锁的自动续期，如果业务超长，运行期间自动锁上新的30s。不用担心业务时间长，锁自动过期被删掉
		// 2）、加锁的业务只要运行完成，就不会给当前锁续期，即使不手动解锁，锁默认会在30s内自动过期，不会产生死锁问题

		// myLock.lock(10,TimeUnit.SECONDS); //10秒钟自动解锁,自动解锁时间一定要大于业务执行时间
		// 问题：在锁时间到了以后，不会自动续期(不会启动看门狗机制)
		// 1、如果我们传递了锁的超时时间，就发送给redis执行脚本，进行占锁，默认超时就是 我们指定的时间
		// 2、如果我们未指定锁的超时时间，就使用 lockWatchdogTimeout = 30 * 1000 【看门狗默认时间】
		// 只要占锁成功，就会启动一个定时任务【重新给锁设置过期时间，新的过期时间就是看门狗的默认时间】,每隔10秒都会自动的再次续期，续成30秒
		// internalLockLeaseTime 【看门狗时间】 / 3， 10s
		try {
			System.out.println("加锁成功，执行业务..." + Thread.currentThread().getId());
			try {
				TimeUnit.SECONDS.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			// 3、解锁 假设解锁代码没有运行，Redisson会不会出现死锁
			System.out.println("释放锁..." + Thread.currentThread().getId());
			myLock.unlock();
		}


	}

}

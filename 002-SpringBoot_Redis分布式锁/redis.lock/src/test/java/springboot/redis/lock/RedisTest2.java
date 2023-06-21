package springboot.redis.lock;

import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import springboot.redis.lock.bean.JacksonUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = App.class)
public class RedisTest2 {

	@Autowired
	private RedisTemplate redisTemplate;

	@Test
	public void test() throws Exception {
		// 向集合中添加数据
		SetOperations<String, String> operations = redisTemplate.opsForSet();
		// 向集合中添加元素,set元素具有唯一性
		operations.add("city", "北京", "上海", "广州", "深圳", "武汉");
		Long size = operations.size("city");
		System.out.println("获取集合总数：" + size);
		// 判断是否是集合中的元素
		Boolean isMember = operations.isMember("city", "广州");
		System.out.println("检查集合中是否存在指定元素：" + isMember);
		Set<String> cityNames = operations.members("city");
		System.out.println("获取集合所有元素：" + JacksonUtil.toJson(cityNames));
		Long remove = operations.remove("city", "广州");
		System.out.println("删除指定元素结果：" + remove);
		// 移除并返回集合中的一个随机元素
		String cityName = operations.pop("city");
		System.out.println("移除并返回集合中的一个随机元素：" + cityName);
	}
	

	@Test
	public void test2() throws Exception {
	    // 向有序集合中添加数据
	    ZSetOperations<String, String> operations = redisTemplate.opsForZSet();
	    //向有序集合中添加元素,set元素具有唯一性
	    operations.add("cityName", "北京", 100);
	    operations.add("cityName", "上海", 95);
	    operations.add("cityName", "广州", 75);
	    operations.add("cityName", "深圳", 85);
	    operations.add("cityName", "武汉", 70);

	    //获取变量指定区间的元素。0, -1表示全部
	    Set<String> ranges = operations.range("cityName", 0, -1);
	    System.out.println("获取有序集合所有元素：" + JacksonUtil.toJson(ranges));
	    Set<String> byScores = operations.rangeByScore("cityName", 85, 100);
	    
	    System.out.println("获取有序集合所有元素（按分数从小到大）："+ JacksonUtil.toJson(byScores));
	    
	    Set<String> rbyScores = operations.reverseRangeByScore("cityName",10, 200);
	    System.out.println("获取有序集合所有元素（按分数从大到小）："+ JacksonUtil.toJson(rbyScores));
	    
	    Long zCard = operations.zCard("cityName");
	    System.out.println("获取有序集合成员数: " + zCard);
	    Long remove = operations.remove("cityName", "武汉");
	    System.out.println("删除某个成员数结果: " + remove);

	}	
}

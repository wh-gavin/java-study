package springboot.redis.lock;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import springboot.redis.lock.bean.JacksonUtil;
import springboot.redis.lock.bean.UserVo;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = App.class)
public class RedisTest1 {

	@Autowired
	private RedisTemplate redisTemplate;

	@Test
	public void test() throws Exception {
	    // 设置对象值，并且2秒自动过期
	    ValueOperations<String, UserVo> operations = redisTemplate.opsForValue();
	    UserVo user = new UserVo("aa@126.com", "张三");
	    operations.set("user", user, 2, TimeUnit.SECONDS);

	    //获取对象值
	    UserVo userVo = operations.get("user");
	    System.out.println(userVo.toString());
	    System.out.println("获取user过期时间（单位秒）：" + redisTemplate.getExpire("user"));


	    //删除key
	    Boolean deleteValue = redisTemplate.delete("user");
	    System.out.println("删除userName结果：" +  deleteValue);
	}

	@Test
	public void test1() throws Exception {
	    // 向列表中添加数据
	    ListOperations<String, UserVo> operations = redisTemplate.opsForList();
	    // 往List左侧插入一个元素
	    operations.leftPush("test:userList", new UserVo("aa@126.com", "张三"));
	    operations.leftPush("test:userList", new UserVo("bb@126.com", "里斯"));
	    //往 List 右侧插入一个元素
	    operations.rightPush("test:userList", new UserVo("cc@126.com", "王五"));
	    operations.rightPush("test:userList", new UserVo("dd@126.com", "赵六"));
	    // 获取List 大小
	    Long size = operations.size("test:userList");
	    System.out.println("获取列表总数：" + size);
	    //遍历整个List
	    List<UserVo> allUserVo1 = operations.range("test:userList", 0, size);
	    System.out.println("遍历列表所有数据：" + JacksonUtil.toJson(allUserVo1));
	    //遍历整个List，-1表示倒数第一个即最后一个
	    List<UserVo> allUserVo2 = operations.range("test:userList", 0, -1);
	    System.out.println("遍历列表所有数据：" + JacksonUtil.toJson(allUserVo2));
	    //从 List 左侧取出第一个元素，并移除
	    Object userVo1 = operations.leftPop("test:userList", 200, TimeUnit.MILLISECONDS);
	    System.out.println("从左侧取出第一个元素并移除：" + userVo1.toString());
	    //从 List 右侧取出第一个元素，并移除
	    Object userVo2 = operations.rightPop("test:userList", 200, TimeUnit.MILLISECONDS);
	    System.out.println("从右侧取出第一个元素并移除：" + userVo2.toString());

	}
	
	@Test
	public void test3() throws Exception {
	    // 向hash中添加数据
	    HashOperations<String, String, Integer> operations = redisTemplate.opsForHash();
	    //Hash 中新增元素。
	    operations.put("score", "张三", 2);
	    operations.put("score", "里斯", 1);
	    operations.put("score", "王五", 3);
	    operations.put("score", "赵六", 4);

	    Boolean hasKey = operations.hasKey("score", "张三");
	    System.out.println("检查是否存在【score】【张三】：" + hasKey);
	    Integer value = operations.get("score", "张三");
	    System.out.println("获取【score】【张三】的值：" + value);
	    Set<String> keys = operations.keys("score");
	    System.out.println("获取hash表【score】所有的key集合：" + JacksonUtil.toJson(keys));
	    List<Integer> values = operations.values("score");
	    System.out.println("获取hash表【score】所有的value集合：" + JacksonUtil.toJson(values));
	    Map<String,Integer> map = operations.entries("score");
	    System.out.println("获取hash表【score】下的map数据：" + JacksonUtil.toJson(map));
	    Long delete = operations.delete("score", "里斯");
	    System.out.println("删除【score】中key为【里斯】的数据：" + delete);
	    Boolean result = redisTemplate.delete("score");
	    System.out.println("删除整个key：" + result);

	}
}

package org.tacos.mybatis;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.tacos.mybatis.mapper.def.CarMapper;
import org.tacos.mybatis.mapper.one.PrimaryUserMapper;
import org.tacos.mybatis.mapper.two.SecondaryUserMapper;
import org.tacos.mybatis.pojo.Car;
import org.tacos.mybatis.pojo.User;

@SpringBootTest
@RunWith(SpringRunner.class)
public class Mybatis001IntroduceApplicationTests {
	

    @Autowired
    private PrimaryUserMapper primaryUserMapper;
    @Autowired
    private SecondaryUserMapper secondaryUserMapper;
    
    @Test
    public void testPrimary() {
    	List<User> list = primaryUserMapper.findAll();
    	System.out.println("primary=" + list.toString());
    }
    @Test
    public void testSecondary() {
    	List<User> list = secondaryUserMapper.findAll();
    	System.out.println("secondary=" + list.toString());
    }
	@Autowired
	private CarMapper carMapper;

    @Test
    public void testInsert(){
        Car car = new Car(null,"111","奔驰",30.00,"2022-10-2","新能源");
        int count = carMapper.insert(car);
        System.out.println((count == 1 ? "插入成功" : "插入失败"));
    }
    @Test
    public void testDelete(){
        int count = carMapper.delete(4L);
        System.out.println((count == 1 ? "删除成功" : "删除失败"));
    }
    @Test
    public void testUpdate(){
        Car car = new Car(6L,"1111","奔驰",30.00,"2022-10-2","新能源");
        int count = carMapper.update(car);
        System.out.println((count == 1 ? "更新成功" : "更新失败"));
    }
    @Test
    public void testGetById() {
    	Long id = 1L;
    	Car c = carMapper.findById(id);
    	System.out.println(c==null ?"none" : c.toString());
    }
}

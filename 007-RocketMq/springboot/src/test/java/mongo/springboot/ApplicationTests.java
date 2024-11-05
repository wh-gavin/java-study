package mongo.springboot;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
 
@SpringBootTest
class ApplicationTests {
 
    @Test
    void contextLoads() {
    }
 
    // 注入MongoTemplate,用于操作MongoDB数据库
    @Autowired
    private MongoTemplate mongoTemplate;
 
    @Test
    void testSave() {
        User user = new User(1L, "zhangsan", "123456");
        System.out.println(mongoTemplate.save(user));
        user = new User(2L, "lisi", "123456");
        System.out.println(mongoTemplate.save(user));
    }
}
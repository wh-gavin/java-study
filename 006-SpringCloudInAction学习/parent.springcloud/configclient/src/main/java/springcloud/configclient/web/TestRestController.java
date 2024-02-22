package springcloud.configclient.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import springcloud.configclient.dao1.UserDao1;
import springcloud.configclient.dao2.UserDao2;

@RestController
@RefreshScope
public class TestRestController {

    @Value("${foo}")
    String foo;
    @Value("${common.id}")
    String common;
    @Autowired
    private UserDao1 userDao1;
    @Autowired
    private UserDao2 userDao2;
    
    @RequestMapping(value = "/hello")
    public String hello(){
        return foo + ";common=" + common;
    }
    
    @RequestMapping(value = "/user1")
    public String user1(){
        return userDao1.findAll().toString();
    } 
    
    @RequestMapping(value = "/user2")
    public String user2(){
        return userDao2.findAll().toString();
    }       
}
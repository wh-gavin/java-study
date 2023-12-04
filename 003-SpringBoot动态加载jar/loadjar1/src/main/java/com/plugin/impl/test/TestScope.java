package com.plugin.impl.test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
 
@RestController
@RequestMapping(value = "/testScope")
// @Scope("prototype")
public class TestScope {
 
    @Autowired
    private Order order;
 
    private String name;
 
    @RequestMapping(value = "/{username}", method = RequestMethod.GET)
    public void userProfile(@PathVariable("username") String username) {
        name = username;
        order.setOrderNum(name);
        try {
            for (int i = 0; i < 100; i++) {
                System.out.println(
                        Thread.currentThread().getId()
                                + "name:" + name
                                + "--order:"
                                + order.getOrderNum());
                Thread.sleep(2000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }
}
 
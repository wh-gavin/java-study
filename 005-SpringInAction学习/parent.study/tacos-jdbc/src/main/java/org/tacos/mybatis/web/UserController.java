package org.tacos.mybatis.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tacos.multi.entity.User;
import org.tacos.mybatis.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/get1")
    public User getUser1() {
    	return userService.getUser1();
    }
    
    @RequestMapping("/get2")
    public User getUser2() {
    	return userService.getUser2();
    }    
}

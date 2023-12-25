package org.tacos.mybatis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tacos.multi.entity.User;

@Service
public class UserService {

    @Autowired
    private org.tacos.mybatis.mapper1.UserMapper userMapper1;
    @Autowired
    private org.tacos.mybatis.mapper2.UserMapper userMapper2;
 
    public User getUser1() {
        return userMapper1.getNewstOne();
    }
    public User getUser2() {
        return userMapper2.getNewstOne();
    }
}
package org.tacos.multi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tacos.multi.db2.UserRepository;
import org.tacos.multi.entity.Goods;
import org.tacos.multi.entity.User;

/**
 * @Author 一一哥Sun
 * @Date Created in 2020/4/7
 * @Description Description
 */
@Service
public class UserServiceImpl {

    @Autowired    //(required = false)
    
    private UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }
    public void insert(User user) {
    	userRepository.save(user);
    }
}
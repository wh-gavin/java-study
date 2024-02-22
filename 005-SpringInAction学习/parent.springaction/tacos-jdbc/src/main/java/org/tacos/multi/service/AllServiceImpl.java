package org.tacos.multi.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tacos.multi.db1.GoodsRepository;
import org.tacos.multi.db2.UserRepository;
import org.tacos.multi.entity.Goods;
import org.tacos.multi.entity.User;

/**
 * @Author 一一哥Sun
 * @Date Created in 2020/4/7
 * @Description Description
 */
@Service
public class AllServiceImpl {

    @Autowired
    private GoodsRepository goodsRepository;
    public List<Goods> findAllGoods() {
        return goodsRepository.findAll();
    }

    @Autowired    //(required = false)
    
    private UserRepository userRepository;
    public List<User> findAllUser() {
        return userRepository.findAll();
    }
}
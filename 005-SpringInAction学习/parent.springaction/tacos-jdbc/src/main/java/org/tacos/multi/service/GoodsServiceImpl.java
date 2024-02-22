package org.tacos.multi.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tacos.multi.db1.GoodsRepository;
import org.tacos.multi.entity.Goods;

/**
 * @Author 一一哥Sun
 * @Date Created in 2020/4/7
 * @Description Description
 */
@Service
public class GoodsServiceImpl {

    @Autowired
    private GoodsRepository goodsRepository;

    public List<Goods> findAll() {
        return goodsRepository.findAll();
    }
    public void insert(Goods good) {
        goodsRepository.save(good);
    }
}
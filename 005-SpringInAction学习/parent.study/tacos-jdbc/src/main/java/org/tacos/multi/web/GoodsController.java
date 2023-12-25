package org.tacos.multi.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tacos.jdbc.JdbcIngredientRepository;
import org.tacos.multi.entity.Goods;
import org.tacos.multi.entity.User;
import org.tacos.multi.service.AllServiceImpl;
import org.tacos.multi.service.GoodsServiceImpl;
import org.tacos.multi.service.UserServiceImpl;

/**
 * @Author 一一哥Sun
 * @Date Created in 2020/4/3
 * @Description Description
 */
@RestController
public class GoodsController {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private GoodsServiceImpl goodsService;
    @Autowired
    private AllServiceImpl allService;
    @Autowired
    private JdbcIngredientRepository jdbcIngredientRepository;
    
    @GetMapping(value = "/users")
    public List<User> users() {
        return userService.findAll();
    }

    @GetMapping(value = "/goods")
    public List<Goods> goods() {
        return goodsService.findAll();
    }

    
    @GetMapping(value="/all")
    public Map<String, Object> getAll() {
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("user", userService.findAll());
    	map.put("good", goodsService.findAll());
    	map.put("ingredient", jdbcIngredientRepository.findAll());
    	return map;
    }
    
    @GetMapping(value="/alls")
    public Map<String, Object> getAllS() {
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("user", allService.findAllUser());
    	map.put("good", allService.findAllGoods());
    	map.put("ingredient", jdbcIngredientRepository.findAll());
    	return map;
    }
    @GetMapping(value="/adds")
    public Map<String, Object> adds() {
    	Goods good = new Goods(System.currentTimeMillis(), "test" + System.currentTimeMillis());
    	System.out.println(good.toString());
    	goodsService.insert(good);
    	User user = new User(System.currentTimeMillis(), "xzw" + System.currentTimeMillis(), "1977-11", "man", "wuhan");
    	System.out.println(user.toString());
    	userService.insert(user);
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("user", allService.findAllUser());
    	map.put("good", allService.findAllGoods());
    	map.put("ingredient", jdbcIngredientRepository.findAll());
    	return map;
    }
    
}
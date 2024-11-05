package com.logicbig.example;


import com.logicbig.example.order.Order;
import com.logicbig.example.order.OrderService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderServiceImpl implements OrderService {
    private List<Order> orders = new ArrayList<>();

    @Override
    public void placeOrder(String item, int quantity) {
        Order order = new Order();
        order.setItem(item);
        order.setQty(quantity);
        order.setOrderDate(LocalDateTime.now());
        System.out.println("placing order: " + order);
        orders.add(order);
    }

    @Override
    public List<Order> getOrderList() {
        return new ArrayList<>(orders);
    }
}
package com.logicbig.example;

import com.logicbig.example.order.Order;
import com.logicbig.example.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class OrderBean {

    @Autowired
    private OrderService orderService;

    public void placeOrder() {
        System.out.println("-- placing orders --");
        orderService.placeOrder("ABC Tablet", 2);
        orderService.placeOrder("XYZ Desktop", 3);
    }

    public void listOrders() {
        System.out.println("-- getting order list from service --");
        List<Order> orderList = orderService.getOrderList();
        System.out.println(orderList);
    }
}
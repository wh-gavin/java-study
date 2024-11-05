package com.logicbig.example;

import com.logicbig.example.order.OrderService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;
import org.springframework.remoting.support.RemoteExporter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@EnableWebMvc
@Configuration
public class AppConfig {

    @Bean
    public OrderService orderService() {
        return new OrderServiceImpl();
    }

    @Bean(name = "/OrderService")
    public RemoteExporter exporter() {
        HttpInvokerServiceExporter hse = new HttpInvokerServiceExporter();
        hse.setService(orderService());
        hse.setServiceInterface(OrderService.class);
        return hse;
    }
}
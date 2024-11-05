package com.logicbig.example;

import com.logicbig.example.order.OrderService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.rmi.RmiServiceExporter;

import java.net.Inet4Address;
import java.net.UnknownHostException;

@Configuration
public class ExampleApp {

    @Bean
    public OrderService orderService() {
        return new OrderServiceImpl();
    }

    @Bean
    public RmiServiceExporter exporter() throws UnknownHostException {
        RmiServiceExporter rse = new RmiServiceExporter();
        rse.setServiceName("OrderService");
        rse.setService(orderService());
        rse.setServiceInterface(OrderService.class);
        rse.setRegistryPort(2099);
        return rse;
    }

    public static void main(String[] args) {
        new AnnotationConfigApplicationContext(ExampleApp.class);
    }

}
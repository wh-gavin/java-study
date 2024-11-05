package com.logicbig.example;

import com.logicbig.example.order.OrderService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;

import java.net.Inet4Address;
import java.net.UnknownHostException;

@Configuration
public class ExampleClient {

    @Bean
    public OrderBean orderBean() {
        return new OrderBean();
    }

    @Bean
    public RmiProxyFactoryBean exporter() throws UnknownHostException {
        RmiProxyFactoryBean rpfb = new RmiProxyFactoryBean();
        rpfb.setServiceInterface(OrderService.class);
        String hostAddress = Inet4Address.getLocalHost()
                                         .getHostAddress();
        rpfb.setServiceUrl(String.format("rmi://%s:2099/OrderService", hostAddress));
        return rpfb;
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(ExampleClient.class);
        OrderBean bean = context.getBean(OrderBean.class);
        bean.placeOrder();
        bean.listOrders();
    }

}
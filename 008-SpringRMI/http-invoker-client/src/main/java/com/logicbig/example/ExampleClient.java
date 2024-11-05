package com.logicbig.example;

import com.logicbig.example.order.OrderService;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.remoting.support.RemoteAccessor;

@Configuration
public class ExampleClient {

    @Bean
    public OrderBean orderBean() {
        return new OrderBean();
    }

    @Bean
    public HttpInvokerProxyFactoryBean exporter() {
        HttpInvokerProxyFactoryBean b = new HttpInvokerProxyFactoryBean();
        b.setServiceUrl("http://localhost:8080/OrderService");
        b.setServiceInterface(OrderService.class);
        return b;
    }

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(ExampleClient.class);
        OrderBean bean = context.getBean(OrderBean.class);
        bean.placeOrder();
        bean.listOrders();
    }

}
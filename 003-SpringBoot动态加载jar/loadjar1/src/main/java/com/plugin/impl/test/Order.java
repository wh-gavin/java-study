package com.plugin.impl.test;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
 
@Service
//@Scope("prototype")
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class Order {
    private String orderNum;
 
    public String getOrderNum() {
        return orderNum;
    }
 
    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }
 
    @Override
    public String toString() {
        return "Order{" +
                "orderNum='" + orderNum + '\'' +
                '}';
    }
}
 
package com.appleyk.controller;

import com.appleyk.IPay;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.ServiceLoader;

/**
 * <p>SPI模式下的Controller</p>
 *
 * @author appleyk
 * @version V.0.1.1
 * @blob https://blog.csdn.net/appleyk
 * @github https://github.com/kobeyk
 * @date created on 2022/11/23-9:28
 */
@RestController
@RequestMapping("/spi")
public class SPIController {
    @GetMapping("pay")
    public String pay(){
        StringBuilder sb = new StringBuilder();
        ServiceLoader<IPay> instances = ServiceLoader.load(IPay.class);
        if (instances == null || !instances.iterator().hasNext()){
            return "empty pay instance";
        }
        for (IPay instance : instances) {
            sb.append(instance.pay());
            sb.append("\n");
        }
        return sb.toString();
    }
}

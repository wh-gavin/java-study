package org.itstack.demo.bytecode.j01;

import java.math.BigDecimal;

@RpcGatewayClazz(clazzDesc = "用户信息查询服务", alias = "api", timeOut = 500)
public class ApiTest {

    @RpcGatewayMethod(methodDesc = "查询息费", methodName = "interestFee")
    public double queryInterestFee(String uId){
        return BigDecimal.TEN.doubleValue();  // 模拟息费计算返回
    }

}

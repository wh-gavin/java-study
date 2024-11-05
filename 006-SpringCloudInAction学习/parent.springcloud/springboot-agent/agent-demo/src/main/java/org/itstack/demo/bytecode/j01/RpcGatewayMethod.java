package org.itstack.demo.bytecode.j01;

public @interface RpcGatewayMethod {

    String methodName() default "";
    String methodDesc() default "";
    
}

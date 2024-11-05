package org.itstack.demo.bytecode.j01;

public @interface RpcGatewayClazz {

    String clazzDesc() default "";
    String alias() default "";
    long timeOut() default 350;

}

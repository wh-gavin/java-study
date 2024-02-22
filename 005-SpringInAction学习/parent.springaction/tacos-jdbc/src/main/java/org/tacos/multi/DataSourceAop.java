package org.tacos.multi;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @Author 一一哥Sun
 * @Date Created in 2020/4/7
 * @Description Description
 */
//@Aspect
@Component
@Slf4j
public class DataSourceAop {

    //@Before("execution(* org.tacos.multi.service.GoodsServiceImpl.*(..))")
	//@Before("within(org.tacos.multi.db1..*.*(..))")
	@Before("execution(* org.tacos.multi.db1 ..*.*(..))")
    public void setDataSource01() {
        log.warn("db01商品数据源");
        DataSourceType.setDataSourceType(DataSourceType.SourceType.DS_SHOP);
    }

    //@Before("execution(* org.tacos.multi.service.UserServiceImpl.*(..))")
	//@Before("within(org.tacos.multi.db2.*)")
    @Before("execution(* org.tacos.multi.db2 ..*.*(..))")
    public void setDataSource02() {
        log.warn("db02用户数据源");
        DataSourceType.setDataSourceType(DataSourceType.SourceType.DS_USER);
    }

    //@Before("execution(* org.tacos.jdbc.JdbcIngredientRepository.*(..))")
    //@Before("execution(* org.tacos.jdbc..*.*(..))")
    @Before("execution(* org.tacos.jdbc ..*.*(..))")
    public void setDataSourceh2() {
        log.warn("h2用户数据源");
        DataSourceType.setDataSourceType(DataSourceType.SourceType.DS_H2);
    }
}
/**
例子:
execution(public * *(..)) 定义任意公共方法的执行
execution(* set*(..)) 定义任何一个以"set"开始的方法的执行
execution(* com.xyz.service.AccountService.*(..)) 定义AccountService 接口的任意方法的执行
execution(* com.xyz.service.*.*(..)) 定义在service包里的任意方法的执行
execution(* com.xyz.service ..*.*(..)) 定义在service包和所有子包里的任意类的任意方法的执行
execution(* com.test.spring.aop.pointcutexp…JoinPointObjP2.*(…)) 定义在pointcutexp包和所有子包里的JoinPointObjP2类的任意方法的执行：
*/
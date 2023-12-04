package com.appleyk.config;

import com.appleyk.loader.HotClassLoader;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * <p>bean配置</p>
 *
 * @author appleyk
 * @version V.0.1.1
 * @blob https://blog.csdn.net/appleyk
 * @github https://github.com/kobeyk
 * @date created on  下午9:30 2022/11/23
 */
@Configuration
public class BeanConfig {

    @Bean
    /** 配置成原型（多例），主要是为了更新jar时，使用新的类加载器实例去加载*/
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public HotClassLoader hotClassLoader(){
        return new HotClassLoader(this.getClass().getClassLoader());
    }
}

package org.tacos.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;



/**
 * @Author 一一哥Sun
 * @Date Created in 2020/4/3
 * @Description 配置数据源、连接工厂、事务管理器、dao目录
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "managerFactoryH2", // 配置连接工厂
        transactionManagerRef = "transactionManagerH2", // 配置事物管理器
        basePackages = {"org.tacos.jdbc"} // 设置dao所在位置

)
public class ManagerFactoryH2Config {

    /**
     * 配置数据源,连接第2个数据源
     */
    @Autowired
    @Qualifier("h2DataSource")
    private DataSource h2DataSource;

    @Bean(name = "managerFactoryH2")
    public LocalContainerEntityManagerFactoryBean buildEntityManagerFactoryH2(EntityManagerFactoryBuilder builder) {
        return builder
                // 设置数据源
                .dataSource(h2DataSource)
                //设置实体类所在位置.扫描所有带有 @Entity 注解的类
                .packages("org.tacos.domain")
                // Spring会将EntityManagerFactory注入到Repository之中.有了 EntityManagerFactory之后,
                // Repository就能用它来创建 EntityManager 了,然后 EntityManager 就可以针对数据库执行操作
                .persistenceUnit("h2PersistenceUnit")
                .build();

    }

    /**
     * 配置事务管理器
     */
    @Bean(name = "transactionManagerH2")
    public PlatformTransactionManager transactionManagerDatabaseH2(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(buildEntityManagerFactoryH2(builder).getObject());
    }
}
package org.tacos.mybatis;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(
        basePackages = "org.tacos.mybatis.mapper2",
        sqlSessionFactoryRef = "sqlSessionFactorySecondary",
        sqlSessionTemplateRef = "sqlSessionTemplateSecondary")
public class UserMapperConfig2 {
    private DataSource ds2DataSource;
    public UserMapperConfig2(@Qualifier("mybatisDs2") DataSource ds2DataSource) {
        this.ds2DataSource = ds2DataSource;
    }
    @Bean("sqlSessionFactorySecondary")
    public SqlSessionFactory sqlSessionFactorySecondary() throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(ds2DataSource);
        return bean.getObject();
    }
    @Bean("sqlSessionTemplateSecondary")
    public SqlSessionTemplate sqlSessionTemplateSecondary() throws Exception {
        return new SqlSessionTemplate(sqlSessionFactorySecondary());
    }
}
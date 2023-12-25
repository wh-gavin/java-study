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
        basePackages = "org.tacos.mybatis.mapper1",
        sqlSessionFactoryRef = "sqlSessionFactoryPrimary",
        sqlSessionTemplateRef = "sqlSessionTemplatePrimary")
public class UserMapperConfig1 {
    private DataSource ds1DataSource;
    public UserMapperConfig1(@Qualifier("mybatisDs1") DataSource ds1DataSource) {
        this.ds1DataSource = ds1DataSource;
    }
    @Bean("sqlSessionFactoryPrimary")
    public SqlSessionFactory sqlSessionFactoryPrimary() throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(ds1DataSource);
        return bean.getObject();
    }
    @Bean("sqlSessionTemplatePrimary")
    public SqlSessionTemplate sqlSessionTemplatePrimary() throws Exception {
        return new SqlSessionTemplate(sqlSessionFactoryPrimary());
    }
}
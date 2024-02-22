package org.tacos.mybatis.config;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@Configuration
//basePackages:接口文件的包路径
@MapperScan(basePackages = "org.tacos.mybatis.mapper.def", sqlSessionFactoryRef = "DefSqlSessionFactory")
public class DefDataSourceConfig {

  @Bean(name = "DefDataSource")
  // 表示这个数据源是默认数据源
  @Primary//这个一定要加，如果两个数据源都没有@Primary会报错
  @ConfigurationProperties(prefix = "spring.datasource")//配置文件中的前缀
  public DataSource getPrimaryDateSource() {
      return DataSourceBuilder.create().build();
  }

  @Bean(name = "DefSqlSessionFactory")
  @Primary
  public SqlSessionFactory primarySqlSessionFactory(@Qualifier("DefDataSource") DataSource datasource)
          throws Exception {
      SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
      bean.setDataSource(datasource);
      bean.setMapperLocations( 
              new PathMatchingResourcePatternResolver().getResources("classpath*:mapper/def/*.xml"));
      return bean.getObject();// 设置mybatis的xml所在位置
  }

  @Bean("DefSqlSessionTemplate")
  // 表示这个数据源是默认数据源
  @Primary
  public SqlSessionTemplate primarySqlSessionTemplate(
          @Qualifier("DefSqlSessionFactory") SqlSessionFactory sessionfactory) {
      return new SqlSessionTemplate(sessionfactory);
  }

}

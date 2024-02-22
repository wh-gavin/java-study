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
@MapperScan(basePackages = "org.tacos.mybatis.mapper.one", sqlSessionFactoryRef = "PrimarySqlSessionFactory")
public class PrimaryDataSourceConfig {

  @Bean(name = "PrimaryDataSource")
  // 表示这个数据源是默认数据源
  @ConfigurationProperties(prefix = "spring.datasource.primary")//配置文件中的前缀
  public DataSource getPrimaryDateSource() {
      return DataSourceBuilder.create().build();
  }

  @Bean(name = "PrimarySqlSessionFactory")
  @Primary
  public SqlSessionFactory primarySqlSessionFactory(@Qualifier("PrimaryDataSource") DataSource datasource)
          throws Exception {
      SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
      bean.setDataSource(datasource);
      bean.setMapperLocations( 
              new PathMatchingResourcePatternResolver().getResources("classpath*:mapper/one/*.xml"));
      return bean.getObject();// 设置mybatis的xml所在位置
  }

  @Bean("PrimarySqlSessionTemplate")
  // 表示这个数据源是默认数据源
  @Primary
  public SqlSessionTemplate primarySqlSessionTemplate(
          @Qualifier("PrimarySqlSessionFactory") SqlSessionFactory sessionfactory) {
      return new SqlSessionTemplate(sessionfactory);
  }

}

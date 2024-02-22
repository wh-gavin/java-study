package org.tacos.multi;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DataSourceRegisterConfig {
    /**
     * 主数据源配置 ds1数据源
     */
    //@Primary
    @Bean(name = "ds1Properties")
    @ConfigurationProperties(prefix = "spring.datasource.ds1")
    public DataSourceProperties ds1DataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * 主数据源 ds1数据源
     */
    //@Primary
    @Bean(name = "ds1DataSource")
    @ConfigurationProperties(prefix = "spring.datasource.ds1")
    public DataSource ds1DataSource(@Qualifier("ds1Properties") DataSourceProperties dataSourceProperties) {
        //HikariDataSource","org.apache.tomcat.jdbc.pool.DataSource", "org.apache.commons.dbcp2.BasicDataSource
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    /**
     * 第二个ds2数据源配置
     */
    @Bean(name = "ds2Properties")
    @ConfigurationProperties(prefix = "spring.datasource.ds2")
    public DataSourceProperties ds2DataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * 第二个ds2数据源
     */
    @Bean("ds2DataSource")
    @ConfigurationProperties(prefix = "spring.datasource.ds2")
    public DataSource ds2DataSource(@Qualifier("ds2Properties") DataSourceProperties dataSourceProperties) { //
    	//return DataSourceBuilder.create().build();
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }
    


}

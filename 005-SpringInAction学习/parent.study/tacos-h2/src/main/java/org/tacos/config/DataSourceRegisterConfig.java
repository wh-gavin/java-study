package org.tacos.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DataSourceRegisterConfig {

    /**
     * 第三个ds2数据源配置
     */
	@Primary
    @Bean(name="h2Properties")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSourceProperties h2DataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * 第三个ds2数据源
     */
    @Primary
    @Bean("h2DataSource")
    public DataSource h2DataSource(@Qualifier("h2Properties") DataSourceProperties dataSourceProperties) {
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }    
    
    @Bean
    JdbcTemplate jdbcTemplateOne(@Qualifier("h2DataSource")DataSource dataSource){
        return new JdbcTemplate(dataSource);
    }
}

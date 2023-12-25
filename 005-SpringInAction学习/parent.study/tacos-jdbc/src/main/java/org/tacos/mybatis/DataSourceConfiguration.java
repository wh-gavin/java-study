package org.tacos.mybatis;

import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSourceConfiguration {
	@Bean("mybatisDs1")
	@ConfigurationProperties(prefix = "spring.datasource.ds1")
	public DataSource primaryDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean("mybatisDs2")
	@ConfigurationProperties(prefix = "spring.datasource.ds2")
	public DataSource secondaryDataSource() {
		return DataSourceBuilder.create().build();
	}

}

package org.tacos.multi;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author 一一哥Sun
 * @Date Created in 2020/4/7
 * @Description 多数据源配置
 */
@Configuration
public class DynamicDataSourceConfig {

    @Bean(name = "dynamicDataSource")
    public DynamicDataSource dynamicDataSource(@Qualifier("ds1DataSource") DataSource ds1DataSource,
                                        @Qualifier("ds2DataSource") DataSource ds2DataSource, @Qualifier("h2DataSource") DataSource h2DataSource) {
        Map<Object, Object> targetDataSource = new HashMap<>();
        targetDataSource.put(DataSourceType.SourceType.DS_SHOP, ds1DataSource);
        targetDataSource.put(DataSourceType.SourceType.DS_USER, ds2DataSource);
        targetDataSource.put(DataSourceType.SourceType.DS_H2, h2DataSource);
        DynamicDataSource dataSource = new DynamicDataSource();
        dataSource.setTargetDataSources(targetDataSource);
        dataSource.setDefaultTargetDataSource(ds2DataSource);
        return dataSource;
    }

}
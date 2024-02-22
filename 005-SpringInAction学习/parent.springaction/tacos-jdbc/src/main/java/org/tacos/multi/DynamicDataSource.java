package org.tacos.multi;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @Author 一一哥Sun
 * @Date Created in 2020/4/7
 * @Description 动态切换数据源
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceType.getDataSourceType();
    }

}
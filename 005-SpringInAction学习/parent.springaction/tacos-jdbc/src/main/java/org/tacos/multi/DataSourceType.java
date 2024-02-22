package org.tacos.multi;


import lombok.extern.slf4j.Slf4j;

/**
 * @Author 一一哥Sun
 * @Date Created in 2020/4/7
 * @Description 数据源类型
 */
@Slf4j
public class DataSourceType {

    public enum SourceType {
        /**
         * 用户数据源
         */
        DS_USER,
        /**
         * 商品数据源
         */
        DS_SHOP,
        
        DS_H2
    }

    /**
     * 使用ThreadLocal保证线程安全
     */
    private static final ThreadLocal<SourceType> TYPES = new ThreadLocal<>();

    /**
     * 往当前线程里设置数据源类型
     */
    public static void setDataSourceType(SourceType dataSourceType) {
        if (dataSourceType == null) {
            throw new NullPointerException();
        }
        log.warn("[设置当前数据源为]：" + dataSourceType);
        TYPES.set(dataSourceType);
    }

    /**
     * 获取数据源类型
     */
    public static SourceType getDataSourceType() {
        SourceType dataSourceType = TYPES.get() == null ? SourceType.DS_USER : TYPES.get();
        log.warn("[当前数据源的类型为]：" + dataSourceType);
        return dataSourceType;
    }

    /**
     * 清空数据类型
     */
    public static void removeDataSourceType() {
        TYPES.remove();
    }

}
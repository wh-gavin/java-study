package com.efuture.omd.storage.mybatis;

import com.alibaba.druid.pool.DruidDataSource;
import com.efuture.omd.storage.mybatis.dialect.Dialect;
import java.sql.Connection;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.ibatis.executor.statement.PreparedStatementHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;

@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class DiclectStatementHandlerInterceptor implements Interceptor {
	Dialect dialect;

	public Object intercept(Invocation invocation) throws Throwable {
		RoutingStatementHandler statement = (RoutingStatementHandler) invocation.getTarget();
		Object delegate = ReflectUtil.getFieldValue(statement, "delegate");
		if (delegate instanceof PreparedStatementHandler) {
			PreparedStatementHandler handler = (PreparedStatementHandler) delegate;
			RowBounds rowBounds = (RowBounds) ReflectUtil.getFieldValue(handler, "rowBounds");
			if (rowBounds != null && rowBounds.getLimit() > 0 && rowBounds.getLimit() < Integer.MAX_VALUE) {
				BoundSql boundSql = statement.getBoundSql();
				String sql = boundSql.getSql();
				this.createDialect(handler);
				String pagesql = this.dialect.getLimitString(sql, rowBounds.getOffset(), rowBounds.getLimit());
				ReflectUtil.setFieldValue(boundSql, "sql", pagesql);
			}
		}

		return invocation.proceed();
	}

	private void createDialect(PreparedStatementHandler handler) {
		Configuration configuration = (Configuration) ReflectUtil.getFieldValue(handler, "configuration");
		Environment environment = (Environment) ReflectUtil.getFieldValue(configuration, "environment");
		String url = "";
		DataSource dataSource = environment.getDataSource();
		if (dataSource instanceof DruidDataSource) {
			url = ((String) ReflectUtil.getFieldValue(dataSource, "jdbcUrl")).toLowerCase();
		} else {
			url = ((String) ReflectUtil.getFieldValue(dataSource, "url")).toLowerCase();
		}

		String dialectClass = "";
		if (url.indexOf("jdbc:db2") > -1) {
			dialectClass = "com.efuture.omd.storage.mybatis.dialect.DB2Dialect";
		} else if (url.indexOf("jdbc:derby") > -1) {
			dialectClass = "com.efuture.omd.storage.mybatis.dialect.DerbyDialect";
		} else if (url.indexOf("jdbc:h2") > -1) {
			dialectClass = "com.efuture.omd.storage.mybatis.dialect.H2Dialect";
		} else if (url.indexOf("jdbc:hsql") > -1) {
			dialectClass = "com.efuture.omd.storage.mybatis.dialect.HSQLDialect";
		} else if (url.indexOf("jdbc:mysql") > -1) {
			dialectClass = "com.efuture.omd.storage.mybatis.dialect.MySQLDialect";
		} else if (url.indexOf("jdbc:oracle") > -1) {
			dialectClass = "com.efuture.omd.storage.mybatis.dialect.OracleDialect";
		} else if (url.indexOf("jdbc:postgresql") > -1) {
			dialectClass = "com.efuture.omd.storage.mybatis.dialect.PostgreSQLDialect";
		} else if (url.indexOf("jdbc:sqlserver2005") > -1) {
			dialectClass = "com.efuture.omd.storage.mybatis.dialect.SQLServer2005Dialect";
		} else if (url.indexOf("jdbc:sqlserver") > -1) {
			dialectClass = "com.efuture.omd.storage.mybatis.dialect.SQLServerDialect";
		} else if (url.indexOf("jdbc:sybase") > -1) {
			dialectClass = "com.efuture.omd.storage.mybatis.dialect.SybaseDialect";
		} else if (url.indexOf("jdbc:sqlite") > -1) {
			dialectClass = "com.efuture.omd.storage.mybatis.dialect.SQLiteDialect";
		}

		try {
			if ("".equals(dialectClass)) {
				throw new RuntimeException("无法得到Dialect类名。");
			} else {
				this.dialect = (Dialect) Class.forName(dialectClass).newInstance();
			}
		} catch (Exception var8) {
			throw new RuntimeException("无法创建Dialect类:" + dialectClass, var8);
		}
	}

	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	public void setProperties(Properties properties) {
	}
}
package com.efuture.omd.storage.mybatis.dialect;

import com.efuture.omd.storage.mybatis.dialect.Dialect;

public class MySQLDialect extends Dialect {
	public boolean supportsLimitOffset() {
		return true;
	}

	public boolean supportsLimit() {
		return true;
	}

	public String getLimitString(String sql, int offset, String offsetPlaceholder, int limit, String limitPlaceholder) {
		if (offset > 0) {
			return sql + " limit " + offsetPlaceholder + "," + limitPlaceholder;
		}
		return sql + " limit " + limitPlaceholder;
	}
}
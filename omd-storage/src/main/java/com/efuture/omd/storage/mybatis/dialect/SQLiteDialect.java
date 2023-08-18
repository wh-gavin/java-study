package com.efuture.omd.storage.mybatis.dialect;

public class SQLiteDialect extends Dialect {
	public boolean supportsLimitOffset() {
		return true;
	}

	public boolean supportsLimit() {
		return true;
	}

	public String getLimitString(String sql, int offset, String offsetPlaceholder, int limit, String limitPlaceholder) {
		return offset > 0
				? sql + " limit " + offsetPlaceholder + "," + limitPlaceholder
				: sql + " limit " + limitPlaceholder;
	}
}
package com.efuture.omd.storage.mybatis.dialect;

public class Dialect {
	public boolean supportsLimit() {
		return false;
	}

	public boolean supportsLimitOffset() {
		return this.supportsLimit();
	}

	public String getLimitString(String sql, int offset, int limit) {
		return this.getLimitString(sql, offset, Integer.toString(offset), limit, Integer.toString(limit));
	}

	public String getLimitString(String sql, int offset, String offsetPlaceholder, int limit, String limitPlaceholder) {
		throw new UnsupportedOperationException("paged queries not supported");
	}
}
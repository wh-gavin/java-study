package com.efuture.omd.storage.mybatis;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.StringTypeHandler;

public class ISO8859String extends StringTypeHandler {
	public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType)
			throws SQLException {
		try {
			String s = new String(parameter.getBytes("GBK"), "ISO8859_1");
			ps.setString(i, s);
		} catch (Exception var6) {
			ps.setString(i, parameter);
		}

	}

	public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
		String s = rs.getString(columnName);

		try {
			return new String(s.getBytes("ISO8859_1"), "GBK");
		} catch (Exception var5) {
			return s;
		}
	}

	public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		String s = cs.getString(columnIndex);

		try {
			return new String(s.getBytes("ISO8859_1"), "GBK");
		} catch (Exception var5) {
			return s;
		}
	}
}
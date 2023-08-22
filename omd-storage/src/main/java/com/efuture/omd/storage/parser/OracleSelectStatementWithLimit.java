package com.efuture.omd.storage.parser;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.visitor.SQLASTOutputVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OracleSelectStatementWithLimit extends SQLSelectStatement {
	int limit = -1;

	public OracleSelectStatementWithLimit() {
	}

	public OracleSelectStatementWithLimit(String dbType) {
		super(DbType.of(dbType));
	}

	public OracleSelectStatementWithLimit(SQLSelect select, int limit) {
		super(select);
		this.limit = limit;
	}

	public OracleSelectStatementWithLimit(SQLSelect select, String dbType) {
		super(select, DbType.of(dbType));
	}

	protected void accept0(SQLASTVisitor visitor) {
		super.accept0(visitor);
		if (this.limit > 0) {
			StringBuilder build = (StringBuilder) ((SQLASTOutputVisitor) visitor).getAppender();
			build.insert(0, "select limit$table.* from (");
			build.append(") limit$table where rownum <= " + this.limit);
		}
	}
}
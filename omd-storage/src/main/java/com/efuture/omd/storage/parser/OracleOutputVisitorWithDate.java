package com.efuture.omd.storage.parser;

import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleDateExpr;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleOutputVisitor;

public class OracleOutputVisitorWithDate extends OracleOutputVisitor {
	private static final String DATETIME_PATTERN = "YYYY-MM-DD HH24:MI:SS";

	public OracleOutputVisitorWithDate(Appendable appender) {
		super(appender, true);
	}

	public OracleOutputVisitorWithDate(Appendable appender, boolean printPostSemi) {
		super(appender, printPostSemi);
	}

	public boolean visit(OracleDateExpr x) {
		this.print("TO_DATE('");
		this.print(x.getLiteral());
		this.print("','");
		this.print("YYYY-MM-DD HH24:MI:SS");
		this.print("')");
		return false;
	}

	public boolean visit(SQLCharExpr x) {
		if (x instanceof SQLTextExpr) {
			this.print(x.getText());
			return false;
		} else {
			return super.visit(x);
		}
	}
}
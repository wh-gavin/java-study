package com.efuture.omd.storage.parser;

import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlOutputVisitor;

public class MySqlOutputVisitorWithText extends MySqlOutputVisitor {
	public MySqlOutputVisitorWithText(Appendable appender) {
		super(appender);
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
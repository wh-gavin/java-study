package com.alibaba.druid.sql.dialect.oracle.ast.expr;

import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.ast.expr.SQLLiteralExpr;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OracleDateExpr extends SQLExprImpl implements SQLLiteralExpr, OracleExpr {
	private String literal;

	public String getLiteral() {
		return this.literal;
	}

	public void setLiteral(String literal) {
		this.literal = literal;
	}

	protected void accept0(SQLASTVisitor visitor) {
		accept0((OracleASTVisitor) visitor);
	}

	public void accept0(OracleASTVisitor visitor) {
		visitor.visit(this);
		visitor.endVisit(this);
	}

	public int hashCode() {
		int prime = 31;
		int result = 1;
		return 31 * result + ((this.literal == null) ? 0 : this.literal.hashCode());
	}

	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		OracleDateExpr other = (OracleDateExpr) obj;
		if (this.literal == null) {
			if (other.literal != null) {
				return false;
			}
		} else if (!this.literal.equals(other.literal)) {
			return false;
		}
		return true;
	}
}

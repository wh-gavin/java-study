package com.efuture.omd.storage.parser;

import java.util.List;

import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.SerializationUtils;
import org.springframework.data.mongodb.core.query.Update;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLLimit;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOpExpr;
import com.alibaba.druid.sql.ast.expr.SQLBinaryOperator;
import com.alibaba.druid.sql.ast.expr.SQLNumberExpr;
import com.alibaba.druid.sql.ast.statement.SQLUpdateSetItem;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;
import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleUpdateStatement;
import com.alibaba.druid.sql.dialect.postgresql.ast.stmt.PGUpdateStatement;
import com.alibaba.druid.sql.dialect.sqlserver.ast.stmt.SQLServerUpdateStatement;
import com.mongodb.DBObject;

public class QueryUpdateExtractor extends QueryExtractor {
	private Query query;
	private Update update;
	private boolean upsert;
	private boolean multi;

	public QueryUpdateExtractor(String collectionName, Query query, Update update) {
		this(collectionName, query, update, false, true);
	}

	public QueryUpdateExtractor(String collectionName, Query query, Update update, boolean upsert, boolean multi) {
		this.collectionName = collectionName;
		this.query = query;
		this.update = update;
		this.upsert = upsert;
		this.multi = multi;
	}

	public String getQueryForMongo() {
		StringBuilder sb = new StringBuilder();

		sb.append("db").append(".").append(collectionName).append(".update(").append(getMongoQueryString(query));
		if (update != null) {
			sb.append(", ");
			sb.append(SerializationUtils.serializeToJsonSafely(update.getUpdateObject()));
		}
		if ((upsert) || (multi)) {
			sb.append(", {");
			String key = "upsert";
			if (multi) {
				key = "multi";
			}
			sb.append("\"").append(key).append("\" : true");
			sb.append("}");
		}
		sb.append(")");
		return sb.toString();
	}

	public String getQueryForSQL() {
		if (upsert) {
			throw new IllegalArgumentException("Upsert not supported");
		}
		SQLUpdateStatement stmt = null;
		switch (SQLDBType) {
			case MYSQL :
				stmt = new MySqlUpdateStatement();
				break;
			case ORACLE :
				stmt = new OracleUpdateStatement();
				break;
			case SQLSERVER :
				stmt = new SQLServerUpdateStatement();
				break;
			case POSTGRESQL :
				stmt = new PGUpdateStatement();
				break;
			default :
				stmt = new SQLUpdateStatement();
		}
		stmt.setTableSource(ExprTranslator.translateTableName(collectionName));
		if ((query != null) && (query.getQueryObject() != null)) {
			stmt.setWhere(ExprTranslator.translateWhere(query.getQueryObject(), SQLDBType));
		}
		parseSetItems(update, stmt);
		if (!multi) {
			if ((stmt instanceof MySqlUpdateStatement)) {
				SQLLimit limit = new SQLLimit();
				limit.setRowCount(new SQLNumberExpr(Integer.valueOf(1)));
				((MySqlUpdateStatement) stmt).setLimit(limit);
			}
		}
		return getSQL(stmt);
	}

	private void parseSetItems(Update update, SQLUpdateStatement stmt) {
		List<SQLUpdateSetItem> updateItems = stmt.getItems();
		DBObject updateObj = update.getUpdateObject();
		for (String op : updateObj.keySet()) {
			DBObject obj = (DBObject) updateObj.get(op);
			switch (MongoExpr.fromString(op)) {
				case 22 :
					for (String key : obj.keySet()) {
						SQLUpdateSetItem item = new SQLUpdateSetItem();
						item.setColumn(ExprTranslator.translateKey(key));
						item.setValue(ExprTranslator.parseObject(obj.get(key), SQLDBType));
						updateItems.add(item);
					}
					break;
				case 23 :
					for (String key : obj.keySet()) {
						SQLUpdateSetItem item = new SQLUpdateSetItem();
						SQLExpr keyExpr = ExprTranslator.translateKey(key);
						item.setColumn(keyExpr);
						SQLExpr incValueExpr = ExprTranslator.parseObject(obj.get(key), SQLDBType);
						SQLExpr valExpr = new SQLBinaryOpExpr(keyExpr, SQLBinaryOperator.Add, incValueExpr);
						item.setValue(valExpr);
						updateItems.add(item);
					}
					break;
				default :
					throw new IllegalArgumentException("Unsupported array operator: " + op);
			}
		}
	}
}
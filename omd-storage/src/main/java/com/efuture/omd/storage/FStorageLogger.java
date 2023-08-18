package com.efuture.omd.storage;

import com.efuture.omd.storage.parser.QueryInsertExtractor;
import com.efuture.omd.storage.parser.QueryRemoveExtractor;
import com.efuture.omd.storage.parser.QuerySelectExtractor;
import com.efuture.omd.storage.parser.QueryUpdateExtractor;
import com.mongodb.DBObject;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public class FStorageLogger {
	private static final Log LOG = LogFactory.getLog(FStorageLogger.class);

	public void logSelectQuery(String collectionName, Query query) {
		if (LOG.isDebugEnabled()) {
			try {
				QuerySelectExtractor extractor = new QuerySelectExtractor(collectionName, query);
				LOG.info("MongoDB syntax: " + extractor.getQueryForMongo());
				LOG.info("SQL syntax: " + extractor.getQueryForSQL());
			} catch (Exception var4) {
				LOG.warn("Got exception when get syntax: " + var4.getMessage());
			}
		}

	}

	public void logInsertQuery(String collectionName, DBObject dbDoc, List<DBObject> dbDocList) {
		if (LOG.isDebugEnabled()) {
			try {
				QueryInsertExtractor extractor;
				if (dbDoc != null) {
					extractor = new QueryInsertExtractor(collectionName, dbDoc);
				} else {
					extractor = new QueryInsertExtractor(collectionName, dbDocList);
				}

				LOG.info("MongoDB syntax: " + extractor.getQueryForMongo());
				LOG.info("SQL syntax: " + extractor.getQueryForSQL());
			} catch (Exception var5) {
				LOG.warn("Got exception when get syntax: " + var5.getMessage());
			}
		}

	}

	public void logUpdateQuery(String collectionName, Query query, Update update, boolean upsert, boolean multi) {
		if (LOG.isDebugEnabled()) {
			try {
				QueryUpdateExtractor extractor = new QueryUpdateExtractor(collectionName, query, update, upsert, multi);
				LOG.info("MongoDB syntax: " + extractor.getQueryForMongo());
				LOG.info("SQL syntax: " + extractor.getQueryForSQL());
			} catch (Exception var7) {
				LOG.warn("Got exception when get syntax: " + var7.getMessage());
			}
		}

	}

	public void logDeleteQuery(String collectionName, Query query) {
		if (LOG.isDebugEnabled()) {
			try {
				QueryRemoveExtractor extractor = new QueryRemoveExtractor(collectionName, query);
				LOG.info("MongoDB syntax: " + extractor.getQueryForMongo());
				LOG.info("SQL syntax: " + extractor.getQueryForSQL());
			} catch (Exception var4) {
				LOG.warn("Got exception when get syntax: " + var4.getMessage());
			}
		}

	}

	public void logSQLQuery(String sql) {
		if (LOG.isDebugEnabled()) {
			LOG.info("SQL syntax: " + sql);
		}

	}
}
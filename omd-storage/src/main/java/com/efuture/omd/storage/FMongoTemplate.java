package com.efuture.omd.storage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.WriteResultChecking;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.WriteResult;

public class FMongoTemplate extends MongoTemplate implements FStorageOperations {
	private FStorageLogger logger = new FStorageLogger();

	public FMongoTemplate(Mongo mongo, String databaseName) {
		super(new SimpleMongoDbFactory(mongo, databaseName), null);
	}

	public FMongoTemplate(Mongo mongo, String databaseName, UserCredentials userCredentials) {
		super(new SimpleMongoDbFactory(mongo, databaseName, userCredentials));
	}

	public FMongoTemplate(MongoDbFactory mongoDbFactory) {
		super(mongoDbFactory, null);
	}

	public FMongoTemplate(MongoDbFactory mongoDbFactory, MongoConverter mongoConverter) {
		super(mongoDbFactory, mongoConverter);
	}

	public void setWriteResultMode(String writeResultMode) {
		if ("None".equalsIgnoreCase(writeResultMode)) {
			setWriteResultChecking(WriteResultChecking.NONE);
		} else if ("Exception".equalsIgnoreCase(writeResultMode)) {
			setWriteResultChecking(WriteResultChecking.EXCEPTION);
		} else if ("Log".equalsIgnoreCase(writeResultMode)) {
			setWriteResultChecking(WriteResultChecking.LOG);
		}
	}

	public void destroy() {
	}

	public <T> T selectOne(Query query, Class<T> entityClass) {
		return findOne(query, entityClass);
	}

	public <T> T selectOne(Query query, Class<T> entityClass, String tableName) {
		return findOne(query, entityClass, tableName);
	}

	public Map<String, Object> selectOne(Query query, String tableName) {
		BasicDBObject obj = (BasicDBObject) findOne(query, BasicDBObject.class, tableName);
		if (obj == null)
			return null;
		obj.remove("_id");
		return obj;
	}

	public <T> List<T> select(Query query, Class<T> entityClass) {
		return find(query, entityClass);
	}

	public <T> List<T> select(Query query, Class<T> entityClass, String tableName) {
		return find(query, entityClass, tableName);
	}

	public List<Map<String, Object>> select(Query query, String tableName) {
		List<BasicDBObject> lst = find(query, BasicDBObject.class, tableName);
		if (lst == null)
			return null;
		List list = new ArrayList();
		for (BasicDBObject obj : lst) {
			obj.remove("_id");
			list.add(obj);
		}
		return list;
	}

	public long count(Query query, Class<?> entityClass) {
		return super.count(query, entityClass);
	}

	public long count(Query query, String tableName) {
		return super.count(query, tableName);
	}

	public void insert(Object objectToSave) {
		super.insert(objectToSave);
	}

	public void insert(Object objectToSave, String tableName) {
		super.insert(objectToSave, tableName);
	}

	public void insert(Collection<? extends Object> batchToSave, Class<?> entityClass) {
		super.insert(batchToSave, entityClass);
	}

	public void insert(Collection<? extends Object> batchToSave, String tableName) {
		super.insert(batchToSave, tableName);
	}

	public void insertAll(Collection<? extends Object> objectsToSave) {
		super.insertAll(objectsToSave);
	}

	public int updateOrInsert(Query query, Update update, Class<?> entityClass) {
		WriteResult wr = upsert(query, update, entityClass);
		if (wr == null)
			return -1;
		return wr.getN();
	}

	public int updateOrInsert(Query query, Update update, String tableName) {
		WriteResult wr = upsert(query, update, tableName);
		if (wr == null)
			return -1;
		return wr.getN();
	}

	public int updateOrInsert(Query query, Update update, Class<?> entityClass, String tableName) {
		WriteResult wr = upsert(query, update, entityClass, tableName);
		if (wr == null)
			return -1;
		return wr.getN();
	}

	public int update(Query query, Update update, Class<?> entityClass) {
		WriteResult wr = updateMulti(query, update, entityClass);
		if (wr == null)
			return -1;
		return wr.getN();
	}

	public int update(Query query, Update update, String tableName) {
		WriteResult wr = updateMulti(query, update, tableName);
		if (wr == null)
			return -1;
		return wr.getN();
	}

	public int update(Query query, Update update, Class<?> entityClass, String tableName) {
		WriteResult wr = updateMulti(query, update, entityClass, tableName);
		if (wr == null)
			return -1;
		return wr.getN();
	}

	public int delete(Object object) {
		remove(object);
		return -1;
	}

	public int delete(Object object, String tableName) {
		remove(object, tableName);
		return -1;
	}

	public int delete(Query query, Class<?> entityClass) {
		remove(query, entityClass);
		return -1;
	}

	public int delete(Query query, Class<?> entityClass, String tableName) {
		remove(query, entityClass, tableName);
		return -1;
	}

	public int delete(Query query, String tableName) {
		remove(query, tableName);
		return -1;
	}

	public <T> T findOne(Query query, Class<T> entityClass, String collectionName) {
		this.logger.logSelectQuery(collectionName, query);

		return super.findOne(query, entityClass, collectionName);
	}

	public <T> List<T> find(Query query, Class<T> entityClass, String collectionName) {
		this.logger.logSelectQuery(collectionName, query);

		return super.find(query, entityClass, collectionName);
	}

	protected Object insertDBObject(String collectionName, DBObject dbDoc, Class<?> entityClass) {
		this.logger.logInsertQuery(collectionName, dbDoc, null);

		return super.insertDBObject(collectionName, dbDoc, entityClass);
	}

	protected List<ObjectId> insertDBObjectList(String collectionName, List<DBObject> dbDocList) {
		this.logger.logInsertQuery(collectionName, null, dbDocList);

		return super.insertDBObjectList(collectionName, dbDocList);
	}

	protected WriteResult doUpdate(String collectionName, Query query, Update update, Class<?> entityClass,
			boolean upsert, boolean multi) {
		this.logger.logUpdateQuery(collectionName, query, update, upsert, multi);

		return super.doUpdate(collectionName, query, update, entityClass, upsert, multi);
	}
	protected <T> WriteResult doRemove(final String collectionName, final Query query, final Class<T> entityClass) {
		this.logger.logDeleteQuery(collectionName, query);
		return super.doRemove(collectionName, query, (Class) entityClass);
	}
}
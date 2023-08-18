package com.efuture.omd.storage;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

public interface FStorageOperations {
  void destroy();
  
  <T> T selectOne(Query paramQuery, Class<T> paramClass);
  
  <T> T selectOne(Query paramQuery, Class<T> paramClass, String paramString);
  
  Map<String, Object> selectOne(Query paramQuery, String paramString);
  
  <T> List<T> select(Query paramQuery, Class<T> paramClass);
  
  <T> List<T> select(Query paramQuery, Class<T> paramClass, String paramString);
  
  List<Map<String, Object>> select(Query paramQuery, String paramString);
  
  long count(Query paramQuery, Class<?> paramClass);
  
  long count(Query paramQuery, String paramString);
  
  void insert(Object paramObject);
  
  void insert(Object paramObject, String paramString);
  
  void insert(Collection<? extends Object> paramCollection, Class<?> paramClass);
  
  void insert(Collection<? extends Object> paramCollection, String paramString);
  
  void insertAll(Collection<? extends Object> paramCollection);
  
  int updateOrInsert(Query paramQuery, Update paramUpdate, Class<?> paramClass);
  
  int updateOrInsert(Query paramQuery, Update paramUpdate, String paramString);
  
  int updateOrInsert(Query paramQuery, Update paramUpdate, Class<?> paramClass, String paramString);
  
  int update(Query paramQuery, Update paramUpdate, Class<?> paramClass);
  
  int update(Query paramQuery, Update paramUpdate, String paramString);
  
  int update(Query paramQuery, Update paramUpdate, Class<?> paramClass, String paramString);
  
  int delete(Object paramObject);
  
  int delete(Object paramObject, String paramString);
  
  int delete(Query paramQuery, Class<?> paramClass);
  
  int delete(Query paramQuery, Class<?> paramClass, String paramString);
  
  int delete(Query paramQuery, String paramString);
}


/* Location:              D:\omd-storage-1.1.1.jar!\com\efuture\omd\storage\FStorageOperations.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.0.6
 */
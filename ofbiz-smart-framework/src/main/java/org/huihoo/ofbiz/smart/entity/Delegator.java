package org.huihoo.ofbiz.smart.entity;

import org.huihoo.ofbiz.smart.base.cache.Cache;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;



/**
 * <p>
 * 数据库委托代理访问接口
 * </p>
 * 
 * @author huangbaihua
 *
 */
public interface Delegator {
  
  Delegator useDataSource(String name);
  Cache getCache();
  //======================================================================================
  //                                 Transaction Operation
  // =====================================================================================
  void beginTransaction();
  void rollback();
  void commitTransaction();
  void endTransaction();
  void executeWithInTx(TxRunnable txRunnable);
  Object executeWithInTx(TxCallable txCallable);
  //======================================================================================
  //                                 Entity Save,Update,Remove
  // =====================================================================================
  void save(Collection<?> entities) throws GenericEntityException;  
  void save(Object entity) throws GenericEntityException;
  void update(Collection<?> entities) throws GenericEntityException; 
  void update(Object entity) throws GenericEntityException;
  void remove(Collection<?> entities) throws GenericEntityException; 
  void remove(Object entity) throws GenericEntityException;
  void removeById(Class<?> entityClazz,Object id) throws GenericEntityException;
  
  // =====================================================================================
  //                                 Entity Find Method
  // =====================================================================================
  Object findById(Class<?> entityClazz, Object id) throws GenericEntityException;
  Object findById(Class<?> entityClazz, Object id, boolean useCache) throws GenericEntityException;
  
  List<Object> findIdsByAnd(Class<?> entityClazz, Map<String, Object> andMap) throws GenericEntityException;
  List<Object> findIdsByCond(Class<?> entityClazz, String cond) throws GenericEntityException;
    
  Object findUniqueByAnd(Class<?> entityClazz, Map<String, Object> andMap) throws GenericEntityException;
  Object findUniqueByAnd(Class<?> entityClazz, Map<String, Object> andMap, Set<String> fieldsToSelect,boolean useCache) throws GenericEntityException;

  List<?> findList(Class<?> entityClazz, String cond) throws GenericEntityException;
  List<?> findList(Class<?> entityClazz, String cond,Set<String> fieldsToSelect,List<String> orderBy) throws GenericEntityException;
  List<?> findList(Class<?> entityClazz, String cond,Set<String> fieldsToSelect,List<String> orderBy,boolean useCache) throws GenericEntityException;
  
  Map<String,Object> findPageByAnd(Class<?> entityClazz, Map<String, Object> andMap,int pageNo,int pageSize) throws GenericEntityException;
  Map<String,Object> findPageByAnd(Class<?> entityClazz, Map<String, Object> andMap,int pageNo,int pageSize,Set<String> fieldsToSelect,List<String> orderBy) throws GenericEntityException;
  Map<String,Object> findPageByAnd(Class<?> entityClazz, Map<String, Object> andMap,int pageNo,int pageSize,Set<String> fieldsToSelect,List<String> orderBy,boolean useCache) throws GenericEntityException;

  Map<String,Object> findPageByCond(Class<?> entityClazz, String cond,int pageNo,int pageSize) throws GenericEntityException;
  Map<String,Object> findPageByCond(Class<?> entityClazz, String cond,int pageNo,int pageSize,Set<String> fieldsToSelect,List<String> orderBy) throws GenericEntityException;
  Map<String,Object> findPageByCond(Class<?> entityClazz, String cond,int pageNo,int pageSize,Set<String> fieldsToSelect,List<String> orderBy,boolean useCache) throws GenericEntityException;
    
  int countByAnd(Class<?> entityClazz, Map<String, Object> andMap) throws GenericEntityException;
  int countByCond(Class<?> entityClazz, String cond) throws GenericEntityException;
  
  //======================================================================================
  //                                 Raw Query And Operation
  // =====================================================================================
  List<Map<String, Object>> findListByRawQuery(String query, List<?> params) throws GenericEntityException;
  List<Map<String, Object>> findListByRawQuery(String query, List<?> params,boolean useCache) throws GenericEntityException;
  int executeByRawSql(String sql) throws GenericEntityException;
  int executeByRawSql(String sql,List<?> params) throws GenericEntityException;
  int countByRawQuery(String query,String countAlias) throws GenericEntityException;
  int countByRawQuery(String query,String countAlias,List<?> params) throws GenericEntityException;
}
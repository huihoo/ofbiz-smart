package org.huihoo.ofbiz.smart.entity;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EbeanDelegator implements Delegator {

  @Override
  public Object useDataSource(String name) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void beginTransaction() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void rollback() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void commitTransaction() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void endTransaction() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void save(Collection<?> entities) throws GenericEntityException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void save(Object entity) throws GenericEntityException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void update(Collection<?> entities) throws GenericEntityException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void update(Object entity) throws GenericEntityException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void remove(Collection<?> entities) throws GenericEntityException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void remove(Object entity) throws GenericEntityException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void removeById(Object id) throws GenericEntityException {
    // TODO Auto-generated method stub
    
  }

  @Override
  public Object findById(Class<?> entityClazz, Object id) throws GenericEntityException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object findById(Class<?> entityClazz, Object id, boolean useCache)
          throws GenericEntityException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Object> findIdsByAnd(Class<?> entityClazz, Map<String, Object> andMap)
          throws GenericEntityException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<Object> findIdsByCond(Class<?> entityClazz, String cond)
          throws GenericEntityException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object findUniqueByAnd(Class<?> entityClazz, Map<String, Object> andMap)
          throws GenericEntityException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Object findUniqueByAnd(Class<?> entityClazz, Map<String, Object> andMap, boolean useCache)
          throws GenericEntityException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<?> findList(Class<?> entityClazz, String cond) throws GenericEntityException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<?> findList(Class<?> entityClazz, String cond, Set<String> fieldsToSelect,
          List<String> orderBy) throws GenericEntityException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<?> findList(Class<?> entityClazz, String cond, Set<String> fieldsToSelect,
          List<String> orderBy, boolean useCache) throws GenericEntityException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<?> findPageByAnd(Class<?> entityClazz, Map<String, Object> andMap, int pageNo,
          int pageSize) throws GenericEntityException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<?> findPageByAnd(Class<?> entityClazz, Map<String, Object> andMap, int pageNo,
          int pageSize, Set<String> fieldsToSelect, List<String> orderBy)
                  throws GenericEntityException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<?> findPageByAnd(Class<?> entityClazz, Map<String, Object> andMap, int pageNo,
          int pageSize, Set<String> fieldsToSelect, List<String> orderBy, boolean useCache)
                  throws GenericEntityException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<?> findPageByCond(Class<?> entityClazz, String cond, int pageNo, int pageSize)
          throws GenericEntityException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<?> findPageByCond(Class<?> entityClazz, String cond, int pageNo, int pageSize,
          Set<String> fieldsToSelect, List<String> orderBy) throws GenericEntityException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<?> findPageByCond(Class<?> entityClazz, String cond, int pageNo, int pageSize,
          Set<String> fieldsToSelect, List<String> orderBy, boolean useCache)
                  throws GenericEntityException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int countByAnd(Class<?> entityClazz, Map<String, Object> andMap) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int countByCond(Class<?> entityClazz, String cond) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public List<Map<String, Object>> findListByRawQuery(String query, List<?> params)
          throws GenericEntityException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int executeByRawSql(String sql) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int executeByRawSql(String sql, List<?> params) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int countByRawQuery(String query, String countAlias) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int countByRawQuery(String query, String countAlias, List<?> params) {
    // TODO Auto-generated method stub
    return 0;
  }

  

}

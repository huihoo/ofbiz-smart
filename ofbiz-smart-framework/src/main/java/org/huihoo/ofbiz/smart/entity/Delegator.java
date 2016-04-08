package org.huihoo.ofbiz.smart.entity;



import java.sql.Connection;
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
 * @since 1.0
 */
public interface Delegator {
  /**
   * <p>
   * 获取原始的数据库连接
   * </p>
   * 
   * @return
   */
  Connection getConnection() throws GenericEntityException;

  /**
   * <p>
   * 使用指定的数据源,实现方应该可以通过指定的名称，切换到该数据源下。以支持多数据源特性。
   * </p>
   * 
   * @param name 要使用的数据源名称
   * @return <code>{@link Delegator}</code>本身，返回自己的目的是可以实现接口的链式访问。
   */
  Delegator useDataSource(String name);


  // ======================================================================================
  // Transaction Operation
  // =====================================================================================

  /**
   * <p>
   * 开启一个事务
   * </p>
   */
  void beginTransaction();

  /**
   * <p>
   * 回滚一个事务
   * </p>
   */
  void rollback();

  /**
   * <p>
   * 提交一个事务
   * </p>
   */
  void commitTransaction();

  /**
   * <p>
   * 结束一个事务
   * </p>
   */
  void endTransaction();

  /**
   *
   * @param txRunnable
   */
  void executeWithInTx(TxRunnable txRunnable);

  /**
   *
   * @param txCallable
   * @return
   */
  Object executeWithInTx(TxCallable txCallable);

  // ======================================================================================
  // Entity Save,Update,Remove
  // =====================================================================================
  void save(Collection<?> entities) throws GenericEntityException;

  void save(Object entity) throws GenericEntityException;

  void update(Collection<?> entities) throws GenericEntityException;

  void update(Object entity) throws GenericEntityException;

  void remove(Collection<?> entities) throws GenericEntityException;

  void remove(Object entity) throws GenericEntityException;

  void removeById(Class<?> entityClazz, Object id) throws GenericEntityException;

  // =====================================================================================
  // Entity Find Method
  // =====================================================================================
  Object findById(Class<?> entityClazz, Object id) throws GenericEntityException;

  Object findById(Class<?> entityClazz, Object id, boolean useCache) throws GenericEntityException;

  Object findById(Class<?> entityClazz, Object id, boolean useCache, 
                                                   int liveTimeInSeconds) throws GenericEntityException;

  List<Object> findIdsByAnd(Class<?> entityClazz, Map<String, Object> andMap) throws GenericEntityException;

  List<Object> findIdsByCond(Class<?> entityClazz, String cond) throws GenericEntityException;
  
  //=======================================================================================
  // Find Entity List By And Map Condition.
  //=======================================================================================
  /**
   * 根据条件Map查找实体集合列表
   * @param entityClazz   要查找的实体
   * @param andMap        条件Map映射 
   * @return              <code>List</code>
   * @throws GenericEntityException
   */
  List<?> findListByAnd(Class<?> entityClazz, Map<String, Object> andMap) throws GenericEntityException;
  
  /**
   * 根据条件Map查找实体集合列表，并按指定的字段进行排序
   * @param entityClazz   要查找的实体
   * @param andMap        条件Map映射
   * @param orderBy       要排序的字段(组合为字段 + asc 或  desc)<code>List</code>集合
   * @return              <code>List</code>
   * @throws GenericEntityException
   */
  List<?> findListByAnd(Class<?> entityClazz, Map<String, Object> andMap, 
                                              List<String> orderBy) throws GenericEntityException;
  
  /**
   * 根据条件Map查找实体集合列表，返回实体的指定字段，并按指定的字段进行排序
   * @param entityClazz    要查找的实体
   * @param andMap         条件Map映射
   * @param fieldsToSelect 要返回的实体字段集合
   * @param orderBy        要排序的字段(组合为字段 + asc 或  desc)<code>List</code>集合
   * @return              <code>List</code>
   * @throws GenericEntityException
   */
  List<?> findListByAnd(Class<?> entityClazz, Map<String, Object> andMap, 
                                              Set<String> fieldsToSelect, 
                                              List<String> orderBy) throws GenericEntityException;
  
  /**
   * @see #findListByAnd(Class, Map, Set, List, int)
   */
  @Deprecated
  List<?> findListByAnd(Class<?> entityClazz, Map<String, Object> andMap,
                                              Set<String> fieldsToSelect, 
                                              List<String> orderBy, 
                                              boolean useCache) throws GenericEntityException;
  /**
   * @see #findListByAnd(Class, Map, Set, List, int)
   */
  @Deprecated
  List<?> findListByAnd(Class<?> entityClazz, Map<String, Object> andMap,
                                              Set<String> fieldsToSelect, 
                                              List<String> orderBy, 
                                              boolean useCache, 
                                              int liveTimeInSeconds) throws GenericEntityException;
  
  /**
   * 根据条件Map查找实体集合列表，返回实体的指定字段，并按指定的字段进行排序，并按指定时间进行缓存
   * @param entityClazz    要查找的实体
   * @param andMap         条件Map映射
   * @param fieldsToSelect 要返回的实体字段集合
   * @param orderBy        要排序的字段(组合为字段 + asc 或  desc)<code>List</code>集合
   * @param liveTimeInSeconds 要缓存的时间，以秒为单位。其中-1表示不缓存，0表示总是缓存, 大于0为实际的缓存时间.
   * @return              <code>List</code>
   * @throws GenericEntityException
   */
  List<?> findListByAnd(Class<?> entityClazz, Map<String, Object> andMap,
                                              Set<String> fieldsToSelect, 
                                              List<String> orderBy, 
                                              int liveTimeInSeconds) throws GenericEntityException;
  
  
  Object findUniqueByAnd(Class<?> entityClazz, Map<String, Object> andMap) throws GenericEntityException;

  Object findUniqueByAnd(Class<?> entityClazz, Map<String, Object> andMap,
                                               Set<String> fieldsToSelect, 
                                               boolean useCache) throws GenericEntityException;

  Object findUniqueByAnd(Class<?> entityClazz, Map<String, Object> andMap,
                                               Set<String> fieldsToSelect, 
                                               boolean useCache, 
                                               int liveTimeInSeconds) throws GenericEntityException;

  List<?> findListByCond(Class<?> entityClazz, String cond) throws GenericEntityException;

  List<?> findListByCond(Class<?> entityClazz, String cond, 
                                               Set<String> fieldsToSelect,
                                               List<String> orderBy) throws GenericEntityException;

  List<?> findListByCond(Class<?> entityClazz, String cond, 
                                               Set<String> fieldsToSelect,
                                               List<String> orderBy, 
                                               boolean useCache) throws GenericEntityException;

  List<?> findListByCond(Class<?> entityClazz, String cond, 
                                               Set<String> fieldsToSelect,
                                               List<String> orderBy, 
                                               boolean useCache, 
                                               int liveTimeInSeconds) throws GenericEntityException;

  Map<String, Object> findPageByAnd(Class<?> entityClazz, Map<String, Object> andMap, 
                                                          int pageNo,
                                                          int pageSize) throws GenericEntityException;

  Map<String, Object> findPageByAnd(Class<?> entityClazz, Map<String, Object> andMap, 
                                                          int pageNo,
                                                          int pageSize, 
                                                          Set<String> fieldsToSelect, 
                                                          List<String> orderBy) throws GenericEntityException;

  Map<String, Object> findPageByAnd(Class<?> entityClazz, Map<String, Object> andMap, 
                                                          int pageNo,
                                                          int pageSize, 
                                                          Set<String> fieldsToSelect, 
                                                          List<String> orderBy, 
                                                          boolean useCache) throws GenericEntityException;

  Map<String, Object> findPageByAnd(Class<?> entityClazz, Map<String, Object> andMap, 
                                                          int pageNo,
                                                          int pageSize, 
                                                          Set<String> fieldsToSelect, 
                                                          List<String> orderBy, 
                                                          boolean useCache,
                                                          int liveTimeInSeconds) throws GenericEntityException;

  Map<String, Object> findPageByCond(Class<?> entityClazz, String cond, 
                                                           int pageNo, 
                                                           int pageSize) throws GenericEntityException;

  Map<String, Object> findPageByCond(Class<?> entityClazz, String cond, 
                                                           int pageNo, 
                                                           int pageSize,
                                                           Set<String> fieldsToSelect, 
                                                           List<String> orderBy) throws GenericEntityException;

  Map<String, Object> findPageByCond(Class<?> entityClazz, String cond, 
                                                           int pageNo, 
                                                           int pageSize,
                                                           Set<String> fieldsToSelect, 
                                                           List<String> orderBy, 
                                                           boolean useCache) throws GenericEntityException;

  Map<String, Object> findPageByCond(Class<?> entityClazz, String cond, 
                                                           int pageNo, 
                                                           int pageSize,
                                                           Set<String> fieldsToSelect, 
                                                           List<String> orderBy, 
                                                           boolean useCache, 
                                                           int liveTimeInSeconds) throws GenericEntityException;

  int countByAnd(Class<?> entityClazz, Map<String, Object> andMap) throws GenericEntityException;

  int countByCond(Class<?> entityClazz, String cond) throws GenericEntityException;

  // ======================================================================================
  // Raw Query And Operation
  // =====================================================================================
  Map<String, Object> findUniqueByRawQuery(String query, List<?> params) throws GenericEntityException;
  Map<String, Object> findUniqueByRawQuery(String query, List<?> params,int liveTimeInSeconds) throws GenericEntityException;
  
  List<Map<String, Object>> findListByRawQuery(String query, List<?> params) throws GenericEntityException;

  List<Map<String, Object>> findListByRawQuery(String query, List<?> params, 
                                                             boolean useCache) throws GenericEntityException;

  int executeByRawSql(String sql) throws GenericEntityException;

  int executeByRawSql(String sql, List<?> params) throws GenericEntityException;

  int countByRawQuery(String query, String countAlias) throws GenericEntityException;

  int countByRawQuery(String query, String countAlias, List<?> params) throws GenericEntityException;
  
  
  void loadSeedData(String seedDataSqlCvs) throws GenericEntityException;
  
  void loadSeedData(String seedDataSqlCvs,boolean executeAnyway) throws GenericEntityException;
}

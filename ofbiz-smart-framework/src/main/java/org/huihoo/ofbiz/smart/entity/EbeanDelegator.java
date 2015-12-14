package org.huihoo.ofbiz.smart.entity;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.avaje.ebean.*;
import com.avaje.ebean.text.PathProperties;
import org.huihoo.ofbiz.smart.base.C;
import org.huihoo.ofbiz.smart.base.cache.Cache;
import org.huihoo.ofbiz.smart.base.cache.LocalConcurrentLRUCache;
import org.huihoo.ofbiz.smart.base.location.FlexibleLocation;
import org.huihoo.ofbiz.smart.base.util.CommUtil;
import org.huihoo.ofbiz.smart.base.util.Log;

import com.avaje.ebean.config.ServerConfig;

public class EbeanDelegator implements Delegator {
  private final String TAG = EbeanDelegator.class.getName();
  private volatile Cache<String,Object> CACHE;
  /** 当前数据源服务器键名 */
  private final String CURRENT_SERVER_NAME = "_current_server_" ;
  /** 默认数据源名称 */
  private final String defaultDataSourceName;
  /** 当前数据源服务器缓存<code>Map</code>*/
  private final ConcurrentHashMap<String, EbeanServer> currentServerMap = new ConcurrentHashMap<String, EbeanServer>();
  /** 所有可用数据源服务器缓存<code>Map</code>*/
  private final ConcurrentHashMap<String, EbeanServer> concMap = new ConcurrentHashMap<String, EbeanServer>();
  
  public EbeanDelegator() throws GenericEntityException {
    Properties applicationProps = new Properties();
    try {
      applicationProps.load(FlexibleLocation.resolveLocation(C.APPLICATION_CONFIG_NAME).openStream());
    } catch (IOException e) {
      throw new GenericEntityException("Unable to load external properties");
    }
    String dbCacheProvider = applicationProps.getProperty(C.CONFIG_DATASOURCE_CACHE_PROVIDER);
    if (CommUtil.isNotEmpty(dbCacheProvider)) {
      try {
        CACHE = (Cache) Class.forName(dbCacheProvider).newInstance();
      } catch (InstantiationException e) {
        throw new GenericEntityException("Unable to init database cache provider [" + dbCacheProvider + "]");
      } catch (IllegalAccessException e) {
        throw new GenericEntityException("Unable to init database cache provider [" + dbCacheProvider + "]");
      } catch (ClassNotFoundException e) {
        throw new GenericEntityException("Class [" + dbCacheProvider + "] not found.");
      }
    } else {
      CACHE = new LocalConcurrentLRUCache<>(128, 32);
    }
    defaultDataSourceName = applicationProps.getProperty(C.CONFIG_DATASOURCE_DEFAULT);
    
    Set<String> dsNames = new LinkedHashSet<>();
    Enumeration<?> keys = applicationProps.keys();
    while(keys.hasMoreElements()) {
      String k = (String) keys.nextElement();
      if (k.startsWith(C.CONFIG_DATASOURCE) && k.indexOf(C.CONFIG_DATASOURCE_USERNAME) >= 0) {
        dsNames.add(k.substring(C.CONFIG_DATASOURCE.length() + 1, k.indexOf("." + C.CONFIG_DATASOURCE_USERNAME)));
      }
    }
    Log.d("Default datasouce[%s], Avaliable datasources[%s]", TAG, defaultDataSourceName,dsNames);
    for (String dsName : dsNames) {
      ServerConfig config = new ServerConfig();
      config.setName(dsName);
      config.loadFromProperties(applicationProps);
      EbeanServer ebeanServer = EbeanServerFactory.create(config);
      concMap.put(dsName, ebeanServer);
      
      if (dsName.equals(defaultDataSourceName)) {
        currentServerMap.put(CURRENT_SERVER_NAME, ebeanServer);
      }
    }
  }
  
  @Override
  public EbeanDelegator useDataSource(String name) {
    if (name == null) {
      name = defaultDataSourceName;
    }
    currentServerMap.put(CURRENT_SERVER_NAME, concMap.get(name));
    return this;
  }

  @Override
  public Cache getCache() {
    return CACHE;
  }

  @Override
  public void beginTransaction() {
    currentServerMap.get(CURRENT_SERVER_NAME).beginTransaction();
  }

  @Override
  public void rollback() {
    currentServerMap.get(CURRENT_SERVER_NAME).rollbackTransaction();
  }

  @Override
  public void commitTransaction() {
    currentServerMap.get(CURRENT_SERVER_NAME).commitTransaction();
  }

  @Override
  public void endTransaction() {
    currentServerMap.get(CURRENT_SERVER_NAME).endTransaction();
  }

  @Override
  public void save(Collection<?> entities) throws GenericEntityException {
    try {
      currentServerMap.get(CURRENT_SERVER_NAME).save(entities);
    } catch(Exception e) {
      Log.e(e, "EbeanDeletagor.save() occurs an exception.", TAG);
      throw new GenericEntityException(e);
    }
  }

  @Override
  public void save(Object entity) throws GenericEntityException {
    try {
      currentServerMap.get(CURRENT_SERVER_NAME).save(entity);
    } catch(Exception e) {
      Log.e(e, "EbeanDeletagor.save() occurs an exception.", TAG);
      throw new GenericEntityException(e);
    }
  }

  @Override
  public void update(Collection<?> entities) throws GenericEntityException {
    try {
      currentServerMap.get(CURRENT_SERVER_NAME).update(entities);
    } catch(Exception e) {
      Log.e(e, "EbeanDeletagor.update() occurs an exception.", TAG);
      throw new GenericEntityException(e);
    }
  }

  @Override
  public void update(Object entity) throws GenericEntityException {
    try {
      currentServerMap.get(CURRENT_SERVER_NAME).update(entity);
    } catch(Exception e) {
      Log.e(e, "EbeanDeletagor.update() occurs an exception.", TAG);
      throw new GenericEntityException(e);
    }
  }

  @Override
  public void remove(Collection<?> entities) throws GenericEntityException {
    try {
      currentServerMap.get(CURRENT_SERVER_NAME).deleteAll(entities);
    } catch(Exception e) {
      Log.e(e, "EbeanDeletagor.remove() occurs an exception.", TAG);
      throw new GenericEntityException(e);
    }
  }

  @Override
  public void remove(Object entity) throws GenericEntityException {
    try {
      currentServerMap.get(CURRENT_SERVER_NAME).delete(entity);
    } catch(Exception e) {
      Log.e(e, "EbeanDeletagor.remove() occurs an exception.", TAG);
      throw new GenericEntityException(e);
    }
  }

  @Override
  public void removeById(Class<?> entityClazz,Object id) throws GenericEntityException {
    try {
      currentServerMap.get(CURRENT_SERVER_NAME).delete(entityClazz,id);
    } catch(Exception e) {
      Log.e(e, "EbeanDeletagor.removeById() occurs an exception.", TAG);
      throw new GenericEntityException(e);
    }
  }

  @Override
  public Object findById(Class<?> entityClazz, Object id) throws GenericEntityException {
    return findById(entityClazz,id,false);
  }

  @Override
  public Object findById(Class<?> entityClazz, Object id, boolean useCache)
          throws GenericEntityException {
    try {
      if (useCache) {
        String cacheKey = entityClazz + "#" + id;
        Object cachedObj = CACHE.get(cacheKey);
        if (cachedObj != null) {
          Log.d("findById[" + cacheKey + "] from cache.",TAG);
          return cachedObj;
        }
        Object fromDbObj = currentServerMap.get(CURRENT_SERVER_NAME).find(entityClazz,id);
        if (fromDbObj != null) {
          CACHE.put(cacheKey,fromDbObj);
        }
        return fromDbObj;

      } else {
        return currentServerMap.get(CURRENT_SERVER_NAME).find(entityClazz,id);
      }
    } catch(Exception e) {
      Log.e(e, "EbeanDeletagor.findById() occurs an exception.", TAG);
      throw new GenericEntityException(e);
    }
  }

  @Override
  public List<Object> findIdsByAnd(Class<?> entityClazz, Map<String, Object> andMap)
          throws GenericEntityException {
    try {
      Query<?> query = currentServerMap.get(CURRENT_SERVER_NAME).find(entityClazz);
      ExpressionList<?> expList = query.where();
      if (CommUtil.isNotEmpty(andMap)) {
        expList.allEq(andMap);
      }
      return expList.findIds();
    } catch (Exception e) {
      Log.e(e, "EbeanDeletagor.findIdsByAnd() occurs an exception.", TAG);
      throw new GenericEntityException(e);
    }
  }

  @Override
  public List<Object> findIdsByCond(Class<?> entityClazz, String cond)
          throws GenericEntityException {
    try {
      Query<?> query = currentServerMap.get(CURRENT_SERVER_NAME).find(entityClazz);
      ExpressionList<?> expList = query.where();
      buildQueryExpression(cond,expList);
      return expList.findIds();
    } catch (Exception e) {
      Log.e(e, "EbeanDeletagor.findIdsByCond() occurs an exception.", TAG);
      throw new GenericEntityException(e);
    }
  }

  @Override
  public Object findUniqueByAnd(Class<?> entityClazz, Map<String, Object> andMap)
          throws GenericEntityException {
    return findUniqueByAnd(entityClazz,andMap,new HashSet<String>(),false);
  }

  @Override
  public Object findUniqueByAnd(Class<?> entityClazz, Map<String, Object> andMap, Set<String> fieldsToSelect,boolean useCache)
          throws GenericEntityException {
    try {
      if (CommUtil.isEmpty(andMap)) {
        return null;
      }

      String cacheKey = entityClazz + "#unique#" + andMap.hashCode();
      if (useCache) {
        Object cachedObj = CACHE.get(cacheKey);
        if (cachedObj != null) {
          Log.d("findUniqueByAnd[" + cacheKey + "]from cache.",TAG);
          return cachedObj;
        }
      }

      Query<?> query = currentServerMap.get(CURRENT_SERVER_NAME).find(entityClazz);
      buildSelectFields(query,fieldsToSelect);
      ExpressionList<?> expList = query.where();
      expList.allEq(andMap);

      Object fromDbObj = expList.findUnique();
      if (useCache && fromDbObj != null) {
        CACHE.put(cacheKey, fromDbObj);
      }
      return fromDbObj;
    } catch(Exception e) {
      Log.e(e, "EbeanDeletagor.findUniqueByAnd() occurs an exception.", TAG);
      throw new GenericEntityException(e);
    }
  }

  @Override
  public List<?> findList(Class<?> entityClazz, String cond) throws GenericEntityException {
    return findList(entityClazz,cond,new HashSet<String>(),new ArrayList<String>(),false);
  }

  @Override
  public List<?> findList(Class<?> entityClazz, String cond, Set<String> fieldsToSelect,
          List<String> orderBy) throws GenericEntityException {
    return findList(entityClazz,cond,fieldsToSelect,orderBy,false);
  }

  @Override
  public List<?> findList(Class<?> entityClazz, String cond, Set<String> fieldsToSelect,
          List<String> orderBy, boolean useCache) throws GenericEntityException {
    try {
      String cacheKey = entityClazz + "#" + cond + "#" + fieldsToSelect + "#" + orderBy;
      if (useCache) {
        List<?> cachedList = (List<?>) CACHE.get(cacheKey);
        if (cachedList != null) {
          Log.d("findList[" + cacheKey + "]from cache.",TAG);
          return cachedList;
        }
      }
      Query<?> query = currentServerMap.get(CURRENT_SERVER_NAME).find(entityClazz);
      buildSelectFields(query,fieldsToSelect);
      ExpressionList<?> expList = query.where();
      buildExpressList(expList, cond, orderBy);
      List<?> objList = expList.findList();
      if (useCache && objList != null) {
        CACHE.put(cacheKey,objList);
      }
      return objList;
    } catch(Exception e) {
      Log.e(e, "EbeanDeletagor.findList() occurs an exception.", TAG);
      throw new GenericEntityException(e);
    }
  }

  @Override
  public Map<String,Object> findPageByAnd(Class<?> entityClazz, Map<String, Object> andMap, int pageNo,
          int pageSize) throws GenericEntityException {
    return findPageByAnd(entityClazz,andMap,pageNo,pageSize,new HashSet<String>(),new ArrayList<String>(),false);
  }

  @Override
  public Map<String,Object> findPageByAnd(Class<?> entityClazz, Map<String, Object> andMap, int pageNo,
          int pageSize, Set<String> fieldsToSelect, List<String> orderBy)
                  throws GenericEntityException {
    return findPageByAnd(entityClazz,andMap,pageNo,pageSize,fieldsToSelect,orderBy,false);
  }

  @Override
  public Map<String,Object> findPageByAnd(Class<?> entityClazz, Map<String, Object> andMap, int pageNo,
          int pageSize, Set<String> fieldsToSelect, List<String> orderBy, boolean useCache)
                  throws GenericEntityException {
    return doPagination(entityClazz,andMap,null,pageNo,pageSize,fieldsToSelect,orderBy,useCache);
  }

  @Override
  public Map<String,Object> findPageByCond(Class<?> entityClazz, String cond, int pageNo, int pageSize)
          throws GenericEntityException {

    return findPageByCond(entityClazz,cond,pageNo,pageSize,new HashSet<String>(),new ArrayList<String>(),false);
  }

  @Override
  public Map<String,Object> findPageByCond(Class<?> entityClazz, String cond, int pageNo, int pageSize,
          Set<String> fieldsToSelect, List<String> orderBy) throws GenericEntityException {

    return findPageByCond(entityClazz,cond,pageNo,pageSize,fieldsToSelect,orderBy,false);
  }

  @Override
  public Map<String,Object> findPageByCond(Class<?> entityClazz, String cond, int pageNo, int pageSize,
          Set<String> fieldsToSelect, List<String> orderBy, boolean useCache)
                  throws GenericEntityException {
    return doPagination(entityClazz,null,cond,pageNo,pageSize,fieldsToSelect,orderBy,useCache);
  }

  /**
   *
   * <p>
   *     分页方法
   * </p>
   * @param entityClazz
   * @param andMap
   * @param cond
   * @param pageNo
   * @param pageSize
   * @param fieldsToSelect
   * @param orderBy
   * @param useCache
   * @return
   * @throws GenericEntityException
   */
  private Map<String,Object> doPagination(Class<?> entityClazz, Map<String,Object> andMap,String cond, int pageNo, int pageSize,
                                          Set<String> fieldsToSelect, List<String> orderBy, boolean useCache)
                                                  throws GenericEntityException{
    String findName = andMap == null ? "findPageByCond" : "findPageByAnd";
    try {
      String cacheKey = entityClazz + "#" + (andMap == null ? cond : andMap)  + "#" + pageNo + "#" +pageSize + "#" + fieldsToSelect + "#" + orderBy;
      if (useCache) {
        Map<String,Object> cachedObj = (Map<String, Object>) CACHE.get(cacheKey);
        if (cachedObj != null) {
          Log.d(findName + "[" + cacheKey + "]from cache.",TAG);
          return cachedObj;
        }
      }

      Map<String, Object> result = new LinkedHashMap<>();
      Query<?> query = currentServerMap.get(CURRENT_SERVER_NAME).find(entityClazz);
      buildSelectFields(query, fieldsToSelect);
      ExpressionList<?> expList = query.where();
      if (CommUtil.isNotEmpty(andMap)) {
        buildExpressList(expList, andMap, orderBy);
      } else {
        buildExpressList(expList, cond, orderBy);
      }
      PagedList<?> pageList = expList.findPagedList(pageNo - 1, pageSize);
      int totalPage = pageList.getTotalPageCount();
      int totalEntry = pageList.getTotalRowCount();
      List<?> list = pageList.getList();

      result.put(C.PAGE_TOTAL_PAGE, totalPage);
      result.put(C.PAGE_TOTAL_ENTRY, totalEntry);
      result.put(C.PAGE_LIST, list);
      result.put(C.PAGE_PAGE_SIZE, pageSize);
      result.put(C.PAGE_PAGE_NO, pageNo);

      if (totalEntry > 0 && useCache) {
        Log.d(findName + "[" + cacheKey + "]from cache.",TAG);
        CACHE.put(cacheKey,result);
      }

      return result;
    } catch(Exception e) {
      Log.e(e, "EbeanDeletagor."+ findName + "() occurs an exception.", TAG);
      throw new GenericEntityException(e);
    }
  }

  @Override
  public int countByAnd(Class<?> entityClazz, Map<String, Object> andMap) throws GenericEntityException{
    try {
      return currentServerMap.get(CURRENT_SERVER_NAME).find(entityClazz).where().allEq(andMap).findRowCount();
    } catch (Exception e) {
      Log.e(e, "EbeanDeletagor.countByAnd() occurs an exception.", TAG);
      throw new GenericEntityException(e);
    }
  }

  @Override
  public int countByCond(Class<?> entityClazz, String cond) throws GenericEntityException{
    try {
      Query<?> query = currentServerMap.get(CURRENT_SERVER_NAME).find(entityClazz);
      ExpressionList<?> expList = query.where();
      buildExpressList(expList, cond, null);
      return expList.findRowCount();
    } catch (Exception e) {
      Log.e(e, "EbeanDeletagor.countByCond() occurs an exception.", TAG);
      throw new GenericEntityException(e);
    }
  }

  @Override
  public List<Map<String, Object>> findListByRawQuery(String query, List<?> params)
          throws GenericEntityException {
    return findListByRawQuery(query,params,false);
  }

  @Override
  public List<Map<String, Object>> findListByRawQuery(String query, List<?> params,boolean useCache)
          throws GenericEntityException {
    try{
      String cacheKey = query + "#" + params;
      if (useCache) {
        List<Map<String,Object>> cachedList = (List<Map<String, Object>>) CACHE.get(cacheKey);
        if (cachedList != null) {
          Log.d("findListByRawQuery[" + cacheKey + "]from cache.",TAG);
          return cachedList;
        }
      }


      List<Map<String, Object>> mapList = new ArrayList<>();
      SqlQuery sq = currentServerMap.get(CURRENT_SERVER_NAME).createSqlQuery(query);
      if (CommUtil.isNotEmpty(params)) {
        for (int i = 0; i < params.size(); i++) {
          sq.setParameter(i + 1, params.get(i));
        }
      }
      List<SqlRow> rows = sq.findList();
      if (rows != null && rows.size() > 0) {
        for (SqlRow r : rows) {
          Map<String, Object> rowMap = new HashMap<>();

          Iterator<String> cKeys = r.keys();
          while (cKeys.hasNext()) {
            String k = cKeys.next();
            rowMap.put(k, r.get(k));
          }
          mapList.add(rowMap);
        }
      }
      if (CommUtil.isNotEmpty(mapList) && useCache) {
        CACHE.put(cacheKey,mapList);
      }
      return mapList;
    } catch(Exception e) {
      Log.e(e, "EbeanDeletagor.findListByRawQuery() occurs an exception.", TAG);
      throw new GenericEntityException(e);
    }
  }

  @Override
  public int executeByRawSql(String sql) throws GenericEntityException{
    return executeByRawSql(sql,new ArrayList<>());
  }

  @Override
  public int executeByRawSql(String sql, List<?> params) throws GenericEntityException{
    try {
      SqlUpdate sqlUpdate = currentServerMap.get(CURRENT_SERVER_NAME).createSqlUpdate(sql);
      if (CommUtil.isNotEmpty(params)) {
        int size = params.size();
        for (int i = 0; i < size; i++) {
          sqlUpdate.setParameter(i + 1, params.get(i));
        }
      }
      return sqlUpdate.execute();
    } catch(Exception e) {
      Log.e(e, "EbeanDeletagor.executeByRawSql() occurs an exception.", TAG);
      throw new GenericEntityException(e);
    }
  }

  @Override
  public int countByRawQuery(String query, String countAlias) throws GenericEntityException{
    return countByRawQuery(query,countAlias,new ArrayList<>());
  }

  @Override
  public int countByRawQuery(String query, String countAlias, List<?> params) throws GenericEntityException{
    try {
      if (query.indexOf(countAlias) == -1) {
        throw new GenericEntityException("The query[" + query + "] has no alias name [" + countAlias + "]");
      }
      SqlQuery sqlQuery = currentServerMap.get(CURRENT_SERVER_NAME).createSqlQuery(query);
      if (CommUtil.isNotEmpty(params)) {
        int size = params.size();
        for (int i = 0; i < size; i++) {
          sqlQuery.setParameter(i + 1, params.get(i));
        }
      }
      return sqlQuery.findUnique().getInteger(countAlias);
    } catch(Exception e) {
      Log.e(e, "EbeanDeletagor.countByRawQuery() occurs an exception.", TAG);
      throw new GenericEntityException(e);
    }
  }

  @Override
  public void executeWithInTx(TxRunnable txRunnable) {
    try {
      beginTransaction();
      txRunnable.run();
    }catch(Exception e){
      Log.e(e, "EbeanDeletagor.executeWithInTx() occurs an exception.", TAG);
      rollback();
    }finally{
      endTransaction();
    }
  }

  @Override
  public Object executeWithInTx(TxCallable txCallable) {
    try {
      beginTransaction();
      return txCallable.call();
    }catch(Exception e){
      Log.e(e, "EbeanDeletagor.executeWithInTx() occurs an exception.", TAG);
      rollback();
      return null;
    }finally{
      endTransaction();
    }
  }

  /**
   * <p>
   *     根据指定的条件字符串，排序字符串集合 构建<code>{@link com.avaje.ebean.Expression}</code>
   * </p>
   * <p>
   *     有关条件字符串的拼接规则，另见{@link #buildQueryExpression(String, ExpressionList)}
   * </p>
   * @param expList      要构建的<code>{@link com.avaje.ebean.Expression}</code>对象
   * @param condition    指定的条件字符串
   * @param orderBy      指定的排序字符串集合<code>List</code>
   * @throws GenericEntityException
   */
  private void buildExpressList(ExpressionList<?> expList, String condition, List<String> orderBy)
          throws GenericEntityException {
    buildQueryExpression(condition, expList);
    buildExprOrderBy(expList,orderBy);
  }

  /**
   * <p>
   *     根据指定的字段映射，排序字符串集合 构建<code>{@link com.avaje.ebean.Expression}</code>
   * </p>
   * @param expList      要构建的<code>{@link com.avaje.ebean.Expression}</code>对象
   * @param andMap       指定的条件字段映射
   * @param orderBy      指定的排序字符串集合<code>List</code>
   * @throws GenericEntityException
   */
  private void buildExpressList(ExpressionList<?> expList, Map<String, Object> andMap, List<String> orderBy) {
    if (CommUtil.isNotEmpty(andMap)) {
      expList.allEq(andMap);
    }
    buildExprOrderBy(expList,orderBy);
  }

  private void buildExprOrderBy(ExpressionList<?> expList,List<String> orderBy) {
    if (CommUtil.isNotEmpty(orderBy)) {
      StringBuffer sb = new StringBuffer();
      for (String order : orderBy) {
        sb.append(order).append(",");
      }
      expList.orderBy(sb.substring(0, sb.length() - 1));
    }
  }

  /**
   * <p>
   *    以指定的条件字符串为基础，构建<code>{@link com.avaje.ebean.ExpressionList}</code>查询条件表达式对象。
   * </p>
   * <p>
   *     条件表达式格式为: {fieldName,expr,condValue}{n+1}
   *     <br/>
   *     比如： 查找 status(状态)等于'active' 且 gender(性别)等于'M' 且 mobile(手机号) 包含 5227 的用户<br/>
   *     则条件字符串为： {status,eq,active}{gender,eq,M}{mobile,like,5227}
   *
   *
   * </p>
   * <p>几种特殊性况</p>
   * <ul>
   *  <li>OR : {fieldName,expr,condValue,or,fieldName,expr,condValue} 中间的or直接分隔了左右两边的条件，比如{gender,eq,M,or,age,le,25} 性别等于'M' 或 年龄小于等于 25</li>
   *  <li>Between : {age,between,25#28} 年龄在 25到28 岁之间 。注意 条件值的拼接格式为 fromValue#toValue </li>
   *  <li>In,NotIn : {id,in,1#2#3#4#5} ID在1,2,3,4,5中间 {id,notIn,4#5} ID不在4,5中间。注意多个值以#号分割 </li>
   *  <li>IsNull,IsNutNull : {birthday,isNull,anyValue} {birthday,isNotNull,anyValue} 注意anyValue表示任意值，仅起到占位的作用，本身没有意义。</li>
   * </ul>
   * @param condition  指定的条件字符串
   * @param exp        要构建的<code>{@link com.avaje.ebean.ExpressionList}</code>查询条件表达式对象
   * @throws GenericEntityException
   */
  private void buildQueryExpression(String condition, ExpressionList<?> exp) throws GenericEntityException{
    if (CommUtil.isEmpty(condition)) {
      return ;
    }

    List<Integer> leftBraceIdxList = new ArrayList<>();
    List<Integer> rightBraceIdxList = new ArrayList<>();

    byte[] bytes = condition.getBytes();
    for (int i = 0; i < bytes.length; i++) {
      byte b = bytes[i];
      if (b == 123) {
        leftBraceIdxList.add(i);
      } else if (b == 125) {
        rightBraceIdxList.add(i);
      }
    }

    if (leftBraceIdxList.size() == 0 || leftBraceIdxList.size() != rightBraceIdxList.size()) {
      throw new GenericEntityException("The condition [" + condition + "] is illegal.");
    }

    for (int i = 0; i < leftBraceIdxList.size(); i++) {
      String cond = condition.substring(leftBraceIdxList.get(i) + 1,rightBraceIdxList.get(i));

      if (CommUtil.isEmpty(cond)) {
        continue;
      }
      EbeanServer server = currentServerMap.get(CURRENT_SERVER_NAME);

      //NOTICE (cond.indexOf(C.EXPR_OR) >= 0) MUST BE HERE...
      if(cond.indexOf(C.EXPR_OR) >= 0) {
        String[] condToken = cond.split("," + C.EXPR_OR + ",");
        String leftCond = condToken[0];
        String rightCond = condToken[1];
        exp.or(setExpression(leftCond,null),setExpression(rightCond,null));
      } else {
        setExpression(cond,exp);
      }
    }
  }

  /**
   * <p>
   *     根据条件字符串，设置查询表达式对象，如果参数<code>exp</code>为空，构建<code>{@link com.avaje.ebean.Expression}</code>并返回，
   *     否则设置的是<code>{@link com.avaje.ebean.ExpressionList}</code>对象
   * </p>
   * @param cond  指定的条件字符串
   * @param exp   要设置的<code>{@link com.avaje.ebean.ExpressionList}</code>
   * @return    如果参数exp为空，返回<code>{@link com.avaje.ebean.Expression}</code>，否则返回<code>NULL</code>
   */
  private Expression setExpression(String cond,ExpressionList<?> exp) {
    if (CommUtil.isNotEmpty(cond)) {
      EbeanServer server = currentServerMap.get(CURRENT_SERVER_NAME);
      ExpressionFactory ef = server.getExpressionFactory();
      if (cond.indexOf("," + C.EXPR_EQ + ",") >= 0) {
        String[] condToken = cond.split("," + C.EXPR_EQ + ",");
        String fieldName = condToken[0];
        String condValue = condToken[1];
        if (CommUtil.isNotEmpty(condValue)) {
          if (exp != null) {
            exp.eq(fieldName, condValue);
          } else {
            return ef.eq(fieldName, condValue);
          }
        }
      } else if(cond.indexOf("," + C.EXPR_NE + ",") >= 0) {
        String[] condToken = cond.split("," + C.EXPR_NE + ",");
        String fieldName = condToken[0];
        String condValue = condToken[1];
        if (CommUtil.isNotEmpty(condValue)) {
          if (exp != null) {
            exp.ne(fieldName, condValue);
          } else {
            return ef.ne(fieldName, condValue);
          }
        }
      } else if(cond.indexOf("," + C.EXPR_IN + ",") >= 0) {
        String[] condToken = cond.split("," + C.EXPR_IN + ",");
        String fieldName = condToken[0];
        String condValue = condToken[1];
        if (CommUtil.isNotEmpty(condValue)) {
          Object[] valueArray = condValue.split("#");
          if (exp != null) {
            exp.in(fieldName, Arrays.asList(valueArray));
          } else {
            return ef.in(fieldName, Arrays.asList(valueArray));
          }
        }
      } else if(cond.indexOf("," + C.EXPR_NIN + ",") >= 0) {
        String[] condToken = cond.split("," + C.EXPR_NIN + ",");
        String fieldName = condToken[0];
        String condValue = condToken[1];
        if (CommUtil.isNotEmpty(condValue)) {
          Object[] valueArray = condValue.split("#");
          if (exp != null) {
            exp.not(server.getExpressionFactory().in(fieldName,Arrays.asList(valueArray)));
          } else {
            return ef.not(server.getExpressionFactory().in(fieldName, Arrays.asList(valueArray)));
          }
        }
      } else if(cond.indexOf("," + C.EXPR_LE + ",") >= 0) {
        String[] condToken = cond.split("," + C.EXPR_LE + ",");
        String fieldName = condToken[0];
        String condValue = condToken[1];
        if (CommUtil.isNotEmpty(condValue)) {
          if (exp != null) {
            exp.le(fieldName, condValue);
          } else {
            return ef.le(fieldName, condValue);
          }
        }
      } else if(cond.indexOf("," + C.EXPR_LT + ",") >= 0) {
        String[] condToken = cond.split("," + C.EXPR_LT + ",");
        String fieldName = condToken[0];
        String condValue = condToken[1];
        if (CommUtil.isNotEmpty(condValue)) {
          if (exp != null) {
            exp.lt(fieldName, condValue);
          } else {
            return ef.lt(fieldName, condValue);
          }
        }
      } else if(cond.indexOf("," + C.EXPR_GE + ",") >= 0) {
        String[] condToken = cond.split("," + C.EXPR_GE + ",");
        String fieldName = condToken[0];
        String condValue = condToken[1];
        if (CommUtil.isNotEmpty(condValue)) {
          if (exp != null) {
            exp.ge(fieldName, condValue);
          } else {
            return ef.ge(fieldName, condValue);
          }
        }
      } else if(cond.indexOf("," + C.EXPR_GT + ",") >= 0) {
        String[] condToken = cond.split("," + C.EXPR_GT + ",");
        String fieldName = condToken[0];
        String condValue = condToken[1];
        if (CommUtil.isNotEmpty(condValue)) {
          if (exp != null) {
            exp.gt(fieldName, condValue);
          } else {
            return ef.gt(fieldName, condValue);
          }
        }
      } else if(cond.indexOf("," + C.EXPR_IS_NULL + ",") >= 0) {
        String[] condToken = cond.split("," + C.EXPR_IS_NULL + ",");
        String fieldName = condToken[0];
        String condValue = condToken[1];
        if (CommUtil.isNotEmpty(condValue)) {
          if (exp != null) {
            exp.isNull(fieldName);
          } else {
            return ef.isNull(fieldName);
          }
        }
      } else if(cond.indexOf("," + C.EXPR_IS_NOT_NULL + ",") >= 0) {
        String[] condToken = cond.split("," + C.EXPR_IS_NOT_NULL + ",");
        String fieldName = condToken[0];
        String condValue = condToken[1];
        if (CommUtil.isNotEmpty(condValue)) {
          if (exp != null) {
            exp.isNotNull(fieldName);
          } else {
            return ef.isNotNull(fieldName);
          }
        }
      } else if(cond.indexOf("," + C.EXPR_LIKE + ",") >= 0) {
        String[] condToken = cond.split("," + C.EXPR_LIKE + ",");
        String fieldName = condToken[0];
        String condValue = condToken[1];
        if (CommUtil.isNotEmpty(condValue)) {
          if (exp != null) {
            exp.like(fieldName, "%" + condValue + "%");
          } else {
            return ef.like(fieldName, "%" + condValue + "%");
          }
        }
      } else if(cond.indexOf("," + C.EXPR_LLIKE + ",") >= 0) {
        String[] condToken = cond.split("," + C.EXPR_LLIKE + ",");
        String fieldName = condToken[0];
        String condValue = condToken[1];
        if (CommUtil.isNotEmpty(condValue)) {
          if (exp != null) {
            exp.like(fieldName, "%" + condValue);
          } else {
            return ef.like(fieldName, "%" + condValue);
          }
        }
      } else if(cond.indexOf("," + C.EXPR_RLIKE + ",") >= 0) {
        String[] condToken = cond.split("," + C.EXPR_RLIKE + ",");
        String fieldName = condToken[0];
        String condValue = condToken[1];
        if (CommUtil.isNotEmpty(condValue)) {
          if (exp != null) {
            exp.like(fieldName, condValue + "%");
          } else {
            return ef.like(fieldName, condValue + "%");
          }
        }
      } else if(cond.indexOf("," + C.EXPR_BETWEEN + ",") >= 0) {
        String[] condToken = cond.split("," + C.EXPR_BETWEEN + ",");
        String fieldName = condToken[0];
        String condValue = condToken[1];
        if (CommUtil.isNotEmpty(condValue)) {
          Object[] valueArray = condValue.split("#");
          if (exp != null) {
            exp.between(fieldName,valueArray[0],valueArray[1]);
          } else {
            return ef.between(fieldName, valueArray[0], valueArray[1]);
          }
        }
      }
    }
    return null;
  }

  /**
   * <p>
   *     将传入的查询字段拼接成 特定格式的字符串，并应用到查询对象<code>{@link com.avaje.ebean.Query}</code>
   * </p>
   * <p>
   *     比如：传的字段Set集合为: <strong>[id,username,email,order.id,order.grandTotal]</strong>
   * </p>
   * <p>
   *     转换后字符串格式为： <strong>(id,username,email,order(id,grandTotal))</strong><br/>
   *     该字符串做为参数 生成 <code>{@link com.avaje.ebean.text.PathProperties}</code>对象实例,并应用到Query查询对象上
   * </p>
   * @param query          <code>{@link com.avaje.ebean.Query}</code>查询对象
   * @param fieldsToSelect 要查询的字段集合
   */
  private void buildSelectFields(Query<?> query, Set<String> fieldsToSelect) {
    if (CommUtil.isNotEmpty(fieldsToSelect)) {
      Set<String> objPropSets = new HashSet<>();
      StringBuffer firstPropSb = new StringBuffer();
      for (String fts : fieldsToSelect) {
        if (fts.indexOf(".") == -1) {
          firstPropSb.append(fts).append(",");
        } else {
          objPropSets.add(fts.substring(0, fts.indexOf(".")));
        }
      }

      Map<String, String> objPathMap = new LinkedHashMap<>();
      for (String prop : objPropSets) {
        StringBuilder tmpSb = new StringBuilder();
        for (String fts : fieldsToSelect) {
          if (fts.startsWith(prop)) {
            tmpSb.append(fts.substring(prop.length() + 1)).append(",");
          }
        }
        objPathMap.put(prop, tmpSb.toString());
      }

      StringBuilder pathPropsSb = new StringBuilder();
      pathPropsSb.append("(");
      if (firstPropSb.length() > 0) {
        pathPropsSb.append(firstPropSb.substring(0, firstPropSb.length() - 1));
      }
      if (CommUtil.isNotEmpty(objPathMap)) {
        StringBuilder tmpSb = new StringBuilder();
        Set<Map.Entry<String, String>> entrySet = objPathMap.entrySet();
        Iterator<Map.Entry<String, String>> iter = entrySet.iterator();
        while (iter.hasNext()) {
          Map.Entry<String, String> entry = iter.next();
          String key = entry.getKey();
          String value = entry.getValue();
          tmpSb.append(key).append("(").append(value.substring(0, value.length() - 1)).append(")").append(",");
        }
        if (firstPropSb.length() > 0) {
          pathPropsSb.append(",");
        }
        pathPropsSb.append(tmpSb.substring(0, tmpSb.length() - 1));
      }
      pathPropsSb.append(")");
      Log.d("PathProperties:" + pathPropsSb.toString(),TAG);
      PathProperties pathProperties = PathProperties.parse(pathPropsSb.toString());
      pathProperties.apply(query);
    }
  }
}

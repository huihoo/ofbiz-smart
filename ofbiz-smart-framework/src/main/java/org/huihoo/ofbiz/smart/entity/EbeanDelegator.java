package org.huihoo.ofbiz.smart.entity;

import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.huihoo.ofbiz.smart.base.C;
import org.huihoo.ofbiz.smart.base.location.FlexibleLocation;
import org.huihoo.ofbiz.smart.base.util.Log;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.config.ServerConfig;

public class EbeanDelegator implements Delegator {
  private final String TAG = EbeanDelegator.class.getName();
  private final String CURRENT_SERVER_NAME = "_current_server_" ;
  private final String defaultDataSourceName;
  private final ConcurrentHashMap<String, EbeanServer> currentServerMap = new ConcurrentHashMap<String, EbeanServer>();
  private final ConcurrentHashMap<String, EbeanServer> concMap = new ConcurrentHashMap<String, EbeanServer>();
  
  public EbeanDelegator() throws GenericEntityException {
    Properties externalProps = new Properties();
    try {
      externalProps.load(FlexibleLocation.resolveLocation(C.APPLICATION_CONFIG_NAME).openStream());
    } catch (IOException e) {
      throw new GenericEntityException("Unable to load external properties");
    }
    
    defaultDataSourceName = externalProps.getProperty("datasource.default");
    
    Set<String> dsNames = new LinkedHashSet<>();
    Enumeration<?> keys = externalProps.keys();
    while(keys.hasMoreElements()) {
      String k = (String) keys.nextElement();
      if (k.startsWith("datasource") && k.indexOf("username") >= 0) {
        dsNames.add(k.substring("datasource.".length(), k.indexOf(".username")));
      }
    }
    Log.d("Default datasouce[%s], Avaliable datasources[%s]", TAG, defaultDataSourceName,dsNames);
    for (String dsName : dsNames) {
      ServerConfig config = new ServerConfig();
      config.setName(dsName);
      config.loadFromProperties(externalProps);
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
      currentServerMap.get(CURRENT_SERVER_NAME).delete(entities);
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

  @Override
  public void executeWithInTx(TxRunnable txRunnable) {
    try {
      beginTransaction();
      txRunnable.run();
    }catch(Exception e){
      Log.e(e, "EbeanDeletagor.execute() occurs an exception.", TAG);
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
      Log.e(e, "EbeanDeletagor.execute() occurs an exception.", TAG);
      rollback();
      return null;
    }finally{
      endTransaction();
    }
  }
}

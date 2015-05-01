/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *******************************************************************************/
package org.huihoo.ofbiz.smart.entity;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.avaje.agentloader.AgentLoader;
import org.huihoo.ofbiz.smart.base.utils.CommUtils;
import org.huihoo.ofbiz.smart.base.utils.Debug;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.ExpressionList;
import com.avaje.ebean.PagedList;
import com.avaje.ebean.Query;
import com.avaje.ebean.SqlQuery;
import com.avaje.ebean.SqlRow;
import com.avaje.ebean.SqlUpdate;
import com.avaje.ebean.config.ServerConfig;


public class EbeanDelegator implements Delegator {
  private final static String module = EbeanDelegator.class.getName();
  private final String entityBasepackage;
  private volatile EbeanServer server;

  private void preInit(String modelBasePackage) throws GenericEntityException {
    if (!AgentLoader.loadAgentFromClasspath("avaje-ebeanorm-agent", "debug=1;packages="
            + modelBasePackage + ".**")) {
      Debug.logError("avaje-agentloader和avaje-ebeanorm-agent未在类路径中找到 - 无法动态加载实体类", module);
      throw new GenericEntityException("类路径缺失avaje-agentloader和avaje-ebeanorm-agent");
    }
  }

  public EbeanDelegator(String name, String entityBasepackage, Properties configProp)
          throws GenericEntityException {
    this.entityBasepackage = entityBasepackage;
    preInit(entityBasepackage);
    ServerConfig config = new ServerConfig();
    config.setName(name);
    config.loadFromProperties(configProp);
    this.server = EbeanServerFactory.create(config);
  }



  @Override
  public void save(Collection<?> modeles) throws GenericEntityException {
    try {
      server.save(modeles);
    } catch (Exception e) {
      Debug.logError(e, "save exception", module);
      throw new GenericEntityException(e);
    }
  }


  @Override
  public void save(Object model) throws GenericEntityException {
    try {
      server.save(model);
    } catch (Exception e) {
      Debug.logError(e, "save exception", module);
      throw new GenericEntityException(e);
    }
  }


  @Override
  public int executeRawSql(String rawSql, List<?> params) throws GenericEntityException {
    try {
      SqlUpdate sqlUpdate = server.createSqlUpdate(rawSql);
      if (CommUtils.isNotEmpty(params)) {
        int size = params.size();
        for (int i = 0; i < size; i++) {
          sqlUpdate.setParameter(i + 1, params.get(i));
        }
      }
      return sqlUpdate.execute();
    } catch (Exception e) {
      Debug.logError(e, "executeRawSql exception", module);
      throw new GenericEntityException(e);
    }
  }


  @Override
  public int executeRawSql(String rawSql) throws GenericEntityException {
    try {
      return executeRawSql(rawSql, new ArrayList<Object>());
    } catch (Exception e) {
      Debug.logError(e, "executeRawSql exception", module);
      throw new GenericEntityException(e);
    }
  }


  @Override
  public void remove(Object model) throws GenericEntityException {
    try {
      server.delete(model);
    } catch (Exception e) {
      Debug.logError(e, "remove exception", module);
      throw new GenericEntityException(e);
    }
  }


  @Override
  public int remove(Collection<?> list) throws GenericEntityException {
    try {
      return server.delete(list);
    } catch (Exception e) {
      Debug.logError(e, "remove exception", module);
      throw new GenericEntityException(e);
    }
  }

  @Override
  public Object findById(String entityName, Object id, boolean useCache)
          throws GenericEntityException {
    try {
      Class<?> entityClazz = classFromName(entityName);
      return server.find(entityClazz, id);
    } catch (Exception e) {
      Debug.logError(e, "findById exception", module);
      throw new GenericEntityException(e);
    }
  }

  @Override
  public Object findUniqueByAnd(String entityName, Map<String, Object> fields, boolean useCache)
          throws GenericEntityException {
    try {
      Class<?> entityClazz = classFromName(entityName);
      ExpressionList<?> expList = server.find(entityClazz).where();

      if (CommUtils.isNotEmpty(fields)) expList.allEq(fields);

      return expList.findUnique();

    } catch (Exception e) {
      Debug.logError(e, "findById exception", module);
      throw new GenericEntityException(e);
    }
  }


  /*
   * (non-Javadoc)
   * 
   * @see com.malllike.entity.Delegator#findByAnd(java.lang.String, java.util.Map, java.util.Set,
   * java.util.List, boolean)
   */
  @Override
  public List<?> findByAnd(String entityName, Map<String, Object> fields,
          Set<String> fieldsToSelect, List<String> orderBy, boolean useCache)
          throws GenericEntityException {
    try {
      Class<?> entityClazz = classFromName(entityName);
      Query<?> query = server.find(entityClazz);
      buildSelectFields(query, fieldsToSelect);

      ExpressionList<?> expList = query.where();

      if (CommUtils.isNotEmpty(fields)) expList.allEq(fields);

      if (CommUtils.isNotEmpty(orderBy)) {
        StringBuffer sb = new StringBuffer();
        for (String order : orderBy) {
          sb.append(order).append(",");
        }
        expList.orderBy(sb.substring(0, sb.length() - 1));
      }

      expList.setUseCache(useCache);

      return expList.findList();

    } catch (Exception e) {
      Debug.logError(e, "findByAnd exception", module);
      throw new GenericEntityException(e);
    }
  }

  @Override
  public List<?> findByAnd(String entityName, Map<String, Object> fields, List<String> orderBy,
          boolean useCache) throws GenericEntityException {
    try {
      return findByAnd(entityName, fields, null, orderBy, useCache);
    } catch (Exception e) {
      Debug.logError(e, "findByAnd exception", module);
      throw new GenericEntityException(e);
    }
  }



  @Override
  public List<?> findList(String entityName, String condition, Set<String> fieldsToSelect,
          List<String> orderBy, boolean useCache) throws GenericEntityException {
    try {
      Class<?> entityClazz = classFromName(entityName);
      Query<?> query = server.find(entityClazz);
      buildSelectFields(query, fieldsToSelect);

      ExpressionList<?> expList = query.where();

      buildExpressList(expList, condition, orderBy);

      expList.setUseCache(useCache);

      return expList.findList();
    } catch (Exception e) {
      Debug.logError(e, "findList exception", module);
      throw new GenericEntityException(e);
    }

  }



  @Override
  public Map<String, Object> findPageByCondition(String entityName, String condition,
          Set<String> fieldsToSelect, List<String> orderBy, int pageNo, int pageSize,
          boolean useCache) throws GenericEntityException {
    try {
      Map<String, Object> result = new HashMap<String, Object>();
      Class<?> entityClazz = classFromName(entityName);
      Query<?> query = server.find(entityClazz);
      buildSelectFields(query, fieldsToSelect);
      ExpressionList<?> expList = query.where();
      buildExpressList(expList, condition, orderBy);

      expList.setUseCache(useCache);

      PagedList<?> pageList = expList.findPagedList(pageNo - 1, pageSize);
      int totalPage = pageList.getTotalPageCount();
      int totalEntry = pageList.getTotalRowCount();
      List<?> list = pageList.getList();

      result.put("totalPage", totalPage);
      result.put("totalEntry", totalEntry);
      result.put("list", list);

      return result;

    } catch (Exception e) {
      Debug.logError(e, "findPageByCondition exception", module);
      throw new GenericEntityException(e);
    }
  }


  @Override
  public Map<String, Object> findPageByAnd(String entityName, Map<String, Object> fields,
          Set<String> fieldsToSelect, List<String> orderBy, int pageNo, int pageSize,
          boolean useCache) throws GenericEntityException {
    Map<String, Object> result = new HashMap<String, Object>();
    try {
      Class<?> entityClazz = classFromName(entityName);
      Query<?> query = server.find(entityClazz);

      buildSelectFields(query, fieldsToSelect);

      ExpressionList<?> expList = query.where();
      expList.setUseCache(useCache);

      buildExpressList(expList, fields, orderBy);

      PagedList<?> pageList = expList.findPagedList(pageNo - 1, pageSize);
      int totalPage = pageList.getTotalPageCount();
      int totalEntry = pageList.getTotalRowCount();
      List<?> list = pageList.getList();

      result.put("totalPage", totalPage);
      result.put("totalEntry", totalEntry);
      result.put("list", list);

    } catch (Exception e) {
      Debug.logError(e, "findPageByAnd exception", module);
      throw new GenericEntityException(e);
    }

    return result;
  }

  /**
   * <p>
   * 构建返回字段
   * </p>
   * 
   * @param query 要构建的<code>Query</code>实例
   * @param fieldsToSelect 构建的字段集合
   */
  private void buildSelectFields(Query<?> query, Set<String> fieldsToSelect) {
    if (CommUtils.isNotEmpty(fieldsToSelect)) {
      StringBuffer sb = new StringBuffer();
      for (String fts : fieldsToSelect) {
        sb.append(fts).append(",");
      }
      query.select(sb.substring(0, sb.length() - 1));
    }
  }

  /**
   * <p>
   * 构建<code>ExpressionList</code>
   * </p>
   * 
   * @param expList 要构建的<code>ExpressionList</code>实例
   * @param condition 条件语句
   * @param orderBy 要排序的字段集合
   * @throws GenericEntityException
   */
  private void buildExpressList(ExpressionList<?> expList, String condition, List<String> orderBy)
          throws GenericEntityException {
    buildQueryCondition(condition, expList);
    if (CommUtils.isNotEmpty(orderBy)) {
      StringBuffer sb = new StringBuffer();
      for (String order : orderBy) {
        sb.append(order).append(",");
      }
      expList.orderBy(sb.substring(0, sb.length() - 1));
    }
  }

  private void buildExpressList(ExpressionList<?> expList, Map<String, Object> fields,
          List<String> orderBy) {
    if (CommUtils.isNotEmpty(fields)) expList.allEq(fields);

    if (CommUtils.isNotEmpty(orderBy)) {
      StringBuffer sb = new StringBuffer();
      for (String order : orderBy) {
        sb.append(order).append(",");
      }
      expList.orderBy(sb.substring(0, sb.length() - 1));
    }
  }

  /**
   * <p>
   * 从指定的业务实体名称生成类
   * <p>
   * 
   * @param entityName 业务实体名称
   * @return 生成成功的类
   * @throws GenericEntityException
   */
  private Class<?> classFromName(String entityName) throws GenericEntityException {
    Class<?> entityClazz;
    try {
      if (entityName.startsWith(entityBasepackage))
        entityClazz = Class.forName(entityName);
      else
        entityClazz = Class.forName(entityBasepackage + "." + entityName);
      return entityClazz;
    } catch (ClassNotFoundException e) {
      Debug.logError(e, e.getMessage(), module);
      throw new GenericEntityException(e);
    }
  }



  @Override
  public int count(String rawSql, List<?> params) throws GenericEntityException {
    try {
      if (CommUtils.isEmpty(rawSql)) return 0;

      if (rawSql.indexOf("_count_") == -1)
        throw new GenericEntityException("rawSql[" + rawSql
                + "]的count函数应该取一个名为_count_的别名.比如:select count(1) as _count_ from table");
      SqlQuery sqlQuery = server.createSqlQuery(rawSql);

      if (CommUtils.isNotEmpty(params)) {
        int size = params.size();
        for (int i = 0; i < size; i++) {
          sqlQuery.setParameter(i + 1, params.get(i));
        }
      }
      return sqlQuery.findUnique().getInteger("_count_");
    } catch (Exception e) {
      Debug.logError(e, "count exception", module);
      throw new GenericEntityException(e);
    }
  }

  @Override
  public int countByAnd(String entityName, Map<String, Object> fields)
          throws GenericEntityException {
    try {
      if (CommUtils.isEmpty(entityName)) return 0;

      Class<?> entityClazz = classFromName(entityName);
      return server.find(entityClazz).where().allEq(fields).findRowCount();

    } catch (Exception e) {
      Debug.logError(e, "countByAnd exception", module);
      throw new GenericEntityException(e);
    }
  }

  @Override
  public int count(String entityName, String queryCondition) throws GenericEntityException {
    try {
      if (CommUtils.isEmpty(entityName)) return 0;
      Class<?> entityClazz = classFromName(entityName);

      Query<?> query = server.find(entityClazz);
      ExpressionList<?> expList = query.where();

      buildExpressList(expList, queryCondition, null);
      return expList.findRowCount();

    } catch (Exception e) {
      Debug.logError(e, "count exception", module);
      throw new GenericEntityException(e);
    }
  }



  @Override
  public Object findById(String entityName, Object id) throws GenericEntityException {
    try {
      return findById(entityName, id, false);
    } catch (Exception e) {
      Debug.logError(e, "findById exception", module);
      throw new GenericEntityException(e);
    }
  }


  @Override
  public Object findUniqueByAnd(String entityName, Map<String, Object> fields)
          throws GenericEntityException {
    try {
      return findUniqueByAnd(entityName, fields, false);
    } catch (Exception e) {
      Debug.logError(e, "findUniqueByAnd exception", module);
      throw new GenericEntityException(e);
    }
  }


  @Override
  public List<?> findByAnd(String entityName, Map<String, Object> fields)
          throws GenericEntityException {
    try {
      return findByAnd(entityName, fields, null, false);
    } catch (Exception e) {
      Debug.logError(e, "findByAnd exception", module);
      throw new GenericEntityException(e);
    }
  }


  @Override
  public List<?> findByAnd(String entityName, Map<String, Object> fields, List<String> orderBy)
          throws GenericEntityException {
    try {
      return findByAnd(entityName, fields, orderBy, false);
    } catch (Exception e) {
      Debug.logError(e, "findByAnd exception", module);
      throw new GenericEntityException(e);
    }
  }


  @Override
  public List<?> findList(String entityName, String condition, Set<String> fieldsToSelect,
          List<String> orderBy) throws GenericEntityException {
    try {
      return findList(entityName, condition, fieldsToSelect, orderBy, false);
    } catch (Exception e) {
      Debug.logError(e, "findList exception", module);
      throw new GenericEntityException(e);
    }
  }


  @Override
  public List<?> findList(String entityName, String condition, Set<String> fieldsToSelect)
          throws GenericEntityException {
    try {
      return findList(entityName, condition, fieldsToSelect, null, false);
    } catch (Exception e) {
      Debug.logError(e, "findList exception", module);
      throw new GenericEntityException(e);
    }
  }


  @Override
  public List<?> findList(String entityName, String condition) throws GenericEntityException {
    try {
      return findList(entityName, condition, null, null, false);
    } catch (Exception e) {
      Debug.logError(e, "findList exception", module);
      throw new GenericEntityException(e);
    }
  }


  @Override
  public Map<String, Object> findPageByCondition(String entityName, String condition,
          Set<String> fieldsToSelect, List<String> orderBy, int pageNo, int pageSize)
          throws GenericEntityException {
    try {
      return findPageByCondition(entityName, condition, fieldsToSelect, orderBy, pageNo, pageSize,
              false);
    } catch (Exception e) {
      Debug.logError(e, "findPageByCondition exception", module);
      throw new GenericEntityException(e);
    }
  }


  @Override
  public Map<String, Object> findPageByCondition(String entityName, String condition,
          Set<String> fieldsToSelect, int pageNo, int pageSize) throws GenericEntityException {
    try {
      return findPageByCondition(entityName, condition, fieldsToSelect, null, pageNo, pageSize,
              false);
    } catch (Exception e) {
      Debug.logError(e, "findPageByCondition exception", module);
      throw new GenericEntityException(e);
    }
  }


  @Override
  public Map<String, Object> findPageByCondition(String entityName, String condition, int pageNo,
          int pageSize) throws GenericEntityException {
    try {
      return findPageByCondition(entityName, condition, null, null, pageNo, pageSize, false);
    } catch (Exception e) {
      Debug.logError(e, "findPageByCondition exception", module);
      throw new GenericEntityException(e);
    }
  }


  @Override
  public Map<String, Object> findPageByAnd(String entityName, Map<String, Object> fields,
          Set<String> fieldsToSelect, List<String> orderBy, int pageNo, int pageSize)
          throws GenericEntityException {
    try {
      return findPageByAnd(entityName, fields, fieldsToSelect, orderBy, pageNo, pageSize, false);
    } catch (Exception e) {
      Debug.logError(e, "findPageByAnd exception", module);
      throw new GenericEntityException(e);
    }
  }


  @Override
  public Map<String, Object> findPageByAnd(String entityName, Map<String, Object> fields,
          Set<String> fieldsToSelect, int pageNo, int pageSize) throws GenericEntityException {
    try {
      return findPageByAnd(entityName, fields, fieldsToSelect, null, pageNo, pageSize, false);
    } catch (Exception e) {
      Debug.logError(e, "findPageByAnd exception", module);
      throw new GenericEntityException(e);
    }
  }


  @Override
  public Map<String, Object> findPageByAnd(String entityName, Map<String, Object> fields,
          int pageNo, int pageSize) throws GenericEntityException {
    try {
      return findPageByAnd(entityName, fields, null, null, pageNo, pageSize, false);
    } catch (Exception e) {
      Debug.logError(e, "findPageByAnd exception", module);
      throw new GenericEntityException(e);
    }
  }

  @Override
  public String toJson(Object entity) throws GenericEntityException {
    try {
      return server.json().toJson(entity);
    } catch (Exception e) {
      Debug.logError(e, "toJson exception", module);
      throw new GenericEntityException(e);
    }
  }


  @Override
  public int count(String rawSql) throws GenericEntityException {
    try {
      return count(rawSql, new ArrayList<>());
    } catch (Exception e) {
      Debug.logError(e, "count exception", module);
      throw new GenericEntityException(e);
    }
  }


  @Override
  public void beginTransaction() {
    server.beginTransaction();
  }

  @Override
  public void endTransaction() {
    server.endTransaction();
  }

  @Override
  public void rollback() {
    server.rollbackTransaction();
  }

  @Override
  public void commitTransaction() {
    server.commitTransaction();
  }

  private void buildQueryCondition(String condition, ExpressionList<?> exp)
          throws GenericEntityException {
    if (CommUtils.isEmpty(condition)) return;
    String[] conditionToken = condition.split(",");
    // TODO 采用antrl语法树构造加强
    for (String ec : conditionToken) {
      if (ec.indexOf("@") == -1) {
        throw new IllegalArgumentException("The condition[" + condition + "] is illegal");
      }
      String[] conditionPair = ec.split("@");
      if (conditionPair.length != 3)
        throw new IllegalArgumentException("The pair[" + conditionPair + "] of condition["
                + condition + "] is illegal");

      String fieldName = conditionPair[0];
      String operator = conditionPair[1];
      String condValue = conditionPair[2];

      if (CommUtils.isEmpty(condValue)) continue;

      switch (operator) {
        case "in":
          Object[] valueArry = condValue.split("#");
          exp.in(fieldName, Arrays.asList(valueArry));
          break;
        case "notIn":
          valueArry = condValue.split("#");
          exp.not(server.getExpressionFactory().in(fieldName, Arrays.asList(valueArry)));
          break;
        case "eq":
          exp.eq(fieldName, condValue);
          break;
        case "like":
          exp.like(fieldName, "%" + condValue + "%");
          break;
        case "llike":
          exp.like(fieldName, "%" + condValue);
          break;
        case "rlike":
          exp.like(fieldName, condValue + "%");
          break;
        case "lt":
          exp.lt(fieldName, condValue);
          break;
        case "le":
          exp.le(fieldName, condValue);
          break;
        case "gt":
          exp.gt(fieldName, condValue);
          break;
        case "ge":
          exp.ge(fieldName, condValue);
          break;
        case "isNull":
          exp.isNull(fieldName);
          break;
        case "notNull":
          exp.isNotNull(fieldName);
          break;
        case "or":
          // TODO
          break;
        default:
          throw new IllegalArgumentException("不支持的操作符:" + operator + "");
      }
    }
  }


  @Override
  public List<Map<String, Object>> findListByRawQuerySql(String sql, List<?> params)
          throws GenericEntityException {
    List<Map<String, Object>> mapList = new ArrayList<>();

    SqlQuery query = server.createSqlQuery(sql);
    if (CommUtils.isNotEmpty(params)) {
      for (int i = 0; i < params.size(); i++) {
        query.setParameter(i + 1, params.get(i));
      }
    }
    List<SqlRow> rows = query.findList();
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
    return mapList;
  }

  @Override
  public Object findUniqueByAnd(String entityName, Map<String, Object> fields,
          Set<String> fieldsToSelect) throws GenericEntityException{
    try {
      Class<?> entityClazz = classFromName(entityName);
      Query<?> query = server.find(entityClazz);
      buildSelectFields(query, fieldsToSelect);
      ExpressionList<?> expList = query.where();
      if(CommUtils.isNotEmpty(fields))
        expList.allEq(fields);      
      return expList.findUnique();
    } catch (Exception e) {
      Debug.logError(e, "toJson exception", module);
      throw new GenericEntityException(e);
    }
  }

  @Override
  public void update(Object entity) throws GenericEntityException {
	 try {
	      server.update(entity);
	    } catch (Exception e) {
	      Debug.logError(e, "update exception", module);
	      throw new GenericEntityException(e);
	    }
  }

  @Override
  public int executeRawSql(String rawSql, Object... params) throws GenericEntityException {
    if(params.length > 0){
      List<Object> list =  Arrays.asList(params);
      return executeRawSql(rawSql, list);
    }
    return 0;
  }
  
  
}

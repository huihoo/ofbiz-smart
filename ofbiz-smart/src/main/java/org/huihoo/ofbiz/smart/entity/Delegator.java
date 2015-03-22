/*******************************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *******************************************************************************/
package org.huihoo.ofbiz.smart.entity;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * <p>
 * 数据库访问委托类，主要负责一般的数据库操作，以满足一般的业务需求
 * </p>
 * 
 * @author huangbohua
 * 
 */
public interface Delegator {

  enum OperationType {
    INSERT, UPDATE, DELETE
  }

  enum ConditionType {
    EQ, NOT_EQ, IN, LIKE, GT, LT, OR
  }

  /**
   * <p>
   * 在当前线程中，开始一个事务
   * </p>
   */
  public void beginTransaction();

  /**
   * <p>
   * 在当前线程中，结束一个事务
   * </p>
   */
  public void endTransaction();

  /**
   * <p>
   * 在当前线程中，回滚一个事务
   * </p>
   */
  public void rollback();

  /**
   * <p>
   * 在当前线程中，提交一个事务
   * </p>
   */
  public void commitTransaction();


  /**
   * <p>
   * 读取记录数<br/>
   * 注意：count函数对应的字段，必须取一个别名，且为_count_<br/>
   * 比如：select count(id) as _count_ from table
   * </p>
   * 
   * @param rawSql 要执行的原生SQL
   * @return 总记录数
   */
  public int count(String rawSql) throws GenericEntityException;

  /**
   * <p>
   * 读取记录数<br/>
   * 注意：count函数对应的字段，必须取一个别名，且为_count_<br/>
   * 比如：select count(id) as _count_ from table
   * </p>
   * 
   * @param rawSql 要执行的原生SQL
   * @param params 原生SQL对应的参数
   * @return 总记录数
   */
  public int count(String rawSql, List<?> params) throws GenericEntityException;

  /**
   * <p>
   * 读取记录数
   * </p>
   * 
   * @param entityName 要查询的业务实体名称
   * @param fields 条件字段<code>Map</code>集合。<code>key</code>为业务实体属性字段,<code>value</code>为字段对应的值
   * @return 总记录数
   */
  public int countByAnd(String entityName, Map<String, Object> fields)
          throws GenericEntityException;

  /**
   * <p>
   * 
   * </p>
   * 
   * @param entityName
   * @param queryCondition
   * @return
   */
  public int count(String entityName, String queryCondition) throws GenericEntityException;

  /**
   * <p>
   * 批量保存或更新(如果实体已经存在)业务实体集合
   * </p>
   * 
   * @param modeles 要更新的业务实体集合
   * @throws GenericEntityException
   */
  public void save(Collection<?> modeles) throws GenericEntityException;

  /**
   * <p>
   * 保存或更新(如果实体已经存在)业务实体
   * </p>
   * 
   * @param modeles 要更新的业务实体
   * @throws GenericEntityException
   */
  public void save(Object model) throws GenericEntityException;

  /**
   * <p>
   * 删除业务实体
   * <p>
   * 
   * @param model 要删除的业务实体
   * @throws GenericEntityException
   */
  public void remove(Object model) throws GenericEntityException;

  /**
   * <p>
   * 批量删除业务实体集合
   * </p>
   * 
   * @param list 要删除的业务实体集合
   * @return 删除的业务实体个数
   * @throws GenericEntityException
   */
  public int remove(Collection<?> list) throws GenericEntityException;

  /**
   * <p>
   * 执行原生的更新或删除语句
   * </p>
   * 
   * @param rawSql 要执行的 原生的更新或删除语句
   * @param params 参数集合。注意： 参数的个数和顺序必须和语句中的占位符?相匹配
   * @return SQL语句影响的记录数
   * @throws GenericEntityException
   */
  public int executeRawSql(String rawSql, List<?> params) throws GenericEntityException;

  /**
   * <p>
   * 执行原生的更新或删除语句
   * </p>
   * 
   * @param rawSql 要执行的 原生的更新或删除语句
   * @return SQL语句影响的记录数
   * @throws GenericEntityException
   */
  public int executeRawSql(String rawSql) throws GenericEntityException;

  /**
   * <p>
   * 根据主键ID查找业务实体
   * </p>
   * 
   * @param entityName 要查找的业务实体名称
   * @param id 要查找的业务实体主键ID
   * @return 如果找到，返回该业务实体；未找到，返回<code>null</code>
   * @throws GenericEntityException
   */
  public Object findById(String entityName, Object id) throws GenericEntityException;

  /**
   * <p>
   * 根据主键ID查找业务实体
   * </p>
   * 
   * @param entityName 要查找的业务实体名称
   * @param id 要查找的业务实体主键ID
   * @param useCache 是否使用缓存 <code>true</code>使用; <code>false</code>不使用
   * @return 如果找到，返回该业务实体；未找到，返回<code>null</code>
   * @throws GenericEntityException
   */
  public Object findById(String entityName, Object id, boolean useCache)
          throws GenericEntityException;



  /**
   * <p>
   * 根据所给的条件进行And查询唯一记录,所有的条件均是And(与)的关系。
   * </p>
   * 
   * @param entityName 要查找到的业务实体名称
   * @param fields 条件字段<code>Map</code>集合。<code>key</code>为业务实体属性字段,<code>value</code>为字段对应的值
   * @return 找到的业务实体<code>List</code>集合
   * @throws GenericEntityException
   */
  public Object findUniqueByAnd(String entityName, Map<String, Object> fields)
          throws GenericEntityException;

  /**
   * <p>
   * 根据所给的条件进行And查询唯一记录,所有的条件均是And(与)的关系。
   * </p>
   * 
   * @param entityName 要查找到的业务实体名称
   * @param fields 条件字段<code>Map</code>集合。<code>key</code>为业务实体属性字段,<code>value</code>为字段对应的值
   * 
   * @param useCache 是否使用缓存 <code>true</code>使用; <code>false</code>不使用
   * @return 找到的业务实体<code>List</code>集合
   * @throws GenericEntityException
   */
  public Object findUniqueByAnd(String entityName, Map<String, Object> fields, boolean useCache)
          throws GenericEntityException;


  /**
   * <p>
   * 根据所给的条件进行And查询,所有的条件均是And(与)的关系。
   * </p>
   * 
   * @param entityName 要查找到的业务实体名称
   * @param fields 条件字段<code>Map</code>集合。<code>key</code>为业务实体属性字段,<code>value</code>为字段对应的值
   * @return 找到的业务实体<code>List</code>集合
   * @throws GenericEntityException
   */
  public List<?> findByAnd(String entityName, Map<String, Object> fields)
          throws GenericEntityException;

  /**
   * <p>
   * 根据所给的条件进行And查询,所有的条件均是And(与)的关系。
   * </p>
   * 
   * @param entityName 要查找到的业务实体名称
   * @param fields 条件字段<code>Map</code>集合。<code>key</code>为业务实体属性字段,<code>value</code>为字段对应的值
   * @param orderBy 要排序的业务实体字段集合。默认为升序(asc)，<br/>
   *        如果指定降序，格式为 <code>字段名 desc</code>。 如 createdAt desc
   * @return 找到的业务实体<code>List</code>集合
   * @throws GenericEntityException
   */
  public List<?> findByAnd(String entityName, Map<String, Object> fields, List<String> orderBy)
          throws GenericEntityException;

  /**
   * <p>
   * 根据所给的条件进行And查询,所有的条件均是And(与)的关系。
   * </p>
   * 
   * @param entityName 要查找到的业务实体名称
   * @param fields 条件字段<code>Map</code>集合。<code>key</code>为业务实体属性字段,<code>value</code>为字段对应的值
   * @param orderBy 要排序的业务实体字段集合。默认为升序(asc)，<br/>
   *        如果指定降序，格式为 <code>字段名 desc</code>。 如 createdAt desc
   * @param useCache 是否使用缓存 <code>true</code>使用; <code>false</code>不使用
   * @return 找到的业务实体<code>List</code>集合
   * @throws GenericEntityException
   */
  public List<?> findByAnd(String entityName, Map<String, Object> fields, List<String> orderBy,
          boolean useCache) throws GenericEntityException;


  /**
   * <p>
   * 根据所给的条件进行And查询,所有的条件均是And(与)的关系。
   * </p>
   * 
   * @param entityName 要查找到的业务实体名称
   * @param fields 条件字段<code>Map</code>集合。<code>key</code>为业务实体属性字段,<code>value</code>为字段对应的值
   * @param fieldsToSelect 要返回的业务实体属性字段集合，如果指定，仅该集合中的字段返回值，其它字段为<code>null</code>
   * @param orderBy 要排序的业务实体字段集合。默认为升序(asc)，<br/>
   *        如果指定降序，格式为 <code>字段名 desc</code>。 如 createdAt desc
   * @param useCache 是否使用缓存 <code>true</code>使用; <code>false</code>不使用
   * @return 找到的业务实体<code>List</code>集合
   * @throws GenericEntityException
   */
  public List<?> findByAnd(String entityName, Map<String, Object> fields,
          Set<String> fieldsToSelect, List<String> orderBy, boolean useCache)
          throws GenericEntityException;


  /**
   * <p>
   * 根据指定的条件语句，查找业务实体
   * </p>
   * 
   * @param entityName 要查找到的业务实体名称
   * @param condition 条件语句
   * @param fieldsToSelect 要返回的业务实体属性字段集合，如果指定，仅该集合中的字段返回值，其它字段为<code>null</code>
   * @param orderBy 要排序的业务实体字段集合。默认为升序(asc)，<br/>
   *        如果指定降序，格式为 <code>字段名 desc</code>。 如 createdAt desc
   * @param useCache 是否使用缓存 <code>true</code>使用; <code>false</code>不使用
   * @return 找到的业务实体<code>List</code>集合
   * @throws GenericEntityException
   */
  public List<?> findList(String entityName, String condition, Set<String> fieldsToSelect,
          List<String> orderBy, boolean useCache) throws GenericEntityException;

  /**
   * <p>
   * 根据指定的条件语句，查找业务实体
   * </p>
   * 
   * @param entityName 要查找到的业务实体名称
   * @param condition 条件语句
   * @param fieldsToSelect 要返回的业务实体属性字段集合，如果指定，仅该集合中的字段返回值，其它字段为<code>null</code>
   * @param orderBy 要排序的业务实体字段集合。默认为升序(asc)，<br/>
   *        如果指定降序，格式为 <code>字段名 desc</code>。 如 createdAt desc
   * @return 找到的业务实体<code>List</code>集合
   * @throws GenericEntityException
   */
  public List<?> findList(String entityName, String condition, Set<String> fieldsToSelect,
          List<String> orderBy) throws GenericEntityException;


  /**
   * <p>
   * 根据指定的条件语句，查找业务实体
   * </p>
   * 
   * @param entityName 要查找到的业务实体名称
   * @param condition 条件语句
   * @param fieldsToSelect 要返回的业务实体属性字段集合，如果指定，仅该集合中的字段返回值，其它字段为<code>null</code>
   * @return 找到的业务实体<code>List</code>集合
   * @throws GenericEntityException
   */
  public List<?> findList(String entityName, String condition, Set<String> fieldsToSelect)
          throws GenericEntityException;



  /**
   * <p>
   * 根据指定的条件语句，查找业务实体
   * </p>
   * 
   * @param entityName 要查找到的业务实体名称
   * @param condition 条件语句
   * @return 找到的业务实体<code>List</code>集合
   * @throws GenericEntityException
   */
  public List<?> findList(String entityName, String condition) throws GenericEntityException;


  /**
   * <p>
   * 根据指定的条件语句，分页查找业务实体
   * </p>
   * 
   * @param entityName 要查找到的业务实体名称
   * @param condition 条件语句
   * @param fieldsToSelect 要返回的业务实体属性字段集合，如果指定，仅该集合中的字段返回值，其它字段为<code>null</code>
   * @param orderBy 要排序的业务实体字段集合。默认为升序(asc)，<br/>
   *        如果指定降序，格式为 <code>字段名 desc</code>。 如 createdAt desc
   * @param pageNo 当前第几页
   * @param pageSize 每页显示大小
   * @param useCache 是否使用缓存 <code>true</code>使用; <code>false</code>不使用
   * @return <code>Map</code>
   * 
   * <pre>
   *         key(<b>totalPage</b>)  :  总页数
   *         key(<b>totalEntry</b>) :  总记录数
   *         key(<b>list</b>)       :  找到的业务实体<code>List</code>集合
   *         </pre>
   * @throws GenericEntityException
   */
  public Map<String, Object> findPageByCondition(String entityName, String condition,
          Set<String> fieldsToSelect, List<String> orderBy, int pageNo, int pageSize,
          boolean useCache) throws GenericEntityException;



  /**
   * <p>
   * 根据指定的条件语句，分页查找业务实体
   * </p>
   * 
   * @param entityName 要查找到的业务实体名称
   * @param condition 条件语句
   * @param fieldsToSelect 要返回的业务实体属性字段集合，如果指定，仅该集合中的字段返回值，其它字段为<code>null</code>
   * @param orderBy 要排序的业务实体字段集合。默认为升序(asc)，<br/>
   *        如果指定降序，格式为 <code>字段名 desc</code>。 如 createdAt desc
   * @param pageNo 当前第几页
   * @param pageSize 每页显示大小
   * @return <code>Map</code>
   * 
   * <pre>
   *         key(<b>totalPage</b>)  :  总页数
   *         key(<b>totalEntry</b>) :  总记录数
   *         key(<b>list</b>)       :  找到的业务实体<code>List</code>集合
   *         </pre>
   * @throws GenericEntityException
   */
  public Map<String, Object> findPageByCondition(String entityName, String condition,
          Set<String> fieldsToSelect, List<String> orderBy, int pageNo, int pageSize)
          throws GenericEntityException;


  /**
   * <p>
   * 根据指定的条件语句，分页查找业务实体
   * </p>
   * 
   * @param entityName 要查找到的业务实体名称
   * @param condition 条件语句
   * @param fieldsToSelect 要返回的业务实体属性字段集合，如果指定，仅该集合中的字段返回值，其它字段为<code>null</code>
   * @param pageNo 当前第几页
   * @param pageSize 每页显示大小
   * @return <code>Map</code>
   * 
   * <pre>
   *         key(<b>totalPage</b>)  :  总页数
   *         key(<b>totalEntry</b>) :  总记录数
   *         key(<b>list</b>)       :  找到的业务实体<code>List</code>集合
   *         </pre>
   * @throws GenericEntityException
   */
  public Map<String, Object> findPageByCondition(String entityName, String condition,
          Set<String> fieldsToSelect, int pageNo, int pageSize) throws GenericEntityException;



  /**
   * <p>
   * 根据指定的条件语句，分页查找业务实体
   * </p>
   * 
   * @param entityName 要查找到的业务实体名称
   * @param condition 条件语句
   * @param pageNo 当前第几页
   * @param pageSize 每页显示大小
   * @return <code>Map</code>
   * 
   * <pre>
   *         key(<b>totalPage</b>)  :  总页数
   *         key(<b>totalEntry</b>) :  总记录数
   *         key(<b>list</b>)       :  找到的业务实体<code>List</code>集合
   *         </pre>
   * @throws GenericEntityException
   */
  public Map<String, Object> findPageByCondition(String entityName, String condition, int pageNo,
          int pageSize) throws GenericEntityException;


  /**
   * <p>
   * 根据指定的条件语句，分页查找业务实体
   * </p>
   * 
   * @param entityName 要查找到的业务实体名称
   * @param fields 条件字段<code>Map</code>集合。<code>key</code>为业务实体属性字段,<code>value</code>为字段对应的值
   * @param fieldsToSelect 要返回的业务实体属性字段集合，如果指定，仅该集合中的字段返回值，其它字段为<code>null</code>
   * @param orderBy 要排序的业务实体字段集合。默认为升序(asc)，<br/>
   *        如果指定降序，格式为 <code>字段名 desc</code>。 如 createdAt desc
   * @param pageNo 当前第几页
   * @param pageSize 每页显示大小
   * @param useCache 是否使用缓存 <code>true</code>使用; <code>false</code>不使用
   * @return <code>Map</code>
   * 
   * <pre>
   *         key(<b>totalPage</b>)  :  总页数
   *         key(<b>totalEntry</b>) :  总记录数
   *         key(<b>list</b>)       :  找到的业务实体<code>List</code>集合
   *         </pre>
   * @throws GenericEntityException
   */
  public Map<String, Object> findPageByAnd(String entityName, Map<String, Object> fields,
          Set<String> fieldsToSelect, List<String> orderBy, int pageNo, int pageSize,
          boolean useCache) throws GenericEntityException;



  /**
   * <p>
   * 根据指定的条件语句，分页查找业务实体
   * </p>
   * 
   * @param entityName 要查找到的业务实体名称
   * @param fields 条件字段<code>Map</code>集合。<code>key</code>为业务实体属性字段,<code>value</code>为字段对应的值
   * @param fieldsToSelect 要返回的业务实体属性字段集合，如果指定，仅该集合中的字段返回值，其它字段为<code>null</code>
   * @param orderBy 要排序的业务实体字段集合。默认为升序(asc)，<br/>
   *        如果指定降序，格式为 <code>字段名 desc</code>。 如 createdAt desc
   * @param pageNo 当前第几页
   * @param pageSize 每页显示大小
   * @return <code>Map</code>
   * 
   * <pre>
   *         key(<b>totalPage</b>)  :  总页数
   *         key(<b>totalEntry</b>) :  总记录数
   *         key(<b>list</b>)       :  找到的业务实体<code>List</code>集合
   *         </pre>
   * @throws GenericEntityException
   */
  public Map<String, Object> findPageByAnd(String entityName, Map<String, Object> fields,
          Set<String> fieldsToSelect, List<String> orderBy, int pageNo, int pageSize)
          throws GenericEntityException;



  /**
   * <p>
   * 根据指定的条件语句，分页查找业务实体
   * </p>
   * 
   * @param entityName 要查找到的业务实体名称
   * @param fields 条件字段<code>Map</code>集合。<code>key</code>为业务实体属性字段,<code>value</code>为字段对应的值
   * @param fieldsToSelect 要返回的业务实体属性字段集合，如果指定，仅该集合中的字段返回值，其它字段为<code>null</code>
   * @param pageNo 当前第几页
   * @param pageSize 每页显示大小
   * @return <code>Map</code>
   * 
   * <pre>
   *         key(<b>totalPage</b>)  :  总页数
   *         key(<b>totalEntry</b>) :  总记录数
   *         key(<b>list</b>)       :  找到的业务实体<code>List</code>集合
   *         </pre>
   * @throws GenericEntityException
   */
  public Map<String, Object> findPageByAnd(String entityName, Map<String, Object> fields,
          Set<String> fieldsToSelect, int pageNo, int pageSize) throws GenericEntityException;


  /**
   * <p>
   * 根据指定的条件语句，分页查找业务实体
   * </p>
   * 
   * @param entityName 要查找到的业务实体名称
   * @param fields 条件字段<code>Map</code>集合。<code>key</code>为业务实体属性字段,<code>value</code>为字段对应的值
   * @param pageNo 当前第几页
   * @param pageSize 每页显示大小
   * @return <code>Map</code>
   * 
   * <pre>
   *         key(<b>totalPage</b>)  :  总页数
   *         key(<b>totalEntry</b>) :  总记录数
   *         key(<b>list</b>)       :  找到的业务实体<code>List</code>集合
   *         </pre>
   * @throws GenericEntityException
   */
  public Map<String, Object> findPageByAnd(String entityName, Map<String, Object> fields,
          int pageNo, int pageSize) throws GenericEntityException;



  /**
   * <p>
   * 将实休对象解析成JSON字符串
   * </p>
   * 
   * @param entity 要解析的实体对象
   * @return 解析过后的JSON字符串
   * @throws GenericEntityException
   */
  public String toJson(Object entity) throws GenericEntityException;

  /**
   * <p>
   * 根据原始的SQL来查询
   * </p>
   * 
   * @param sql 要查询的SQL
   * @param params SQL语句对应的参数值
   * @return 查到的结果集合<code>List</code>
   * @throws GenericEntityException
   */
  public List<Map<String, Object>> findListByRawQuerySql(String sql, List<?> params)
          throws GenericEntityException;

}

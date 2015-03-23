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
package org.huihoo.ofbiz.smart.service.engine;

import java.util.Map;

import org.huihoo.ofbiz.smart.service.GenericServiceException;



/**
 * <p>
 * 服务引擎接口，它定义了服务引擎，必须要实现的接口。
 * </p>
 * <ul>
 * <li><code>runSync</code>服务的同步执行</li>
 * <li><code>runAsync</code>服务的异步执行</li> </li>
 * 
 * 
 * @author huangbohua
 * 
 */
public interface GenericEngine {
  /**
   * <p>
   * 同步执行服务并返回执行结果
   * </p>
   * 
   * @param localName 要执行的服务名称
   * @param context 服务执行上下文<code>Map</code>对象实例
   * @return 服务返回<code>Map</code>对象实例
   * @throws GenericServiceException
   */
  public Map<String, Object> runSync(String localName, Map<String, Object> context)
          throws GenericServiceException;


  /**
   * <p>
   * 异步执行服务
   * </p>
   * 
   * @param localName 要执行的服务名称
   * @param context 服务执行上下文<code>Map</code>对象实例
   * @throws GenericServiceException
   */
  public void runAsync(String localName, Map<String, Object> context)
          throws GenericServiceException;

}

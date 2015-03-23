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
import org.huihoo.ofbiz.smart.service.ServiceDispatcher;



/**
 * <p>
 * 抽象的异步服务引擎类，继承自{@link com.dnx.service.engine.AbstractEngine}<br/>
 * 它实现了服务的异步执行。所有的服务引擎实现类，都应该继承该类。
 * </p>
 * 
 * @author huangbohua
 * 
 */
public abstract class GenericAsyncEngine extends AbstractEngine {


  public GenericAsyncEngine(ServiceDispatcher dispatcher) {
    super(dispatcher);
  }

  public abstract Map<String, Object> runSync(String localName, Map<String, Object> context)
          throws GenericServiceException;

  @Override
  public void runAsync(String localName, Map<String, Object> context)
          throws GenericServiceException {

    throw new GenericServiceException("Not Implementation Yet");
  }

}

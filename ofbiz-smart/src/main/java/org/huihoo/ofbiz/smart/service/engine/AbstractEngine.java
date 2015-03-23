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

import org.huihoo.ofbiz.smart.service.ServiceDispatcher;

/**
 * <p>
 * 抽象服务引擎类，它继承自{@link com.dnx.service.engine.GenericEngine}<br/>
 * 同时，它定义了每个服务引擎实现类 必须继承的构造方法
 * </p>
 * 
 * @author huangbohua
 * @version 1.0
 */
public abstract class AbstractEngine implements GenericEngine {
  protected ServiceDispatcher dispatcher = null;

  public AbstractEngine(ServiceDispatcher dispatcher) {
    this.dispatcher = dispatcher;
  }
}

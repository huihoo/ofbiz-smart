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
package org.huihoo.ofbiz.smart.service.engine;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.huihoo.ofbiz.smart.base.utils.CommUtils;
import org.huihoo.ofbiz.smart.service.GenericServiceException;
import org.huihoo.ofbiz.smart.service.ServiceDispatcher;




/**
 * <p>
 * 服务引擎工厂类，主要负责服务引擎的获取
 * </p>
 * 
 * @author huangbohua
 *
 */
public class GenericEngineFactory {

  protected ServiceDispatcher dispatcher = null;
  protected Map<String, GenericEngine> engines = null;

  public GenericEngineFactory(ServiceDispatcher dispatcher) {
    this.dispatcher = dispatcher;
    engines = new HashMap<String, GenericEngine>();
  }

  /**
   * <p>
   * 获取服务引擎
   * </p>
   * 
   * @param engineName 要获取的服务引擎名称
   * @return
   * @throws GenericServiceException
   */
  public GenericEngine getGenericEngine(String engineName) throws GenericServiceException {
    GenericEngine engine = engines.get(engineName);
    if (engine == null) {
      synchronized (GenericEngineFactory.class) {
        engine = engines.get(engineName);
        if (engine == null) {
          try {
            
            String className = null;
            String packageName = getClass().getPackage().getName();
            
            if ("java".equals(engineName)) {
              className = packageName+"."+"StandardJavaEngine";
            }else if("simple".equals(engineName)){ 
              className = packageName+"."+"SimpleServiceEngine";
            }
            
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            Class<?> c = loader.loadClass(className);
            Constructor<GenericEngine> cn =
                    CommUtils.cast(c.getConstructor(ServiceDispatcher.class));
            
            engine = cn.newInstance(dispatcher);
            
          } catch (Exception e) {
            throw new GenericServiceException(e.getMessage(), e);
          }
          
          if (engine != null) {
            engines.put(engineName, engine);
          }
          
        }
      }
    }
    return engine;
  }


  public Map<String, GenericEngine> getEngines() {
    return engines;
  }

}

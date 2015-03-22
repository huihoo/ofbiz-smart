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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.huihoo.ofbiz.smart.base.utils.CommUtils;
import org.huihoo.ofbiz.smart.base.utils.Debug;
import org.huihoo.ofbiz.smart.service.GenericServiceException;
import org.huihoo.ofbiz.smart.service.ModelService;
import org.huihoo.ofbiz.smart.service.ServiceDispatcher;





/**
 * <p>
 * 标准的Java服务引擎。它执行标准的Java实现服务
 * </p>
 * 
 * @author huangbohua
 *
 */
public class StandardJavaEngine extends GenericAsyncEngine {
  private static final String module = StandardJavaEngine.class.getName();

  public StandardJavaEngine(ServiceDispatcher dispatcher) {
    super(dispatcher);
  }

  @Override
  public Map<String, Object> runSync(String localName, Map<String, Object> context)
          throws GenericServiceException {
    Object result = serviceInvoke(localName, context);
    if (result == null || !(result instanceof Map<?, ?>))
      throw new GenericServiceException("服务 [" + localName + " ] 返回对象类型不是Map类型");
    return CommUtils.checkMap(result);
  }

  private Object serviceInvoke(String localName, Map<String, Object> context)
          throws GenericServiceException {
    if (localName == null) {
      Debug.logError("本地服务名称为空",module);
      throw new GenericServiceException("本地服务名称为空");
    }

    if (context == null) {
      Debug.logError("服务参数上下文为空",module);
      throw new GenericServiceException("服务参数上下文为空");
    }

    ModelService modelService = this.dispatcher.getLocalContext(localName);
    if (modelService == null) {
      Debug.logError("名为[" + localName + "]的服务模型为空",module);
      throw new GenericServiceException("名为[" + localName + "]的服务模型为空");
    }

    if (modelService.location == null || modelService.invoke == null) {
      throw new GenericServiceException("服务 [" + modelService.name + "] 缺失 location 或 invoke 参数");
    }
    
    Object result = null;
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    try {
      Class<?> c = cl.loadClass(modelService.location);
      Method m = c.getMethod(modelService.invoke, Map.class);
      if (Modifier.isStatic(m.getModifiers())) {
        result = m.invoke(null, context);
      } else {
        result = m.invoke(c.newInstance(), context);
      }
    } catch (ClassNotFoundException e) {
      Debug.logError(e,"无法找到服务[" + modelService.name + "]的实现类[" + modelService.location + "]",module);
      throw new GenericServiceException("无法找到服务[" + modelService.name + "]的实现类["+ modelService.location + "]");
    } catch (NoSuchMethodException e) {
      Debug.logError(e,"服务[" + modelService.name + "]的实现类[" + modelService.location + "]未定义的方法["+ modelService.invoke + "]",module);
      throw new GenericServiceException("服务[" + modelService.name + "]的实现类["+ modelService.location + "]未定义的方法[" + modelService.invoke + "]");
    } catch (SecurityException e) {
      Debug.logError(e,"服务[" + modelService.name + "]调用["+ modelService.location+"]["+modelService.invoke+ "]安全异常",module);
      throw new GenericServiceException("服务[" + modelService.name + "]调用["+ modelService.location+"]["+modelService.invoke+ "]安全异常");
    } catch (IllegalAccessException e) {
      Debug.logError(e,"服务[" + modelService.name + "]调用["+ modelService.location+"]["+modelService.invoke+ "]非法访问异常",module);
      throw new GenericServiceException("服务[" + modelService.name + "]调用["+ modelService.location+"]["+modelService.invoke+ "]非法访问异常");
    } catch (IllegalArgumentException e) {
      Debug.logError(e,"服务[" + modelService.name + "]调用["+ modelService.location+"]["+modelService.invoke+ "]非法参数异常",module);
      throw new GenericServiceException("服务[" + modelService.name + "]调用["+ modelService.location+"]["+modelService.invoke+ "]非法参数异常");
    } catch (InvocationTargetException e) {
      Debug.logError(e,"服务[" + modelService.name + "]调用["+ modelService.location+"]["+modelService.invoke+ "]方法调用异常",module);
      throw new GenericServiceException("服务[" + modelService.name + "]调用["+ modelService.location+"]["+modelService.invoke+ "]调用对象异常");
    } catch (InstantiationException e) {
      Debug.logError(e,"服务[" + modelService.name + "]调用["+ modelService.location+"]["+modelService.invoke+ "]实例化异常",module);
      throw new GenericServiceException("服务[" + modelService.name + "]调用["+ modelService.location+"]["+modelService.invoke+ "]实例化异常");
    }
    return result;
  }

}

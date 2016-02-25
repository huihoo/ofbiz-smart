package org.huihoo.ofbiz.smart.service.engine;


import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.huihoo.ofbiz.smart.base.util.CommUtil;
import org.huihoo.ofbiz.smart.base.util.Log;
import org.huihoo.ofbiz.smart.service.GenericServiceException;
import org.huihoo.ofbiz.smart.service.ServiceDispatcher;
import org.huihoo.ofbiz.smart.service.ServiceModel;

public class StandardJavaEngine extends GenericAsyncEngine {
  private final static String TAG = StandardJavaEngine.class.getName();
  
  private final static Map<String, Class<?>> SERVICE_CLAZZ_MAP = new ConcurrentHashMap<>();
  
  public StandardJavaEngine(ServiceDispatcher serviceDispatcher) {
    super(serviceDispatcher);
  }

  @Override
  public Map<String, Object> runSync(String serviceName, Map<String, Object> ctx) throws GenericServiceException {
    Object result = serviceCall(serviceName, ctx);
    if (result == null || !(result instanceof Map<?, ?>)) {
      throw new GenericServiceException("Service [" + serviceName + "] return object must be a Map<String,Object> instance.");
    }
    return CommUtil.checkMap(result);
  }

  @Override
  public String getName() {
    return "java";
  }

  private Object serviceCall(String serviceName, Map<String, Object> ctx) throws GenericServiceException{
    if (CommUtil.isEmpty(serviceName)) {
      throw new GenericServiceException("The serviceName is empty.");
    }
    if (ctx == null) {
      throw new GenericServiceException("The service context is null.");
    }

    ServiceModel serviceModel = serviceDispatcher.getServiceContextMap().get(serviceName);
    if (serviceModel == null) {
      throw new GenericServiceException("Unable to locate the service [" + serviceName + "]");
    }

    if (CommUtil.isEmpty(serviceModel.invoke) || CommUtil.isEmpty(serviceModel.location)) {
      throw new GenericServiceException("The service [" + serviceName + "] has not been set invoke or location");
    }
    
    
    Object result = null;
    
    ClassLoader cl = Thread.currentThread().getContextClassLoader();
    Class<?> c = SERVICE_CLAZZ_MAP.get(serviceModel.location);
    try {
      if (c == null) {
          c = cl.loadClass(serviceModel.location);
          SERVICE_CLAZZ_MAP.put(serviceModel.location, c);
      }
      
      MethodHandles.Lookup lookup = MethodHandles.lookup();
      MethodHandle mh = lookup.findStatic(c, serviceModel.invoke, MethodType.methodType(Map.class,Map.class));
      result = mh.invoke(ctx);
      return result;
    } catch (Throwable e) {
      String msg = String.format("Service[%s] call[%s] failed.", serviceModel.location,serviceModel.invoke);
      Log.w(msg, TAG);
      throw new GenericServiceException(e);
    }
  }
}

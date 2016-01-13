package org.huihoo.ofbiz.smart.service.engine;


import org.huihoo.ofbiz.smart.base.util.CommUtil;
import org.huihoo.ofbiz.smart.base.util.Log;
import org.huihoo.ofbiz.smart.service.GenericServiceException;
import org.huihoo.ofbiz.smart.service.ServiceDispatcher;
import org.huihoo.ofbiz.smart.service.ServiceModel;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
      Method m = c.getMethod(serviceModel.invoke, Map.class);
      if (Modifier.isStatic(m.getModifiers())) {
        result = m.invoke(null, ctx);
      } else {
        result = m.invoke(c.newInstance(), ctx);
      }
    } catch (ClassNotFoundException e) {
      String msg = String.format("Service class [%s] not found.", serviceModel.location);
      Log.w(msg, TAG);
      throw new GenericServiceException(msg);
    } catch (NoSuchMethodException e) {
      String msg = String.format("Method [%s] of service class [%s] not found.", serviceModel.location,serviceModel.invoke);
      Log.w(msg, TAG);
      throw new GenericServiceException(msg);
    } catch (SecurityException e) {
      String msg = String.format("Method [%s] of service class [%s] can not access.", serviceModel.location,serviceModel.invoke);
      Log.w(msg, TAG);
      throw new GenericServiceException(msg);
    } catch (IllegalAccessException e) {
      String msg = String.format("Method [%s] of service class [%s] can not access.", serviceModel.location,serviceModel.invoke);
      Log.w(msg, TAG);
      throw new GenericServiceException(e);
    } catch (IllegalArgumentException e) {
      String msg = String.format("Method [%s] of service class [%s] can not access.", serviceModel.location,serviceModel.invoke);
      Log.w(msg, TAG);
      throw new GenericServiceException(e);
    } catch (InvocationTargetException e) {
      String msg = String.format("Method [%s] of service class [%s] can not access.", serviceModel.location,serviceModel.invoke);
      Log.w(msg, TAG);
      throw new GenericServiceException(e);
    } catch (InstantiationException e) {
      String msg = String.format("Method [%s] of service class [%s] can not access.", serviceModel.location,serviceModel.invoke);
      Log.w(msg, TAG);
      throw new GenericServiceException(e);
    }
    return result;
  }
}

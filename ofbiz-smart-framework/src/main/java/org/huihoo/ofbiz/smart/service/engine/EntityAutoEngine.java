package org.huihoo.ofbiz.smart.service.engine;



import org.huihoo.ofbiz.smart.base.C;
import org.huihoo.ofbiz.smart.base.util.CommUtil;
import org.huihoo.ofbiz.smart.base.util.ServiceUtil;
import org.huihoo.ofbiz.smart.entity.Delegator;
import org.huihoo.ofbiz.smart.service.GenericServiceException;
import org.huihoo.ofbiz.smart.service.ServiceDispatcher;
import org.huihoo.ofbiz.smart.service.ServiceModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityAutoEngine extends GenericAsyncEngine {
  private final static String TAG = EntityAutoEngine.class.getName();

  private final static Map<String, Class<?>> ENGITY_CLAZZ_MAP = new ConcurrentHashMap<>();

  public EntityAutoEngine(ServiceDispatcher serviceDispatcher) {
    super(serviceDispatcher);
  }

  @Override
  public Map<String, Object> runSync(String serviceName, Map<String, Object> ctx) throws GenericServiceException {
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

    if (CommUtil.isEmpty(serviceModel.invoke) || CommUtil.isEmpty(serviceModel.engineName)) {
      throw new GenericServiceException("The service [" + serviceName + "] has not been set invoke or engineName");
    }

    Delegator delegator = ServiceUtil.getDelegator(ctx);
    if (delegator == null) {
      throw new GenericServiceException("Service [" + serviceName + "] required to set Delegator.");
    }

    Class<?> engityClazz = ENGITY_CLAZZ_MAP.get(serviceModel.engityName);
    if (engityClazz == null) {
      try {
        engityClazz = Thread.currentThread().getContextClassLoader().loadClass(serviceModel.engityName);
        ENGITY_CLAZZ_MAP.put(serviceModel.engityName, engityClazz);
      } catch (ClassNotFoundException e) {
        throw new GenericServiceException("EngityClass [" + serviceModel.engityName + "] not found.");
      }
    }

    switch (serviceModel.invoke) {
      case C.SERVICE_ENGITYAUTO_CREATE:
        
        break;
      case C.SERVICE_ENGITYAUTO_UPDATE:

        break;
      case C.SERVICE_ENGITYAUTO_REMOVE:

        break;
      case C.SERVICE_ENGITYAUTO_FINDBYID:

        break;
      case C.SERVICE_ENGITYAUTO_FINDBYAND:

        break;
      case C.SERVICE_ENGITYAUTO_FINDLIST:

        break;
      case C.SERVICE_ENGITYAUTO_FINDPAGEBYAND:
      case C.SERVICE_ENGITYAUTO_FINDPAGEBYCOND:
        
        break;
      default:
        //Ingore..
        break;
    }

    return null;
  }

  @Override
  public String getName() {
    return "entityAuto";
  }
  
  
  
  //=============================================================
  // Private Method
  //=============================================================
  
}

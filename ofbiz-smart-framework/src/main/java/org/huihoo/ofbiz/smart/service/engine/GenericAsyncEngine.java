package org.huihoo.ofbiz.smart.service.engine;

import org.huihoo.ofbiz.smart.base.util.Log;
import org.huihoo.ofbiz.smart.service.GenericServiceException;
import org.huihoo.ofbiz.smart.service.ServiceDispatcher;

import java.util.Map;


public abstract class GenericAsyncEngine extends AbstractEngine {

  public GenericAsyncEngine(ServiceDispatcher serviceDispatcher) {
    super(serviceDispatcher);
  }

  @Override
  public abstract Map<String, Object> runSync(String serviceName, Map<String, Object> ctx)
      throws GenericServiceException;

  @Override
  public void runAsync(String serviceName, Map<String, Object> ctx) throws GenericServiceException {
    // TODO 异步待实现
    Log.w("Not Implementation Yet.", GenericAsyncEngine.class.getName());
  }

  @Override
  public String getName() {
    return null;
  }
}

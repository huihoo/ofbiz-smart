package org.huihoo.ofbiz.smart.service;

import java.util.Map;

import org.huihoo.ofbiz.smart.base.C;
import org.huihoo.ofbiz.smart.base.util.CommUtil;
import org.huihoo.ofbiz.smart.base.util.Log;
import org.huihoo.ofbiz.smart.base.util.ServiceUtil;
import org.huihoo.ofbiz.smart.entity.BaseTestCase;
import org.huihoo.ofbiz.smart.entity.Customer;
import org.junit.Assert;
import org.junit.Test;



public class ServiceDispatcherTest extends BaseTestCase {
  private final static String TAG = ServiceDispatcherTest.class.getName();

  @Test
  public void testAllInOne() throws GenericServiceException {
    Log.d("start testing.....", TAG);
    Map<String, Object> ctx = CommUtil.toMap(C.CTX_DELETAGOR, delegator);
    ServiceDispatcher serviceDispatcher = new ServiceDispatcher(delegator);
    Map<String,Object> resultMap = serviceDispatcher.runSync("serviceNotFound", ctx);
    Log.d("resultMap > " + resultMap, TAG);
    Assert.assertEquals(true, ServiceUtil.isError(resultMap));
    
    ServiceModel sm = new ServiceModel();
    sm.engineName = "entityAuto";
    sm.entityName = Customer.class.getName();
    sm.name = sm.engineName + "#" + C.SERVICE_ENGITYAUTO_CREATE;
    sm.invoke = C.SERVICE_ENGITYAUTO_CREATE;
    serviceDispatcher.registerService(sm);
    
    resultMap = serviceDispatcher.runSync(sm.name, ctx);
    Log.d("resultMap > " + resultMap, TAG);
    Assert.assertEquals(true, ServiceUtil.isError(resultMap));
    Assert.assertEquals(true, resultMap.containsKey(C.RESPOND_VALIDATION_ERRORS));
    
    ctx.put("firstName", "huang");
    ctx.put("lastName", "baihua");
    ctx.put("status", "ACTIVE");
    ctx.put("gender", "Male");
    ctx.put("level", 2);
    ctx.put("birthday", "1986-05-18");
    resultMap = serviceDispatcher.runSync(sm.name, ctx);
    Log.d("resultMap > " + resultMap, TAG);
    Assert.assertEquals(true, ServiceUtil.isSuccess(resultMap));
    Assert.assertEquals(true, resultMap.containsKey(C.ENTITY_MODEL_NAME));
  }
}

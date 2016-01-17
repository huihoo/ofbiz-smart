package test.service;

import java.util.List;
import java.util.Map;

import org.huihoo.ofbiz.smart.base.C;
import org.huihoo.ofbiz.smart.base.util.CommUtil;
import org.huihoo.ofbiz.smart.base.util.Log;
import org.huihoo.ofbiz.smart.base.util.ServiceUtil;
import org.huihoo.ofbiz.smart.base.validation.ConstraintViolation;
import org.huihoo.ofbiz.smart.service.GenericServiceException;
import org.huihoo.ofbiz.smart.service.ServiceDispatcher;
import org.huihoo.ofbiz.smart.service.ServiceModel;
import org.junit.Assert;
import org.junit.Test;

import test.entity.BaseTestCase;
import test.entity.Customer;



public class ServiceDispatcherTest extends BaseTestCase {
  private final static String TAG = ServiceDispatcherTest.class.getName();

  @SuppressWarnings("unchecked")
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
    Log.d("response > " + resultMap, TAG);
    Assert.assertEquals(true, ServiceUtil.isSuccess(resultMap));
    Assert.assertEquals(true, resultMap.containsKey(C.ENTITY_MODEL_NAME));
    
    resultMap = serviceDispatcher.runSync("createOrderFail", ctx);
    Log.d("response > " + resultMap, TAG);
    Assert.assertEquals(true, ServiceUtil.isError(resultMap));
    
    Map<String, List<ConstraintViolation>> violationMap = (Map<String, List<ConstraintViolation>>) 
                                                                resultMap.get(C.RESPOND_VALIDATION_ERRORS);
    Assert.assertNotNull(violationMap);
    Assert.assertEquals(true, violationMap.containsKey("createOrderFail"));
    List<ConstraintViolation> violations = violationMap.get("createOrderFail");
    Assert.assertEquals(3, violations.size());
    
    ctx.put("fromChannel", "WEB");
    ctx.put("paymentMethod", "PAYPAL_PAY");
    ctx.put("userId", "10000");
    resultMap = serviceDispatcher.runSync("createOrderFail", ctx);
    Log.d("response > " + resultMap, TAG);
    Assert.assertEquals(true, ServiceUtil.isError(resultMap));
    
    
    ctx.put("fromChannel", "WEB");
    ctx.put("paymentMethod", "PAYPAL_PAY");
    ctx.put("userId", "10000");
    resultMap = serviceDispatcher.runSync("createOrderSuccess", ctx);
    Log.d("response > " + resultMap, TAG);
    Assert.assertEquals(true, ServiceUtil.isSuccess(resultMap));
  }
}

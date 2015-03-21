package test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.huihoo.ofbiz.smart.base.utils.CommUtils;
import org.huihoo.ofbiz.smart.base.utils.Debug;
import org.huihoo.ofbiz.smart.base.utils.ServiceUtils;
import org.huihoo.ofbiz.smart.entity.Delegator;
import org.huihoo.ofbiz.smart.entity.EbeanDelegator;
import org.huihoo.ofbiz.smart.entity.GenericEntityException;
import org.huihoo.ofbiz.smart.service.GenericServiceException;
import org.huihoo.ofbiz.smart.service.ServiceDispatcher;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import entity.Customer;
import entity.Product;

public class ServiceDispatcherTest {
  private static final String module = ServiceDispatcherTest.class.getName();
  Delegator delegator;
  ServiceDispatcher dispatcher;
  @Before
  public void init() {
    Properties p = new Properties();
    try {
      p.load(getClass().getResourceAsStream("/datasource-test.properties"));
      delegator = new EbeanDelegator("h2", "entity", p);
      dispatcher = new ServiceDispatcher(delegator);
    } catch (GenericEntityException | IOException | GenericServiceException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testAllInOne() throws GenericEntityException {
    Assert.assertNotNull(delegator);
    Assert.assertNotNull(dispatcher);
    Debug.logDebug(""+dispatcher.getLocalContext(), module);
    
    Map<String,Object> ctx = new HashMap<>();
    ctx.put("name", "Peter");
    ctx.put("countryCode", "CN");
    ctx.put("line1", "line-1");
    ctx.put("line2", "line-2");
    ctx.put("city", "ChengDu");
    Map<String,Object> result = dispatcher.runSync("createCustomer", ctx);
    Assert.assertEquals(true, ServiceUtils.isSuccess(result));
    
    Customer customer = (Customer) result.get("customer");
    Assert.assertEquals("Peter", customer.getName());
    
    Product testProduct = new Product();
    testProduct.setName("Test-Product");
    testProduct.setSku("SKU-001");
    delegator.save(testProduct);
    
    ctx = new HashMap<>();
    ctx.put("customerId", customer.getId());
    ctx.put("productId", testProduct.getId());
    ctx.put("orderQty", BigDecimal.ONE);
    
    result = dispatcher.runSync("createOrder", ctx);
    Debug.logDebug(""+CommUtils.printMap(result), module);
    Assert.assertEquals(true, ServiceUtils.isSuccess(result));
  }
}

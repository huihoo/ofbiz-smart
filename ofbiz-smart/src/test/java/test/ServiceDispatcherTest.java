package test;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.huihoo.ofbiz.smart.base.utils.CommUtils;
import org.huihoo.ofbiz.smart.base.utils.Debug;
import org.huihoo.ofbiz.smart.base.utils.ServiceUtils;
import org.huihoo.ofbiz.smart.entity.GenericEntityException;

import org.junit.Assert;

import org.junit.Test;

import entity.Customer;
import entity.Product;

public class ServiceDispatcherTest extends BaseTest {
  private static final String module = ServiceDispatcherTest.class.getName();


  @Test
  public void testAllInOne() throws GenericEntityException {
    Assert.assertNotNull(delegator);
    Assert.assertNotNull(dispatcher);
    Assert.assertEquals(true,dispatcher.getLocalContext().size()>0);
    Debug.logDebug("" + dispatcher.getLocalContext(), module);

    Map<String, Object> ctx = new HashMap<>();
    ctx.put("name", "Peter");
    ctx.put("countryCode", "CN");
    ctx.put("line1", "line-1");
    ctx.put("line2", "line-2");
    ctx.put("city", "ChengDu");
    Map<String, Object> result = dispatcher.runSync("createCustomer", ctx);
    Assert.assertEquals(true, ServiceUtils.isSuccess(result));

    Customer customer = (Customer) result.get("customer");
    Assert.assertEquals("Peter", customer.getName());

    Product testProduct = new Product();
    testProduct.setName("Test-Product");
    testProduct.setSku("SKU-001");
    delegator.save(testProduct);

    ctx = new HashMap<>();
    ctx.put("customerId", ""+customer.getId());
    ctx.put("productId", ""+testProduct.getId());
    ctx.put("orderQty", ""+BigDecimal.ONE);

    result = dispatcher.runSync("createOrder", ctx);
    Debug.logDebug("" + CommUtils.printMap(result), module);
    Assert.assertEquals(true, ServiceUtils.isSuccess(result));
  }
}

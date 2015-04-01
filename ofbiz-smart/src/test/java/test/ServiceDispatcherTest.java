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
package test;


import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.huihoo.ofbiz.smart.base.utils.CommUtils;
import org.huihoo.ofbiz.smart.base.utils.Debug;
import org.huihoo.ofbiz.smart.base.utils.ServiceUtils;

import org.junit.Assert;

import org.junit.Test;

import entity.Customer;
import entity.Product;

public class ServiceDispatcherTest extends BaseTest {
  private static final String module = ServiceDispatcherTest.class.getName();


  @Test
  public void testAllInOne()  {
    try{
      
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
    }catch(Exception e){
      
      //Debug级别的日志
      Debug.logDebug("msg", module);
      //Info级别的日志
      Debug.logInfo("msg", module);
      //Info级别带参数的日志
      Debug.logInfo("msg1% msg2%", module, new Object[]{"msg-value1","msg-value2"});
      //Waring级别的日志
      Debug.logWaring("msg", module);
      //Error级别的日志
      Debug.logError(e, "", module);
      
      
    }
  }
}

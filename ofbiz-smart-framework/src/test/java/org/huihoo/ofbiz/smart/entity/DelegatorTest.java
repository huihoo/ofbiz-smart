package org.huihoo.ofbiz.smart.entity;


import org.huihoo.ofbiz.smart.base.util.CommUtil;
import org.huihoo.ofbiz.smart.base.util.Log;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


public class DelegatorTest extends BaseTestCase{
  private final static String TAG = DelegatorTest.class.getName();
  @Test
  public void testAllInOne() throws GenericEntityException {
    Customer customer = new Customer();
    customer.setName("Peter");
    customer.setGender("Male");
    delegator.save(customer);
    Assert.assertEquals("Peter",customer.getName());

    Customer newestCustomer = (Customer) delegator.findById(Customer.class,customer.getId());
    Assert.assertNotNull(newestCustomer);

    newestCustomer = (Customer) delegator.findById(Customer.class,customer.getId(),true);
    Assert.assertNotNull(newestCustomer);

    newestCustomer = (Customer) delegator.findById(Customer.class,customer.getId(),true);
    Assert.assertNotNull(newestCustomer);
    Assert.assertEquals(1,delegator.getCache().getHitCount());

    newestCustomer = (Customer) delegator.findUniqueByAnd(Customer.class, CommUtil.toMap("name","Peter"));
    Assert.assertNotNull(newestCustomer);

    newestCustomer = (Customer) delegator.findUniqueByAnd(Customer.class, CommUtil.toMap("name","PeterNotFound"));
    Assert.assertNull(newestCustomer);

    Set<String> selectToFields = new LinkedHashSet<>();
    selectToFields.add("name");
    newestCustomer = (Customer) delegator.findUniqueByAnd(Customer.class, CommUtil.toMap("name","Peter"),selectToFields,true);
    Assert.assertNotNull(newestCustomer.getName());

    newestCustomer = (Customer) delegator.findUniqueByAnd(Customer.class, CommUtil.toMap("name","Peter"),selectToFields,true);
    Assert.assertNotNull(newestCustomer);
    Assert.assertEquals(2,delegator.getCache().getHitCount());

    List<?> customerList = delegator.findList(Customer.class,"{name,eq,Peter}",selectToFields, Arrays.asList(new String[]{"birthday desc"}));
    Assert.assertNotNull(customerList);
    Assert.assertEquals(1,customerList.size());

    customerList = delegator.findList(Customer.class,"{name,eq,Peter,or,gender,eq,Male}",selectToFields, Arrays.asList(new String[]{"birthday desc"}));
    Assert.assertNotNull(customerList);
    Assert.assertEquals(1,customerList.size());
  }
}   

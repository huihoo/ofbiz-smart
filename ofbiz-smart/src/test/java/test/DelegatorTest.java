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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.huihoo.ofbiz.smart.base.utils.CommUtils;
import org.huihoo.ofbiz.smart.base.utils.Debug;
import org.huihoo.ofbiz.smart.entity.GenericEntityException;
import org.junit.Assert;
import org.junit.Test;

import entity.Address;
import entity.Country;
import entity.Customer;
import entity.CustomerStatus;
import entity.Order;
import entity.OrderDetail;
import entity.OrderStatus;
import entity.Product;

public class DelegatorTest  extends BaseTest{
  private static final String module = DelegatorTest.class.getName();
  

  @SuppressWarnings("unchecked")
  @Test
  public void testAllInOne() {
    Assert.assertNotNull(delegator);
    try {
      Debug.logDebug("创建国家代码", module);
      List<Country> countries = new ArrayList<>();
      Country cn = new Country();
      cn.setCode("CN");
      cn.setName("China");
      countries.add(cn);
      
      Country en = new Country();
      en.setCode("EN");
      en.setName("England");
      countries.add(en);
      
      Country usa = new Country();
      usa.setCode("USA");
      usa.setName("America");
      countries.add(usa);
      
      delegator.save(countries);
      
      Country cnFromDb = (Country) delegator.findById("Country", "CN");
      Assert.assertNotNull(cnFromDb);
      Assert.assertEquals("CN", cnFromDb.getCode());
      Assert.assertEquals("China", cnFromDb.getName());
      
      Country enFromDb = (Country) delegator.findUniqueByAnd("Country", CommUtils.toMap("code","EN"));
      Assert.assertNotNull(enFromDb);
      Assert.assertEquals("EN", enFromDb.getCode());
      Assert.assertEquals("England", enFromDb.getName());
      
      List<Country> countriesFromDb = (List<Country>) delegator.findList("Country", null);
      Assert.assertNotNull(countriesFromDb);
      Assert.assertEquals(3, countriesFromDb.size());
      Debug.logDebug("Countries -> "+countriesFromDb, module);
      
      CustomerStatus activeStatus = new CustomerStatus();
      activeStatus.setCode("ACTIVE");
      activeStatus.setTitle("Active Customer");
      delegator.save(activeStatus);
      
      Debug.logDebug("创建客户资料", module);
      
      Map<Country,List<String>> cityMap = new HashMap<Country, List<String>>();
      List<String> cityList = new ArrayList<>();
      cityList.add("成都");
      cityList.add("北京");
      cityList.add("重庆");
      cityList.add("深圳");
      cityMap.put(cn, cityList);
      
      cityList = new ArrayList<>();
      cityList.add("New York");
      cityList.add("Los Angeles");
      cityList.add("Chicago");
      cityList.add("休士顿");
      cityMap.put(usa, cityList);
      
      cityList = new ArrayList<>();
      cityList.add("London");
      cityList.add("Manchester");
      cityList.add("York");
      cityMap.put(en, cityList);
     
      int length = 100;
      List<Customer> customers = new ArrayList<>(length);
      for(int i = 0; i < length; i++){
        Address addr = new Address();
        Country c = countriesFromDb.get(new Random().nextInt(3));
        addr.setCountryCode(c);
        List<String> cities = cityMap.get(c);
        addr.setCity(cities.get(new Random().nextInt(cities.size())));
        addr.setLine1("Address Line 1");
        addr.setLine2("Address Line 2");
        addr.setRegion("Region "+(i+1));
        delegator.save(addr);
        
        Customer customer = new Customer();
        customer.setName("Customer-Name-"+(i+1));
        customer.setShippingAddress(addr);
        customer.setBillingAddress(addr);
        customer.setStatusCode(activeStatus);
        customers.add(customer);
      }
      delegator.save(customers);
      
      List<Address> addressFromDb = (List<Address>) delegator.findList("Address", null);
      Assert.assertNotNull(addressFromDb);
      Assert.assertEquals(length, addressFromDb.size());
      
      List<Customer> customersFromDb = (List<Customer>) delegator.findList("Customer", null);
      Assert.assertNotNull(customersFromDb);
      Assert.assertEquals(length, customersFromDb.size());
      
      Debug.logDebug("Customers -> "+customersFromDb, module);
      
      
      List<String> orderByList = new ArrayList<>();
      orderByList.add("id desc");
      List<Customer> findCustomers = (List<Customer>) 
              delegator.findByAnd("Customer", CommUtils.toMap("statusCode.code","ACTIVE"), orderByList);
      Assert.assertNotNull(findCustomers);
      Assert.assertEquals(length, findCustomers.size());
      Assert.assertEquals(100L, findCustomers.get(0).getId().longValue());
      Assert.assertEquals(1L, findCustomers.get(length-1).getId().longValue());
      
      
      findCustomers = (List<Customer>) 
              delegator.findByAnd("Customer", CommUtils.toMap("statusCode.code","INACTIVE"), orderByList);
      Assert.assertEquals(0, findCustomers.size());
      
      Debug.logDebug("创建产品资料", module);
      int pLength = 10;
      List<Product> products = new ArrayList<>(pLength);
      for (int i = 0; i < pLength; i++) {
        Product p = new Product();
        p.setName("Product-"+(i+1));
        p.setSku("P-SKU-"+(i+1));
        products.add(p);
      }
      delegator.save(products);
      
      List<Product> productsFromDb = (List<Product>) delegator.findList("Product", null);
      Assert.assertNotNull(productsFromDb);
      Assert.assertEquals(pLength, productsFromDb.size());
      Debug.logDebug("Products -> "+productsFromDb, module);
      
      Debug.logDebug("模拟客户购买商品", module);
      
      List<OrderStatus> orderStatus = new ArrayList<>();
      OrderStatus orderCreated = new OrderStatus();
      orderCreated.setCode("ORDER_CREATED");
      orderCreated.setTitle("Order Created");
      orderStatus.add(orderCreated);
      
      OrderStatus orderApproved = new OrderStatus();
      orderApproved.setCode("ORDER_APPROVED");
      orderApproved.setTitle("Order Approved");
      orderStatus.add(orderApproved);
      
      OrderStatus orderPaid = new OrderStatus();
      orderPaid.setCode("ORDER_PAID");
      orderPaid.setTitle("Order Paid");
      orderStatus.add(orderPaid);
      
      OrderStatus orderShipped = new OrderStatus();
      orderShipped.setCode("ORDER_SHIPPED");
      orderShipped.setTitle("Order Shipped");
      orderStatus.add(orderShipped);
      
      OrderStatus orderCancled = new OrderStatus();
      orderCancled.setCode("ORDER_CANCLED");
      orderCancled.setTitle("Order Cancled");
      orderStatus.add(orderCancled);
      delegator.save(orderStatus);
      
      List<OrderStatus> orderStatusFromDb = (List<OrderStatus>) delegator.findList("OrderStatus", null);
      Assert.assertEquals(orderStatus.size(), orderStatusFromDb.size());
      
      for (int i = 0; i < length; i++) {
        Order order = new Order();
        order.setCustomer(customersFromDb.get(i));
        order.setStatusCode(orderCreated);
        order.setOrderDate(new Date());
        order.setShipDate(new Date());
        delegator.save(order);
        for(int j = 0; j < 3; j++){
          OrderDetail od = new OrderDetail();
          od.setOrder(order);
          od.setOrderQty(BigDecimal.ONE);
          od.setShipQty(BigDecimal.ONE);
          od.setProduct(productsFromDb.get(new Random().nextInt(pLength)));
          delegator.save(od);
        }
      }
      
      List<Order> orderFromDb = (List<Order>) delegator.findList("Order", null);
      Assert.assertEquals(length, orderFromDb.size());
      
      Map<String,Object> fields = CommUtils.toMap("order.id",orderFromDb.get(0).getId());
      List<OrderDetail> firstOrderDetails = (List<OrderDetail>)delegator.findByAnd("OrderDetail", fields);
      Assert.assertEquals(3, firstOrderDetails.size());
      
      fields = CommUtils.toMap("statusCode.code",orderCreated.getCode());
      Map<String,Object> pMap = delegator.findPageByAnd("Order", fields, 1, 10);
      int totalPage = (Integer) pMap.get("totalPage");
      int totalEntry = (Integer) pMap.get("totalEntry");
      List<Order> orderCreatdList = (List<Order>) pMap.get("list");
      Assert.assertEquals(totalPage, 10);
      Assert.assertEquals(100, totalEntry);
      Assert.assertEquals(10, orderCreatdList.size());
      
      for (Order order : orderCreatdList) {
        order.setStatusCode(orderApproved);
        delegator.save(order);
      }
      
      List<Order> orderApprovedList = (List<Order>) delegator.findByAnd("Order", CommUtils.toMap("statusCode.code",orderApproved.getCode()));
      Assert.assertEquals(10, orderApprovedList.size());
      
      for (Order order : orderApprovedList) {
        order.setStatusCode(orderPaid);
        delegator.save(order);
      }
      
      List<Order> orderPaidList = (List<Order>) delegator.findByAnd("Order", CommUtils.toMap("statusCode.code",orderPaid.getCode()));
      Assert.assertEquals(10, orderPaidList.size());
      
      for (Order order : orderPaidList) {
        order.setStatusCode(orderShipped);
        delegator.save(order);
      }
      List<Order> orderShippedList = (List<Order>) delegator.findByAnd("Order", CommUtils.toMap("statusCode.code",orderShipped.getCode()));
      Assert.assertEquals(10, orderShippedList.size());
      
      StringBuffer sql = new StringBuffer();
      sql.append(" select o.* from test_order o inner join test_customer c");
      sql.append(" on o.customer_id = c.id ");
      sql.append(" inner join test_address addr");
      sql.append(" on c.shipping_address_id = addr.id ");
      sql.append(" inner join test_country tc");
      sql.append(" on addr.country_code = tc.code ");
      sql.append(" where tc.code = ?");
      sql.append(" and (o.order_date >= ? and o.order_date <= ?)");
      sql.append(" order by o.created_at desc");
      sql.append(" limit ?,?");
      
      List<Object> params = new ArrayList<>();
      params.add("CN");
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
      Date now = new Date();
      params.add(sdf.format(now)+" 00:00:00");
      params.add(sdf.format(now)+" 23:59:59");
      params.add(1);
      params.add(10);
      
      List<Map<String,Object>> mapList = delegator.findListByRawQuerySql(sql.toString(), params);
      Assert.assertEquals(true, mapList.size() > 0);
      
      Debug.logInfo("testAllInOne Succeed.", module);
    } catch (GenericEntityException e) {
      Debug.logError(e, "testAllInOne Exception", module);
    }
  }

}

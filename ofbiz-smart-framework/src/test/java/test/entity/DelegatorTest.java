package test.entity;


import org.huihoo.ofbiz.smart.base.C;
import org.huihoo.ofbiz.smart.base.cache.SimpleCacheManager;
import org.huihoo.ofbiz.smart.base.util.CommUtil;
import org.huihoo.ofbiz.smart.base.util.Log;
import org.huihoo.ofbiz.smart.entity.Expr;
import org.huihoo.ofbiz.smart.entity.GenericEntityException;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class DelegatorTest extends BaseTestCase {
  private final static String TAG = DelegatorTest.class.getName();

  @Test
  public void testAllInOne() throws GenericEntityException, ParseException, SQLException {
    Log.i("start testing.........", TAG);
    Customer customer = new Customer();
    customer.setLastName("Peter");
    customer.setGender("Male");
    customer.setLevel(1);
    customer.setSalary(new BigDecimal("5000"));
    customer.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse("1983-05-18"));
    delegator.save(customer);

    Address billAddr = new Address();
    billAddr.setAddr1("This is my billing address.");
    billAddr.setAddrType("BILLING");
    billAddr.setCustomer(customer);
    customer.setBirthday(new SimpleDateFormat("yyyy-MM-dd").parse("1988-05-18"));
    delegator.save(billAddr);

    Address shipAddr = new Address();
    shipAddr.setAddr1("This is my shipping address.");
    shipAddr.setAddrType("SHIPPING");
    shipAddr.setCustomer(customer);
    delegator.save(shipAddr);

    Customer customer002 = new Customer();
    customer002.setLastName("Peter002");
    customer002.setGender("Male");
    customer002.setLevel(2);
    customer002.setSalary(new BigDecimal("8000"));
    delegator.save(customer002);

    Assert.assertEquals("Peter", customer.getLastName());
    Assert.assertNotNull(customer.getAddress());

    Customer newestCustomer = (Customer) delegator.findById(Customer.class, customer.getId());
    Assert.assertNotNull(newestCustomer);

    newestCustomer = (Customer) delegator.findById(Customer.class, customer.getId(), true);
    Assert.assertNotNull(newestCustomer);

    newestCustomer = (Customer) delegator.findById(Customer.class, customer.getId(), true);
    Assert.assertNotNull(newestCustomer);
    Assert.assertEquals(1, SimpleCacheManager.getCache("EntityCache").getHitCount());

    newestCustomer = (Customer) delegator.findUniqueByAnd(Customer.class, CommUtil.toMap("lastName", "Peter"));
    Assert.assertNotNull(newestCustomer);

    newestCustomer = (Customer) delegator.findUniqueByAnd(Customer.class, CommUtil.toMap("lastName", "PeterNotFound"));
    Assert.assertNull(newestCustomer);

    Set<String> selectToFields = new LinkedHashSet<>();
    selectToFields.add("lastName");
    newestCustomer =
        (Customer) delegator.findUniqueByAnd(Customer.class, CommUtil.toMap("lastName", "Peter"), selectToFields, true);
    Assert.assertNotNull(newestCustomer.getLastName());

    newestCustomer =
        (Customer) delegator.findUniqueByAnd(Customer.class, CommUtil.toMap("lastName", "Peter"), selectToFields, true);
    Assert.assertNotNull(newestCustomer);
    Assert.assertEquals(2, SimpleCacheManager.getCache("EntityCache").getHitCount());

    List<?> customerList = delegator.findListByCond(Customer.class, "{lastName,eq,Peter}", selectToFields,
        Arrays.asList(new String[] {"birthday desc"}));
    Assert.assertNotNull(customerList);
    Assert.assertEquals(1, customerList.size());

    customerList = delegator.findListByCond(Customer.class, "{lastName,eq,Peter,or,gender,eq,Male}", selectToFields,
        Arrays.asList(new String[] {"birthday desc"}));
    Assert.assertNotNull(customerList);
    Assert.assertEquals(2, customerList.size());

    int count = delegator.countByAnd(Customer.class, CommUtil.toMap("lastName", "Peter"));
    Assert.assertEquals(1, count);

    count = delegator.countByAnd(Customer.class, CommUtil.toMap("lastName", "Peter2"));
    Assert.assertEquals(0, count);

    count = delegator.countByCond(Customer.class, "{lastName,eq,Peter}");
    Assert.assertEquals(1, count);

    count = delegator.countByCond(Customer.class, "{lastName,eq,Peter2}");
    Assert.assertEquals(0, count);

    List<?> ids = delegator.findIdsByAnd(Customer.class, CommUtil.toMap("lastName", "Peter"));
    Assert.assertEquals(1, ids.size());

    ids = delegator.findIdsByCond(Customer.class, "{lastName,eq,Peter}");
    Assert.assertEquals(1, ids.size());

    Map<String, Object> pageResult =
        delegator.findPageByAnd(Customer.class, CommUtil.toMap("lastName", "Peter"), 1, 10);
    Assert.assertNotNull(pageResult);
    Assert.assertEquals(1, pageResult.get(C.PAGE_TOTAL_ENTRY));
    Assert.assertEquals(1, pageResult.get(C.PAGE_TOTAL_PAGE));
    Assert.assertEquals(true, pageResult.get(C.PAGE_LIST) instanceof List<?>);

    pageResult =
        delegator.findPageByAnd(Customer.class, CommUtil.toMap("lastName", "Peter"), 1, 10, selectToFields, null);
    Assert.assertNotNull(pageResult);
    Assert.assertEquals(1, pageResult.get(C.PAGE_TOTAL_ENTRY));
    Assert.assertEquals(1, pageResult.get(C.PAGE_TOTAL_PAGE));
    Assert.assertEquals(true, pageResult.get(C.PAGE_LIST) instanceof List<?>);

    pageResult = delegator.findPageByAnd(Customer.class, CommUtil.toMap("gender", "Male"), 1, 10, selectToFields, null);
    Assert.assertNotNull(pageResult);
    Assert.assertEquals(2, pageResult.get(C.PAGE_TOTAL_ENTRY));
    Assert.assertEquals(1, pageResult.get(C.PAGE_TOTAL_PAGE));
    Assert.assertEquals(true, pageResult.get(C.PAGE_LIST) instanceof List<?>);



    pageResult = delegator.findPageByAnd(Customer.class, CommUtil.toMap("gender", "Male"), 1, 10, selectToFields,
        Arrays.asList(new String[] {"createdAt asc"}));
    Assert.assertNotNull(pageResult);
    Assert.assertEquals(2, pageResult.get(C.PAGE_TOTAL_ENTRY));
    Assert.assertEquals(1, pageResult.get(C.PAGE_TOTAL_PAGE));
    Assert.assertEquals(true, pageResult.get(C.PAGE_LIST) instanceof List<?>);
    Assert.assertEquals("Peter", ((Customer) ((List<?>) pageResult.get(C.PAGE_LIST)).get(0)).getLastName());


    pageResult = delegator.findPageByAnd(Customer.class, CommUtil.toMap("gender", "Male"), 1, 10, selectToFields,
        Arrays.asList(new String[] {"createdAt desc"}));
    Assert.assertNotNull(pageResult);
    Assert.assertEquals(2, pageResult.get(C.PAGE_TOTAL_ENTRY));
    Assert.assertEquals(1, pageResult.get(C.PAGE_TOTAL_PAGE));
    Assert.assertEquals(true, pageResult.get(C.PAGE_LIST) instanceof List<?>);
    Assert.assertEquals("Peter002", ((Customer) ((List<?>) pageResult.get(C.PAGE_LIST)).get(0)).getLastName());


    pageResult = delegator.findPageByAnd(Customer.class, CommUtil.toMap("gender", "Male"), 1, 10, selectToFields,
        Arrays.asList(new String[] {"createdAt desc", "birthday desc"}), true);
    Assert.assertNotNull(pageResult);
    Assert.assertEquals(2, pageResult.get(C.PAGE_TOTAL_ENTRY));
    Assert.assertEquals(1, pageResult.get(C.PAGE_TOTAL_PAGE));
    Assert.assertEquals(true, pageResult.get(C.PAGE_LIST) instanceof List<?>);
    Assert.assertEquals("Peter002", ((Customer) ((List<?>) pageResult.get(C.PAGE_LIST)).get(0)).getLastName());


    pageResult = delegator.findPageByAnd(Customer.class, CommUtil.toMap("gender", "Male"), 1, 10, selectToFields,
        Arrays.asList(new String[] {"createdAt desc", "birthday desc"}), true);
    Assert.assertNotNull(pageResult);



    pageResult = delegator.findPageByCond(Customer.class, "{gender,eq,Male}", 1, 10, selectToFields,
        Arrays.asList(new String[] {"createdAt asc"}));
    Assert.assertNotNull(pageResult);
    Assert.assertEquals(2, pageResult.get(C.PAGE_TOTAL_ENTRY));
    Assert.assertEquals(1, pageResult.get(C.PAGE_TOTAL_PAGE));
    Assert.assertEquals(true, pageResult.get(C.PAGE_LIST) instanceof List<?>);
    Assert.assertEquals("Peter", ((Customer) ((List<?>) pageResult.get(C.PAGE_LIST)).get(0)).getLastName());

    pageResult = delegator.findPageByCond(Customer.class,
        "{gender,eq,Male}" + "{level,between,1#5}{salary,between,5000#8000}" + "{id,in,1#2#3}"
            + "{lastName,isNotNull,any}{firstName,isNull,any}{level,ge,1}{level,gt,0}{level,le,10}{level,lt,10}"
            + "{id,notIn,8#9#10}{id,ge,1,or,id,le,100}",
        1, 10, selectToFields, Arrays.asList(new String[] {"createdAt asc"}));
    Assert.assertNotNull(pageResult);
    Assert.assertEquals(2, pageResult.get(C.PAGE_TOTAL_ENTRY));
    Assert.assertEquals(1, pageResult.get(C.PAGE_TOTAL_PAGE));
    Assert.assertEquals(true, pageResult.get(C.PAGE_LIST) instanceof List<?>);
    Assert.assertEquals("Peter", ((Customer) ((List<?>) pageResult.get(C.PAGE_LIST)).get(0)).getLastName());

    String exprCond = Expr.create().eq("gender", "Male")
                                   .between("level", 1,5)
                                   .between("salary", 5000, 8000)
                                   .in("id", Arrays.asList(new Object[]{1,2,3}))
                                   .notIn("id", Arrays.asList(new Object[]{8,9,10}))
                                   .isNotNull("lastName")
                                   .isNull("firstName")
                                   .ge("level", 1)
                                   .gt("level", 0)
                                   .le("level", 10)
                                   .lt("level", 10)
                                   .or("id,ge,1", "id,le,100")
                                   .build();
    
    Log.d("exprCond -> " + exprCond, TAG);
    pageResult = delegator.findPageByCond(Customer.class,exprCond,1, 10, selectToFields, Arrays.asList(new String[] {"createdAt asc"}));
    Assert.assertNotNull(pageResult);
    Assert.assertEquals(2, pageResult.get(C.PAGE_TOTAL_ENTRY));
    Assert.assertEquals(1, pageResult.get(C.PAGE_TOTAL_PAGE));
    Assert.assertEquals(true, pageResult.get(C.PAGE_LIST) instanceof List<?>);
    Assert.assertEquals("Peter", ((Customer) ((List<?>) pageResult.get(C.PAGE_LIST)).get(0)).getLastName());
    
    Connection con = delegator.getConnection();
    Assert.assertNotNull("raw connection ---> " + con);
    con.close();
  }
}

package test.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.huihoo.ofbiz.smart.base.util.CommUtil;
import org.huihoo.ofbiz.smart.base.util.Log;
import org.huihoo.ofbiz.smart.entity.EntityConverter;
import org.huihoo.ofbiz.smart.entity.GenericEntityException;
import org.junit.Assert;
import org.junit.Test;



public class EntityConvertorTest extends BaseTestCase{
  private final static String TAG = EntityConvertorTest.class.getName();
  private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
  
  @Test
  public void testAllInOne() throws ParseException, GenericEntityException {
    Map<String,Object> ctx = CommUtil.toMap("level","2",
                                            "firstName","hbh",
                                            "salary","1500",
                                            "birthday","1986-05-18",
                                            "tracked","1",
                                            "gender","Male"
    ); 
    Customer customer = (Customer)  EntityConverter.convertFrom(Customer.class, ctx,delegator);
    Log.d("" + customer, TAG);
    Assert.assertEquals(2, customer.getLevel());
    Assert.assertEquals("hbh", customer.getFirstName());
    Assert.assertEquals(1500L, customer.getSalary().longValue());
    Assert.assertEquals("Male", customer.getGender());
    Assert.assertEquals(0, customer.getBirthday().compareTo(sdf.parse("1986-05-18")));
    Assert.assertEquals(true, customer.isTracked());
    Assert.assertNull(customer.getStatus());
    
    delegator.save(customer);
    
    ctx = CommUtil.toMap("grandTotal","300",
                          "remainTotal","200",
                          "currentStatus","ORDER_CREATED",
                          "fromChannel","WEB",
                          "customer.id",customer.getId()
    );
    
    OrderHeader orderHeader = (OrderHeader) EntityConverter.convertFrom(OrderHeader.class, ctx,delegator);
    Log.d("" + orderHeader, TAG);
    Assert.assertEquals(300L, orderHeader.getGrandTotal().longValue());
    Assert.assertEquals(200L, orderHeader.getRemainTotal().longValue());
    Assert.assertNotNull(orderHeader.getCustomer());
    delegator.save(orderHeader);
  }
}

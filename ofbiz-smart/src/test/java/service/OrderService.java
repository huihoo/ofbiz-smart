package service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import org.huihoo.ofbiz.smart.base.C;
import org.huihoo.ofbiz.smart.base.utils.CommUtils;
import org.huihoo.ofbiz.smart.base.utils.Debug;
import org.huihoo.ofbiz.smart.base.utils.ServiceUtils;
import org.huihoo.ofbiz.smart.entity.Delegator;
import org.huihoo.ofbiz.smart.entity.GenericEntityException;

import entity.Customer;
import entity.Order;
import entity.OrderDetail;
import entity.Product;


public class OrderService {
  private static final String module = OrderService.class.getName();

  public static Map<String, Object> createOrder(Map<String, Object> ctx) {
    Debug.logDebug("Order Ctx->" + CommUtils.printMap(ctx), module);
    Map<String, Object> success = ServiceUtils.returnSuccess();
    
    Long customerId = (Long) ctx.get("customerId");
    Long productId = (Long) ctx.get("productId");
    BigDecimal orderQty = (BigDecimal) ctx.get("orderQty");
    
    Delegator delegator = (Delegator) ctx.get(C.CTX_DELEGATOR);
    try {
      Customer customer = (Customer) delegator.findById("Customer", customerId);
      if(customer == null)
        return ServiceUtils.returnMessage("CUSTOMER_NOT_EXIST", "客户["+customerId+"]不存在");
      
      Product product = (Product) delegator.findById("Product", productId);
      if(product == null)
        return ServiceUtils.returnMessage("PRODUCT_NOT_EXIST", "产品["+customerId+"]不存在");
        
      Order order = new Order();
      order.setOrderDate(new Date());
      order.setShipDate(new Date());
      order.setCustomer(customer);
      delegator.save(order);
      
      OrderDetail orderDetail = new OrderDetail();
      orderDetail.setOrder(order);
      orderDetail.setProduct(product);
      orderDetail.setOrderQty(orderQty);
      orderDetail.setShipQty(orderQty);
      delegator.save(orderDetail);
      
      success.put("orderId", order.getId());
      return success;

    } catch (GenericEntityException e) {
      Debug.logError(e, "createOrder Exception",module);
      return ServiceUtils.returnError("createOrder Exception");
    }
    
  }
}

package test.service;

import java.math.BigDecimal;
import java.util.Map;

import org.huihoo.ofbiz.smart.base.util.Log;
import org.huihoo.ofbiz.smart.base.util.ServiceUtil;
import org.huihoo.ofbiz.smart.service.annotation.InParameter;
import org.huihoo.ofbiz.smart.service.annotation.OutParameter;
import org.huihoo.ofbiz.smart.service.annotation.Service;
import org.huihoo.ofbiz.smart.service.annotation.ServiceDefinition;

@Service
public class OrderService {
  private final static String TAG = OrderService.class.getName();
  
  
  @ServiceDefinition(
    name = "createOrder"
    ,description = "创建订单"
    ,inParameters = {
        @InParameter(name = "fromChannel",required = true,description = "订单来源")
       ,@InParameter(name = "userId",required = true,description = "订单创建者ID",type = Long.class)
       ,@InParameter(name = "paymentMethod",required = true,description = "支付方式") 
    }
    ,outParameters = {
        @OutParameter(name = "orderId",required = true,description = "创建成功的订单编号") 
       ,@OutParameter(name = "grandTotal",required = true,description = "订单总价",type = BigDecimal.class)  
    }
    ,responseJsonExample = "{'orderId','20151231001','grandTotal',300.00}"
  )
  public static Map<String,Object> createOrder(Map<String,Object> ctx) {
    Map<String, Object> success = ServiceUtil.returnSuccess();
    Log.i("Begining create order.", TAG);
    return success;
  }
}

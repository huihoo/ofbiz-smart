package test.service;

import java.math.BigDecimal;
import java.util.Map;

import org.huihoo.ofbiz.smart.base.util.Log;
import org.huihoo.ofbiz.smart.base.util.ServiceUtil;
import org.huihoo.ofbiz.smart.service.annotation.Parameter;
import org.huihoo.ofbiz.smart.service.annotation.Service;
import org.huihoo.ofbiz.smart.service.annotation.ServiceDefinition;

@Service
public class OrderService {
  private final static String TAG = OrderService.class.getName();
  
  @ServiceDefinition(
    name = "createOrder"
    ,description = "创建订单"
    ,parameters = {
        @Parameter(name = "fromChannel",optinal = false,mode="IN", description = "订单来源")
       ,@Parameter(name = "userId",type = Long.class,optinal = false,mode="IN",description = "订单创建者ID")
       ,@Parameter(name = "paymentMethod",optinal = false,mode="IN",description = "支付方式") 
       ,@Parameter(name = "orderId",optinal = false,mode="OUT",description = "创建成功的订单编号") 
       ,@Parameter(name = "grandTotal",type = BigDecimal.class,optinal = false,mode="OUT",description = "订单总价") 
    }
    ,responseJsonExample = "{'orderId','20151231001','grandTotal',300.00}"
  )
  public static Map<String,Object> createOrder(Map<String,Object> ctx) {
    try {
      Map<String, Object> resultMap = ServiceUtil.returnSuccess();
      //TODO
      return resultMap;
    } catch(Exception e) {
      Log.e(e, "OrderService.createOrder occurs exception.", TAG);
      return ServiceUtil.returnProplem("SERVICE_EXCEPTION", "服务执行发生异常");
    } 
  }
}

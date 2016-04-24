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
    name = "createOrderFail"
    ,apiAlias = "org.huihoo.order.createFail"
    ,description = "创建订单但参数验证不通过"
    ,parameters = {
        @Parameter(name = "fromChannel",optinal = false,mode="IN", description = "订单来源")
       ,@Parameter(name = "userId",type = Long.class,optinal = false,mode="IN",description = "订单创建者ID")
       ,@Parameter(name = "paymentMethod",optinal = false,mode="IN",description = "支付方式") 
       ,@Parameter(name = "orderId",optinal = false,mode="OUT",description = "创建成功的订单编号") 
       ,@Parameter(name = "grandTotal",type = BigDecimal.class,optinal = false,mode="OUT",description = "订单总价") 
    }
    ,responseJsonExample = "{'orderId','20151231001','grandTotal',300.00}"
  )
  public static Map<String,Object> createOrderFail(Map<String,Object> ctx) {
    try {
      Map<String, Object> resultMap = ServiceUtil.returnSuccess();
      //TODO
      return resultMap;
    } catch(Exception e) {
      Log.e(e, "OrderService.createOrder occurs exception.", TAG);
      return ServiceUtil.returnProplem("SERVICE_EXCEPTION", "服务执行发生异常");
    } 
  }
  
  
  @ServiceDefinition(
    name = "createOrderSuccess"
    ,apiAlias = "org.huihoo.order.createSuccess"
    ,export = true
    ,description = "成功创建订单"
    ,parameters = {
        @Parameter(name = "fromChannel",optinal = false,mode="IN", description = "订单来源")
       ,@Parameter(name = "userId",type = Long.class,optinal = false,mode="IN",description = "订单创建者ID")
       ,@Parameter(name = "paymentMethod",optinal = false,mode="IN",description = "支付方式") 
       ,@Parameter(name = "orderId",optinal = false,mode="OUT",description = "创建成功的订单编号") 
       ,@Parameter(name = "grandTotal",type = BigDecimal.class,optinal = false,mode="OUT",description = "订单总价") 
    }
    ,callback = {OrderServiceCallback.class}
    ,responseJsonExample = "{'orderId','20151231001','grandTotal',300.00}"
  )
  public static Map<String,Object> createOrderSuccess(Map<String,Object> ctx) {
    try {
      Map<String, Object> resultMap = ServiceUtil.returnSuccess();
      resultMap.put("orderId", "2016010199292");
      resultMap.put("grandTotal", "999999");
      return resultMap;
    } catch(Exception e) {
      Log.e(e, "OrderService.createOrder occurs exception.", TAG);
      return ServiceUtil.returnProplem("SERVICE_EXCEPTION", "服务执行发生异常");
    } 
  }
  
  
  
  @ServiceDefinition(
    name = "createOrderSuccessRequireAuth"
    ,apiAlias = "org.huihoo.order.createSuccess.auth"
    ,export = true
    ,description = "成功创建订单"
    ,requireAuth = true
    ,parameters = {
        @Parameter(name = "fromChannel",optinal = false,mode="IN", description = "订单来源")
       ,@Parameter(name = "userId",type = Long.class,optinal = false,mode="IN",description = "订单创建者ID")
       ,@Parameter(name = "paymentMethod",optinal = false,mode="IN",description = "支付方式") 
       ,@Parameter(name = "orderId",optinal = false,mode="OUT",description = "创建成功的订单编号") 
       ,@Parameter(name = "grandTotal",type = BigDecimal.class,optinal = false,mode="OUT",description = "订单总价") 
    }
    ,callback = {OrderServiceCallback.class}
    ,responseJsonExample = "{'orderId','20151231001','grandTotal',300.00}"
  )
  public static Map<String,Object> createOrderSuccessRequireAuth(Map<String,Object> ctx) {
    try {
      Map<String, Object> resultMap = ServiceUtil.returnSuccess();
      resultMap.put("orderId", "2016010199292");
      resultMap.put("grandTotal", "999999");
      return resultMap;
    } catch(Exception e) {
      Log.e(e, "OrderService.createOrderSuccessRequireAuth occurs exception.", TAG);
      return ServiceUtil.returnProplem("SERVICE_EXCEPTION", "服务执行发生异常");
    } 
  }
}

package service;

import java.util.Map;

import org.huihoo.ofbiz.smart.base.utils.CommUtils;
import org.huihoo.ofbiz.smart.base.utils.Debug;
import org.huihoo.ofbiz.smart.base.utils.ServiceUtils;


public class OrderService {
  private static final String module = OrderService.class.getName();

  public static Map<String, Object> createOrder(Map<String, Object> ctx) {
    Debug.logDebug("Order Ctx->" + CommUtils.printMap(ctx), module);
    Map<String, Object> success = ServiceUtils.returnSuccess();
    return success;
  }
}

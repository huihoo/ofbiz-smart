package test.service;

import java.util.Map;

import org.huihoo.ofbiz.smart.base.util.ServiceUtil;
import org.huihoo.ofbiz.smart.service.annotation.Service;
import org.huihoo.ofbiz.smart.service.annotation.ServiceDefinition;

@Service
public class OrderService {
  private final static String TAG = OrderService.class.getName();
  
  @ServiceDefinition(name = "createOrder")
  public static Map<String,Object> createOrder(Map<String,Object> ctx) {
    Map<String, Object> success = ServiceUtil.returnSuccess();
    return success;
  }
}

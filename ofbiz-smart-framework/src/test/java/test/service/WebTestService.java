package test.service;

import java.util.Map;

import org.huihoo.ofbiz.smart.base.util.Log;
import org.huihoo.ofbiz.smart.base.util.ServiceUtil;
import org.huihoo.ofbiz.smart.service.annotation.Service;
import org.huihoo.ofbiz.smart.service.annotation.ServiceDefinition;

@Service
public class WebTestService {
  private final static String TAG = WebTestService.class.getName();
  
  @ServiceDefinition(name="testGetContentOk")
  public static Map<String,Object> testGetContentOk(Map<String,Object> ctx) {
    try {
      Map<String, Object> resultMap = ServiceUtil.returnSuccess();
      resultMap.put("Content", "It works");
      return resultMap;
    } catch(Exception e) {
      Log.e(e, "WebTestService.testGetContentOk occurs exception.", TAG);
      return ServiceUtil.returnProplem("SERVICE_EXCEPTION", "服务执行发生异常");
    } 
  }
  
}

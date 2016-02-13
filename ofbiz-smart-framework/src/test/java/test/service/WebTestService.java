package test.service;

import java.io.File;
import java.util.Map;

import org.huihoo.ofbiz.smart.base.util.Log;
import org.huihoo.ofbiz.smart.base.util.ServiceUtil;
import org.huihoo.ofbiz.smart.service.annotation.Service;
import org.huihoo.ofbiz.smart.service.annotation.ServiceDefinition;
import org.junit.Assert;

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
  
  @ServiceDefinition(name="testUploadFileOk")
  public static Map<String,Object> testUploadFileOk(Map<String,Object> ctx) {
    try {
      Map<String, Object> resultMap = ServiceUtil.returnSuccess();
      File file = (File) ctx.get("file_file_saved_file");
      Assert.assertNotNull(file);
      if (file.exists()) {
        file.delete();
      }
      return resultMap;
    } catch(Exception e) {
      Log.e(e, "WebTestService.testUploadFileOk occurs exception.", TAG);
      return ServiceUtil.returnProplem("SERVICE_EXCEPTION", "服务执行发生异常");
    } 
  }
  
}

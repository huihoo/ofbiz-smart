package org.huihoo.ofbiz.smart.webapp;

import java.io.InputStream;
import java.util.Map;


public interface FileUploadHandler {
  
  /**
   * 处理文件上传
   * 
   * @param fieldName   文件域的名称
   * @param fileName    文件的原始名称
   * @param contentType 文件的类型
   * @param inputStream 文件的输入流
   * @param webCtx      WebApp上下文
   * @return Map
   */
  Map<String,Object> handle(String fieldName,String fileName,String contentType,InputStream inputStream,Map<String,Object> webCtx);
  
}

package org.huihoo.ofbiz.smart.webapp;

import java.io.InputStream;
import java.util.Map;


public interface FileUploadHandler {
  
  Map<String,Object> handle(String fieldName,String fileName,String contentType,InputStream inputStream,Map<String,Object> webCtx);
  
}

package org.huihoo.ofbiz.smart.webapp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;


import org.apache.commons.io.IOUtils;
import org.huihoo.ofbiz.smart.base.C;
import org.huihoo.ofbiz.smart.base.util.Log;
import org.huihoo.ofbiz.smart.base.util.ServiceUtil;

public class DefaultFileUploadHandler implements FileUploadHandler {
  private final static String TAG = DefaultFileUploadHandler.class.getName();
  
  private final static SimpleDateFormat FORMATTER = new SimpleDateFormat("yyyy/MM/dd"); 
  private final static String DEFAULT_CONTENT_TYPE_CVS = "image/x-png,image/pjpeg,image/gif,image/jpeg,image/png";
  
  @Override
  public Map<String, Object> handle(String fieldName,String fileName,String contentType,InputStream inputStream,Map<String, Object> webCtx) {
    if (inputStream == null) {
      //Ignore.
      return ServiceUtil.returnSuccess();
    }
    
    HttpServletRequest req = (HttpServletRequest) webCtx.get(C.CTX_WEB_HTTP_SERVLET_REQUEST);
    Properties prop = (Properties) webCtx.get(C.APPLICATION_CONFIG_PROP_KEY);
    String saveRootPath = req.getServletContext().getRealPath("");
    String fileSaveRelativePath = prop.getProperty("file.upload.save.relative.path","/upload");
    String[] allowContentTypes = prop.getProperty("file.upload.allow.content.type",DEFAULT_CONTENT_TYPE_CVS).split(",");
    
    boolean allowed = false;
    for (String ct : allowContentTypes) {
      if (ct.equals(contentType)) {
        allowed = true;
        break;
      }
    }
    
    if (!allowed) {
      Log.w("Uploaded file [%s] contentType[%s] is invalid.", TAG,fileName,contentType);
      return ServiceUtil.returnProplem("INVALID_FILE_CONTENTTYPE", "invalid file content type");
    }
    
    File uploadDir = new File(saveRootPath + fileSaveRelativePath);
    if (!uploadDir.exists()) {
      uploadDir.mkdirs();
    }
    
    String fmtFolder = FORMATTER.format(new Date());
    File fmtDir = new File(saveRootPath + fileSaveRelativePath + "/" + fmtFolder);
    if (!fmtDir.exists()) {
      fmtDir.mkdirs();
    }
    
   
    
    Log.d("field[%s] file[%s] contentType[%s] will be saved in [%s]", TAG,fieldName,fileName,contentType,fmtDir);
    
    String fileSuffix = "";
    int lastDotIdx = fileName.lastIndexOf(".");
    if (lastDotIdx != -1) {
      fileSuffix = fileName.substring(lastDotIdx);
    }
    
    String fileNewName = UUID.randomUUID().toString().replaceAll("-", "") + fileSuffix;
    String fileRelativePath = fileSaveRelativePath + "/" + fmtFolder + "/" + fileNewName;
    File targetFile = new File(saveRootPath + fileRelativePath);
    
    
    try {
      FileOutputStream fos = new FileOutputStream(targetFile);
      IOUtils.copy(inputStream, fos);
      Map<String,Object> resultMap = ServiceUtil.returnSuccess();
      resultMap.put(fieldName, fileRelativePath);
      resultMap.put(fieldName + "_saved_file", targetFile);
      return resultMap; 
    } catch (Exception e) {
      Log.e(e, "Save File occurs an exception.", TAG);
      return ServiceUtil.returnProplem("FILE_UPLOAD_EXCEPTION", "file upload exception.");
    }
  }
}

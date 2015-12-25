package org.huihoo.ofbiz.smart.webapp;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.huihoo.ofbiz.smart.base.C;
import org.huihoo.ofbiz.smart.base.util.CommUtil;
import org.huihoo.ofbiz.smart.base.util.Log;
import org.huihoo.ofbiz.smart.entity.Delegator;
import org.huihoo.ofbiz.smart.service.ServiceDispatcher;

public class WebAppUtil {
  
  private final static String TAG = WebAppUtil.class.getName();
  
  public static Map<String, Object> buildWebCtx(HttpServletRequest req) {
    ServletContext sc = req.getServletContext();
    
    Delegator delegator = (Delegator) sc.getAttribute(C.CTX_DELETAGOR);
    ServiceDispatcher serviceDispatcher =(ServiceDispatcher) sc.getAttribute(C.CTX_SERVICE_DISPATCHER);
    Properties applicationConfig = (Properties) sc.getAttribute(C.APPLICATION_CONFIG_PROP_KEY);
    
    Map<String, Object> ctx =CommUtil.toMap(C.CTX_DELETAGOR, delegator
                                           ,C.CTX_SERVICE_DISPATCHER, serviceDispatcher
                                           ,C.ACTION_CONFIG_BASEPATH_KEY, applicationConfig
                                           ,C.CTX_WEB_HTTP_SERVLET_REQUEST, req
    );
    
    boolean isMultipart = ServletFileUpload.isMultipartContent(req);
    
    if (isMultipart) {
      ServletFileUpload upload = new ServletFileUpload();
      FileItemIterator iter;
      try {
        iter = upload.getItemIterator(req);
        while (iter.hasNext()) {
          FileItemStream item = iter.next();
          String name = item.getFieldName();
          InputStream stream = item.openStream();
          if (item.isFormField()) {
            ctx.put(name, Streams.asString(stream, C.UTF_8));
          } else {
             //TODO File
          }
        }
      } catch (FileUploadException | IOException e) {
        Log.w("Process MultipartContent failed.", TAG);
      }
      
    } else {
      Enumeration<String> parameterNames = req.getParameterNames();
      while (parameterNames.hasMoreElements()) {
        String parameterName = parameterNames.nextElement();
        String value = req.getParameter(parameterName);
        if (CommUtil.isNotEmpty(value)) {
          ctx.put(parameterName, value);
        } else {
          String[] arrayValue = req.getParameterValues(parameterName);
          if (CommUtil.isNotEmpty(arrayValue)) {
            ctx.put(parameterName, arrayValue);
          } else {
            ctx.put(parameterName, value);
          }
        }
      }
    }
    
    
    return ctx;
  }
  
  
  public static String analyzeParamPairString(String paramPair,HttpServletRequest req) {
    StringBuilder sb = new StringBuilder();
    String[] pToken = paramPair.split(",");
    int len = pToken.length;
    for (int i = 0; i < len; i++) {
      String t = pToken[i];
      if (t.startsWith("requestScope.")) {
        sb.append(req.getParameter(t.substring("requestScope.".length())));
      } else if (t.startsWith("sessionScope.")){
        sb.append(req.getSession().getAttribute(t.substring("requestScope.".length())));
      } else {
        sb.append(t);
      }
      
      if (i < len - 1) {
        sb.append(",");
      }
    }
    return sb.toString();
  }

}

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

import ognl.Ognl;
import ognl.OgnlException;

public class WebAppManager {
  
  private final static String TAG = WebAppManager.class.getName();
  
  private static volatile FileUploadHandler fileUploadHandler;
  
  public static Map<String, Object> buildWebCtx(HttpServletRequest req) {
    ServletContext sc = req.getSession().getServletContext();
    
    Delegator delegator = (Delegator) sc.getAttribute(C.CTX_DELETAGOR);
    ServiceDispatcher serviceDispatcher =(ServiceDispatcher) sc.getAttribute(C.CTX_SERVICE_DISPATCHER);
    Properties applicationConfig = (Properties) sc.getAttribute(C.APPLICATION_CONFIG_PROP_KEY);
    
    Map<String, Object> ctx =CommUtil.toMap(C.CTX_DELETAGOR, delegator
                                           ,C.CTX_SERVICE_DISPATCHER, serviceDispatcher
                                           ,C.APPLICATION_CONFIG_PROP_KEY, applicationConfig
                                           ,C.CTX_WEB_HTTP_SERVLET_REQUEST, req
    );
    
    boolean isMultipart = ServletFileUpload.isMultipartContent(req);
    
    if (isMultipart) {
      if (fileUploadHandler == null) {
        String handlerName = applicationConfig.getProperty("file.upload.handler", "org.huihoo.ofbiz.smart.webapp.DefaultFileUploadHandler");
        try {
          fileUploadHandler = (FileUploadHandler) Class.forName(handlerName).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
          Log.w("Unable to load file upload handler [%s]", TAG,handlerName);
        }
      }
      int fileSizeMax = Integer.valueOf(applicationConfig.getProperty("file.upload.per.sizeinmb.max", "5"));
      int sizeMax = Integer.valueOf(applicationConfig.getProperty("file.upload.sizeinmb.max", "10"));
      ServletFileUpload upload = new ServletFileUpload();
      upload.setHeaderEncoding(C.UTF_8); 
      upload.setFileSizeMax(1024 * 1024 * fileSizeMax);
      upload.setSizeMax(1024 * 1024 * sizeMax);
      FileItemIterator iter;
      try {
        iter = upload.getItemIterator(req);
        while (iter.hasNext()) {
          FileItemStream item = iter.next();
          String name = item.getFieldName();
          InputStream stream = item.openStream();
          if (item.isFormField()) {
            String val = Streams.asString(stream, C.UTF_8);
            ctx.put(name, val);
            req.setAttribute(name, val);
          } else {
            if (fileUploadHandler != null) {
              String fileName = item.getName();
              String contentType = item.getContentType();
              ctx.putAll(fileUploadHandler.handle(name,fileName,contentType,stream, ctx));
            }
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
          req.setAttribute(parameterName, value);
        } else {
          String[] arrayValue = req.getParameterValues(parameterName);
          if (CommUtil.isNotEmpty(arrayValue)) {
            ctx.put(parameterName, arrayValue);
            req.setAttribute(parameterName, arrayValue);
          } else {
            ctx.put(parameterName, value);
            req.setAttribute(parameterName, value);
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
        String val = req.getParameter(t.substring("requestScope.".length()));
        if (val == null) {
          val = "";
        }
        sb.append(val);
      } else if (t.startsWith("sessionScope.")){
        Object val = req.getSession().getAttribute(t.substring("requestScope.".length()));
        if (val == null) {
          val = "";
        }
        sb.append(val);
      } else {
        sb.append(t);
      }
      
      if (i < len - 1) {
        sb.append(",");
      }
    }
    return sb.toString();
  }
  
  
  public static String rebuildRequestParams(String requestParams,HttpServletRequest req,Map<String,Object> resultMap) {
    StringBuilder sb = new StringBuilder();
    if (CommUtil.isEmpty(requestParams)) {
      return sb.toString();
    }
    String[] paramPair = requestParams.split("=");
    if (paramPair.length % 2 != 0) {
      throw new IllegalArgumentException("Illegal request params [" + requestParams + "]");
    }
    for (int i = 0; i < paramPair.length; i++) {
      String key = paramPair[i];
      String val = paramPair[++i];
      int leftBrace = val.indexOf("{");
      int rightBrace = val.indexOf("}");
      if (leftBrace >= 0 && rightBrace >= 0) {
        String valKey = val.substring(leftBrace + 1, rightBrace);
        
        if (valKey.startsWith("requestScope.")) {
          String finalVal = req.getParameter( valKey.substring("requestScope.".length()) );
          if (finalVal == null) {
            finalVal = "";
          }
          sb.append(key).append("=").append(finalVal);
        } else if (valKey.startsWith("sessionScope.")) {
          Object finalVal = req.getSession().getAttribute( valKey.substring("sessionScope.".length()) );
          if (finalVal == null) {
            finalVal = "";
          }
          sb.append(key).append("=").append(finalVal);
        } else {
          if (resultMap != null) {
            try {
              Object finalVal = Ognl.getValue(valKey, resultMap);
              sb.append(key).append("=").append(finalVal);
            } catch (OgnlException e) {
              sb.append(key).append("=").append("unResloved");
              Log.w("Unable to get value of [" + valKey + "]", TAG);
            }
          } 
        }
        
      } else {
        sb.append(key).append("=").append(val); 
      }
    }
    return sb.toString();
  }
  
  
  public static void setModelAsRequestAttributies(Map<String, Object> model, HttpServletRequest request) {
    for (Map.Entry<String, Object> entry : model.entrySet()) {
      String modelName = entry.getKey();
      Object modelValue = entry.getValue();
      if (modelValue != null) {
        request.setAttribute(modelName, modelValue);
      } else {
        request.removeAttribute(modelName);
      }
    }
  }
  
  
  public static String buildQueryString(HttpServletRequest request) {
    StringBuilder sb = new StringBuilder();
    String queryString = request.getQueryString();
    if (CommUtil.isNotEmpty(queryString)) {
      
    }
    return sb.toString();
  }
}

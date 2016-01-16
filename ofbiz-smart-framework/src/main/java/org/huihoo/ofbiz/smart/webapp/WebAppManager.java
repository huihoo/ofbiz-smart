package org.huihoo.ofbiz.smart.webapp;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
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
    
    Map<String, Object> ctx = CommUtil.toMap(C.CTX_DELETAGOR, delegator
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
      int fileSizeMax = Integer.parseInt(applicationConfig.getProperty("file.upload.per.sizeinmb.max", "5"));
      int sizeMax = Integer.parseInt(applicationConfig.getProperty("file.upload.sizeinmb.max", "10"));
      ServletFileUpload upload = new ServletFileUpload();
      upload.setHeaderEncoding(C.UTF_8); 
      upload.setFileSizeMax(1024L * 1024 * fileSizeMax);
      upload.setSizeMax(1024L * 1024 * sizeMax);
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
          if (CommUtil.isNotEmpty(arrayValue) && CommUtil.isNotEmpty(arrayValue[0])) {
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
  
  
  public static String parseParamPairString(String paramPair,HttpServletRequest req) {
    StringBuilder sb = new StringBuilder();
    String[] pToken = paramPair.split(",");
    int len = pToken.length;
    for (int i = 0; i < len; i++) {
      String t = pToken[i];
      Object val = t;
      if (t.startsWith("requestScope.")) {
        val = req.getParameter( t.substring("requestScope.".length()) );
      } else if (t.startsWith("sessionScope.")){
        val = req.getSession().getAttribute( t.substring("requestScope.".length()) );
      }
      if (CommUtil.isEmpty(val)) {
        val = "";
      }
      
      sb.append(val);
      
      if (i < len - 1) {
        sb.append(",");
      }
    }
    Log.d("Origal paramPair[%s] parsed paramPair[%s]", TAG,paramPair,sb);
    return sb.toString();
  }
  
  
  public static String parseConditionFromQueryString(HttpServletRequest req) {
    StringBuilder sb = new StringBuilder();
    String queryString = req.getQueryString();
    if (CommUtil.isNotEmpty(queryString)) {
      String[] paramPairArray = queryString.split("&");
      int len = paramPairArray.length;
      for (int i = 0; i <len ; i++) {
        String[] paramPair = paramPairArray[i].split("=");
        String key = paramPair[0];
        if (!key.startsWith("s_")) {
          continue;
        }
        String[] keyToken = key.split("_");
        String fieldName = keyToken[1];
        String expr = keyToken[2];
        
        String t = paramPair.length == 1 ? "" : paramPair[1];
        Object val = t;
        if (t.startsWith("requestScope.")) {
          val = req.getParameter( t.substring("requestScope.".length()) );
        } else if (t.startsWith("sessionScope.")){
          val = req.getSession().getAttribute( t.substring("requestScope.".length()) );
        }
        if (CommUtil.isEmpty(val)) {
          val = "";
        }
        if (CommUtil.isNotEmpty(val)) {
          sb.append("{")
            .append(fieldName)
            .append(",")
            .append(expr)
            .append(",")
            .append(val)
            .append("}");
        }
      }
    }
    Log.d("Origal condition[%s] parsed condition[%s]", TAG,queryString,sb);
    return sb.toString();
  }
  
  public static String parseCondition(String condition,HttpServletRequest req) {
    StringBuilder sb = new StringBuilder();
    List<Integer> braceIdxList = new ArrayList<>();
    if (CommUtil.isNotEmpty(condition)) {
      char[] chars = condition.toCharArray();
      int clen = chars.length;
      for (int i = 0; i < clen; i++) {
        int c = (int) chars[i];
        if (c == 123 || c == 125) {
          braceIdxList.add(i);
        }
      }
    }
    if (!braceIdxList.isEmpty()) {
      int bsize = braceIdxList.size();
      if (bsize % 2 == 0) {
        for (int j = 0; j < bsize; j++) {
          String tmpCond = condition.substring(braceIdxList.get(j) + 1,braceIdxList.get(++j));
          String[] tmpCondToken = tmpCond.split(",");
          String tmpField = tmpCondToken[0];
          String tmpExpr = tmpCondToken[1];
          String tmpVal = tmpCondToken[2];
          Object val = tmpVal;
          if (tmpVal.startsWith("requestScope.")) {
            val = req.getParameter( tmpVal.substring("requestScope.".length()) );
          } else if (tmpVal.startsWith("sessionScope.")) {
            val = req.getSession().getAttribute( tmpVal.substring("requestScope.".length()) );
          }
          if (CommUtil.isNotEmpty(val)) {
            sb.append("{")
              .append(tmpField)
              .append(",")
              .append(tmpExpr)
              .append(",")
              .append(val)
              .append("}");
          }
        }
      }
    }
    Log.d("Origal condition[%s] parsed condition[%s]", TAG,condition,sb);
    return sb.toString();
  }
  
  
  public static String parseQueryString(String queryString,HttpServletRequest req,Map<String,Object> resultMap) {
    StringBuilder sb = new StringBuilder();
    if (CommUtil.isEmpty(queryString)) {
      return sb.toString();
    }
    String[] paramPairArray = queryString.split("&");
    int len = paramPairArray.length;
    for (int i = 0; i <len ; i++) {
      String[] paramPair = paramPairArray[i].split("=");
      String key = paramPair[0];
      String val = paramPair.length == 1 ? "" : paramPair[1];
      int leftBrace = val.indexOf("{");
      int rightBrace = val.indexOf("}");
      if (leftBrace >= 0 && rightBrace >= 0) {
        String valKey = val.substring(leftBrace + 1, rightBrace);
        Object finalVal = null;
        if (valKey.startsWith("requestScope.")) {
          finalVal = req.getParameter( valKey.substring("requestScope.".length()) );
        } else if (valKey.startsWith("sessionScope.")) {
          finalVal = req.getSession().getAttribute( valKey.substring("sessionScope.".length()) );
        } else {
          if (resultMap != null) {
            try {
              finalVal = Ognl.getValue(valKey, resultMap);
            } catch (OgnlException e) {
              sb.append(key).append("=").append("unResloved");
              Log.w("Unable to get value of [" + valKey + "]", TAG);
            }
          } 
        }
        if (finalVal == null) {
          finalVal = "";
        }
        sb.append(key).append("=").append(finalVal);
      } else {
        sb.append(key).append("=").append(val); 
      }
      if (i < len -1) {
        sb.append("&");
      }
    }
    Log.d("Origal queryString[%s] parsed queryString[%s]", TAG,queryString,sb);
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
}

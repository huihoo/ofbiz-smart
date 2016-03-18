package org.huihoo.ofbiz.smart.webapp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.huihoo.ofbiz.smart.base.C;
import org.huihoo.ofbiz.smart.base.util.AppConfigUtil;
import org.huihoo.ofbiz.smart.base.util.CommUtil;
import org.huihoo.ofbiz.smart.base.util.Log;
import org.huihoo.ofbiz.smart.entity.Delegator;
import org.huihoo.ofbiz.smart.service.ServiceDispatcher;

import ognl.Ognl;
import ognl.OgnlException;

public class WebAppManager {
  
  private final static String TAG = WebAppManager.class.getName();
  
  private static volatile FileUploadHandler fileUploadHandler;
  
  public static Map<String, Object> buildWebCtx(HttpServletRequest req,HttpServletResponse resp) {
    ServletContext sc = req.getServletContext();
    Delegator delegator = (Delegator) sc.getAttribute(C.CTX_DELEGATOR);
    ServiceDispatcher serviceDispatcher =(ServiceDispatcher) sc.getAttribute(C.CTX_SERVICE_DISPATCHER);
    
    Map<String, Object> ctx = CommUtil.toMap(C.CTX_DELEGATOR, delegator
                                            ,C.CTX_SERVICE_DISPATCHER, serviceDispatcher
                                            ,C.CTX_WEB_HTTP_SERVLET_REQUEST, req
                                            ,C.CTX_WEB_HTTP_SERVLET_RESPONSE,resp
    );
    
    boolean isMultipart = ServletFileUpload.isMultipartContent(req);
    if (isMultipart) {
      if (fileUploadHandler == null) {
        String handlerName = AppConfigUtil.getProperty("file.upload.handler", "org.huihoo.ofbiz.smart.webapp.DefaultFileUploadHandler");
        try {
          fileUploadHandler = (FileUploadHandler) Class.forName(handlerName).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
          Log.w("Unable to load file upload handler [%s]", TAG,handlerName);
        }
      }
      int fileSizeMax = Integer.parseInt(AppConfigUtil.getProperty("file.upload.per.sizeinmb.max", "5"));
      int sizeMax = Integer.parseInt(AppConfigUtil.getProperty("file.upload.sizeinmb.max", "10"));
      DiskFileItemFactory f = new DiskFileItemFactory();
      f.setRepository((File)req.getServletContext().getAttribute(C.TEMP_DIR_CONTEXT_ATTRIBUTE));
      f.setSizeThreshold(1024*8);
      ServletFileUpload upload = new ServletFileUpload(f);
      upload.setHeaderEncoding(C.UTF_8); 
      upload.setFileSizeMax(1024L * 1024 * fileSizeMax);
      upload.setSizeMax(1024L * 1024 * sizeMax);
      try {
        Map<String,String> multiValueMap = new LinkedHashMap<>();
        List<FileItem> fiList = upload.parseRequest(req);
        for (FileItem fi : fiList) {
          String name = fi.getFieldName();
          if (fi.isFormField()) {
            String val = CommUtil.stripXSS(fi.getString(C.UTF_8));
            if (multiValueMap.containsKey(name)) { //多个值的处理
              ctx.remove(name);
              req.removeAttribute(name);
              multiValueMap.put(name, multiValueMap.get(name) + "," + val);
            } else {
              multiValueMap.put(name, val);
              ctx.put(name, val);
              req.setAttribute(name, val);
            }       
          } else {
            long size = fi.getSize();
            if (size >0 && fileUploadHandler != null) {
              String fileName = fi.getName();
              String contentType = fi.getContentType();
              ctx.putAll(fileUploadHandler.handle(name,fileName,contentType,fi.getInputStream(), ctx));
            }
          }
        }
        
        //多个值转换为数组
        Iterator<Entry<String, String>> mIter = multiValueMap.entrySet().iterator();
        while (mIter.hasNext()) {
          Entry<String, String>  entry = mIter.next();
          String ekey = entry.getKey();
          String eval = entry.getValue();
          if (CommUtil.isNotEmpty(eval) && eval.indexOf(",") >= 0) {
            String[] eArray = eval.split(",");
            ctx.put(ekey, eArray);
            req.setAttribute(ekey, eArray);
          }
        }
      } catch (FileUploadException | IOException e) {
        Log.w("Process MultipartContent failed.", TAG);
      }
    } else {
      Enumeration<String> parameterNames = req.getParameterNames();
      while (parameterNames.hasMoreElements()) {
        String parameterName = parameterNames.nextElement();
        if (parameterName.endsWith("[]")) {
          String[] arrayValue = CommUtil.stripXSS(req.getParameterValues(parameterName));
          if (CommUtil.isNotEmpty(arrayValue) && CommUtil.isNotEmpty(arrayValue[0])) {
            ctx.put(parameterName, arrayValue);
            req.setAttribute(parameterName, arrayValue);
          } 
        } else {
          String rawValue = req.getParameter(parameterName);
          String value = CommUtil.stripXSS(rawValue) ;
          if (CommUtil.isNotEmpty(value)) {
            ctx.put(parameterName, value);
            req.setAttribute(parameterName, value);
          } else {
            String[] arrayValue = CommUtil.stripXSS(req.getParameterValues(parameterName));
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
        val = req.getSession().getAttribute( t.substring("sessionScope.".length()) );
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
          val = req.getSession().getAttribute( t.substring("sessionScope.".length()) );
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
    List<Integer> braceIdxList = buildBraceList(condition);
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
            val = req.getSession().getAttribute( tmpVal.substring("sessionScope.".length()) );
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
  
  public static String parseRedirectUrl(String redirectUrl,Map<String,Object> modelMap,HttpServletRequest req) {
    String parsedRedirectUrl = WebAppManager.analyzeString(redirectUrl, modelMap, req);
    Log.d("Origal redirectUrl[%s] parsed redirectUrl[%s]", TAG,redirectUrl,parsedRedirectUrl);
    return parsedRedirectUrl;
  }
  
  
  public static String analyzeString(String input,Map<String,Object> modelMap,HttpServletRequest req) {
    if (CommUtil.isEmpty(input)) {
      return "";
    }
    StringBuilder sb = new StringBuilder(input);
    List<Integer> braceIdxList = buildBraceList(input);
    if (!braceIdxList.isEmpty()) {
      int bsize = braceIdxList.size();
      if (bsize % 2 == 0) {
        for (int j = 0; j < bsize; j++) {
          int fromIdx = braceIdxList.get(j) + 1;
          int toIdx = braceIdxList.get(++j);
          String tmpExpr = input.substring(fromIdx,toIdx);
          Object val = "";
          if (tmpExpr.startsWith("requestScope.")) {
            val = req.getParameter( tmpExpr.substring("requestScope.".length()) );
          } else if (tmpExpr.startsWith("sessionScope.")) {
            val = req.getSession().getAttribute( tmpExpr.substring("sessionScope.".length()) );
          } else if (tmpExpr.equalsIgnoreCase("uriSuffix")) {
            WebAppContext wac = (WebAppContext) req.getServletContext().getAttribute("webAppContext");
            val = wac.uriSuffix;
          } else {
            try {
              val = Ognl.getValue(tmpExpr, modelMap);
            } catch (OgnlException e) {
              Log.w("Unable to get %s's value of [%s]", TAG,tmpExpr,modelMap);
            }
          }
          sb.replace(fromIdx - 1, toIdx + 1, "" + val);
        }
      }
    }
    return sb.toString();
  }
  
  public static List<Integer> buildBraceList(String str) {
    List<Integer> braceIdxList = new ArrayList<>();
    if (CommUtil.isNotEmpty(str)) {
      char[] chars = str.toCharArray();
      int clen = chars.length;
      for (int i = 0; i < clen; i++) {
        int c = (int) chars[i];
        if (c == 123 || c == 125) {
          braceIdxList.add(i);
        }
      }
    }
    return braceIdxList;
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
  
  
  public static String getClientIp(HttpServletRequest request) {
    String ip = request.getHeader("x-forwarded-for");
    if (CommUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if (CommUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if (CommUtil.isEmpty(ip) || "unknown".equalsIgnoreCase(ip)) {
      ip = request.getRemoteAddr();
    }
    if (ip != null && ip.indexOf(",") >= 0) {
      ip = ip.substring(0, ip.indexOf(","));
    }
    return ip;
  }
}

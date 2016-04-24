package org.huihoo.ofbiz.smart.webapp.handler;


import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.huihoo.ofbiz.smart.base.C;
import org.huihoo.ofbiz.smart.base.util.AppConfigUtil;
import org.huihoo.ofbiz.smart.base.util.CommUtil;
import org.huihoo.ofbiz.smart.base.util.Log;
import org.huihoo.ofbiz.smart.base.util.ServiceUtil;
import org.huihoo.ofbiz.smart.entity.Delegator;
import org.huihoo.ofbiz.smart.entity.GenericEntityException;
import org.huihoo.ofbiz.smart.service.ServiceDispatcher;
import org.huihoo.ofbiz.smart.service.ServiceModel;
import org.huihoo.ofbiz.smart.webapp.FileUploadHandler;
import org.huihoo.ofbiz.smart.webapp.WebAppContext;
import org.huihoo.ofbiz.smart.webapp.view.View;
import org.huihoo.ofbiz.smart.webapp.view.ViewException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * 
 * @author huangbohua
 * 
 * @since 1.0
 */
public class HttpApiRequestHandler implements RequestHandler {
  private final static String TAG = HttpApiRequestHandler.class.getName();
  private static volatile FileUploadHandler fileUploadHandler;
  private final static List<String> SIGN_METHODS = Arrays.asList(new String[]{"md5","hmac"});
  private final static String APP_QUERY_SQL = "select app_key,app_secret,app_status from apps where app_key = ?";
  private final static String APP_USER_ACCESS_TOKEN_SQL = "select expired_in,created_at from app_user_access_tokens where access_token = ?";
  
  @Override
  public void handleRequest(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {
    // 公共参数包括(method,appKey,session,timestamp,format,signMethod,sign,clientId)
    // 业务参数(通用业务参数 + 一般业务参数)
    // 鉴权调用
    // 服务调用
    String apiQuerySql = AppConfigUtil.getProperty("smart.api.apps.query.sql",APP_QUERY_SQL);
    WebAppContext wac = (WebAppContext) req.getServletContext().getAttribute("webAppContext");
    ServiceDispatcher serviceDispatcher = wac.serviceDispatcher;
    Delegator delegator = (Delegator) req.getServletContext().getAttribute(C.CTX_DELEGATOR);    
    Map<String, Object> ctx = CommUtil.toMap(C.CTX_DELEGATOR, delegator
                                            ,C.CTX_SERVICE_DISPATCHER, serviceDispatcher
                                            ,C.CTX_WEB_HTTP_SERVLET_REQUEST, req
    );
    
    String method = req.getParameter("method");
    String format = req.getParameter("format");
    String appKey = req.getParameter("appKey");
    String timestamp = req.getParameter("timestamp");
    String signMethod = req.getParameter("signMethod");
    String sign = req.getParameter("sign"); 
    
    if (CommUtil.isEmpty(format)) {
      format = "json";
    }
    
    View view = wac.viewCache.get(format);
    try {
      if (view == null) {
        ObjectMapper objectMapper = new ObjectMapper();
        resp.setContentType(C.JSON_CONTENT_TYPE);
        resp.getWriter().write(objectMapper.writeValueAsString(ServiceUtil.returnProplem("UN_SUPPORTED_FORMAT", "Un supported format [" + format + "]")));
        resp.getWriter().flush();
        return ;
      }
      if (CommUtil.isEmpty(method)) {
        view.render(ServiceUtil.returnProplem("PARAM_METHOD_REQUIRED", "Parameter[method] required."), req, resp);
        return ;
      }      
      if (CommUtil.isEmpty(appKey)) {
        view.render(ServiceUtil.returnProplem("PARAM_APP_KEY_REQUIRED", "Parameter[app_key] required."), req, resp);
        return ;
      }      
      if (CommUtil.isEmpty(timestamp)) {
        view.render(ServiceUtil.returnProplem("PARAM_TIMESTAMP_REQUIRED", "Parameter[timestamp] required."), req, resp);
        return ;
      }
      if (CommUtil.isEmpty(signMethod)) {
        view.render(ServiceUtil.returnProplem("PARAM_SIGN_METHOD_REQUIRED", "Parameter[sign_method] required."), req, resp);
        return ;
      }
      if (CommUtil.isEmpty(sign)) {
        view.render(ServiceUtil.returnProplem("PARAM_SIGN_REQUIRED", "Parameter[sign] required."), req, resp);
        return ;
      }
      if (!SIGN_METHODS.contains(signMethod)) {
        view.render(ServiceUtil.returnProplem("UN_SUPPORTED_SIGN_METHOD", "Un supported sign method [" + signMethod + "]"), req, resp);
        return ;
      }
      Map<String,Object> appMap = null;
      try {
        appMap = delegator.findUniqueByRawQuery(apiQuerySql, Arrays.asList(new String[]{appKey}));
      } catch (GenericEntityException ge) {
        Log.d("Finding app with key [%s] has an exception [%s]", TAG,appKey,ge.getMessage());
      }
      
      if (appMap == null || CommUtil.isEmpty(appMap.get("app_key"))) {
        view.render(ServiceUtil.returnProplem("APP_IS_NOT_EXISTS", "App[" + appKey+ "] is not exists."), req, resp);
        return ;
      }
      
      //Sign
      Map<String,String> paramsMap = new TreeMap<>();
      Enumeration<String> paraNames = req.getParameterNames();
      while (paraNames.hasMoreElements()) {
        String pname = paraNames.nextElement();
        if (!"sign".equals(pname)) {
          String pval = req.getParameter(pname);
          paramsMap.put(pname, pval);
          ctx.put(pname, pval);
        }
      }
      
      StringBuilder sb = new StringBuilder();
      Iterator<Entry<String, String>> paraIter = paramsMap.entrySet().iterator();
      while (paraIter.hasNext()) {
        Entry<String, String> pEntry = paraIter.next();
        sb.append(pEntry.getKey()).append(pEntry.getValue());
      }
      Log.d("params[%s],to sign string [%s]",TAG,paramsMap.toString(),sb.toString());
      String appSecret = (String) appMap.get("app_secret");
      String signed = "";
      if ("md5".equals(signMethod)) {
        signed = CommUtil.md5(appSecret + sb.toString() + appSecret);
      } else if ("hmac".equals(signMethod)) {
        signed = CommUtil.hmacSha1(sb.toString(), appSecret);
      }
      if (!signed.equals(sign)) {
        view.render(ServiceUtil.returnProplem("SIGN_ILLEGAL", "App signed illegally."), req, resp);
        return ;
      }
      ServiceModel foundSm = null;
      Map<String, ServiceModel> scMap = serviceDispatcher.getServiceContextMap();
      Iterator<Entry<String, ServiceModel>> smIter = scMap.entrySet().iterator();
      while (smIter.hasNext()) {
        Entry<String, ServiceModel> entry = smIter.next();
        ServiceModel sm = entry.getValue();
        if (sm.export && method.equalsIgnoreCase(sm.apiAlias)) {
          foundSm = sm;
          break;
        }
      }
      if (foundSm == null) {
        view.render(ServiceUtil.returnProplem("SERVICE_NOT_FOUND", "Requested service [" + method + "] is not exist."), req, resp);
        return ;
      }
      
      //HTTP API ACCESS_TOKEN 
      if (foundSm.requireAuth) {
        String accessToken = req.getParameter("accessToken");
        if (CommUtil.isEmpty(accessToken)) {
          view.render(ServiceUtil.returnProplem("ACCESS_TOKEN_REQUIRED", "AccessToken required."), req, resp);
          return ;
        }
        String appUserAccessTokenSql = AppConfigUtil.getProperty("smart.api.apps.query.sql",APP_USER_ACCESS_TOKEN_SQL);
        try {
          Map<String,Object> accessTokenMap = delegator.findUniqueByRawQuery(appUserAccessTokenSql, Arrays.asList(new String[]{accessToken}));
          Date createdAt = (Date) accessTokenMap.get("created_at");
          int expiredIn = (Integer) accessTokenMap.get("expired_in");
          Date now = new Date();
          long spanMs = now.getTime() - createdAt.getTime();
          if (expiredIn * 1000 - spanMs <= 0) { //expired
            view.render(ServiceUtil.returnProplem("ACCESS_TOKEN_EXPIRED", "AccessToken Expired."), req, resp);
            return ;
          }
        } catch (GenericEntityException ge) {
          Log.d("Finding accessToken with accessToken [%s] has an exception [%s]", TAG,accessToken,ge.getMessage());
          view.render(ServiceUtil.returnProplem("ACCESS_TOKEN_FETCH_FAILED", "AccessToken fetch failed."), req, resp);
          return ;
        }
      }
      
      //二进制文件的处理 
      boolean isMultipart = ServletFileUpload.isMultipartContent(req);
      if (isMultipart) {
        if (fileUploadHandler == null) {
          String handlerName = AppConfigUtil.getProperty("http.api.file.upload.handler");
          if (CommUtil.isEmpty(handlerName)) {
            handlerName = AppConfigUtil.getProperty("file.upload.handler", "org.huihoo.ofbiz.smart.webapp.DefaultFileUploadHandler");
          }
          try {
            fileUploadHandler = (FileUploadHandler) Class.forName(handlerName).newInstance();
          } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            Log.w("Unable to load file upload handler [%s]", TAG,handlerName);
          }
        }
        if (fileUploadHandler != null) {
          int fileSizeMax = Integer.parseInt(AppConfigUtil.getProperty("file.upload.per.sizeinmb.max", "5")); //file per max size : 5MB
          int sizeMax = Integer.parseInt(AppConfigUtil.getProperty("file.upload.sizeinmb.max", "10"));        //all file max size:  10MB
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
              if (!item.isFormField()) {
                 String fileName = item.getName();
                 String contentType = item.getContentType();
                 ctx.putAll(fileUploadHandler.handle(name,fileName,contentType,stream, ctx));
              }
            }
          } catch (FileUploadException | IOException e) {
            Log.w("Process MultipartContent failed.", TAG);
          }
        }
      }
      Map<String,Object> resultMap = serviceDispatcher.runSync(foundSm.name, ctx);
      view.render(resultMap, req, resp);
    } catch (Exception e) {
      Log.e(e, "Requested service[" + method + "] has an exception.", TAG);
      try {
        view.render(ServiceUtil.returnProplem("SERVICE_UNAVAILABLE", "Requested service is unavailable."), req, resp);
      } catch (ViewException e1) {
        //Ignore...
      }
    }
  }
}

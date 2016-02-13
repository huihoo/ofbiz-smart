package org.huihoo.ofbiz.smart.webapp.handler;


import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.huihoo.ofbiz.smart.base.C;
import org.huihoo.ofbiz.smart.base.util.AppConfigUtil;
import org.huihoo.ofbiz.smart.base.util.CommUtil;
import org.huihoo.ofbiz.smart.base.util.Log;
import org.huihoo.ofbiz.smart.base.util.ServiceUtil;
import org.huihoo.ofbiz.smart.entity.Delegator;
import org.huihoo.ofbiz.smart.entity.GenericEntityException;
import org.huihoo.ofbiz.smart.service.ServiceDispatcher;
import org.huihoo.ofbiz.smart.service.ServiceModel;
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
  
  private final static List<String> SIGN_METHODS = Arrays.asList(new String[]{"md5","hmac"});
  private final static String APP_QUERY_SQL = "select app_key,app_secret,app_status from apps where app_key = ?";

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
    Delegator delegator = (Delegator) req.getServletContext().getAttribute(C.CTX_DELETAGOR);
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
        List<Map<String,Object>> apiList = delegator.findListByRawQuery(apiQuerySql, Arrays.asList(new String[]{appKey}));
        if (CommUtil.isNotEmpty(apiList)) {
          appMap = apiList.get(0);
        }
      } catch (GenericEntityException ge) {
        Log.d("Finding app with key [%s] has an exception [%s]", TAG,appKey,ge.getMessage());
      }
      if (appMap == null || CommUtil.isEmpty(appMap.get("app_key"))) {
        view.render(ServiceUtil.returnProplem("APP_IS_NOT_EXISTS", "App[" + appKey+ "] is not exists."), req, resp);
        return ;
      }
      Map<String,Object> ctx = new HashMap<>();
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
      //TODO 二进制文件的处理 
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

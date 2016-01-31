package org.huihoo.ofbiz.smart.webapp.handler;


import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.huihoo.ofbiz.smart.base.util.AppConfigUtil;
import org.huihoo.ofbiz.smart.base.util.CommUtil;
import org.huihoo.ofbiz.smart.base.util.Log;
import org.huihoo.ofbiz.smart.base.util.ServiceUtil;
import org.huihoo.ofbiz.smart.service.ServiceDispatcher;
import org.huihoo.ofbiz.smart.service.ServiceModel;
import org.huihoo.ofbiz.smart.webapp.WebAppContext;
import org.huihoo.ofbiz.smart.webapp.view.View;
import org.huihoo.ofbiz.smart.webapp.view.ViewException;

/**
 * 基于HTTP的，轻量级的，淘宝API风格的,API调用处理
 * 
 * @author huangbohua
 * 
 * @since 1.0
 */
public class HttpApiRequestHandler implements RequestHandler {

  private final static String TAG = HttpApiRequestHandler.class.getName();
  
  private final static String APP_QUERY_SQL = "select app_key,app_secret,app_status from apps where app_key = ?";

  @Override
  public void handleRequest(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {
    // 公共参数包括(method,app_key,session,timestamp,format,sign_method,sign,client_id)
    // 业务参数(通用业务参数 + 一般业务参数)
    // 鉴权调用
    // 服务调用
    AppConfigUtil.getProperty("smart.api.apps.query.sql",APP_QUERY_SQL);
    WebAppContext wac = (WebAppContext) req.getServletContext().getAttribute("webAppContext");
    ServiceDispatcher serviceDispatcher = wac.serviceDispatcher;
    String method = req.getParameter("method");
    String format = req.getParameter("format");
    if (CommUtil.isEmpty(format)) {
      format = "json";
    }
    
    View view = wac.viewCache.get(format);
    try {
      if (CommUtil.isEmpty(method)) {
        view.render(ServiceUtil.returnProplem("", ""), req, resp);
      }
    } catch (ViewException e) {
      
    }
    Map<String, ServiceModel> scMap = serviceDispatcher.getServiceContextMap();
    Iterator<Entry<String, ServiceModel>> smIter = scMap.entrySet().iterator();
    
  }

}

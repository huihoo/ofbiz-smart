package org.huihoo.ofbiz.smart.webapp.handler;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * 基于HTTP的，轻量级的，淘宝API风格的,API调用处理
 * 
 * @author huangbohua
 * 
 * @since 1.0
 */
public class HttpApiRequestHandler implements RequestHandler {
  @Override
  public void handleRequest(HttpServletRequest req, HttpServletResponse resp)  throws ServletException, IOException{
    //公共参数包括(method,app_key,session,timestamp,format,sign_method,sign)放在HTTP header里
    //业务参数(通用业务参数 + 一般业务参数)
    //鉴权调用
    //服务调用
  }
}

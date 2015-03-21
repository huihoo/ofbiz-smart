package org.huihoo.ofbiz.smart.webapp.control;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * Action拦截器接口
 * </p>
 * 
 * @author huangbohua
 *
 */
public interface ActionInterceptorInterface {

  public void before(HttpServletRequest req, HttpServletResponse resp);


  public void success(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> result);
}

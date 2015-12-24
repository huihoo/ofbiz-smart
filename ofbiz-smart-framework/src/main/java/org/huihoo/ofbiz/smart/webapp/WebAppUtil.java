package org.huihoo.ofbiz.smart.webapp;

import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.huihoo.ofbiz.smart.base.C;
import org.huihoo.ofbiz.smart.base.util.CommUtil;
import org.huihoo.ofbiz.smart.entity.Delegator;
import org.huihoo.ofbiz.smart.service.ServiceDispatcher;

public class WebAppUtil {

  public static Map<String, Object> buildWebCtx(HttpServletRequest req) {
    ServletContext sc = req.getServletContext();
    Delegator delegator = (Delegator) sc.getAttribute(C.CTX_DELETAGOR);
    ServiceDispatcher serviceDispatcher =
            (ServiceDispatcher) sc.getAttribute(C.CTX_SERVICE_DISPATCHER);
    Properties applicationConfig = (Properties) sc.getAttribute(C.APPLICATION_CONFIG_PROP_KEY);
    Map<String, Object> ctx =
            CommUtil.toMap(C.CTX_DELETAGOR, delegator, C.CTX_SERVICE_DISPATCHER, serviceDispatcher);
    ctx.put(C.ACTION_CONFIG_BASEPATH_KEY, applicationConfig);
    ctx.put(C.CTX_WEB_HTTP_SERVLET_REQUEST, req);
    //TODO req parameters.
    return ctx;
  }

}

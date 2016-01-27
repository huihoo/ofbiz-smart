package org.huihoo.ofbiz.smart.webapp;


import java.util.List;

import javax.servlet.ServletContext;

import org.huihoo.ofbiz.smart.base.C;
import org.huihoo.ofbiz.smart.base.cache.Cache;
import org.huihoo.ofbiz.smart.entity.Delegator;
import org.huihoo.ofbiz.smart.service.ServiceDispatcher;
import org.huihoo.ofbiz.smart.webapp.view.View;

public class WebAppContext{

  @SuppressWarnings("unchecked")
  public WebAppContext(ServletContext servletContext) {
    this.serviceDispatcher = (ServiceDispatcher) servletContext.getAttribute(C.CTX_SERVICE_DISPATCHER);
    this.actionModels = (List<ActionModel>) servletContext.getAttribute(C.CTX_ACTION_MODEL);
    this.viewCache = (Cache<String, View>) servletContext.getAttribute(C.CTX_SUPPORTED_VIEW_ATTRIBUTE);
    this.jspViewBasePath = (String) servletContext.getAttribute(C.CTX_JSP_VIEW_BASEPATH);
    this.uriSuffix = (String) servletContext.getAttribute(C.CTX_URI_SUFFIX);
  }


  public ServiceDispatcher serviceDispatcher;
  public Delegator delegator;
  public Cache<String, View> viewCache;
  public List<ActionModel> actionModels;
  public String jspViewBasePath;
  public String uriSuffix;

}

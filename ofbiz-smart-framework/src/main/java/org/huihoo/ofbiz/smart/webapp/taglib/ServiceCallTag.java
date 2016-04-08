package org.huihoo.ofbiz.smart.webapp.taglib;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.huihoo.ofbiz.smart.base.C;
import org.huihoo.ofbiz.smart.base.util.CommUtil;
import org.huihoo.ofbiz.smart.base.util.ServiceUtil;
import org.huihoo.ofbiz.smart.service.ServiceDispatcher;
import org.huihoo.ofbiz.smart.service.ServiceEngineType;
import org.huihoo.ofbiz.smart.service.ServiceModel;
import org.huihoo.ofbiz.smart.webapp.WebAppManager;

public class ServiceCallTag extends TagSupport {
  private static final long serialVersionUID = 1L;

  private String serviceName;
  private String entityName;
  private String resultName;
  private String paramPairs;
  private String condition;
  private String orderBy;
  private boolean useCache;
  private int liveTimeInSeconds;

  @Override
  public int doStartTag() throws JspException {
    HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
    HttpServletResponse resp = (HttpServletResponse) pageContext.getResponse();
    ServiceDispatcher serviceDispatcher =
        (ServiceDispatcher) pageContext.getServletContext().getAttribute(C.CTX_SERVICE_DISPATCHER);
    
    Map<String, Object> webCtx = WebAppManager.buildWebCtx(req,resp);
    ServiceModel sm = new ServiceModel();
    
    if (CommUtil.isNotEmpty(resultName)) {
      webCtx.put(C.SERVICE_RESULT_NAME_ATTRIBUTE, resultName);
    }
    
    if (CommUtil.isNotEmpty(paramPairs)) {
      String p = WebAppManager.parseParamPairString(paramPairs, req);
      Map<String,Object> paramMap = ServiceUtil.covertParamPairToMap(p);
      webCtx.put(C.ENTITY_ANDMAP, paramMap);
      webCtx.putAll(paramMap);
    }
    
    if (CommUtil.isNotEmpty(condition)) {
      webCtx.put(C.ENTITY_CONDTION, WebAppManager.parseCondition(condition, req));
    } else {
      webCtx.put(C.ENTITY_CONDTION, WebAppManager.parseConditionFromQueryString(req));
    }

    
    webCtx.put(C.ENTITY_USE_CACHE, useCache);
    webCtx.put(C.ENTITY_LIVETIMEIN_SECONDS, liveTimeInSeconds);
    if (CommUtil.isNotEmpty(orderBy)) {
      webCtx.put(C.ENTITY_ORDERBY,Arrays.asList(orderBy.split(",")));
    }
    
    if (serviceName.startsWith(ServiceEngineType.ENTITY_AUTO.value() + "#")) {
      sm.name = serviceName;
      sm.engineName = ServiceEngineType.ENTITY_AUTO.value();
      sm.entityName = entityName;
      sm.invoke = serviceName.substring((ServiceEngineType.ENTITY_AUTO.value() + "#").length());
      serviceDispatcher.registerService(sm);
    } else {
      sm.name = serviceName;
      sm.engineName = ServiceEngineType.JAVA.value();
    }

    Map<String, Object> resultMap = serviceDispatcher.runSync(sm.name, webCtx);
    if (ServiceUtil.isSuccess(resultMap)) {
      Iterator<Entry<String, Object>> iter = resultMap.entrySet().iterator();
      while (iter.hasNext()) {
        Entry<String, Object> entry = iter.next();
        pageContext.setAttribute(entry.getKey(), entry.getValue());
      }
    }
    return EVAL_BODY_INCLUDE;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public String getEntityName() {
    return entityName;
  }

  public void setEntityName(String entityName) {
    this.entityName = entityName;
  }

  public String getResultName() {
    return resultName;
  }

  public void setResultName(String resultName) {
    this.resultName = resultName;
  }

  public String getParamPairs() {
    return paramPairs;
  }

  public void setParamPairs(String paramPairs) {
    this.paramPairs = paramPairs;
  }

  public String getCondition() {
    return condition;
  }

  public void setCondition(String condition) {
    this.condition = condition;
  }

  public int getLiveTimeInSeconds() {
    return liveTimeInSeconds;
  }

  public void setLiveTimeInSeconds(int liveTimeInSeconds) {
    this.liveTimeInSeconds = liveTimeInSeconds;
  }

  public boolean isUseCache() {
    return useCache;
  }

  public void setUseCache(boolean useCache) {
    this.useCache = useCache;
  }

  public String getOrderBy() {
    return orderBy;
  }

  public void setOrderBy(String orderBy) {
    this.orderBy = orderBy;
  }
}

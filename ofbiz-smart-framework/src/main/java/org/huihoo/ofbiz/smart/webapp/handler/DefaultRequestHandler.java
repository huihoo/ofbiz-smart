package org.huihoo.ofbiz.smart.webapp.handler;


import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.huihoo.ofbiz.smart.base.C;
import org.huihoo.ofbiz.smart.base.cache.Cache;
import org.huihoo.ofbiz.smart.base.cache.SimpleCacheManager;
import org.huihoo.ofbiz.smart.base.util.AntPathMatcher;
import org.huihoo.ofbiz.smart.base.util.CommUtil;
import org.huihoo.ofbiz.smart.base.util.Log;
import org.huihoo.ofbiz.smart.base.util.PathMatcher;
import org.huihoo.ofbiz.smart.base.util.ServiceUtil;
import org.huihoo.ofbiz.smart.entity.Delegator;
import org.huihoo.ofbiz.smart.service.ServiceDispatcher;
import org.huihoo.ofbiz.smart.service.ServiceModel;
import org.huihoo.ofbiz.smart.webapp.ActionModel;
import org.huihoo.ofbiz.smart.webapp.ProcessType;
import org.huihoo.ofbiz.smart.webapp.ActionModel.Action;
import org.huihoo.ofbiz.smart.webapp.ActionModel.ServiceCall;
import org.huihoo.ofbiz.smart.webapp.WebAppUtil;
import org.huihoo.ofbiz.smart.webapp.view.View;
import org.huihoo.ofbiz.smart.webapp.view.ViewException;

import ognl.Ognl;
import ognl.OgnlException;




public class DefaultRequestHandler implements RequestHandler {
  private final static String TAG = DefaultRequestHandler.class.getName();
  
  @SuppressWarnings("unchecked")
  private final static Cache<String,String> ENTITY_CLAZZ_NAME_CACHE = 
                   (Cache<String,String>) SimpleCacheManager.createCache("Request-Handler-EntityClazz-Cache");
  
  
  
  @SuppressWarnings("unchecked")
  @Override
  public void handleRequest(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {
    ServletContext sc = req.getServletContext();
    
    Delegator delegator = (Delegator) sc.getAttribute(C.CTX_DELETAGOR);
    ServiceDispatcher serviceDispatcher = (ServiceDispatcher) sc.getAttribute(C.CTX_SERVICE_DISPATCHER);
    Properties applicationConfig = (Properties) sc.getAttribute(C.APPLICATION_CONFIG_PROP_KEY);
    List<ActionModel> actionModels = (List<ActionModel>) sc.getAttribute(C.CTX_ACTION_MODEL);
    Cache<String,View> viewCache = (Cache<String,View>) sc.getAttribute(C.CTX_SUPPORTED_VIEW_ATTRIBUTE);
    String jspViewBasePath = (String) sc.getAttribute(C.CTX_JSP_VIEW_BASEPATH);
    String uriSuffix = (String) sc.getAttribute(C.CTX_URI_SUFFIX);

    String targetUri = req.getRequestURI();
    if (targetUri.startsWith(req.getContextPath())) {
      targetUri = targetUri.substring(req.getContextPath().length());
    }

    if (targetUri.endsWith(uriSuffix)) {
      targetUri = targetUri.substring(0, targetUri.indexOf(uriSuffix));
    }
    Action reqAction = matchAction(actionModels, targetUri);
    Log.d("Action : " + reqAction, TAG);
    if (reqAction != null) {
      Map<String, Object> modelMap = ServiceUtil.returnSuccess();
      Map<String, Object> ctx = WebAppUtil.buildWebCtx(req);

      // TODO request convert to map

      List<ServiceCall> serviceCalls = reqAction.serviceCallList;
      if (CommUtil.isNotEmpty(serviceCalls)) {
        for (ServiceCall serviceCall : serviceCalls) {
          ctx.put(C.SERVICE_RESULT_NAME_ATTRIBUTE, serviceCall.resultName);
          
          ServiceModel sm = new ServiceModel();
          if (CommUtil.isNotEmpty(serviceCall.paramPairs)) {
            String p = WebAppUtil.analyzeParamPairString(serviceCall.paramPairs, req);
            ctx.putAll(ServiceUtil.covertParamPairToMap(p));
          }
          
          if (serviceCall.serviceName.startsWith("entityAuto#")) {
            sm.name = serviceCall.serviceName;
            sm.engineName = "entityAuto";
            sm.entityName = serviceCall.entityName;
            sm.invoke = serviceCall.serviceName.substring("entityAuto#".length());
          } else {
            sm.name = serviceCall.serviceName;
            sm.engineName = "java";
          }
          
          serviceDispatcher.registerService(sm);
          
          Map<String, Object> sResult = serviceDispatcher.runSync(sm.name, ctx);
          
          if (ServiceUtil.isSuccess(sResult)) {
            modelMap.putAll(sResult);
          }
        }
      }

      setPageAttributies(req, reqAction);

      String viewType = null;
      String layout = null;
      if (reqAction.response != null) {
        viewType = reqAction.response.viewType;
        layout = reqAction.response.layout;
      }

      if (viewType == null) {
        viewType = "jsp";
      }

      View view = viewCache.get(viewType);
      
      if (ProcessType.URI_AUTO.value().equals(reqAction.processType)) {
        String viewName = null;
        if (layout != null) {
          viewName = jspViewBasePath + layout;
          req.setAttribute(C.JSP_VIEW_LAYOUT_CONTENT_VIEW_ATTRIBUTE, jspViewBasePath + targetUri + ".jsp");
        } else {
          viewName = jspViewBasePath + targetUri + ".jsp";
        }
        req.setAttribute(C.JSP_VIEW_NAME_ATTRIBUTE, viewName);
      } else if (ProcessType.ENTITY_AUTO.value().equals(reqAction.processType)) {

        try {
          view = viewCache.get("jsp");
          doEntityAutoAction(modelMap,serviceDispatcher, req, resp,applicationConfig, targetUri, 
                                                          jspViewBasePath,uriSuffix,viewCache,view,ctx,layout);
        } catch (ViewException e) {
          throw new ServletException(e);
        }
        //自动处理时，忽略后面的处理
        return ;

      } else if (ProcessType.BY_CONFIG.value().equals(reqAction.processType)) {
        req.setAttribute(C.JSP_VIEW_NAME_ATTRIBUTE, jspViewBasePath + targetUri + ".jsp");
      }

      try {
        view.render(modelMap, req, resp);
      } catch (ViewException e) {
        throw new ServletException(e);
      }
    }
  }

  private void doEntityAutoAction(Map<String,Object> modelMap,
                                  ServiceDispatcher serviceDispatcher, 
                                  HttpServletRequest req,
                                  HttpServletResponse resp,
                                  Properties applicationConfig,
                                  String targetUri, 
                                  String jspViewBasePath,
                                  String uriSuffix,
                                  Cache<String, View> viewCache,
                                  View view,
                                  Map<String,Object> ctx,
                                  String layout) throws ViewException {
    
    String[] spiltUri = targetUri.split("/");
    String firstUriString = spiltUri[1];
    String guessEntityName =
            firstUriString.substring(0, 1).toUpperCase() + firstUriString.substring(1);
    Log.d("Guessed Engity Name : " + guessEntityName, TAG);

    String entityClazzName = ENTITY_CLAZZ_NAME_CACHE.get(guessEntityName);
    
    if (entityClazzName == null) {
      String entityScanningPkg = applicationConfig.getProperty(C.ENTITY_SCANNING_PACKAGES);
      String[] espToken = entityScanningPkg.split(",");
      for (String t : espToken) {
        entityClazzName = t.substring(0,t.indexOf(".**")) + "." + guessEntityName;
        Log.d("Guessed Engity Class Name : " + entityClazzName, TAG);
        try {
          Class.forName(entityClazzName);
          ENTITY_CLAZZ_NAME_CACHE.put(guessEntityName, entityClazzName);
        } catch (ClassNotFoundException e) {
          Log.w("Entity class [" + entityClazzName + "] not found.", TAG);
        }
      }
    }
    
    if (entityClazzName == null) {
      throw new IllegalArgumentException("Entity name [" + guessEntityName + "] related to entity class not found.");
    }
    
    Map<String,Object> result = ServiceUtil.returnSuccess();
    
    ServiceModel sm = new ServiceModel();
    sm.engineName = "entityAuto";
    sm.entityName = entityClazzName;
    
    String viewName = "";
    View redirectView = null;
    
    if (targetUri.endsWith("/list") || targetUri.endsWith("/index")) {
      sm.name = sm.engineName + "#" + C.SERVICE_ENGITYAUTO_FINDPAGEBYCOND;
      sm.invoke = C.SERVICE_ENGITYAUTO_FINDPAGEBYCOND;
      viewName = jspViewBasePath + "/" +firstUriString + "/list.jsp";
    } else if (targetUri.endsWith("/create") || targetUri.endsWith("/add")) {
      viewName = jspViewBasePath + "/" + firstUriString + "/form.jsp";
    } else if (targetUri.endsWith("/save") || targetUri.endsWith("/new")) {
      sm.name = sm.engineName + "#" + C.SERVICE_ENGITYAUTO_CREATE;
      sm.invoke = C.SERVICE_ENGITYAUTO_CREATE;
      viewName = jspViewBasePath + "/" +firstUriString + "/view.jsp";
      
      redirectView = viewCache.get("redirect");
      
    } else if (targetUri.endsWith("/update") || targetUri.endsWith("/modify")) {
      sm.name = sm.engineName + "#" + C.SERVICE_ENGITYAUTO_UPDATE;
      sm.invoke = C.SERVICE_ENGITYAUTO_UPDATE;
      viewName = jspViewBasePath + "/" +firstUriString + "/view.jsp";
      
      redirectView = viewCache.get("redirect");
      
    } else if (targetUri.endsWith("/view") || targetUri.endsWith("/detail")) {
      sm.name = sm.engineName + "#" + C.SERVICE_ENGITYAUTO_FINDBYID;
      sm.invoke = C.SERVICE_ENGITYAUTO_FINDBYID;
      viewName = jspViewBasePath + "/" +firstUriString + "/view.jsp";
    }
    
    serviceDispatcher.registerService(sm);

    if (sm.invoke != null) {
      result = serviceDispatcher.runSync(sm.name, ctx);
    }
    
    
    if (ServiceUtil.isSuccess(result)) {
      
      modelMap.putAll(result);
      
      if (redirectView != null) {
        Object idValue;
        try {
          idValue = Ognl.getValue("id", result.get(C.ENTITY_MODEL_NAME));
        } catch (OgnlException e) {
          throw new ViewException("Unable to get id");
        }
        req.setAttribute("targetUri", req.getContextPath() + "/" + firstUriString + "/view" + uriSuffix + "?id=" + idValue);
        redirectView.render(modelMap, req, resp);
      } else {
        req.setAttribute(C.JSP_VIEW_NAME_ATTRIBUTE, jspViewBasePath + layout);
        req.setAttribute(C.JSP_VIEW_LAYOUT_CONTENT_VIEW_ATTRIBUTE, viewName);
        Log.d("layoutContentView : " + viewName, TAG);
        view.render(modelMap, req, resp);
      }
    } else {
      
    }
  }

  private Action matchAction(List<ActionModel> actionModels, String uri) {
    PathMatcher pathMatcher = new AntPathMatcher();
    for (ActionModel actionModel : actionModels) {
      for (Action action : actionModel.actionList) {
        if (pathMatcher.match(action.uri, uri)) {
          return action;
        }
      }
    }
    return null;
  }

  private void setPageAttributies(HttpServletRequest req, Action reqAction) {
    String ctxPath = req.getContextPath();
    req.setAttribute("navTag", reqAction.navTag);
    req.setAttribute("pageTitle", reqAction.pageTitle);
    String moreCss = reqAction.moreCss;
    if (CommUtil.isNotEmpty(moreCss)) {
      String[] cssArray = moreCss.split(",");
      StringBuilder sb = new StringBuilder();
      for (String css : cssArray) {
        if (!css.startsWith(ctxPath) && !css.startsWith("http://")) {
          css = ctxPath + css;
        }
        sb.append("<link rel=\"stylesheet\" href=\"" + css + "\">");
      }
      req.setAttribute("moreCss", sb.toString());
    }
    String moreJavascripts = reqAction.moreJavascripts;
    if (CommUtil.isNotEmpty(moreJavascripts)) {
      String[] jsArray = moreJavascripts.split(",");
      StringBuilder sb = new StringBuilder();
      for (String js : jsArray) {
        if (!js.startsWith(ctxPath) && !js.startsWith("http://")) {
          js = ctxPath + js;
        }
        sb.append("<script src=\"" + js + "\"></script>");
      }
      req.setAttribute("moreJavascripts", sb.toString());
    }
  }
}

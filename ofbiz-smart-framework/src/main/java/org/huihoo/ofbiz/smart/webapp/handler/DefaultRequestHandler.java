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
import org.huihoo.ofbiz.smart.service.ServiceEngineType;
import org.huihoo.ofbiz.smart.service.ServiceModel;
import org.huihoo.ofbiz.smart.webapp.ActionModel;
import org.huihoo.ofbiz.smart.webapp.ProcessType;
import org.huihoo.ofbiz.smart.webapp.ActionModel.Action;
import org.huihoo.ofbiz.smart.webapp.ActionModel.ServiceCall;
import org.huihoo.ofbiz.smart.webapp.WebAppManager;
import org.huihoo.ofbiz.smart.webapp.view.CaptchaView;
import org.huihoo.ofbiz.smart.webapp.view.JsonView;
import org.huihoo.ofbiz.smart.webapp.view.JspView;
import org.huihoo.ofbiz.smart.webapp.view.RedirectView;
import org.huihoo.ofbiz.smart.webapp.view.View;
import org.huihoo.ofbiz.smart.webapp.view.ViewException;
import org.huihoo.ofbiz.smart.webapp.view.XmlView;

import ognl.Ognl;
import ognl.OgnlException;




public class DefaultRequestHandler implements RequestHandler {
  private final static String TAG = DefaultRequestHandler.class.getName();
  
  @SuppressWarnings("unchecked")
  private final static Cache<String,String> ENTITY_CLAZZ_NAME_CACHE = 
                   (Cache<String,String>) SimpleCacheManager.createCache("Request-Handler-EntityClazz-Cache");
  
  
  private static volatile Delegator delegator;
  private static volatile ServiceDispatcher serviceDispatcher;
  private static volatile Properties applicationConfig;
  private static volatile List<ActionModel> actionModels;
  private static volatile Cache<String,View> viewCache;
  private static volatile String jspViewBasePath;
  private static volatile String uriSuffix;
  
  @SuppressWarnings("unchecked")
  @Override
  public void handleRequest(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {
    ServletContext sc = req.getSession().getServletContext();
    //get deletagor,serviceDispatcher,applicationProp from web context.
    if  (delegator == null) {
      delegator = (Delegator) sc.getAttribute(C.CTX_DELETAGOR);
    }
    if (serviceDispatcher == null) {
      serviceDispatcher = (ServiceDispatcher) sc.getAttribute(C.CTX_SERVICE_DISPATCHER);
    }
    if (applicationConfig == null) {
      applicationConfig = (Properties) sc.getAttribute(C.APPLICATION_CONFIG_PROP_KEY);
    }
    if (actionModels == null) {
      actionModels = (List<ActionModel>) sc.getAttribute(C.CTX_ACTION_MODEL);
    }
    if (viewCache == null) {
      viewCache = (Cache<String,View>) sc.getAttribute(C.CTX_SUPPORTED_VIEW_ATTRIBUTE);
    }
    if (jspViewBasePath == null) {
      jspViewBasePath = (String) sc.getAttribute(C.CTX_JSP_VIEW_BASEPATH);
    }
    if (uriSuffix == null) {
      uriSuffix = (String) sc.getAttribute(C.CTX_URI_SUFFIX);
    }
    
    //remove contextpath
    String targetUri = req.getRequestURI();
    if (targetUri.startsWith(req.getContextPath())) {
      targetUri = targetUri.substring(req.getContextPath().length());
    }

    //remove uri suffix
    if (CommUtil.isNotEmpty(uriSuffix) && targetUri.endsWith(uriSuffix)) {
      targetUri = targetUri.substring(0, targetUri.indexOf(uriSuffix));
    }
    
    Action reqAction = matchAction(actionModels, targetUri);
    Log.d("Action : " + reqAction, TAG);
    
    if (reqAction != null) {
      String actionMethod = reqAction.method;
      String method = req.getMethod();
      
      boolean allowedMethod = true;
      if (CommUtil.isNotEmpty(actionMethod) && !"all".equals(actionMethod) && !actionMethod.equalsIgnoreCase(method)) {
        allowedMethod = false;
      }
      if (!allowedMethod) {
        resp.sendError(400, "Unsupported method.");
        return ;
      }
      
      Map<String, Object> modelMap = ServiceUtil.returnSuccess();
      
      //build web ctx for service call.
      Map<String, Object> webCtx = WebAppManager.buildWebCtx(req);
      
      //calling all available configured service
      Map<String, Object> lastResult = null; 
      List<ServiceCall> serviceCalls = reqAction.serviceCallList;
      if (CommUtil.isNotEmpty(serviceCalls)) {
        for (ServiceCall serviceCall : serviceCalls) {
          webCtx.put(C.SERVICE_RESULT_NAME_ATTRIBUTE, serviceCall.resultName);//set service result name
          ServiceModel sm = new ServiceModel();
          //set param pairs
          if (CommUtil.isNotEmpty(serviceCall.paramPairs)) {
            String p = WebAppManager.parseParamPairString(serviceCall.paramPairs, req);
            webCtx.put(C.ENTITY_ANDMAP, ServiceUtil.covertParamPairToMap(p));
          }
          //set condition
          if (CommUtil.isNotEmpty(serviceCall.condition)) {
            webCtx.put(C.ENTITY_CONDTION, "");
          }
          
          //entityAuto service
          if (serviceCall.serviceName.startsWith( ServiceEngineType.ENTITY_AUTO.value() + "#") ) {
            sm.name = serviceCall.serviceName;
            sm.engineName = ServiceEngineType.ENTITY_AUTO.value();
            sm.entityName = serviceCall.entityName;
            sm.invoke = serviceCall.serviceName.substring( (ServiceEngineType.ENTITY_AUTO.value() + "#").length() );
          } else {//normal java service
            sm.name = serviceCall.serviceName;
            sm.engineName = ServiceEngineType.JAVA.value();
          }
          serviceDispatcher.registerService(sm);
          lastResult = serviceDispatcher.runSync(sm.name, webCtx);
          if (ServiceUtil.isError(lastResult)) {
            break;
          }
          modelMap.putAll(lastResult);
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
      //Just for test.
      if (view == null) {
        if ("jsp".equals(viewType)) {
          view = new JspView();          
        } else if ("redirect".equals(viewType)) {
          view = new RedirectView();
        } else if ("json".equals(viewType)) {
          view = new JsonView();
        } else if ("xml".equals(viewType)) {
          view = new XmlView();
        } else if ("captcha".equals(viewType)) {
          view = new CaptchaView();
        }
        viewCache.put(viewType, view);
      }
      
      if (ProcessType.URI_AUTO.value().equals(reqAction.processType)) {
        String viewName = null;
        if (layout != null && !"none".equals(layout)) {
          viewName = jspViewBasePath + layout;
          req.setAttribute(C.JSP_VIEW_LAYOUT_CONTENT_VIEW_ATTRIBUTE, jspViewBasePath + targetUri + ".jsp");
        } else {
          viewName = jspViewBasePath + targetUri + ".jsp";
        }
        if (reqAction.response != null && reqAction.response.viewName != null) {
          viewName = reqAction.response.viewName;
        }
        req.setAttribute(C.JSP_VIEW_NAME_ATTRIBUTE, viewName);
        //TODO
      } else if (ProcessType.ENTITY_AUTO.value().equals(reqAction.processType)) {
        try {
          view = viewCache.get("jsp");
          doEntityAutoAction(modelMap,req, resp,targetUri,view,reqAction,webCtx,layout);
          
        } catch (ViewException e) {
          throw new ServletException(e);
        }
        return ;
      } else if (ProcessType.BY_CONFIG.value().equals(reqAction.processType)) {
        if (reqAction.response != null) {
          String latyout = reqAction.response.layout;      
          String tmpViewName = reqAction.response.viewName;
          
          if (latyout != null && !"none".equals(layout)) {
            req.setAttribute(C.JSP_VIEW_NAME_ATTRIBUTE, jspViewBasePath + layout);
            if (tmpViewName == null) {
              tmpViewName = jspViewBasePath + targetUri + ".jsp";
            }
            req.setAttribute(C.JSP_VIEW_LAYOUT_CONTENT_VIEW_ATTRIBUTE, tmpViewName);
          } else {
            if (tmpViewName == null) {
              tmpViewName = jspViewBasePath + targetUri + ".jsp";
            }
            req.setAttribute(C.JSP_VIEW_NAME_ATTRIBUTE, tmpViewName);
          }
        } else {
          req.setAttribute(C.JSP_VIEW_NAME_ATTRIBUTE, jspViewBasePath + targetUri + ".jsp");
        }
        
      }
      try {
        handleResultAndRenderView(modelMap, lastResult, webCtx, view, reqAction,null, req, resp);
      } catch (ViewException e) {
        throw new ServletException(e);
      }
    } else {
      resp.sendError(404, "Page not found.");
    }
  }

  /**
   * 自动根据targetUri路径字符串中是否含有对应实体的名称来进行各种操作和界面渲染
   * <p> targetUri字符串必须以如下如式的字符串结尾： /entityName/verb
   * <p> entityName为实体的名称，命名规则为骆驼式命名法
   * <p> verb为操作的名称 目前支持  list,index,home,create,add,save,new,update,modify,edit,view,detail
   * <p>
   * 实体名称的截取规则: 以/号分隔，倒数第二个为期待的实体名称，得到该值后，
   * 尝试加载对应的实体类(应用配置entity.scanning.packages属性 + 实体名称),如果能加载
   * 则进行下面的操作和界面渲染,否则抛出异常
   * <ul>
   *  <li>路径以  /list,/index,/home 结尾,进行实体查询操作和列表界面的渲染</li>
   *  <li>路径以 /create,/add 结尾,不进行任何操作，直接渲染新增界面的渲染</li>
   *  <li>路径以 /save,/new结尾,进行实体的保存操作，成功后跳转至实体详细信息界面,失败则返回到新增界面</li>
   *  <li>路径以 /update,/modify结尾,进行实体的更新操作，成功后跳转至实体的详细信息界面，失败则返回到新界面</li>
   *  <li>路径以 /edit 结尾,进行根据实体ID进行的查找操作，直接渲染编辑界面</li>
   *  <li>路径以 /view,/detail结尾,进行根据实体ID进行的查找操作，直接渲染实体的详细信息界面</li>
   * </ul>
   * @param modelMap           界面<code>{@link View}</code>中要用到的值映射
   * @param req                当前请求对象
   * @param resp               当前响应对象
   * @param targetUri          目标路径Uri 
   * @param view               当前界面对象
   * @param reqAction          当前Action
   * @param webCtx             当前请求上下文
   * @param layout             布局
   * @throws ViewException
   */
  private void doEntityAutoAction(Map<String,Object> modelMap,
                                  HttpServletRequest req,
                                  HttpServletResponse resp,
                                  String targetUri, 
                                  View view,
                                  Action reqAction,
                                  Map<String,Object> webCtx,
                                  String layout) throws ViewException {
    String[] spiltUri = targetUri.split("/");
    if (spiltUri.length < 2) {
      throw new IllegalArgumentException("The string array length of targeturi splited by '/' must be greater and equals to 2.");
    }
    String entityNameInUri = spiltUri[spiltUri.length - 2];
    String guessEntityName = entityNameInUri.substring(0, 1).toUpperCase() + entityNameInUri.substring(1);
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
    sm.engineName = ServiceEngineType.ENTITY_AUTO.value();
    sm.entityName = entityClazzName;
    
    
    String viewName = "";
    if (targetUri.endsWith("/list") || targetUri.endsWith("/index")) {
      sm.name = sm.engineName + "#" + C.SERVICE_ENGITYAUTO_FINDPAGEBYCOND;
      sm.invoke = C.SERVICE_ENGITYAUTO_FINDPAGEBYCOND;
      viewName = jspViewBasePath + "/" +entityNameInUri + "/list.jsp";
    } else if (targetUri.endsWith("/create") || targetUri.endsWith("/add")) {
      viewName = jspViewBasePath + "/" + entityNameInUri + "/form.jsp";
    } else if (targetUri.endsWith("/save") || targetUri.endsWith("/new")) {
      sm.name = sm.engineName + "#" + C.SERVICE_ENGITYAUTO_CREATE;
      sm.invoke = C.SERVICE_ENGITYAUTO_CREATE;
      viewName = jspViewBasePath + "/" +entityNameInUri + "/view.jsp"; 
      view = viewCache.get("redirect");
    } else if (targetUri.endsWith("/update") || targetUri.endsWith("/modify")) {
      sm.name = sm.engineName + "#" + C.SERVICE_ENGITYAUTO_UPDATE;
      sm.invoke = C.SERVICE_ENGITYAUTO_UPDATE;
      viewName = jspViewBasePath + "/" +entityNameInUri + "/view.jsp";
      view = viewCache.get("redirect");
    } else if (targetUri.endsWith("/view") || targetUri.endsWith("/detail")) {
      sm.name = sm.engineName + "#" + C.SERVICE_ENGITYAUTO_FINDBYID;
      sm.invoke = C.SERVICE_ENGITYAUTO_FINDBYID;
      viewName = jspViewBasePath + "/" +entityNameInUri + "/view.jsp";
    } else if (targetUri.endsWith("/edit")) {
      sm.name = sm.engineName + "#" + C.SERVICE_ENGITYAUTO_FINDBYID;
      sm.invoke = C.SERVICE_ENGITYAUTO_FINDBYID;
      viewName = jspViewBasePath + "/" + entityNameInUri + "/form.jsp";
    }
    req.setAttribute(C.JSP_VIEW_NAME_ATTRIBUTE, viewName);
    //TODO 验证输入参数
    serviceDispatcher.registerService(sm);
    if (sm.invoke != null) {
      result = serviceDispatcher.runSync(sm.name, webCtx);
    }
    
    handleResultAndRenderView(modelMap, result, webCtx, view, reqAction,entityNameInUri, req, resp);
  }

  private void handleResultAndRenderView(Map<String,Object> modelMap,Map<String,Object> result,
                                                        Map<String,Object> webCtx,
                                                        View view,Action reqAction,
                                                        String entityNameInUri,
                                                        HttpServletRequest req,
                                                        HttpServletResponse resp) throws ViewException {
    if (result == null) {
      view.render(modelMap, req, resp);
      return ;
    }
    
    String viewName = (String) req.getAttribute(C.JSP_VIEW_NAME_ATTRIBUTE);
    if (ServiceUtil.isSuccess(result)) {      
      boolean overrideRedirectFlag = false;
      if (reqAction.response != null && "redirect".equals(reqAction.response.viewType)) {
        viewName = reqAction.response.viewName;
        if (CommUtil.isNotEmpty(viewName)) {
          int sIdx = viewName.indexOf("?");
          if (sIdx >= 0) {
            String newParamString = WebAppManager.parseQueryString(viewName.substring(sIdx + 1), req, result);
            viewName = viewName.substring(0, sIdx) + uriSuffix + "?" + newParamString;
          }
        }
        overrideRedirectFlag = true;
      }
      
      modelMap.putAll(result);
      
      if (view != null && view instanceof RedirectView) {
        if (overrideRedirectFlag) {
          req.setAttribute("redirectUrl", req.getContextPath() + viewName);
          view.render(modelMap, req, resp);
        } else {
          if (ProcessType.ENTITY_AUTO.value().equals(reqAction.processType)) {
            Object idValue;
            try {
              idValue = Ognl.getValue("id", result.get(C.ENTITY_MODEL_NAME));
            } catch (OgnlException e) {
              throw new ViewException("Unable to get id");
            }
            req.setAttribute("redirectUrl", req.getContextPath() + "/" + entityNameInUri + "/view" + uriSuffix + "?id=" + idValue);
          } else {
            req.setAttribute("redirectUrl",viewName);
          }
          view.render(modelMap, req, resp);
        }
      } else {
        String layout = reqAction.response == null ? "" : reqAction.response.layout;
        req.setAttribute(C.JSP_VIEW_NAME_ATTRIBUTE, jspViewBasePath + layout);
        req.setAttribute(C.JSP_VIEW_LAYOUT_CONTENT_VIEW_ATTRIBUTE, viewName);
        Log.d("layoutContentView : " + viewName, TAG);
        view.render(modelMap, req, resp);
        //clear temp object in session.
        req.getSession().removeAttribute("flashMap");
        req.getSession().removeAttribute("validationErrors");
        req.getSession().removeAttribute("errorMessage");
        req.getSession().removeAttribute("error");
      }
    } else {
      if ("post".equalsIgnoreCase(req.getMethod())) {
        //add temp object in session for next uri can get it.
        req.getSession().setAttribute("flashMap", webCtx);
        req.getSession().setAttribute("validationErrors", result.get(C.RESPOND_VALIDATION_ERRORS));
        req.getSession().setAttribute("errorMessage", result.get(ServiceUtil.RESPONSE_MESSAGE));
        req.getSession().setAttribute("error", result.get(ServiceUtil.RESPOND_ERROR));
        //redirect to last uri
        String referer = req.getHeader("referer");
        String queryString = req.getQueryString();
        String encodedRedirectURL = resp.encodeRedirectURL(queryString == null ? referer : referer + "?" + queryString);
        resp.setStatus(303);
        resp.setHeader("Location", encodedRedirectURL);
      } else {
        view.render(modelMap, req, resp);
      }
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

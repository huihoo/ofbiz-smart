package org.huihoo.ofbiz.smart.webapp.handler;


import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.huihoo.ofbiz.smart.base.C;
import org.huihoo.ofbiz.smart.base.util.AntPathMatcher;
import org.huihoo.ofbiz.smart.base.util.CommUtil;
import org.huihoo.ofbiz.smart.base.util.Log;
import org.huihoo.ofbiz.smart.base.util.PathMatcher;
import org.huihoo.ofbiz.smart.base.util.ServiceUtil;
import org.huihoo.ofbiz.smart.entity.Delegator;
import org.huihoo.ofbiz.smart.service.ServiceDispatcher;
import org.huihoo.ofbiz.smart.webapp.ActionModel;
import org.huihoo.ofbiz.smart.webapp.ActionModel.Action;
import org.huihoo.ofbiz.smart.webapp.ActionModel.ServiceCall;
import org.huihoo.ofbiz.smart.webapp.view.JsonView;
import org.huihoo.ofbiz.smart.webapp.view.JspView;
import org.huihoo.ofbiz.smart.webapp.view.RedirectView;
import org.huihoo.ofbiz.smart.webapp.view.View;
import org.huihoo.ofbiz.smart.webapp.view.ViewException;
import org.huihoo.ofbiz.smart.webapp.view.XmlView;

public class DefaultRequestHandler implements RequestHandler {
  private final static String TAG = DefaultRequestHandler.class.getName();
  
  @SuppressWarnings("unchecked")
  @Override
  public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    ServletContext sc = req.getServletContext();
    Delegator delegator = (Delegator) sc.getAttribute(C.CTX_DELETAGOR);
    ServiceDispatcher serviceDispatcher = (ServiceDispatcher) sc.getAttribute(C.CTX_SERVICE_DISPATCHER);
    List<ActionModel> actionModels = (List<ActionModel>) sc.getAttribute(C.CTX_ACTION_MODEL);
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
      
      
      
      Map<String,Object> modelMap = new LinkedHashMap<>();
      Map<String,Object> ctx = CommUtil.toMap(C.CTX_DELETAGOR,delegator,C.CTX_SERVICE_DISPATCHER,serviceDispatcher);
      
      //TODO request convert to map
      
      List<ServiceCall> serviceCalls = reqAction.serviceCallList;
      if (CommUtil.isNotEmpty(serviceCalls)) {
        for (ServiceCall serviceCall: serviceCalls) {
          Map<String,Object> sResult = serviceDispatcher.runSync(serviceCall.serviceName, ctx);
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
      
      View view = null;
      
      switch (viewType) {
        case "jsp":
          view = new JspView();
          break;
        case "json":
          view = new JsonView();
          break;
        case "xml":
          view = new XmlView();
          break;
        case "redirect":
          view = new RedirectView();
          break;
        case "auto":
          view = new JspView();
          break;
        default:
          view = new JspView();
          break;
      }
      
      if ("uriAuto".equals(reqAction.processType)) {
        String viewName = null;
        if (layout != null) {
          viewName = jspViewBasePath + layout;
          req.setAttribute("layoutContentView", jspViewBasePath + targetUri + ".jsp");
        } else {
          viewName = jspViewBasePath + targetUri + ".jsp";
        }
        req.setAttribute("viewName", viewName);
      } else if ("entityAuto".equals(reqAction.processType)) {
        req.setAttribute("viewName", jspViewBasePath + targetUri + ".jsp");
      } else if ("byConfig".equals(reqAction.processType)) {
        req.setAttribute("viewName", jspViewBasePath + targetUri + ".jsp");
      }
      
      try {
        view.render(modelMap, req, resp);
      } catch (ViewException e) {
        e.printStackTrace();
      }
    }
  }
  
  
  private Action matchAction(List<ActionModel> actionModels,String uri) {
    PathMatcher pathMatcher = new AntPathMatcher();
    for (ActionModel actionModel : actionModels) {
      for(Action action : actionModel.actionList) {
        if (pathMatcher.match(action.uri, uri)) {
          return action;
        }
      }
    }
    return null;
  }
  
  private void setPageAttributies(HttpServletRequest req,Action reqAction) {
    String ctxPath = req.getContextPath();
    req.setAttribute("pageTitle", reqAction.pageTitle);
    String moreCss = reqAction.moreCss;
    if (CommUtil.isNotEmpty(moreCss)) {
      String[] cssArray = moreCss.split(",");
      StringBuilder sb = new StringBuilder();
      for (String css : cssArray) {
        if (!css.startsWith(ctxPath) && !css.startsWith("http://")) {
          css = ctxPath + css;
        }
        sb.append("<link rel=\"stylesheet\" href=\"" + css+ "\">");
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

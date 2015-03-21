package org.huihoo.ofbiz.smart.webapp.control;


import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.huihoo.ofbiz.smart.base.C;
import org.huihoo.ofbiz.smart.base.utils.CommUtils;
import org.huihoo.ofbiz.smart.base.utils.Debug;
import org.huihoo.ofbiz.smart.base.utils.ServiceUtils;
import org.huihoo.ofbiz.smart.entity.Delegator;
import org.huihoo.ofbiz.smart.entity.GenericEntityException;
import org.huihoo.ofbiz.smart.service.GenericServiceException;
import org.huihoo.ofbiz.smart.service.ModelService;
import org.huihoo.ofbiz.smart.service.ServiceDispatcher;
import org.huihoo.ofbiz.smart.webapp.control.ConfigXMLLoader.Action;
import org.huihoo.ofbiz.smart.webapp.control.ConfigXMLLoader.ActionInterceptor;
import org.huihoo.ofbiz.smart.webapp.control.ConfigXMLLoader.ActionMap;
import org.huihoo.ofbiz.smart.webapp.control.ConfigXMLLoader.Event;
import org.huihoo.ofbiz.smart.webapp.control.ConfigXMLLoader.Response;
import org.huihoo.ofbiz.smart.webapp.view.JspViewHandler;
import org.huihoo.ofbiz.smart.webapp.view.ViewHandler;
import org.huihoo.ofbiz.smart.webapp.view.ViewHandlerException;

public class RequestHandler {

  private static final String module = RequestHandler.class.getName();
  private static final ConcurrentHashMap<String, Class<?>> NORMAL_EVENT_CLASS_MAP = new ConcurrentHashMap<String, Class<?>>();
  private static final ConcurrentHashMap<String, Class<?>> ACTION_INTERCEPTOR_CLASS_MAP = new ConcurrentHashMap<String, Class<?>>();
  private final ViewHandler jspViewHandler = new JspViewHandler();
  private static final String ERROR_JSON = "{\"error\":\"true\",\"message\":\"ERROR\"}";
  private static final String CONTENT_TYPE_JSON = "application/json;charset=utf-8";


  
  @SuppressWarnings("unchecked")
  public void handleRequest(HttpServletRequest req, HttpServletResponse resp, ActionMap actionMap) {
    try {
      long beginTime = System.currentTimeMillis();
      String ctxPath = req.getContextPath();
      String target = req.getRequestURI();
      target = target.substring(ctxPath.length(), target.length());
      String reqMethod = req.getMethod();
      req.setAttribute("uri", req.getRequestURI());

      Action currentAction = null;
      AntPathRequestMatcher antPathRequestMatcher = null;

      for (Action action : actionMap.actions) {
        String uriPattern = action.uri;
        if (CommUtils.isNotEmpty(actionMap.actionUriSuffix)) {
          if (!uriPattern.endsWith("*")) uriPattern = uriPattern + actionMap.actionUriSuffix;
        }

        antPathRequestMatcher = new AntPathRequestMatcher(uriPattern);
        if (antPathRequestMatcher.matches(req)) {
          currentAction = action;
          break;
        }
      }

      if (currentAction == null) {
        Debug.logError("请求[" + target + "]未找到对应的Action", module);
        resp.sendError(404, target + " NOT FOUND");
        return;
      }

      String allowMethod = currentAction.allowMethod;
      if (CommUtils.isNotEmpty(allowMethod) && !C.ALLOW_ALL.equals(allowMethod)
              && !reqMethod.equalsIgnoreCase(allowMethod)) {
        Debug.logWaring("不支持的请求方法 " + reqMethod + " " + allowMethod, module);
        resp.sendError(400, "NOT SUPPORT METHOD");
        return;
      }


      Debug.logDebug("当前Action:" + currentAction, module);
      Action cloneCurrentAction = (Action) currentAction.clone();

      String pt = cloneCurrentAction.pageTitle;
      req.setAttribute("pageTitle", (pt == null ? "" : pt) + " " + actionMap.appName);


      ServiceDispatcher serviceDispatcher =
              (ServiceDispatcher) req.getAttribute(C.CTX_SERVICE_DISPATCHER);
      Event event = cloneCurrentAction.event;

      if (event != null) {
        String eventType = event.type;
        Map<String, Object> returnResult = null;
        
        ActionInterceptorInterface aii = null;
        ActionInterceptor interceptor = cloneCurrentAction.actionInterceptor;
        if(interceptor != null){
          ClassLoader loader = getClass().getClassLoader();
          Class<?> c = ACTION_INTERCEPTOR_CLASS_MAP.get(interceptor.interceptorName);
          if (C.ENV_MODE_DEVELOP.equals(actionMap.envMode))
            c = null;
          if (c == null) {
            c = loader.loadClass(interceptor.interceptorName);
            ACTION_INTERCEPTOR_CLASS_MAP.put(interceptor.interceptorName, c);
          }
          aii = (ActionInterceptorInterface) c.newInstance();
        }
        
        if(aii != null && interceptor != null && 
                ("all".equals(interceptor.triggerAt) || "before".equals(interceptor.triggerAt ))){
          aii.before(req, resp);
        }
        
        if (C.EVENT_JAVA.equalsIgnoreCase(eventType)) {
          callNormalJavaEvent(actionMap, event, req, resp);
        } else if (C.EVENT_SERVICE.equalsIgnoreCase(eventType)) {
          Map<String, Object> webContext = buildWebContext(req, resp, actionMap);
          if(cloneCurrentAction.sea != null){
            ModelService modelService = serviceDispatcher.getLocalContext(event.sname);
            modelService.sea = cloneCurrentAction.sea;
          }
          returnResult = serviceDispatcher.runSync(event.sname, webContext);
          doResponse(req, resp, returnResult, actionMap, cloneCurrentAction);
        } else if (C.EVENT_SIMPLE.equalsIgnoreCase(eventType)) {
          Map<String, Object> webContext = buildWebContext(req, resp, actionMap);
          ModelService modelService = new ModelService();
          modelService.name = event.type + event.invoke;
          modelService.engineName = C.EVENT_SIMPLE;
          modelService.location = serviceDispatcher.getLocationOfGenericEngine(C.EVENT_SIMPLE);
          modelService.invoke = event.invoke;
          modelService.defaultEntityName = event.defaultEntityName;
          
          if(cloneCurrentAction.sea != null){
            modelService.sea = cloneCurrentAction.sea;
          }
          
          serviceDispatcher.putModelService(modelService);
          // 条件转换
          if (CommUtils.isNotEmpty(event.condition)) {
            webContext.put("condition", convertCondition(event.condition, req));
          }
          returnResult = serviceDispatcher.runSync(modelService.name, webContext);

          // 如果指定调用的服务，还可以调用服务
          if (CommUtils.isNotEmpty(event.sname) && returnResult != null) {
            if (ServiceUtils.isSuccess(returnResult)) {
              webContext.putAll(returnResult);// 将上一次执行的结果放到上下文中
              Object otherResult = serviceDispatcher.runSync(event.sname, webContext);
              Map<String, Object> otherMap = (Map<String, Object>) otherResult;
              if (ServiceUtils.isSuccess(otherMap)) {
                returnResult.putAll(otherMap);
              }
            }
          }
        } else if (C.EVENT_AUTO.equalsIgnoreCase(eventType)) {
          returnResult = doAutoAction(req, resp, cloneCurrentAction, actionMap, serviceDispatcher);
          doResponse(req, resp, returnResult, actionMap, cloneCurrentAction);
        } else if (C.EVENT_NONE.equalsIgnoreCase(eventType)) {
          // TODO
        } else if (C.EVENT_API.equalsIgnoreCase(eventType)) {
          returnResult = doApiAction(req, resp, actionMap, serviceDispatcher);
          doResponse(req, resp, returnResult, actionMap, cloneCurrentAction);
        } else {
          Debug.logWaring("不支持的事件处理类型[" + eventType + "]", module);
        }
        
        if(aii != null && interceptor != null && 
                ("all".equals(interceptor.triggerAt) || "success".equals(interceptor.triggerAt ))){
          aii.success(req, resp, returnResult);
        }
      }

      
      
      Debug.logInfo("请求[" + target + "]耗时:" + (System.currentTimeMillis() - beginTime) + " ms.",module);

    } catch (IOException e) {
      Debug.logError(e, "IO异常", module);
      try {
        resp.sendError(500, "IO Exception");
      } catch (IOException e1) {
        
      }
    } catch (ViewHandlerException e) {
      Debug.logError(e, "渲染视图发生异常", module);
      try {
        resp.sendError(500, "View Render Exception");
      } catch (IOException e1) {
        
      }
    } catch (Exception e) {
      Debug.logError(e, "请求发生异常", module);
      try {
        resp.sendError(500, "View Render Exception");
      } catch (IOException e1) {
        
      }
    }
  }

  private void doResponse(HttpServletRequest req, HttpServletResponse resp,
          Map<String, Object> resultMap, ActionMap actionMap, Action action) throws IOException,
          ViewHandlerException {
    String ajaxHeader = req.getHeader("x-requested-with");
    boolean isAjaxReq = CommUtils.isNotEmpty(ajaxHeader);
    Response eventResp = action.response;

    String responseType = eventResp.type;

    if (resultMap != null) {
      // 对于重定向，需要在Session中保存成功或是失败的提示信息，以便重定向后的页面，能根据Session中的信息来提示信息给用户
      // 注意：重定向过后的界面要清理掉这些提示信息
      if (C.RESP_REDIRECT.equalsIgnoreCase(responseType) && !isAjaxReq) {
        if (ServiceUtils.isSuccess(resultMap) && resultMap.containsKey(C.TIP_FLASH_SUCCESS)) {
          req.getSession().setAttribute(C.TIP_FLASH_SUCCESS, resultMap.get(C.TIP_FLASH_SUCCESS));
        } else {
          if (resultMap.containsKey(C.TIP_FLASH_ERROR))
            req.getSession().setAttribute(C.TIP_FLASH_ERROR,
                    resultMap.get(ServiceUtils.RESPONSE_MESSAGE));
        }
      }
    }


    if (isAjaxReq) {
      jsonResponse(req, resp, resultMap); // Ajax请求直接返回JSON
    } else {
      // 2. 生成Response
      if (C.RESP_INCLUDE_VIEW.equalsIgnoreCase(responseType)) {
        includeView(req, resp, resultMap, eventResp, actionMap);
      } else if (C.RESP_REDIRECT.equalsIgnoreCase(responseType)) {
        redirect(req, resp, resultMap, eventResp, actionMap);
      } else if (C.RESP_JSON.equalsIgnoreCase(responseType)) {
        jsonResponse(req, resp, resultMap);
      } else if (C.RESP_HTML.equals(responseType)) {
        htmlResponse(req, resp, resultMap, eventResp);
      } else if (C.RESP_NONE.equals(responseType)) {
        // NONE
      }
    }
  }


  private Map<String, Object> buildWebContext(HttpServletRequest req, HttpServletResponse resp,
          ActionMap actionMap) throws Exception {
    Map<String, Object> webContext = putAllRequestParamsToContext(req, resp, actionMap);
    // 注意：WEB环境下要把容器对象实例request和response设置至服务执行上下文中(因为有的服务要依赖这两个对象)
    webContext.put(C.IS_WEB_CONTEXT, true);
    webContext.put(C.CTX_REQUEST, req);
    webContext.put(C.CTX_RESPONSE, resp);
    return webContext;
  }

  private Map<String, Object> doApiAction(HttpServletRequest req, HttpServletResponse resp,
          ActionMap actionMap, ServiceDispatcher serviceDispatcher) throws Exception {
    // TODO APP配置加强，以后可以存在数据库里
    Map<String, String> keySecretMap = new HashMap<>();
    Properties prop = (Properties) req.getAttribute(C.CTX_APP_CONFIG_PROP);
    String configKeyAndSecrets = prop.getProperty("api.keys_and_secrets");
    if (CommUtils.isEmpty(configKeyAndSecrets)) {
      Debug.logWaring("配置文件缺失名为 api.keys_and_secrets 针对API请求Key和Secret的设置", module);
    } else {
      String[] keySecretTokens = configKeyAndSecrets.split(",");
      for (String kst : keySecretTokens) {
        String[] keySecretPair = kst.split("@");
        keySecretMap.put(keySecretPair[0], keySecretPair[1]);
      }
    }

    String appKey = req.getParameter("appKey");
    String method = req.getParameter("method");
    String timestamp = req.getParameter("timestamp");
    String sign = req.getParameter("sign");

    if (CommUtils.isEmpty(appKey)) {
      return ServiceUtils.returnProplem("SYSTEM_PARAM_REQUIRED", "缺失appKey参数或值为空");
    }

    if (CommUtils.isEmpty(method)) {
      return ServiceUtils.returnProplem("SYSTEM_PARAM_REQUIRED", "缺失method参数或值为空");
    }

    if (CommUtils.isEmpty(timestamp)) {
      return ServiceUtils.returnProplem("SYSTEM_PARAM_REQUIRED", "缺失timestamp参数或值为空");
    }

    if (CommUtils.isEmpty(sign)) {
      return ServiceUtils.returnProplem("SYSTEM_PARAM_REQUIRED", "缺失sign参数或值为空");
    }

    // 除去几个系统级参数后，对其它参数按字典排序，构造参数-值对
    // 构造格式为 a=2&b=3&c=4&timestamp=1425736060372
    // 然后再加上app_secret
    // 比如app_secret为secret123,最终的字符串为 a=2&b=3&c=4&timestamp=1425736060372secret123
    Map<String, String> bizTreeMap = new TreeMap<String, String>();
    Enumeration<String> paraNames = req.getParameterNames();
    while (paraNames.hasMoreElements()) {
      String pName = paraNames.nextElement();
      // 过滤掉几个系统级参数
      if (pName.equals("appKey") || pName.equals("appSecret") || pName.equals("method")
              || pName.equals("sign")) {
        continue;
      }

      String pValue = URLDecoder.decode(req.getParameter(pName), "UTF-8");
      bizTreeMap.put(pName, pValue);
    }

    StringBuilder sb = new StringBuilder();
    Set<Entry<String, String>> set = bizTreeMap.entrySet();
    Iterator<Entry<String, String>> setIter = set.iterator();
    while (setIter.hasNext()) {
      Entry<String, String> entry = setIter.next();
      sb.append(entry.getKey());
      sb.append("=");
      sb.append(entry.getValue());
      sb.append("&");
    }
    String paramStr = sb.substring(0, sb.length() - 1);
    Debug.logDebug("paramStr->" + paramStr, module);

    String appSecret = keySecretMap.get(appKey);
    if (CommUtils.isEmpty(appSecret)) {
      Debug.logError("不存的应用appKey[" + appKey + "]", module);
      return ServiceUtils.returnProplem("APP_KEY_NOT_EXIST", "不存的应用appKey");
    }


    String md5Signed = CommUtils.md5(paramStr + appSecret);

    Debug.logDebug("sign->" + sign + " md5Signed->" + md5Signed, module);


    if (md5Signed.equals(sign)) {
      Map<String, Object> webContext = buildWebContext(req, resp, actionMap);
      webContext.put(C.IS_EXPORT_API, true);
      return serviceDispatcher.runSync(method, webContext);
    } else {
      return ServiceUtils.returnProplem("SIGN_ILLEGAL", "签名不正确");
    }
  }


  @SuppressWarnings("unchecked")
  private Map<String, Object> doAutoAction(HttpServletRequest req, HttpServletResponse resp,
          Action action, ActionMap actionMap, ServiceDispatcher serviceDispatcher) throws Exception {
    String target = req.getRequestURI();
    String ctxPath = req.getContextPath();
    String moduleName = target.substring(ctxPath.length(), target.lastIndexOf("/"));
    if (moduleName.startsWith("/")) moduleName = moduleName.substring(1);
    action.event.type = C.EVENT_SIMPLE;

    // 首页或列表页
    if (target.indexOf("/index") != -1 || target.indexOf("/list") != -1) {
      action.event.invoke = "findPageByCondition";
      action.response.type = C.RESP_INCLUDE_VIEW;
      action.response.layout = "/layout.jsp";
      action.response.value = "/" + moduleName + "/list.jsp";
    }
    // 新增页面
    else if (target.indexOf("/create") != -1) {
      action.event.invoke = "findById";
      action.response.type = C.RESP_INCLUDE_VIEW;
      action.response.layout = "/layout.jsp";
      action.response.value = "/" + moduleName + "/form.jsp";
    }
    // 新增保存
    else if (target.indexOf("/save") != -1) {
      action.event.invoke = "create";
      action.response.type = C.RESP_REDIRECT;
      action.response.value = "/" + moduleName + "/list";
    }
    // 更新保存
    else if (target.indexOf("/update") != -1) {
      action.event.invoke = "update";
      action.response.type = C.RESP_REDIRECT;
      action.response.value = "/" + moduleName + "/list";
    }
    // 查看或编辑
    else if (target.indexOf("/view") != -1 || target.indexOf("/edit") != -1) {
      action.event.invoke = "findById";
      action.response.type = C.RESP_INCLUDE_VIEW;
      action.response.layout = "/layout.jsp";
      action.response.value = "/" + moduleName + "/form.jsp";
    }

    Map<String, Object> webContext = buildWebContext(req, resp, actionMap);



    ModelService modelService = new ModelService();
    modelService.name = action.event.type + action.event.invoke;
    modelService.engineName = C.EVENT_SIMPLE;
    modelService.location = serviceDispatcher.getLocationOfGenericEngine(C.EVENT_SIMPLE);
    modelService.invoke = action.event.invoke;
    modelService.defaultEntityName = action.event.defaultEntityName;
    if(action.sea != null){
      modelService.sea = action.sea;
    }
    
    serviceDispatcher.putModelService(modelService);

    // 条件转换
    if (CommUtils.isNotEmpty(action.event.condition)) {
      webContext.put("condition", convertCondition(action.event.condition, req));
    }

    Map<String, Object> resultMap = serviceDispatcher.runSync(modelService.name, webContext);

    // 如果指定调用的服务，还可以调用服务
    if (CommUtils.isNotEmpty(action.event.sname) && resultMap != null) {
      if (ServiceUtils.isSuccess(resultMap)) {
        webContext.putAll(resultMap);// 将上一次执行的结果放到上下文中

        Object otherResult = serviceDispatcher.runSync(action.event.sname, webContext);
        Map<String, Object> otherMap = (Map<String, Object>) otherResult;
        if (ServiceUtils.isSuccess(otherMap)) {
          resultMap.putAll(otherMap);
        }
      }
    }

    // 成功调用后，删除之
    serviceDispatcher.removeModelService(modelService.name);

    return resultMap;
  }

  /**
   * <p>
   * 将Event中配置的condition在Web环境中进行转换。<br/>
   * 如果condition中指定了Web环境中的变量，需要<br/>
   * 在当前环境中去取。
   * </p>
   * 
   * @param condition
   * @param req
   * @return
   */
  private String convertCondition(String condition, HttpServletRequest req) {
    StringBuffer convertedSb = new StringBuffer();
    if (CommUtils.isNotEmpty(condition)) {
      String[] cTokens = condition.split(",");
      for (int i = 0; i < cTokens.length; i++) {
        String[] cPair = cTokens[i].split("@");
        if (cPair.length == 3) {
          String fieldName = cPair[0];
          String operator = cPair[1];
          String condValue = cPair[2];
          Object realCondValue = null;
          if (condValue.startsWith("sessionScope.")) {
            String fName = condValue.substring("sessionScope.".length());
            realCondValue = req.getSession().getAttribute(fName);
          } else if (condValue.startsWith("requestScope.")) {
            String fName = condValue.substring("sessionScope.".length());
            realCondValue = req.getAttribute(fName);
            if (realCondValue == null) realCondValue = req.getParameter(fName);
          } else {
            realCondValue = condValue;
          }
          if (CommUtils.isNotEmpty(realCondValue)) {
            convertedSb.append(fieldName).append("@").append(operator).append("@")
                    .append(realCondValue).append(",");
          }
        }
      }
    }

    Enumeration<String> pNames = req.getParameterNames();
    while (pNames.hasMoreElements()) {
      String pName = pNames.nextElement();
      // s_代表单个输入 m_代表选择多项
      if (pName.startsWith("m_") || pName.startsWith("s_")) {
        String reqCondtion = pName.substring(2);
        int sIdx = reqCondtion.indexOf("_");
        String condOperator = reqCondtion.substring(0, sIdx);
        String condName = reqCondtion.substring(sIdx + 1);
        Object realCondValue = null;
        if (pName.startsWith("s_"))
          realCondValue = req.getParameter(pName);
        else if (pName.startsWith("m_")) {
          String[] tValues = req.getParameterValues(pName);
          if (CommUtils.isNotEmpty(tValues)) {
            StringBuffer tSb = new StringBuffer();
            for (String s : tValues) {
              tSb.append(s).append("#");
            }
            realCondValue = tSb.substring(0, tSb.length() - 1);
          }
        }
        if (CommUtils.isNotEmpty(realCondValue)) {
          convertedSb.append(condName).append("@").append(condOperator).append("@")
                  .append(realCondValue).append(",");
          // 将带有.号的参数名替换成_，页面上要取
          pName = pName.replaceAll("\\.", "_");
          req.setAttribute(pName, realCondValue);
        }
      }
    }
    String cstr = convertedSb.toString();
    if (cstr.toString().endsWith(",")) cstr = cstr.substring(0, cstr.length() - 1);

    Debug.logDebug("原条件:%s 转换后:%s", module, condition, cstr);

    return convertedSb.toString();
  }

  /**
   * <p>
   * 服务器内部跳转包含界面
   * </p>
   * 
   * @param req
   * @param resp
   * @param returnResult 普通Java类调用或服务调用的返回值
   * @param eventResp
   * @param actionMap
   * @throws ViewHandlerException
   */
  private void includeView(HttpServletRequest req, HttpServletResponse resp,
          Map<String, Object> resultMap, Response eventResp, ActionMap actionMap)
          throws ViewHandlerException {
    req.setAttribute("reqUri", req.getRequestURI());
    req.setAttribute("jspViewBasePath", actionMap.jspViewBasePath);
    if (resultMap != null) {
      Set<Entry<String, Object>> entrySet = resultMap.entrySet();
      Iterator<Entry<String, Object>> iter = entrySet.iterator();
      while (iter.hasNext()) {
        Entry<String, Object> entry = iter.next();
        String key = entry.getKey();
        req.setAttribute(key, entry.getValue());
      }
    }
    jspViewHandler.render(eventResp.value, eventResp.layout, req, resp);
  }


  private void redirect(HttpServletRequest req, HttpServletResponse resp,
          Map<String, Object> resultMap, Response eventResp, ActionMap actionMap)
          throws IOException {
    resp.sendRedirect(req.getContextPath() + eventResp.value + actionMap.actionUriSuffix);
  }

  @SuppressWarnings("unchecked")
  private void jsonResponse(HttpServletRequest req, HttpServletResponse resp, Object returnResult)
          throws IOException {
    resp.setContentType(CONTENT_TYPE_JSON);
    if (returnResult != null) {
      Map<String, Object> resultMap = (Map<String, Object>) returnResult;
      Delegator delegator = (Delegator) req.getAttribute(C.CTX_DELEGATOR);
      try {
        resp.getWriter().print(delegator.toJson(resultMap));
      } catch (GenericEntityException e) {
        Debug.logError(e, "解析返回结果错误", module);
        resp.getWriter().print(ERROR_JSON);
      }
    } else {
      resp.getWriter().print(ERROR_JSON);
    }
  }

  private void htmlResponse(HttpServletRequest req, HttpServletResponse resp,
          Map<String, Object> resultMap, Response eventResp) throws IOException,
          ViewHandlerException {
    req.setAttribute("result", resultMap);
    jspViewHandler.render(eventResp.value, eventResp.layout, req, resp);
  }


  private void callNormalJavaEvent(ActionMap actionMap, Event event, HttpServletRequest req,
          HttpServletResponse resp) throws GenericServiceException {
    Map<String, Object> context = null;
    try {
      context = putAllRequestParamsToContext(req, resp, actionMap);
    } catch (Exception e) {
      Debug.logError(e, "封装Request至Context发生异常", module);
      throw new GenericServiceException(e);
    }

    try {
      String clazzName = event.path;
      String method = event.invoke;
      ClassLoader loader = getClass().getClassLoader();
      Class<?> c = NORMAL_EVENT_CLASS_MAP.get(clazzName);
      // 开发模式下，每次重新加载类
      if (C.ENV_MODE_DEVELOP.equals(actionMap.envMode)) c = null;
      if (c == null) {
        c = loader.loadClass(clazzName);
        NORMAL_EVENT_CLASS_MAP.put(clazzName, c);
      }

      Method m =
              c.getMethod(method, HttpServletRequest.class, HttpServletResponse.class, Map.class);

      if (Modifier.isStatic(m.getModifiers())) {
        m.invoke(null, req, resp, context);
      } else {
        m.invoke(c.newInstance(), req, resp, context);
      }

    } catch (Exception e) {
      Debug.logError(e, "调用发生异常", module);
      throw new GenericServiceException(e);
    }
  }


  @SuppressWarnings("unchecked")
  private Map<String, Object> putAllRequestParamsToContext(HttpServletRequest request,
          HttpServletResponse response, ActionMap actionMap) throws Exception {
    Map<String, Object> context = new HashMap<String, Object>();
    if (ServletFileUpload.isMultipartContent(request)) {
      String uploadRelativePath = actionMap.uploadRelativePath;
      String rootPath = request.getServletContext().getRealPath("");
      context.put("rootPath", rootPath);
      File saveFilePath = new File(rootPath + uploadRelativePath);
      if (!saveFilePath.exists()) saveFilePath.mkdirs();

      FileItemFactory factory = new DiskFileItemFactory();
      ServletFileUpload fileUpload = new ServletFileUpload(factory);
      fileUpload.setSizeMax(10 * 1024 * 1024); // XXX 文件上传最大10M

      List<FileItem> fileItems = fileUpload.parseRequest(request);
      Iterator<FileItem> itemsIt = fileItems.iterator();
      while (itemsIt.hasNext()) {
        FileItem item = itemsIt.next();
        if (item.isFormField()) {
          String pName = item.getFieldName();
          String pValue = item.getString("UTF-8");
          context.put(pName, pValue);
          request.setAttribute(pName, pValue);
        } else {
          if (item.getSize() > 0) {
            // TODO 文件上传加强
            String fieldName = item.getFieldName();
            String fileName = item.getName();
            String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
            String finalFileName = UUID.randomUUID().toString().replaceAll("-", "") + fileSuffix;
            String finalSaveRelativePath = uploadRelativePath + "/" + finalFileName;
            File targetFile = new File(rootPath + finalSaveRelativePath);
            item.write(targetFile);
            context.put(fieldName, finalSaveRelativePath);
            context.put(fieldName + "_file", targetFile);
          }
        }
      }
    } else {
      Enumeration<?> pNames = request.getParameterNames();
      while (pNames.hasMoreElements()) {
        String pName = (String) pNames.nextElement();
        if (pName.startsWith("s_chk")) {
          String[] arrayValue = request.getParameterValues(pName);
          context.put(pName, arrayValue);
        } else if (pName.endsWith(".id")) {
          String[] arrayValue = request.getParameterValues(pName);
          if (CommUtils.isNotEmpty(arrayValue)) {
            if (arrayValue.length > 1)
              context.put(pName, arrayValue);
            else
              context.put(pName, arrayValue[0]);
          } else {
            context.put(pName, request.getParameter(pName));
          }
        } else {
          String pValue = request.getParameter(pName);
          context.put(pName, pValue);
          request.setAttribute(pName, pValue);
        }
      }
    }

    Debug.logDebug(CommUtils.printMap(context), module);
    return context;
  }


}

package org.huihoo.ofbiz.smart.webapp;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Properties;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.huihoo.ofbiz.smart.base.C;
import org.huihoo.ofbiz.smart.base.cache.Cache;
import org.huihoo.ofbiz.smart.base.cache.SimpleCacheManager;
import org.huihoo.ofbiz.smart.base.location.FlexibleLocation;
import org.huihoo.ofbiz.smart.base.util.AppConfigUtil;
import org.huihoo.ofbiz.smart.base.util.CommUtil;
import org.huihoo.ofbiz.smart.base.util.Log;
import org.huihoo.ofbiz.smart.base.util.xml.IXmlConverter;
import org.huihoo.ofbiz.smart.entity.Delegator;
import org.huihoo.ofbiz.smart.entity.EbeanDelegator;
import org.huihoo.ofbiz.smart.entity.GenericEntityException;
import org.huihoo.ofbiz.smart.service.GenericServiceException;
import org.huihoo.ofbiz.smart.service.ServiceDispatcher;
import org.huihoo.ofbiz.smart.session.ExpiringSession;
import org.huihoo.ofbiz.smart.session.MapSessionRepository;
import org.huihoo.ofbiz.smart.session.SessionRepository;
import org.huihoo.ofbiz.smart.webapp.handler.ApiDocRequestHandler;
import org.huihoo.ofbiz.smart.webapp.handler.DefaultRequestHandler;
import org.huihoo.ofbiz.smart.webapp.handler.HttpApiRequestHandler;
import org.huihoo.ofbiz.smart.webapp.handler.RequestHandler;
import org.huihoo.ofbiz.smart.webapp.handler.RestfulRequestHandler;
import org.huihoo.ofbiz.smart.webapp.view.View;


public class DispatchServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;

  private static final String TAG = DispatchServlet.class.getName();

  private volatile Cache<String, RequestHandler> handlerCache;

  @SuppressWarnings("unchecked")
  private final static Cache<String,View> VIEW_CACHE = 
                            (Cache<String,View>) SimpleCacheManager.createCache("Request-View-Cache");
  
  private volatile boolean useSmartSession;
  /** JSP界面所在跟目录 */
  private volatile String jspViewBathPath;
  /** 请求uri后缀 */
  private volatile String uriSuffix;
  /** http api 类型的请求uri根 */
  private volatile String httpApiUrlBase;
  /** rest api 类型的请求uri根 */
  private volatile String restApiUrlBase;
  /** api 文档请求uri根 */
  private volatile String apiDocUriBase;
  
  private static final String[] BUILTIN_VIEWS = {
                     "json#org.huihoo.ofbiz.smart.webapp.view.JsonView",
                     "jsp#org.huihoo.ofbiz.smart.webapp.view.JspView",
                     "redirect#org.huihoo.ofbiz.smart.webapp.view.RedirectView",
                     "xml#org.huihoo.ofbiz.smart.webapp.view.XmlView",
                     "doc#org.huihoo.ofbiz.smart.webapp.view.HttpApiDocView",
                     "captcha#org.huihoo.ofbiz.smart.webapp.view.CaptchaView",
  };
  
  private static final String XML_HANDLE="org.huihoo.ofbiz.smart.base.util.xml.impl.SmartXmlConverter";

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {
    processRequest(req, resp);
  }

  @Override
  public void doPost(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {
    processRequest(req, resp);
  }

  protected final void processRequest(HttpServletRequest request, HttpServletResponse response)
          throws ServletException, IOException {
    String targetUri = request.getRequestURI().substring(request.getContextPath().length());
    long startTime = System.currentTimeMillis();
    Throwable failureCause = null;
    try {
      Properties applicationConfig = (Properties) getServletContext().getAttribute(C.APPLICATION_CONFIG_PROP_KEY);
      if (!C.PROFILE_PRODUCTION.equals(applicationConfig.getProperty(C.PROFILE_NAME))) {
        //非生产环境，总是重新加载
        try {
          loadAppConfig(getServletContext());
        } catch (GenericServiceException e) {
          Log.w("Unable to load app config : " + e.getMessage(), TAG);
        }
      }
      RequestHandler requestHandler = null;
      if (targetUri.startsWith(restApiUrlBase)) {
        requestHandler = handlerCache.get("Rest");
        if (requestHandler == null) {
          requestHandler = new RestfulRequestHandler();
          handlerCache.put("Rest", requestHandler);
        }
      } else if (targetUri.startsWith(httpApiUrlBase)) {
        requestHandler = handlerCache.get("Api");
        if (requestHandler == null) {
          requestHandler = new HttpApiRequestHandler();
          handlerCache.put("Api", requestHandler);
        }
      } else if (targetUri.startsWith(apiDocUriBase)) {
        requestHandler = handlerCache.get("ApiDoc");
        if (requestHandler == null) {
          requestHandler = new ApiDocRequestHandler();
          handlerCache.put("ApiDoc", requestHandler);
        }
      } else {
        requestHandler = handlerCache.get("Default");
        if (requestHandler == null) {
          requestHandler = new DefaultRequestHandler();
          handlerCache.put("Default", requestHandler);
        }
      }
      
      request.setAttribute("uri", targetUri);
      request.setAttribute("uriSuffix", uriSuffix);
      request.setAttribute("ctxPath", request.getContextPath());
      requestHandler.handleRequest(request, response);
    } catch (ServletException ex) {
      failureCause = ex;
      throw ex;
    } catch (IOException ex) {
      failureCause = ex;
      throw ex;
    } finally {
      long processingTime = System.currentTimeMillis() - startTime;
      if (failureCause != null) {
        Log.i("Process request [" + targetUri + "] cost [" + processingTime
                + "] ms. But an error happend : " + failureCause.getMessage(), TAG);
      } else {
        Log.i("Process request [" + targetUri + "] cost [" + processingTime + "] ms", TAG);
      }
    }
  }



  @SuppressWarnings("unchecked")
  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    
    jspViewBathPath = config.getInitParameter("jsp-view-base-path");
    uriSuffix = config.getInitParameter("uri-suffix");
    httpApiUrlBase = config.getInitParameter("http-api-uri-base");
    restApiUrlBase = config.getInitParameter("rest-api-uri-base");
    apiDocUriBase = config.getInitParameter("api-doc-uri-base");
    String tmpUss = config.getInitParameter("use-smart-session");
    useSmartSession = Boolean.valueOf(tmpUss == null ? "false": tmpUss);
    
    if (useSmartSession) {
      SessionRepository<ExpiringSession> sessionRepository =new MapSessionRepository();
      SessionRepositoryFilter<ExpiringSession> filter = new SessionRepositoryFilter<ExpiringSession>(sessionRepository);
      Dynamic fr = config.getServletContext().addFilter("smartSessionFilter", filter);
      fr.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "*" + uriSuffix);
      fr.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "*.jsp");
      Log.i("Use smart session to replace container session.", TAG);
    }
    
    if (CommUtil.isEmpty(jspViewBathPath)) {
      jspViewBathPath = "/WEB-INF/views";
    }
    if (CommUtil.isEmpty(uriSuffix)) {
      uriSuffix = "";
    }
    if (CommUtil.isEmpty(httpApiUrlBase)) {
      httpApiUrlBase = "/api/router";
    }
    if (CommUtil.isEmpty(restApiUrlBase)) {
      httpApiUrlBase = "/rest";
    }

    ServletContext sc = config.getServletContext();
    sc.setAttribute(C.CTX_JSP_VIEW_BASEPATH, jspViewBathPath);
    sc.setAttribute(C.CTX_URI_SUFFIX, uriSuffix);
    
    initWebContext(sc);

    handlerCache =
            (Cache<String, RequestHandler>) SimpleCacheManager.createCache("RequestHandler-Cache");
    
    loadSeedData(sc);
  }

  protected void initWebContext(ServletContext servletContext) throws ServletException {
    try {
      Delegator delegator = new EbeanDelegator();
      servletContext.setAttribute(C.CTX_DELETAGOR, delegator);
      loadAppConfig(servletContext);
    } catch (GenericEntityException e) {
      throw new ServletException("Unable to initialize Delegator.");
    } catch (GenericServiceException e) {
      throw new ServletException("Unable to initialize ServiceDispatcher.");
    }
  }
  
  protected void loadAppConfig(ServletContext servletContext) throws GenericServiceException {
    //NOTICE 以下对象可以重新加载,除了Delegator
    ServiceDispatcher serviceDispatcher = new ServiceDispatcher((Delegator)servletContext.getAttribute(C.CTX_DELETAGOR));
    servletContext.setAttribute(C.CTX_SERVICE_DISPATCHER, serviceDispatcher);
    String actionConfigBasePath = AppConfigUtil.getProperty(C.ACTION_CONFIG_BASEPATH_KEY, "./");
    List<ActionModel> actionModels = new ArrayList<>();
    try {
      ActionModelXmlConfigLoader.me().loadXml(FlexibleLocation.resolveLocation(actionConfigBasePath).getPath(),actionModels);
    } catch (MalformedURLException e1) {
      Log.e(e1, "Unable to load action config file in dir : " + actionConfigBasePath, TAG);
    }
    servletContext.setAttribute(C.CTX_ACTION_MODEL,actionModels);
    
    String[] customSupportedView = null;
    String supportedViews = AppConfigUtil.getProperty("webapp.supported.views");
    if (CommUtil.isNotEmpty(supportedViews)) {
      customSupportedView = supportedViews.split(",");
    }
    
    String[] allSupportedViews = null;
    if (CommUtil.isNotEmpty(customSupportedView)) {
      allSupportedViews = new String[BUILTIN_VIEWS.length + customSupportedView.length];
      System.arraycopy(BUILTIN_VIEWS, 0, allSupportedViews, 0, BUILTIN_VIEWS.length);
      System.arraycopy(customSupportedView, BUILTIN_VIEWS.length - 1, customSupportedView, 0, customSupportedView.length);
    } else {
      allSupportedViews = BUILTIN_VIEWS;
    }
    
    for (String view : allSupportedViews) {
      String[] viewToken = view.split("#");
      String viewType = viewToken[0];
      String viewName = viewToken[1];
      try {
        VIEW_CACHE.put(viewType, (View) Class.forName(viewName).newInstance());
        Log.d("Loaded view [%s] [%s]", TAG,viewType,viewName);
      } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
        Log.e(e, "Unable to load supported view [" + viewType+ "] [" + viewName +"]", TAG);
      }
    }
    servletContext.setAttribute(C.CTX_SUPPORTED_VIEW_ATTRIBUTE,VIEW_CACHE);  
    /**xml handle**/
    String xmlhandle = AppConfigUtil.getProperty("smart.xml.handle");
    if(CommUtil.isEmpty(xmlhandle)){
    	xmlhandle = XML_HANDLE;
    }
    if(CommUtil.isNotEmpty(xmlhandle)){
    	try {
			Object xmlObhject = Class.forName(xmlhandle).newInstance();
			if(xmlObhject instanceof IXmlConverter){
				servletContext.setAttribute(C.CTX_SUPPORTED_XML_HANDLE_ATTRIBUTE, xmlObhject);
			}else{
				Log.e(null, xmlhandle+"does not implement the interface org.huihoo.ofbiz.smart.base.util.xml.IXmlConverter"+xmlhandle, TAG);
			}
		} catch (InstantiationException e) {
			Log.e(e, "Unable to new  instance "+xmlhandle, TAG);
		} catch (IllegalAccessException e) {
			Log.e(e, "Unable to new  instance "+xmlhandle, TAG);
		} catch (ClassNotFoundException e) {
			Log.e(e, "Unable to new  instance "+xmlhandle, TAG);
		}
    	if((null ==servletContext.getAttribute(C.CTX_SUPPORTED_XML_HANDLE_ATTRIBUTE)) && !(XML_HANDLE.equals(xmlhandle))){
    		try {
				Object xmlObhject = Class.forName(XML_HANDLE).newInstance();
				servletContext.setAttribute(C.CTX_SUPPORTED_XML_HANDLE_ATTRIBUTE, xmlObhject);
			} catch (Exception e) {
				// TODO Auto-generated catch block
			}
		}
    }
    

    WebAppContext webAppContext = new WebAppContext(servletContext);
    servletContext.setAttribute("webAppContext", webAppContext);
  }
  
  protected void loadSeedData(ServletContext servletContext){
    //Load seed data
    String seedDataSqlFile = AppConfigUtil.getProperty(C.SEED_DATA_SQL_FILE_ATTRIBUTE);
    if (CommUtil.isNotEmpty(seedDataSqlFile)) {
      Delegator delegator = (Delegator) servletContext.getAttribute(C.CTX_DELETAGOR);
      try {
        delegator.loadSeedData(seedDataSqlFile);
      } catch (GenericEntityException e) {
        Log.e(e, e.getMessage(),TAG);
      }
    }
  }
}

package org.huihoo.ofbiz.smart.webapp;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
import org.huihoo.ofbiz.smart.base.util.CommUtil;
import org.huihoo.ofbiz.smart.base.util.Log;
import org.huihoo.ofbiz.smart.entity.Delegator;
import org.huihoo.ofbiz.smart.entity.EbeanDelegator;
import org.huihoo.ofbiz.smart.entity.GenericEntityException;
import org.huihoo.ofbiz.smart.service.GenericServiceException;
import org.huihoo.ofbiz.smart.service.ServiceDispatcher;
import org.huihoo.ofbiz.smart.webapp.handler.ApiDocRequestHandler;
import org.huihoo.ofbiz.smart.webapp.handler.DefaultRequestHandler;
import org.huihoo.ofbiz.smart.webapp.handler.HttpApiRequestHandler;
import org.huihoo.ofbiz.smart.webapp.handler.RequestHandler;
import org.huihoo.ofbiz.smart.webapp.handler.RestfulRequestHandler;


public class DispatchServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;

  private static final String TAG = DispatchServlet.class.getName();

  private static volatile Cache<String, RequestHandler> HANDLER_CACHE;

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
  

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
          throws ServletException, IOException {
    processRequest(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
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
        requestHandler = HANDLER_CACHE.get("Rest");
        if (requestHandler == null) {
          requestHandler = new RestfulRequestHandler();
          HANDLER_CACHE.put("Rest", requestHandler);
        }
      } else if (targetUri.startsWith(httpApiUrlBase)) {
        requestHandler = HANDLER_CACHE.get("Api");
        if (requestHandler == null) {
          requestHandler = new HttpApiRequestHandler();
          HANDLER_CACHE.put("Api", requestHandler);
        }

      } else if (targetUri.startsWith(apiDocUriBase)) {
        requestHandler = HANDLER_CACHE.get("ApiDoc");
        if (requestHandler == null) {
          requestHandler = new ApiDocRequestHandler();
          HANDLER_CACHE.put("ApiDoc", requestHandler);
        }
      } else {
        requestHandler = HANDLER_CACHE.get("Default");
        if (requestHandler == null) {
          requestHandler = new DefaultRequestHandler();
          HANDLER_CACHE.put("Default", requestHandler);
        }
      }
      
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

    HANDLER_CACHE =
            (Cache<String, RequestHandler>) SimpleCacheManager.createCache("RequestHandler-Cache");

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
    } catch (MalformedURLException e) {
      throw new ServletException("Unable to load gobal config file : " + C.APPLICATION_CONFIG_NAME);
    } catch (IOException e) {
      throw new ServletException("Unable to load gobal config file : " + C.APPLICATION_CONFIG_NAME);
    }
  }
  
  protected void loadAppConfig(ServletContext servletContext) throws MalformedURLException, IOException, GenericServiceException {
    //NOTICE 以下对象可以重新加载,除了Delegator
    ServiceDispatcher serviceDispatcher = new ServiceDispatcher((Delegator)servletContext.getAttribute(C.CTX_DELETAGOR));
    Properties applicationConfig = new Properties();
    applicationConfig.load(FlexibleLocation.resolveLocation(C.APPLICATION_CONFIG_NAME).openStream());
    servletContext.setAttribute(C.CTX_SERVICE_DISPATCHER, serviceDispatcher);
    servletContext.setAttribute(C.APPLICATION_CONFIG_PROP_KEY, applicationConfig);
    String actionConfigBasePath = applicationConfig.getProperty(C.ACTION_CONFIG_BASEPATH_KEY, "./");
    List<ActionModel> actionModels = new ArrayList<>();
    ActionModelXmlConfigLoader.me().loadXml(FlexibleLocation.resolveLocation(actionConfigBasePath).getPath(),actionModels);
    servletContext.setAttribute(C.CTX_ACTION_MODEL,actionModels);
  }
}

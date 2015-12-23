package org.huihoo.ofbiz.smart.webapp;

import java.io.IOException;
import java.net.MalformedURLException;
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
  
  private static final  String TAG = DispatchServlet.class.getName();

  private static volatile Cache<String, RequestHandler> HANDLER_CACHE;

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    processRequest(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    processRequest(req, resp);
  }

  protected final void processRequest(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String targetUri = request.getRequestURI();
    long startTime = System.currentTimeMillis();
    Throwable failureCause = null;
    try {
      RequestHandler requestHandler = null;
      if (targetUri.startsWith("/rest/")) {
        requestHandler = HANDLER_CACHE.get("Rest");
      } else if (targetUri.startsWith("/api/")) {
        requestHandler = HANDLER_CACHE.get("Api");
      } else if (targetUri.startsWith("/api_doc/")) {
        requestHandler = HANDLER_CACHE.get("ApiDoc");
      } else {
        requestHandler = HANDLER_CACHE.get("Default");
      }
      
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
        Log.i("Process request [" + targetUri + "] cost [" + processingTime + "] ms. But an error happend : " + failureCause.getMessage(), TAG);
      } else {
        Log.i("Process request [" + targetUri + "] cost [" + processingTime + "] ms", TAG);
      }
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);
    
    initWebContext(config.getServletContext());
    
    HANDLER_CACHE = (Cache<String, RequestHandler>) SimpleCacheManager.createCache("RequestHandler-Cache");

    loadBuiltInHandler();
  }

  protected void initWebContext(ServletContext servletContext) throws ServletException{
    try {
      Delegator delegator = new EbeanDelegator();
      ServiceDispatcher serviceDispatcher = new ServiceDispatcher(delegator);
      Properties applicationConfig = new Properties();
      applicationConfig.load(FlexibleLocation.resolveLocation(C.APPLICATION_CONFIG_NAME).openStream());
      servletContext.setAttribute(C.CTX_DELETAGOR,delegator);
      servletContext.setAttribute(C.CTX_SERVICE_DISPATCHER, serviceDispatcher);
      servletContext.setAttribute(C.APPLICATION_CONFIG_PROP_KEY, applicationConfig);
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
  
  protected void loadBuiltInHandler() {
    HANDLER_CACHE.put("Default", new DefaultRequestHandler());
    HANDLER_CACHE.put("Api", new HttpApiRequestHandler());
    HANDLER_CACHE.put("Rest", new RestfulRequestHandler());
    HANDLER_CACHE.put("ApiDoc", new ApiDocRequestHandler());
  }
}

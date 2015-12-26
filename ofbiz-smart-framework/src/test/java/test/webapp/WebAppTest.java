package test.webapp;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
import org.huihoo.ofbiz.smart.webapp.ActionModel;
import org.huihoo.ofbiz.smart.webapp.ActionModel.Action;
import org.huihoo.ofbiz.smart.webapp.ActionModelXmlConfigLoader;
import org.huihoo.ofbiz.smart.webapp.DispatchServlet;
import org.huihoo.ofbiz.smart.webapp.view.View;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;



public class WebAppTest {
  private final static String TAG = WebAppTest.class.getName();
  
  private ServletContext context;
  private ServletConfig config;
  private HttpServletRequest req;
  private HttpServletResponse resp;
  private HttpSession session;
  private Properties applicationConfig;
  
  @Before
  public void setUp() {
    req = mock(HttpServletRequest.class);
    resp = mock(HttpServletResponse.class);
    session = mock(HttpSession.class);
    context = mock(ServletContext.class);
    config = mock(ServletConfig.class);
    applicationConfig = mock(Properties.class);
  }
  
  @Test
  public void testDispatchServletInit() throws ServletException, IOException {
   
    when(context.getAttribute(C.APPLICATION_CONFIG_PROP_KEY)).thenReturn(applicationConfig);
    when(config.getInitParameter("jsp-view-base-path")).thenReturn("");
    when(config.getInitParameter("uri-suffix")).thenReturn("");
    when(config.getInitParameter("http-api-uri-base")).thenReturn("/api");
    when(config.getInitParameter("rest-api-uri-base")).thenReturn("/rest");
    when(config.getInitParameter("api-doc-uri-base")).thenReturn("/doc");
    when(config.getServletContext()).thenReturn(context);
    when(req.getContextPath()).thenReturn("");
    when(req.getSession()).thenReturn(session);
    when(req.getServletContext()).thenReturn(context);
    when(applicationConfig.getProperty(C.SEED_DATA_SQL_FILE_ATTRIBUTE)).thenReturn("");
    
    DispatchServlet dispatchServlet = new DispatchServlet();
    dispatchServlet.init(config);
    
    
    verify(config,times(1)).getInitParameter("jsp-view-base-path");
    verify(config,times(1)).getInitParameter("uri-suffix");
    verify(config,times(1)).getInitParameter("http-api-uri-base");
    verify(config,times(1)).getInitParameter("rest-api-uri-base");
    verify(config,times(1)).getInitParameter("api-doc-uri-base");
    
    Assert.assertNotNull(config.getInitParameter("http-api-uri-base"));
  }
  
  @Test
  public void testDoGet() throws IOException, ServletException, GenericEntityException, GenericServiceException {
    Cache<String,View> viewCache = (Cache<String,View>) SimpleCacheManager.createCache("mock-cache");
    Delegator delegator = new EbeanDelegator();
    ServiceDispatcher serviceDispatcher = new ServiceDispatcher(delegator);
    MockServletOutputStream msos = new MockServletOutputStream();
    StringWriter writer = new StringWriter();
    
    
    when(context.getAttribute(C.APPLICATION_CONFIG_PROP_KEY)).thenReturn(applicationConfig);
    when(config.getInitParameter("jsp-view-base-path")).thenReturn("");
    when(config.getInitParameter("uri-suffix")).thenReturn("");
    when(config.getInitParameter("http-api-uri-base")).thenReturn("/api");
    when(config.getInitParameter("rest-api-uri-base")).thenReturn("/rest");
    when(config.getInitParameter("api-doc-uri-base")).thenReturn("/doc");
    when(config.getServletContext()).thenReturn(context);
    
    when(context.getAttribute(C.CTX_DELETAGOR)).thenReturn(delegator);
    when(context.getAttribute(C.CTX_SERVICE_DISPATCHER)).thenReturn(serviceDispatcher);
    when(context.getAttribute(C.APPLICATION_CONFIG_PROP_KEY)).thenReturn(applicationConfig);
    when(context.getAttribute(C.CTX_JSP_VIEW_BASEPATH)).thenReturn("/");
    when(context.getAttribute(C.CTX_URI_SUFFIX)).thenReturn("");
    when(context.getAttribute(C.CTX_SUPPORTED_VIEW_ATTRIBUTE)).thenReturn(viewCache);
    when(context.getAttribute(C.CTX_ACTION_MODEL)).thenReturn(getActionModels());
    
    when(req.getServletContext()).thenReturn(context);
    when(req.getContextPath()).thenReturn("");
    when(req.getRequestURI()).thenReturn("/");
    
    Vector<String> v = new Vector<String>();
    v.addElement("test");
    when(req.getParameter("test")).thenReturn("test");
    
    Enumeration<String> enumeration = v.elements();
    when(req.getParameterNames()).thenReturn(enumeration);
    
    when(resp.getOutputStream()).thenReturn(msos);
    when(resp.getWriter()).thenReturn(new PrintWriter(writer)); 
    
    
    DispatchServlet dispatchServlet = new DispatchServlet();
    dispatchServlet.init(config);
    dispatchServlet.doGet(req, resp);
    Log.d("Response > " + writer.toString(), TAG);
  }
  
  @Test
  public void testLoadConfig() throws IOException {
    Log.d("Start testing....", TAG);
    
    String path = FlexibleLocation.resolveLocation("./").getPath();
    Log.d("Path >" + path, TAG);
    List<ActionModel> actionModels = new ArrayList<>(); 
    ActionModelXmlConfigLoader.me().loadXml(path,actionModels);
    Log.d("actionModels > " +actionModels, TAG);
    Assert.assertEquals(true, actionModels.size() > 0);
    
    Action action = shouldHasAction("/order/create", actionModels);
    Assert.assertNotNull(action);
    Assert.assertEquals(true, "byConfig".equals(action.processType));
    Assert.assertEquals(true, action.serviceCallList.size() == 1);
    
    Assert.assertEquals(true, action.serviceCallList.get(0).serviceName.equals("createOrder"));
    Assert.assertEquals(true, "json".equals(action.response.viewType));
    
    action = shouldHasAction("/product/detail", actionModels);
    Assert.assertNotNull(action);
    
    action = shouldHasAction("/news/**", actionModels);
    Assert.assertNotNull(action);
    
    action = shouldHasAction("/customer/detail", actionModels);
    Assert.assertNotNull(action);
  }
  
  
  private Action shouldHasAction(String actionUri,List<ActionModel> actionModels) {
    for (ActionModel actionModel : actionModels) {
      List<Action> actions = actionModel.actionList;
      for (Action action : actions) {
        if (actionUri.equals(action.uri)) {
          return action;
        }
      }
    }
    return null;
  }
  
  private List<ActionModel> getActionModels() throws MalformedURLException {
    List<ActionModel> actionModels = new ArrayList<ActionModel>();
    ActionModelXmlConfigLoader.me().loadXml(FlexibleLocation.resolveLocation("./").getPath(), actionModels);
    return actionModels;
  }
}

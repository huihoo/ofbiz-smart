package test.webapp;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.huihoo.ofbiz.smart.base.C;
import org.huihoo.ofbiz.smart.base.cache.Cache;
import org.huihoo.ofbiz.smart.base.cache.SimpleCacheManager;
import org.huihoo.ofbiz.smart.base.location.FlexibleLocation;
import org.huihoo.ofbiz.smart.base.util.Log;
import org.huihoo.ofbiz.smart.base.util.ServiceUtil;
import org.huihoo.ofbiz.smart.entity.Delegator;
import org.huihoo.ofbiz.smart.entity.EbeanDelegator;
import org.huihoo.ofbiz.smart.entity.GenericEntityException;
import org.huihoo.ofbiz.smart.service.GenericServiceException;
import org.huihoo.ofbiz.smart.service.ServiceDispatcher;
import org.huihoo.ofbiz.smart.webapp.ActionModel;
import org.huihoo.ofbiz.smart.webapp.ActionModel.Action;
import org.huihoo.ofbiz.smart.webapp.ActionModelXmlConfigLoader;
import org.huihoo.ofbiz.smart.webapp.DispatchServlet;
import org.huihoo.ofbiz.smart.webapp.WebAppContext;
import org.huihoo.ofbiz.smart.webapp.WebAppManager;
import org.huihoo.ofbiz.smart.webapp.view.CaptchaView;
import org.huihoo.ofbiz.smart.webapp.view.JsonView;
import org.huihoo.ofbiz.smart.webapp.view.JspView;
import org.huihoo.ofbiz.smart.webapp.view.RedirectView;
import org.huihoo.ofbiz.smart.webapp.view.View;
import org.huihoo.ofbiz.smart.webapp.view.XmlView;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockRequestDispatcher;

import com.fasterxml.jackson.databind.ObjectMapper;



public class WebAppTest {
  private final static String TAG = WebAppTest.class.getName();

  private ServletContext context;
  private ServletConfig config;
  private MockHttpServletRequest req;
  private MockHttpServletResponse resp;
  private HttpSession session;
  private Properties applicationConfig;
  private DispatchServlet dispatchServlet;
  private Cache<String, View> viewCache = null;
  private Delegator delegator = null;
  private ServiceDispatcher serviceDispatcher = null;

  @SuppressWarnings("unchecked")
  @Before
  public void setUp() throws GenericEntityException, GenericServiceException {
    dispatchServlet = spy(new DispatchServlet());
    req = mock(MockHttpServletRequest.class);
    resp = new MockHttpServletResponse();
    session = mock(HttpSession.class);
    context = mock(ServletContext.class);
    config = mock(ServletConfig.class);
    applicationConfig = mock(Properties.class);
    viewCache = (Cache<String, View>) SimpleCacheManager.createCache("mock-cache");
    viewCache.put("jsp", new JspView());
    viewCache.put("redirect", new RedirectView());
    viewCache.put("json", new JsonView());
    viewCache.put("xml", new XmlView());
    viewCache.put("captcha", new CaptchaView());
    
    delegator = new EbeanDelegator();
    serviceDispatcher = new ServiceDispatcher(delegator);
  }

  @Test
  public void testDispatchServletInit() throws ServletException, IOException {
    when(context.getAttribute(C.APPLICATION_CONFIG_PROP_KEY)).thenReturn(applicationConfig);
    when(context.getAttribute(C.CTX_DELETAGOR)).thenReturn(delegator);
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

    verify(config, times(1)).getInitParameter("jsp-view-base-path");
    verify(config, times(1)).getInitParameter("uri-suffix");
    verify(config, times(1)).getInitParameter("http-api-uri-base");
    verify(config, times(1)).getInitParameter("rest-api-uri-base");
    verify(config, times(1)).getInitParameter("api-doc-uri-base");

    Assert.assertNotNull(config.getInitParameter("http-api-uri-base"));
  }

  @Test
  public void testDoGetSuccess() throws Exception {
    //JspView
    String viewName = "/WEB-INF/views/index.jsp";
    when(req.getRequestURI()).thenReturn("/");
    initMockReq(viewName);

    Vector<String> v = new Vector<String>();
    v.addElement("username");
    when(req.getParameter("username")).thenReturn("hbh");
    Enumeration<String> enumeration = v.elements();
    when(req.getParameterNames()).thenReturn(enumeration);
    
    dispatchServlet.init(config);
    dispatchServlet.doGet(req, resp);
    
    String viewContent = resp.getContentAsString();
    Log.d("View content:" + viewContent, TAG);

    verify(req, times(1)).getRequestDispatcher(viewName);
    Assert.assertEquals("text/html;charset=utf-8", resp.getContentType());
    Assert.assertEquals("hbh", req.getParameter("username"));
    Assert.assertEquals("/WEB-INF/views/index.jsp", req.getAttribute(C.JSP_VIEW_NAME_ATTRIBUTE));
    Assert.assertNull(req.getAttribute(C.JSP_VIEW_LAYOUT_CONTENT_VIEW_ATTRIBUTE));
    
  }
  
  
  @SuppressWarnings("rawtypes")
  @Test
  public void testRequestForJsonViewFail() throws Exception {
    //JspView
    String viewName = "";
    initMockReq(viewName);
    when(req.getRequestURI()).thenReturn("/order/createFail");
    
    Vector<String> v = new Vector<String>();
    v.addElement("username");
    when(req.getParameter("username")).thenReturn("hbh");
    Enumeration<String> enumeration = v.elements();
    when(req.getParameterNames()).thenReturn(enumeration);
    
    dispatchServlet.init(config);
    dispatchServlet.doGet(req, resp);
    String viewContent = resp.getContentAsString();
    Log.d("View content:" + viewContent, TAG);
    ObjectMapper objectMapper = new ObjectMapper();
    Map jsonMap =  objectMapper.readValue(viewContent, Map.class);
    Assert.assertEquals(true, jsonMap.containsKey("error"));
    Assert.assertEquals(true, jsonMap.containsKey("validation_errors"));
  }
  
  @SuppressWarnings("rawtypes")
  @Test
  public void testRequestForJsonViewSuccess() throws Exception {
    String viewName = "";
    initMockReq(viewName);
    when(req.getRequestURI()).thenReturn("/order/createSuccess");
    Vector<String> v = new Vector<String>();
    v = new Vector<String>();
    v.addElement("fromChannel");
    v.addElement("userId");
    v.addElement("paymentMethod");
    when(req.getParameter("fromChannel")).thenReturn("WEB");
    when(req.getParameter("userId")).thenReturn("1000");
    when(req.getParameter("paymentMethod")).thenReturn("ALI_PAY");
    Enumeration<String> enumeration = v.elements();
    when(req.getParameterNames()).thenReturn(enumeration);
    dispatchServlet.init(config);
    dispatchServlet.doGet(req, resp);
    String viewContent = resp.getContentAsString();
    Log.d("View content:" + viewContent, TAG);
    ObjectMapper objectMapper = new ObjectMapper();
    Map jsonMap =  objectMapper.readValue(viewContent, Map.class);
    Assert.assertEquals(true, jsonMap.containsKey("success"));
  }

  @Test
  public void testLoadActionXmlConfig() throws IOException {
    String path = FlexibleLocation.resolveLocation("./").getPath();
    Log.d("Path >" + path, TAG);
    List<ActionModel> actionModels = new ArrayList<>();
    ActionModelXmlConfigLoader.me().loadXml(path, actionModels);
    Log.d("actionModels > " + actionModels, TAG);
    Assert.assertEquals(true, actionModels.size() > 0);

    Action action = shouldHasAction("/order/createFail", actionModels);
    Assert.assertNotNull(action);
    Assert.assertEquals(true, "byConfig".equals(action.processType));
    Assert.assertEquals(true, action.serviceCallList.size() == 1);

    Assert.assertEquals(true, action.serviceCallList.get(0).serviceName.equals("createOrderFail"));
    Assert.assertEquals(true, "json".equals(action.response.viewType));

    action = shouldHasAction("/product/detail", actionModels);
    Assert.assertNotNull(action);

    action = shouldHasAction("/news/**", actionModels);
    Assert.assertNotNull(action);

    action = shouldHasAction("/customer/detail", actionModels);
    Assert.assertNotNull(action);
  }

  
  @Test
  public void testWebAppManagerFunc() throws MalformedURLException {
    String viewName = "/WEB-INF/views/index.jsp";
    when(req.getRequestURI()).thenReturn("/");
    initMockReq(viewName);
    
    when(req.getParameter("cityId")).thenReturn("1000");
    when(session.getAttribute("code")).thenReturn("ABC");
    
    String condition =  "{age,eq,30}{gender,eq,male}{status,eq,1}{city,eq,requestScope.cityId}{code,eq,sessionScope.code}";
    String parsedCond = WebAppManager.parseCondition(condition, req);
    Assert.assertEquals("{age,eq,30}{gender,eq,male}{status,eq,1}{city,eq,1000}{code,eq,ABC}", parsedCond);
    
    when(req.getQueryString()).thenReturn("s_age_eq=30&s_gender_eq=male&s_status_eq=1&s_city_eq_=1000&s_code_eq=ABC");
    parsedCond = WebAppManager.parseConditionFromQueryString(req);
    Assert.assertEquals("{age,eq,30}{gender,eq,male}{status,eq,1}{city,eq,1000}{code,eq,ABC}", parsedCond);
    
    Map<String,Object> resultMap = ServiceUtil.returnSuccess();
    resultMap.put("model", "MODEL");
    
    when(session.getAttribute("userId")).thenReturn("1000");
    String queryString = "name=hbh&status=active&userid={sessionScope.userId}&model={model}";
    String parsedStr = WebAppManager.parseQueryString(queryString, req, resultMap);
    Assert.assertEquals("name=hbh&status=active&userid=1000&model=MODEL", parsedStr);
    
    when(session.getAttribute("t")).thenReturn("t");
    when(req.getParameter("userId")).thenReturn("1000");
    String pps = WebAppManager.parseParamPairString("name,hbh,status,active,user_id,requestScope.userId,t,sessionScope.t", req);
    Assert.assertEquals("name,hbh,status,active,user_id,1000,t,t", pps);
    
    Map<String,Object> modelMap = ServiceUtil.returnSuccess();
    
    Map<String,Object> errorMap = ServiceUtil.returnProplem("ERROR01", "Error1");
    
    modelMap.putAll(errorMap);
    
    
    errorMap = ServiceUtil.returnProplem("ERROR02", "Error2");
    modelMap.putAll(errorMap);
    
    Log.d("" + modelMap, TAG);
    
  }

  private void initMockReq(String viewName) throws MalformedURLException {
    when(config.getInitParameter("jsp-view-base-path")).thenReturn("/WEB-INF/views/");
    when(config.getInitParameter("uri-suffix")).thenReturn("");
    when(config.getInitParameter("http-api-uri-base")).thenReturn("/api");
    when(config.getInitParameter("rest-api-uri-base")).thenReturn("/rest");
    when(config.getInitParameter("api-doc-uri-base")).thenReturn("/doc");
    when(config.getServletContext()).thenReturn(context);

    when(context.getAttribute(C.APPLICATION_CONFIG_PROP_KEY)).thenReturn(applicationConfig);
    when(context.getAttribute(C.APPLICATION_CONFIG_PROP_KEY)).thenReturn(applicationConfig);
    when(context.getAttribute(C.CTX_DELETAGOR)).thenReturn(delegator);
    when(context.getAttribute(C.CTX_SERVICE_DISPATCHER)).thenReturn(serviceDispatcher);
    when(context.getAttribute(C.APPLICATION_CONFIG_PROP_KEY)).thenReturn(applicationConfig);
    when(context.getAttribute(C.CTX_JSP_VIEW_BASEPATH)).thenReturn("/WEB-INF/views");
    when(context.getAttribute(C.CTX_URI_SUFFIX)).thenReturn("");
    when(context.getAttribute(C.CTX_SUPPORTED_VIEW_ATTRIBUTE)).thenReturn(viewCache);
    when(context.getAttribute(C.CTX_ACTION_MODEL)).thenReturn(getActionModels());
    
    WebAppContext webAppContext = new WebAppContext(context);
    when(context.getAttribute("webAppContext")).thenReturn(webAppContext);

    
    when(req.getServletContext()).thenReturn(context);
    when(req.getContextPath()).thenReturn("");
    when(session.getServletContext()).thenReturn(context);
    when(req.getSession()).thenReturn(session);
    
    MockRequestDispatcher requestDispatcher = new MockRequestDispatcher(viewName);
    when(req.getAttribute("viewName")).thenReturn(viewName);
    when(req.getRequestDispatcher(viewName)).thenReturn(requestDispatcher);
  }

  private Action shouldHasAction(String actionUri, List<ActionModel> actionModels) {
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

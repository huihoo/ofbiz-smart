package webapp;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.huihoo.ofbiz.smart.base.C;
import org.huihoo.ofbiz.smart.base.utils.Debug;
import org.huihoo.ofbiz.smart.entity.Delegator;
import org.huihoo.ofbiz.smart.entity.EbeanDelegator;
import org.huihoo.ofbiz.smart.entity.GenericEntityException;
import org.huihoo.ofbiz.smart.service.GenericServiceException;
import org.huihoo.ofbiz.smart.service.ServiceDispatcher;
import org.huihoo.ofbiz.smart.webapp.control.ControlServlet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import entity.Customer;
import entity.Order;
import entity.Product;





public class ControlServletTest {
  private static final String module = ControlServletTest.class.getName();
  private static final File responseFile = new File("response.txt");

  Delegator delegator;
  ServiceDispatcher dispatcher;
  @Before
  public void init() {
    Properties p = new Properties();
    try {
      p.load(getClass().getResourceAsStream("/datasource-test.properties"));
      delegator = new EbeanDelegator("h2", "entity", p);
      dispatcher = new ServiceDispatcher(delegator);
    } catch (GenericEntityException | IOException | GenericServiceException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testAllInOne() throws IOException {
    HttpServletRequest req = mock(HttpServletRequest.class);
    HttpServletResponse resp = mock(HttpServletResponse.class);
    HttpSession session = mock(HttpSession.class);
    ServletContext context = mock(ServletContext.class);
    ServletConfig config = mock(ServletConfig.class);
    
    String path = ControlServletTest.class.getResource("").getPath();
    Debug.logInfo(path, module);
    String fPath = path.substring(1);
    Debug.logInfo(fPath, module);
    
    when(config.getInitParameter("app-config-file")).thenReturn("app.properties");
    when(config.getInitParameter("api-router-gateway")).thenReturn("/api/router");
    
    
    when(session.getServletContext()).thenReturn(context);
    
    when(context.getRealPath("")).thenReturn(fPath);    
    when(config.getServletContext()).thenReturn(context);
    
    when(req.getSession()).thenReturn(session);
    when(req.getContextPath()).thenReturn("");
    when(req.getPathInfo()).thenReturn("");
    when(req.getMethod()).thenReturn("post");
    
    when(req.getAttribute(C.CTX_DELEGATOR)).thenReturn(delegator);
    when(req.getAttribute(C.CTX_SERVICE_DISPATCHER)).thenReturn(dispatcher);
    
    when(context.getAttribute(C.CTX_DELEGATOR)).thenReturn(delegator);
    when(context.getAttribute(C.CTX_SERVICE_DISPATCHER)).thenReturn(dispatcher);
    
    PrintWriter out = new PrintWriter(responseFile);
    when(resp.getWriter()).thenReturn(out);

    when(req.getRequestURI()).thenReturn("/customer/new");
    when(req.getServletPath()).thenReturn("/customer/new");

    Map<String, String> bizParaMap = new HashMap<String, String>();
    bizParaMap.put("name", "Huangbaihua");
    bizParaMap.put("countryCode", "countryCode");
    bizParaMap.put("line1", "line1");
    bizParaMap.put("line2", "line2");

    Vector<String> paramVetor = new Vector<String>();
    Set<Entry<String, String>> bizParaSet = bizParaMap.entrySet();
    Iterator<Entry<String, String>> bizIt = bizParaSet.iterator();
    bizIt = bizParaSet.iterator();
    while (bizIt.hasNext()) {
      Entry<String, String> entry = bizIt.next();
      paramVetor.add(entry.getKey());
    }

    when(req.getParameterNames()).thenReturn(paramVetor.elements());

    bizIt = bizParaSet.iterator();
    while (bizIt.hasNext()) {
      Entry<String, String> entry = bizIt.next();
      when(req.getParameter(entry.getKey())).thenReturn(entry.getValue());
    }

    
    try {
      ControlServlet controlServlet = new ControlServlet();
      controlServlet.init(config);
      controlServlet.doPost(req, resp);
      out.flush();
      String response = readResponseFile();
      Debug.logDebug("response->"+response, module);
      Assert.assertEquals(true, response.indexOf("success") != -1);    
      
      Customer customer = (Customer) delegator.findById("Customer", 1L);
      Assert.assertNotNull(customer);
      Assert.assertEquals(1, customer.getId().longValue());
      
      //=============================================================
      //
      //=============================================================
      Product testProduct = new Product();
      testProduct.setName("Test-Product");
      testProduct.setSku("SKU-001");
      delegator.save(testProduct);
      
      when(req.getRequestURI()).thenReturn("/order/create");
      when(req.getServletPath()).thenReturn("/order/create");

      bizParaMap = new HashMap<String, String>();
      bizParaMap.put("customerId", "1");
      bizParaMap.put("productId", "1");
      bizParaMap.put("orderQty", "2");

      paramVetor = new Vector<String>();
      bizParaSet = bizParaMap.entrySet();
      bizIt = bizParaSet.iterator();
      bizIt = bizParaSet.iterator();
      while (bizIt.hasNext()) {
        Entry<String, String> entry = bizIt.next();
        paramVetor.add(entry.getKey());
      }

      when(req.getParameterNames()).thenReturn(paramVetor.elements());

      bizIt = bizParaSet.iterator();
      while (bizIt.hasNext()) {
        Entry<String, String> entry = bizIt.next();
        when(req.getParameter(entry.getKey())).thenReturn(entry.getValue());
      }
      controlServlet.doPost(req, resp);
      out.flush();
      response = readResponseFile();
      Debug.logDebug("response->"+response, module);
      Order order = (Order) delegator.findById("Order", 1L);  
      Assert.assertNotNull(order);
      Assert.assertEquals(1, order.getId().longValue());
      
    } catch (ServletException | GenericEntityException e) {
      e.printStackTrace();
    }

  }


  public String readResponseFile() {
    try {
      InputStreamReader isr = new InputStreamReader(new FileInputStream(responseFile), "utf-8");
      BufferedReader br = new BufferedReader(isr);
      String line = br.readLine();
      StringBuffer sb = new StringBuffer();
      while (line != null) {
        sb.append(line);
        line = br.readLine();
      }
      br.close();
      return sb.toString();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

}

package test.webapp;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.util.ByteArrayISO8859Writer;
import org.huihoo.ofbiz.smart.base.util.Log;
import org.huihoo.ofbiz.smart.webapp.DispatchServlet;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;

public class WebWithJettyTest {
  private static final String TAG = WebWithJettyTest.class.getName();
  protected static Server server;

  @Before
  public void setUp() throws Exception {
    Log.i("It's starting server...", TAG);
    server = new Server(8080);
    server.setStopAtShutdown(true);
    //Custom handler for processing...
    ContextHandler context = new ServletContextHandler();
    
    ServletHandler handler = new ServletHandler();
    handler.addServletWithMapping(DispatchServlet.class, "*.htm");
    handler.addServletWithMapping(DispatchServlet.class, "/api");
    handler.addServletWithMapping(DispatchServlet.class, "/rest");
    
    context.setHandler(handler);
    context.setResourceBase(".");
    context.setContextPath("");
    
    server.setHandler(context); 
    server.start();
  }

  @Test
  public void testGetContentOk() throws Exception {
    OkHttpClient client = new OkHttpClient();
    com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
            .url("http://127.0.0.1:8080/testGetContentOk.htm").build();

    Response response = client.newCall(request).execute();

    String result = response.body().string();
    Assert.assertEquals(true, result.contains("It works"));
  }

  private class MockGetContentOkHandler extends AbstractHandler {
    @Override
    public void handle(String s, Request req, HttpServletRequest httpServletRequest,
            HttpServletResponse response) throws IOException, ServletException {
      OutputStream out = response.getOutputStream();
      ByteArrayISO8859Writer writer = new ByteArrayISO8859Writer();

      writer.write("It works");
      writer.flush();
      response.setContentLength(writer.size());
      writer.writeTo(out);
      out.flush();
      writer.close();
      req.setHandled(true);
    }
  }

  @After
  public void tearDown() throws Exception {
    Log.i("It's stopping server...", TAG);
    server.stop();
  }
}
